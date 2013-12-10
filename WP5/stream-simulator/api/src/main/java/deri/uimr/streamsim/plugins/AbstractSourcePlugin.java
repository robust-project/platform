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

import deri.uimr.streamsim.EventsQueue;

import deri.uimr.utilities.concurrent.AbstractUIMRRunnable;

/**
 * This class provides a skeletal implementation of the {@code SourcePlugin} interface to minimize the effort required to implement this interface.
 *
 * <p>You must extend this class and implement your own {@link #run()} method.</p>
 * <p>The simulator will instantiate source plugins using a two parameter constructor ({@code String name} and {@code EventsQueue eventsQueue}), thus you need to implement such a constructor and ensure you call {@code super(String, EventsQueue)}.</p>
 *
 * <p>Although not strictly required, you should use the provided {@link #eventsQueue} protected field when refering to the events queue.</p>
 *
 * @since 3.0
 * @see SourcePlugin
 * @see AbstractUIMRRunnable
 * @see EventsQueue
 */
public abstract class AbstractSourcePlugin extends AbstractUIMRRunnable implements SourcePlugin {
    /**
     * The events queue this source plugin uses to put the events on.
     * @see EventsQueue
     */
    protected final EventsQueue eventsQueue;

    /**
     * Creates a new {@code AbstractSourcePlugin} with the specified name and {@code EventsQueue}.
     *
     * @param name the runnable name for this source plugin.
     * @param eventsQueue the {@code EventsQueue} this source plugin must use.
     * @see #eventsQueue
     */
    public AbstractSourcePlugin(final String name, final EventsQueue eventsQueue) {
        super(name);
        this.eventsQueue = eventsQueue;
    }
}
