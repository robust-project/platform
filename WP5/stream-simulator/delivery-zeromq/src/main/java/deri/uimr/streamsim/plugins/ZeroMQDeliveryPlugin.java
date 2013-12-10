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
import deri.uimr.utilities.SmartProperties;

import org.zeromq.ZMQ;

/**
 * ZeroMQ message bus delivery plugin which creates a publisher bus to deliver events to.
 *
 * <p>This plugin uses a properties file to specify ZeroMQ parameters to use. See {@link #DEFAULT_PROPERTIES_FILENAME}.</p>
 * <p>It uses one property: {@code socket_binding} with a default value of {@code tcp://*:5556}. This value is used for creating the ZeroMQ Publisher socket.</p>
 *
 * <p>This plugin delivers events using the following envelope (frame) structure:</p>
 *
 * <ol>
 * <li>First, the envelope key is built using the event type (from {@link Event#getType()}.</li>
 * <li>Secondly, the event timestamp is added (from {@link Event#getTimestamp()}.</li>
 * <li>And finally the string representation (using {@code toString()} method) of the event's content.</li>
 * </ol>
 *
 * @author Hugo Hromic
 * @since 3.0
 * @see DeliveryPlugin
 * @see ZMQ
 */
public class ZeroMQDeliveryPlugin extends AbstractDeliveryPlugin {
    /**
     * Default plugin properties file name.
     *
     * <p>Value: {@code zeromqdeliveryplugin.properties}.</p>
     */
    public static final String DEFAULT_PROPERTIES_FILENAME = "zeromqdeliveryplugin.properties";

    /**
     * Internal ZeroMQ Context for this delivery plugin.
     * @see ZMQ
     */
    private ZMQ.Context context;

    /**
     * Internal ZeroMQ Socket for this delivery plugin.
     * @see ZMQ
     */
    private ZMQ.Socket socket;

    /**
     * The ZeroMQ socket binding for this delivery plugin.
     * @see ZMQ
     */
    private final String socketBinding;

    /**
     * The number of delivered events for this delivery plugin.
     */
    private volatile long numberDeliveredEvents = 0;

    /**
     * The number of failed events for this delivery plugin.
     */
    private volatile long numberFailedEvents = 0;

    /**
     * Creates a new {@code ZeroMQDeliveryPlugin} with specified name.
     *
     * @param name the runnable name for this delivery plugin.
     */
    public ZeroMQDeliveryPlugin(final String name) {
        super(name);

        // Get ZeroMQ parameters from properties
        final SmartProperties properties = new SmartProperties();
        properties.loadFromFile(DEFAULT_PROPERTIES_FILENAME);
        socketBinding = properties.getString("socket_binding", "tcp://*:5556");

        // Create ZeroMQ context and socket
        try {
           context = ZMQ.context(1);
           socket = context.socket(ZMQ.PUB);
           socket.bind(socketBinding);
           Logger.log("ZeroMQ version: " + ZMQ.getVersionString());
           Logger.log("ZeroMQ socket binding: " + socketBinding);
        }
        catch( Exception e ) {
           Logger.log("Can't initialize ZeroMQ: " + e.getMessage());
           context = null;
           socket = null;
        }
    }

    /**
     * Gets the ZeroMQ socket binding for this delivery plugin.
     *
     * @return the ZeroMQ socket binding for this delivery plugin.
     */
    public String getSocketBinding() {
        return socketBinding;
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
     * Gets the number of failed events for this delivery plugin.
     *
     * @return the number of failed events for this delivery plugin.
     */
    public long getNumberFailedEvents() {
        return numberFailedEvents;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This delivery plugin can deliver only if ZeroMQ was correctly initialized.</p>
     *
     * @return {@code true} if the plugin can deliver events or {@code false} if ZeroMQ couldn't be initialized.
     */
    @Override
    public boolean canDeliver(final Event event) {
        if( context == null || socket == null )
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        if( socket == null || context == null )
            return;
        Logger.log("Started.");

        // Loop until interrupted
        while( !Thread.currentThread().isInterrupted() ) {
            try {
                // Wait for events to be delivered
                final Event event = deliveryQueue.take();
                if( socket.send(event.getType().getBytes(), ZMQ.SNDMORE) &&
                        socket.send(new Long(event.getTimestamp()).toString().getBytes(), ZMQ.SNDMORE) &&
                        socket.send(event.getContent().toString().getBytes(), 0) )
                    numberDeliveredEvents++;
                else numberFailedEvents++;
            }
            catch( InterruptedException e ) {
                Logger.log("Interrupted.");
                break;
            }
        }

        // Terminated.
        socket.close();
        context.term();
        Logger.log("Terminated.");
    }
}
