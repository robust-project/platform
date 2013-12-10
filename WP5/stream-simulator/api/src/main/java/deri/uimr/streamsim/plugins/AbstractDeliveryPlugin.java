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

import deri.uimr.utilities.concurrent.AbstractUIMRRunnable;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * This class provides a skeletal implementation of the {@code DeliveryPlugin} interface to minimize the effort required to implement this interface.
 *
 * <p>You must extend this class and implement your own {@link #canDeliver(Event)} and {@link #run()} methods.</p>
 * <p>The simulator will instantiate delivery plugins using a single parameter constructor ({@code String name}), thus you need to implement such a constructor and ensure you call {@code super(String)}.</p>
 *
 * <p>Although not strictly required, you should use the provided {@link #deliveryQueue} protected field when delivering events.</p>
 *
 * <p>This base class uses a {@code LinkedBlockingQueue} to automatically enqueue delivered events to be processed by your code. You can override all of this base functionality if you want full control of the delivery process. However, it's strongly suggested you use a {@code BlockingQueue} back-end.</p>
 *
 * @since 3.0
 * @see SourcePlugin
 * @see AbstractUIMRRunnable
 * @see BlockingQueue
 */
public abstract class AbstractDeliveryPlugin extends AbstractUIMRRunnable implements DeliveryPlugin {
    /**
     * The delivery blocking queue this delivery plugin uses to get events from.
     *
     * @see LinkedBlockingQueue
     */
    protected final BlockingQueue<Event> deliveryQueue = new LinkedBlockingQueue<Event>();

    /**
     * Creates a new {@code AbstractDeliveryPlugin} with the specified name.
     *
     * @param name the runnable name for this delivery plugin.
     */
    public AbstractDeliveryPlugin(final String name) {
        super(name);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This method enqueues the event into the delivery blocking queue.</p>
     *
     * @return {@code true} if the event was enqueued successfully or {@code false} if the queue was full.
     * @see #deliveryQueue
     */
    public boolean deliver(final Event event) {
        return deliveryQueue.offer(event);
    }
}
