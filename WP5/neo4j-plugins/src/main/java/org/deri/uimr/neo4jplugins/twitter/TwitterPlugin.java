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
package org.deri.uimr.neo4jplugins.twitter;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;

import org.neo4j.server.plugins.ServerPlugin;
import org.neo4j.server.plugins.Name;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.PluginTarget;
import org.neo4j.server.plugins.Source;
import org.neo4j.server.plugins.Parameter;

import java.util.Map;
import java.util.HashMap;

/**
 * Twitter Plugin main class.
 *
 * <p>This plugin allows batch insertion of tweets relationships (replies, mentions, refers_to and retweest).</p>
 *
 * <p>Transactions are used only once per request for the batch insertion to be done as fast as possible.</p>
 *
 * @since 1.0
 */
@Description("Twitter plugin for handling Twitter graph building")
public class TwitterPlugin extends ServerPlugin
{
    /** Relationship types this plugin uses. */
    public static enum RelType implements RelationshipType {
        REPLIES, MENTIONS, REFERS_TO, RETWEETS
    }

    /**
     * Create a new relationship for a tweet.
     *
     * @param indexManager the index manager to use for indexing the new relationship
     * @param tweet the tweet to get data for use in the new relationship attributes
     * @param source the source user node for the new relationship
     * @param target the target user/hashtag node for the new relationship
     * @param type the type of the new relationship
     */
    private void createTweetRelationship(
            final IndexManager indexManager, final Tweet tweet,
            final Node source, final Node target, final RelType type) {
        final Relationship relationship = source.createRelationshipTo(target, type);
        relationship.setProperty("timestamp", tweet.timestamp);

        // Index the relationship by its timestamp (the date part)
        final RelationshipIndex timestamps = indexManager.forRelationships("timestamps");
        timestamps.add(relationship, "date", tweet.createdAt.substring(0, tweet.createdAt.indexOf(" ")));
    }

    /**
     * Batch inserts tweets into the graph database.
     *
     * <p>See {@link Tweet#parse(String)} for the string format details.</p>
     *
     * @param tweets_str the tweets string (with proper format) to batch insert into the graph database
     * @return the number of tweets successfully inserted
     */
    @Name("push_tweets")
    @Description("Push new tweets into the Twitter graph")
    @PluginTarget( GraphDatabaseService.class )
    public int push_tweets( @Source GraphDatabaseService graphDb,
           @Description("Tweets string representations to push")
           @Parameter(name="tweets", optional=false) String[] tweets_str ) {

        // Initialize
        final IndexManager indexManager = graphDb.index();
        final Map<String,TwitterUniqueNodeFactory> uniqueNodeFactories = new HashMap<String,TwitterUniqueNodeFactory>();
        uniqueNodeFactories.put("user", new TwitterUniqueNodeFactory(indexManager, "user"));
        uniqueNodeFactories.put("hashtag", new TwitterUniqueNodeFactory(indexManager, "hashtag"));
        int num_pushed = 0;

        // Begin transaction
        final Transaction tx = graphDb.beginTx();
        try {
            // Process each tweet string representation
            for( final String tweet_str : tweets_str ) {
                final Tweet tweet = Tweet.parse(tweet_str);
                if( tweet == null ) continue;

                // Insert author user node and account the tweet
                final Node author = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("name", tweet.author);

                // Is there a reply?
                if( tweet.reply != null ) {
                    final Node node = uniqueNodeFactories.get("user").getOrCreateIgnoreCase("name", tweet.reply);
                    createTweetRelationship(indexManager, tweet, author, node, RelType.REPLIES);
                }

                // Go through the tweet lists to create relationships
                for( final String[] list : new String[][]{tweet.mentions, tweet.hashtags, tweet.rtOrigins} ) {
                    if( list != null ) {
                        // Get the list type (users or hashtags)
                        String type = "user";
                        RelType reltype = null;
                        if( list == tweet.mentions ) reltype = RelType.MENTIONS;
                        else if( list == tweet.hashtags ) { reltype = RelType.REFERS_TO; type = "hashtag"; }
                        else if( list == tweet.rtOrigins ) reltype = RelType.RETWEETS;

                        // Create node and relationship to author for each item
                        for( String item : list ) {
                            final Node node = uniqueNodeFactories.get(type).getOrCreateIgnoreCase("name", item);
                            createTweetRelationship(indexManager, tweet, author, node, reltype);
                        }
                    }
                }

                // Account pushed tweets
                num_pushed++;
            }

            // Everything went fine
            tx.success();
        }
        finally {
            // Finish the transaction
            tx.finish();
        }

        // Finished
        return num_pushed;
    }
}
