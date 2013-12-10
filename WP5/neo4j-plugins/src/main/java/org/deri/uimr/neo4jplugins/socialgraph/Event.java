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

import java.util.Arrays;

/**
 * Represents an event to be inserted in the social graph.
 *
 * @since 1.0
 */
public class Event {
    /** The timestamp of this event. */
    private long timestamp = Long.MIN_VALUE;

    /** The list of interactions within this event. */
    private Interaction[] interactions = null;

    /**
     * Gets the timestamp of this event.
     *
     * @return this event's timestamp.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the list of interactions within this event.
     *
     * @return this event's list of interactions.
     * @see Interaction
     */
    public Interaction[] getInteractions() {
        return interactions;
    }

    /**
     * Sets the timestamp of this event.
     *
     * @param timestamp the timestamp to set.
     */
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Sets the list of interactions of this event.
     *
     * @param interactions the list of interactions to set.
     * @see Interaction
     */
    public void setInteractions(final Interaction[] interactions) {
        this.interactions = interactions;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("<Event @ %s: %s>", timestamp, Arrays.toString(interactions));
    }
}
