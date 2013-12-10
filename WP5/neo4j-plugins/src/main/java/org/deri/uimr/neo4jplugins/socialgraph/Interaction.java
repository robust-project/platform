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

import java.util.Map;

/**
 * Represents an interaction within a social graph event.
 *
 * @since 1.0
 */
public class Interaction {
    /** The type of this interaction. */
    private InteractionType type = null;

    /** The attributes of this interaction. */
    private Map<String,Object> attributes = null;

    /**
     * Gets the type of this interaction.
     *
     * @return this interaction's type.
     * @see InteractionType
     */
    public InteractionType getType() {
        return type;
    }

    /**
     * Gets the attributes of this interaction.
     *
     * @return this interaction's attributes.
     */
    public Map<String,Object> getAttributes() {
        return attributes;
    }

    /**
     * Sets the type of this interaction.
     *
     * @param type the interaction type to set.
     * @see InteractionType
     */
    public void setType(final InteractionType type) {
        this.type = type;
    }

    /**
     * Sets the attributes of this interaction.
     *
     * @param attributes the attributes to set.
     */
    public void setAttributes(final Map<String,Object> attributes) {
        this.attributes = attributes;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("<Interaction '%s': %s>", type, attributes);
    }
}
