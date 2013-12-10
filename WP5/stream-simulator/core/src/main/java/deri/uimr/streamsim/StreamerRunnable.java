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

import deri.uimr.streamsim.plugins.DeliveryPlugin;

import deri.uimr.utilities.Logger;
import deri.uimr.utilities.concurrent.AbstractUIMRRunnable;

import java.util.List;
import java.util.ArrayList;

/**
 * Implements the streamer runnable of the simulator.
 *
 * <p>This runnable simulates the streaming behaviour of events put in the events queue by the source plugins and delivers them using the delivery plugins.</p>
 *
 * @see AbstractUIMRRunnable
 *
 * @since 1.0
 */
public class StreamerRunnable extends AbstractUIMRRunnable {
    /**
     * A {@code SimulatorProperties} singleton instance.
     */
    private final SimulatorProperties properties = SimulatorProperties.getInstance();

    /**
     * Internal {@code EventsQueue} for this streamer to use.
     */
    private final EventsQueue eventsQueue;

    /**
     * Internal list of {@code DeliveryPlugin} for this streamer to use.
     */
    private final List<DeliveryPlugin> deliveryPlugins;

    /**
     * Holds the last event streamed by this streamer.
     */
    private volatile Event lastEvent = null;

    /**
     * The number of late events read by this streamer.
     */
    private volatile long numberLateEvents = 0;

    /**
     * The number of streamed events by this streamer.
     */
    private volatile long numberStreamedEvents = 0;

    /**
     * The current streaming mode used by this streamer.
     */
    private volatile StreamingModes streamingMode = properties.getStreamingMode();

    /**
     * The current start delay used by this streamer.
     */
    private volatile double startDelay = properties.getStartDelay();

    /**
     * The current speed rate used by this streamer.
     */
    private volatile double speedRate = properties.getSpeedRate();

    /**
     * The current constant delay used by this streamer.
     */
    private volatile double constantDelay = properties.getConstantDelay();

    /**
     * Supported streaming modes.
     *
     * @see SimulatorProperties
     */
    public static enum StreamingModes
    {
        /**
         * This mode honours event timestamps while streaming. Rate is controlled with the {@code speed_rate} property.
         */
        NORMAL,

        /**
         * This mode bypasses event timestamps and streams with a fixed delay. The delay is controlled with the {@code constant_delay} property.
         */
        CONSTANT_DELAY;

        /**
         * Gets the default streaming mode to be used.
         *
         * <p>The default mode is {@link StreamingModes#NORMAL}.</p>
         *
         * @return the default streaming mode.
         */
        public static StreamingModes getDefault() {
            return NORMAL;
        }
    }

    /**
     * Creates a new {@code StreamerRunnable} with the specified name and events queue.
     *
     * @param name the runnable name for this streamer.
     * @param eventsQueue the events queue to use with this streamer.
     * @see EventsQueue
     */
    public StreamerRunnable(final String name, final EventsQueue eventsQueue, final List<DeliveryPlugin> deliveryPlugins) {
        super(name);
        this.eventsQueue = eventsQueue;
        this.deliveryPlugins = deliveryPlugins;

        // Print initial run-time information
        Logger.log("Streaming mode: " + getStreamingMode());
        Logger.log("Streamer start delay: " + getStartDelay() + " ms");
        Logger.log("NORMAL mode speed rate: " + getSpeedRate() + "x");
        Logger.log("CONSTANT_DELAY mode constant delay: " + getConstantDelay() + " ms");
    }

    /**
     * Gets the last streamed event of this streamer runnable.
     *
     * @return the current streaming mode of this streamer runnable.
     */
    public Event getLastEvent() {
        return lastEvent;
    }

    /**
     * Gets the number of late events read by this streamer.
     *
     * <p>A late event is one whose timestamp is older than the timestamp of the last streamed event.</p>
     *
     * @return the number of late events read by this streamer.
     */
    public long getNumberLateEvents() {
        return numberLateEvents;
    }

    /**
     * Gets the number of streamed events by this streamer.
     *
     * @return the number of streamed events by this streamer.
     */
    public long getNumberStreamedEvents() {
        return numberStreamedEvents;
    }

    /**
     * Gets the current streaming mode of this streamer runnable.
     *
     * @return the current streaming mode of this streamer runnable.
     */
    public StreamingModes getStreamingMode() {
        return streamingMode;
    }

    /**
     * Sets the streaming mode for this streamer runnable.
     *
     * @param streamingMode the streaming mode to set for this streamer runnable.
     */
    public void setStreamingMode(final StreamingModes streamingMode) {
        this.streamingMode = streamingMode;
    }

    /**
     * Gets the current start delay of this streamer runnable.
     *
     * @return the current start delay of this streamer runnable.
     */
    public double getStartDelay() {
        return startDelay;
    }

    /**
     * Sets the start delay for this streamer runnable.
     *
     * @param startDelay the start delay to set for this streamer runnable.
     */
    public void setStartDelay(final double startDelay) {
        this.startDelay = startDelay;
    }

    /**
     * Gets the current speed rate of this streamer runnable.
     *
     * @return the current speed rate of this streamer runnable.
     */
    public double getSpeedRate() {
        return speedRate;
    }

    /**
     * Sets the speed rate for this streamer runnable.
     *
     * @param speedRate the speed rate to set for this streamer runnable.
     */
    public void setSpeedRate(final double speedRate) {
        this.speedRate = speedRate;
    }

    /**
     * Gets the current constant delay of this streamer runnable.
     *
     * @return the current constant delay of this streamer runnable.
     */
    public double getConstantDelay() {
        return constantDelay;
    }

    /**
     * Sets the constant delay for this streamer runnable.
     *
     * @param constantDelay the constant delay set for this streamer runnable.
     */
    public void setConstantDelay(final double constantDelay) {
        this.constantDelay = constantDelay;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        // Wait for the events queue to fill-up
        Logger.log("Waiting " + getStartDelay() + " ms before starting...");
        try {
            suspendRunnable(getStartDelay());
        }
        catch (InterruptedException e) {
            Logger.log("Interrupted.");
            return;
        }

        // Start the streamer loop
        Logger.log("Started.");
        while( !Thread.currentThread().isInterrupted() ) {
            try {
                // Get next event from the queue and discard if it is late
                final Event nextEvent = eventsQueue.take();
                if( lastEvent != null && nextEvent.getTimestamp() < lastEvent.getTimestamp() )
                {
                    numberLateEvents++;
                    continue;
                }

                // Log what is going on
                if( isVerbose() )
                    Logger.log("Next: " + nextEvent + ", Last: " + lastEvent);

                // Wait before delivering the event (using the streaming mode)
                if( lastEvent != null )
                    switch( streamingMode ) {
                        case NORMAL:
                            suspendRunnable((nextEvent.getTimestamp() - lastEvent.getTimestamp()) / getSpeedRate());
                            break;
                        case CONSTANT_DELAY:
                            suspendRunnable(getConstantDelay());
                            break;
                        default:
                            throw new UnsupportedOperationException(streamingMode.toString());
                    }

                // Deliver the event using the available delivery plugins
                for( final DeliveryPlugin deliveryPlugin : deliveryPlugins )
                    if( deliveryPlugin.canDeliver(nextEvent) && !deliveryPlugin.deliver(nextEvent) )
                        Logger.log(deliveryPlugin.getClass().getName() + " instance didn't succeed for " + nextEvent);
                numberStreamedEvents++;

                // Save this event for the next iteration
                lastEvent = nextEvent;
            }
            catch( UnsupportedOperationException e ) {
                Logger.log("Unsupported streaming mode: " + e.getMessage());
                break;
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
