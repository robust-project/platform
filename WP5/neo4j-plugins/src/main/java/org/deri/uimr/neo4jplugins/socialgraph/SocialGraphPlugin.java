/**
 * Copyright 2013 DERI, National University of Ireland Galway.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.deri.uimr.neo4jplugins.socialgraph;

import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Name;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.PluginTarget;
import org.neo4j.server.plugins.Source;
import org.neo4j.server.plugins.Parameter;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;

import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Locale;

/**
 * Social Graph Plugin main class.
 *
 * <p>This plugin allows batch insertion of social graph events and interactions.</p>
 *
 * <p>Transactions are used only once per request for the batch insertion to be done as fast as possible.</p>
 *
 * @since 1.0
 */
@Description("Social Graph plugin for handling graph building from events")
public class SocialGraphPlugin extends ServerPlugin {
    /** Jackson Object Mapper to parse JSON data */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Relationship types this plugin uses. */
    public static enum RelType implements RelationshipType {
        PUBLISHED, QUOTED, REPLIED, MENTIONED
    }

    /** This class represents an exception during processing. */
    private class ProcessingException extends Exception {
        ProcessingException(final String message) {
            super(message);
        }
    }

    /**
     * Batch inserts Social Graph events into the graph database.
     *
     * @param eventsJSON the tweets string (with proper format) to batch insert into the graph database
     * @return the number of tweets successfully inserted
     */
    @Name("push_events")
    @Description("Push new social graph events into the graph")
    @PluginTarget( GraphDatabaseService.class )
    public long pushEvents( @Source final GraphDatabaseService graphDb,
           @Description("JSON-encoded string representation of social graph events to push")
           @Parameter(name="eventsJSON", optional=false) final String eventsJSON ) throws Exception {

        // Parse the JSON-encoded events data
        final Event[] events = objectMapper.readValue(eventsJSON, Event[].class);

        // Initialize working variables
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"), Locale.ENGLISH);
        final IndexManager indexManager = graphDb.index();
        final RelationshipIndex timestampsIndex = indexManager.forRelationships("timestamps");
        final Map<String,SocialGraphUniqueNodeFactory> uniqueNodeFactories = new HashMap<String,SocialGraphUniqueNodeFactory>();
        uniqueNodeFactories.put("user", new SocialGraphUniqueNodeFactory(indexManager, "user"));
        uniqueNodeFactories.put("content", new SocialGraphUniqueNodeFactory(indexManager, "content"));
        long num_interactions = 0;

        // Begin transaction
        final Transaction tx = graphDb.beginTx();
        try {
            // Process each pushed event
            for( final Event event : events ) {
                // Process event timestamp
                final long timestamp = event.getTimestamp();
                calendar.setTimeInMillis(timestamp);
                final String eventDate = String.format("%04d-%02d-%02d-H%02d",
                                                       calendar.get(Calendar.YEAR),
                                                       calendar.get(Calendar.MONTH) + 1,
                                                       calendar.get(Calendar.DAY_OF_MONTH),
                                                       calendar.get(Calendar.HOUR_OF_DAY));

                // Process each interaction inside the event
                for( final Interaction interaction : event.getInteractions() ) {
                    try {
                        // According to the interaction type, create relationships and target nodes
                        switch(interaction.getType()) {
                            case CONTENT_PUBLICATION: { // actor_id, content_id
                                final Node actor = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "actor_id"));
                                final Node content = uniqueNodeFactories.get("content").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "content_id"));
                                final Relationship relationship = actor.createRelationshipTo(content, RelType.PUBLISHED);
                                relationship.setProperty("timestamp", timestamp);
                                timestampsIndex.add(relationship, "date", eventDate);
                            }
                            break;
                            case CONTENT_QUOTATION: { // actor_id, content_id
                                final Node actor = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "actor_id"));
                                final Node content = uniqueNodeFactories.get("content").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "content_id"));
                                final Relationship relationship = actor.createRelationshipTo(content, RelType.QUOTED);
                                relationship.setProperty("timestamp", timestamp);
                                timestampsIndex.add(relationship, "date", eventDate);
                            }
                            break;
                            case CONTENT_REPLY: { // actor_id, content_id
                                final Node actor = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "actor_id"));
                                final Node content = uniqueNodeFactories.get("content").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "content_id"));
                                final Relationship relationship = actor.createRelationshipTo(content, RelType.REPLIED);
                                relationship.setProperty("timestamp", timestamp);
                                timestampsIndex.add(relationship, "date", eventDate);
                            }
                            break;
                            case USER_MENTION: { // actor_id, user_id
                                final Node actor = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "actor_id"));
                                final Node user = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("id",
                                         getStringAttribute(interaction, "user_id"));
                                final Relationship relationship = actor.createRelationshipTo(user, RelType.MENTIONED);
                                relationship.setProperty("timestamp", timestamp);
                                timestampsIndex.add(relationship, "date", eventDate);
                            }
                            break;
                        }

                        // Account processed interactions
                        num_interactions++;
                    }
                    catch( ProcessingException e ) {
                        System.out.println(e.toString());
                        continue;
                    }
                }
            }

            // Everything went fine
            tx.success();
        }
        finally {
            // Finish the transaction
            tx.finish();
        }

        // Finished
        return num_interactions;
    }

    /**
     * Gets a specified attribute as a string from specified interaction.
     *
     * @param interaction the interaction to get the attribute from.
     * @param name the attribute name to get the value from.
     */
    private String getStringAttribute(final Interaction interaction, final String name) throws ProcessingException {
        final Map<String,Object> attributes = interaction.getAttributes();
        if( !attributes.containsKey(name) )
            throw new ProcessingException("interaction does not contain the specified attribute");
        final Object value = attributes.get(name);
        if( value == null )
            throw new ProcessingException("specified attribute value is null");
        return String.valueOf(value);
    }
}
