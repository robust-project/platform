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

package deri.uimr.streamsim;

import java.util.Collection;

/**
 * An events queue for holding events to be streamed by the simulator.
 *
 * @see Event
 *
 * @since 1.0
 */
public interface EventsQueue {
    /**
     * Retrieves and removes the head of this queue, waiting if necessary until an event becomes available.
     *
     * @return the head event of this queue.
     */
    public abstract Event take() throws InterruptedException;

    /**
     * Inserts the specified event into this queue, waiting if necessary until space becomes available.
     *
     * @param event the event to put into this queue.
     */
    public abstract void put(final Event event) throws InterruptedException;

    /**
     * Returns the number of events in this queue.
     *
     * @return the number of events in this queue.
     */
    public abstract int size();

    /**
     * Returns the remaining capacity of this queue.
     *
     * @return the remaining capacity of this queue.
     */
    public abstract int remainingCapacity();

    /**
     * Removes all available elements from this queue and adds them
     * to the given collection of {@code Event}. This operation may be more
     * efficient than repeatedly polling this queue.
     *
     * @param c the collection to transfer elements into
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *         is not supported by the specified collection
     * @throws NullPointerException if the specified collection is null
     * @throws IllegalArgumentException if some property of an element
     *         of this queue prevents it from being added to the specified collection
     */
    public abstract int drainTo(final Collection<Event> c);

    /**
     * Removes at most the given number of available elements from
     * this queue and adds them to the given collection of {@code Event}.
     *
     * @param c the collection to transfer elements into
     * @param maxElements the maximum number of elements to transfer
     * @return the number of elements transferred
     * @throws UnsupportedOperationException if addition of elements
     *         is not supported by the specified collection
     * @throws NullPointerException if the specified collection is null
     * @throws IllegalArgumentException if some property of an element
     *         of this queue prevents it from being added to the specified collection
     */
    public abstract int drainTo(final Collection<Event> c, final int maxElements);
}
