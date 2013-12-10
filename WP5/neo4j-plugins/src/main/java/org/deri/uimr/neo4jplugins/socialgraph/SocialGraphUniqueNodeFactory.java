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

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.UniqueFactory.UniqueNodeFactory;

import java.util.Collections;
import java.util.Map;

/**
 * Node factory for creating new unique Social Graph nodes.
 *
 * @since 1.0
 */
public class SocialGraphUniqueNodeFactory extends UniqueNodeFactory {
    /** Node type for this factory. */
    private final String nodeType;

    /**
     * Creates a new {@code SocialGraphUniqueNodeFactory} with specified index manager and node type.
     *
     * @param indexManager the index manager to use for node lookup
     * @param nodeType the node type to use when creating new nodes
     */
    public SocialGraphUniqueNodeFactory(final IndexManager indexManager, final String nodeType) {
        super(indexManager.forNodes(String.format("%ss", nodeType)));
        this.nodeType = nodeType;
    }

    /**
     * Gets or create a node emulating a case insensitive index.
     *
     * @param key the index key to use
     * @param value the index value to compare case insensitively to
     * @return an existing {@code Node} with matching value for the key, or a new one if not found
     */
    public Node getOrCreateIgnoreCase(final String key, final String value) {
        final Transaction tx = graphDatabase().beginTx();
        try {
            Node result = index().get(key, value.toLowerCase()).getSingle();
            if( result == null ) {
                final Map<String, Object> properties = Collections.singletonMap(key, (Object)value);
                final Node created = create(properties);
                result = index().putIfAbsent(created, key, value.toLowerCase());
                if( result == null ) {
                    initialize(created, properties);
                    result = created;
                }
                else {
                    delete(created);
                }
            }
            tx.success();
            return result;
        }
        finally {
            tx.finish();
        }
    }

    /** {@inheritDoc}
     * <p>New nodes are created with a "type" attribute and the key used for searching.</p>
     */
    @Override
    protected void initialize(final Node createdNode, final Map<String, Object> properties) {
        for( Map.Entry<String,Object> property : properties.entrySet() )
            createdNode.setProperty(property.getKey(), property.getValue());
        createdNode.setProperty("type", nodeType);
    }
}
