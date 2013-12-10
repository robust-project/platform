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

/**
 * Represents a particular event in time.
 *
 * <p>The event's timestamp is represented using the UNIX epoch (long), with milliseconds resolution.</p>
 * <p>You can put any kind of object you need as the event's content. This object is meant to be manipulated by the delivery plugins.</p>
 * <p>As for the event type, you can use any string you might think is suitable for your purposes.</p>
 *
 * @since 1.0
 */
public class Event implements Comparable<Event> {
    /**
     * Internal timestamp representation.
     */
    private final long timestamp;

    /**
     * Internal event type identifier.
     */
    private final String type;

    /**
     * Internal event content object.
     */
    private final Object content;

    /**
     * Creates an {@code Event} with the specified timestamp, type and content.
     *
     * @param timestamp the timestamp (milliseconds since UNIX epoch) of the new event.
     * @param type the event type of the new event.
     * @param content the content of the new event.
     */
    public Event(final long timestamp, final String type, final Object content) {
        this.timestamp = timestamp;
        this.type = type;
        this.content = content;
    }

    /**
     * Gets this event timestamp.
     *
     * @return the timestamp of this event in milliseconds since UNIX epoch.
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets this event type.
     *
     * @return the type of this event.
     */
    public String getType() {
        return type;
    }

    /**
     * Gets this event content.
     *
     * @return the content of this event.
     */
    public Object getContent() {
        return content;
    }

    /**
     * Returns a string representation of this event.
     *
     * @return the string representation of this event.
     */
    @Override
    public String toString() {
        return String.format("<Event '%s' @ %tY-%tm-%td %tH:%tM:%tS.%tL>", type, timestamp);
    }

    /**
     * Compares this event with the specified event for order.
     *
     * <p>The comparison is done over time using the events timestamps.</p>
     *
     * @param otherEvent the event to be compared.
     * @return a negative integer, zero, or a positive integer as this event is older than, same time, or newer than the specified event.
     */
    @Override
    public int compareTo(final Event otherEvent) {
        // Same event?
        if( this == otherEvent ) return 0;

        // Compare events timestamps
        final long otherTimestamp = otherEvent.getTimestamp();
        if( timestamp > otherTimestamp ) return 1;
        else if( timestamp < otherTimestamp ) return -1;
        else return 0;
    }
}
