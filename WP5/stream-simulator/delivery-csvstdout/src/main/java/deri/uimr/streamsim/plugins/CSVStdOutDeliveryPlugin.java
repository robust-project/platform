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

package deri.uimr.streamsim.plugins;

import deri.uimr.streamsim.Event;

import deri.uimr.utilities.Logger;

/**
 * Basic delivery plugin which delivers events in CSV format to the standard output.
 *
 * <p>The output format is:</p>
 *
 * <pre>
 * EVENT1_TYPE;TIMESTAMP1;EVENT1_CONTENT_STRING_REPRESENTATION
 * EVENT2_TYPE;TIMESTAMP2;EVENT2_CONTENT_STRING_REPRESENTATION
 * EVENT3_TYPE;TIMESTAMP3;EVENT3_CONTENT_STRING_REPRESENTATION
 *    :           :          :
 * </pre>
 *
 * <p>The timestamps are milliseconds since UNIX epoch.</p>
 *
 * @author Hugo Hromic
 * @since 3.0
 * @see DeliveryPlugin
 */
public class CSVStdOutDeliveryPlugin extends AbstractDeliveryPlugin {
    /**
     * The number of delivered events for this delivery plugin.
     */
    private volatile long numberDeliveredEvents = 0;

    /**
     * Creates a new {@code CSVStdOutDeliveryPlugin} with specified name.
     *
     * @param name the runnable name for this delivery plugin.
     */
    public CSVStdOutDeliveryPlugin(final String name) {
        super(name);
    }

    /**
     * Gets the number of delivered events for this delivery plugin.
     *
     * @return the number of delivered events for this delivery plugin.
     */
    public long getNumberDeliveredEvents() {
        return numberDeliveredEvents;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This delivery plugin can deliver all kind of events.</p>
     *
     * @return always {@code true}.
     */
    @Override
    public boolean canDeliver(final Event event) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        Logger.log("Started.");

        // Loop until interrupted
        while( !Thread.currentThread().isInterrupted() ) {
            try {
                // Wait for events to be delivered
                final Event event = deliveryQueue.take();
                System.out.println(event.getType() + ";" + event.getTimestamp() + ";" + event.getContent());
                numberDeliveredEvents++;
            }
            catch( InterruptedException e ) {
                Logger.log("Interrupted.");
                break;
            }
        }

        // Terminated
        Logger.log("Terminated.");
    }
}
