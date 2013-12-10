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

import deri.uimr.utilities.concurrent.UIMRRunnable;

/**
 * A delivery plugin to be used with the simulator.
 *
 * <p>The role of a delivery plugin is to take simulated stream events and deliver to appropiate places like a message bus or a remote server.</p>
 *
 * @since 3.0
 */
public interface DeliveryPlugin extends UIMRRunnable {
    /**
     * Checks if a given event can be delivered by this delivery plugin.
     *
     * @param event the {@code Event} to check.
     * @return {@code true} if this delivery plugin can deliver the event or {@code false} otherwise.
     */
    public abstract boolean canDeliver(final Event event);

    /**
     * Delivers an event using this delivery plugin.
     *
     * @param event the {@code Event} to deliver.
     * @return {@code true} if succeed or {@code false} otherwise.
     */
    public abstract boolean deliver(final Event event);
}
