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

package deri.uimr.streamsim.webadmin;

import deri.uimr.streamsim.EventsQueue;
import deri.uimr.streamsim.StreamerRunnable;

import deri.uimr.streamsim.plugins.SourcePlugin;
import deri.uimr.streamsim.plugins.DeliveryPlugin;

import deri.uimr.utilities.concurrent.UIMRExecutor;
import deri.uimr.utilities.concurrent.UIMRRunnable;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import java.util.List;
import java.util.Arrays;

/**
 * WebAdmin service Management handler class.
 *
 * <p>This class handles all web traffic needed for the webadmin management interface.</p>
 * <p>To manage the simulator components, this class makes use of Java Beans conventions.</p>
 *
 * <p><b>WARNING: VERY BASIC STAGE AT THE MOMENT!</b></p>
 *
 * @since 3.0
 */
public class ManagementHandler extends AbstractHandler {
    /**
     * The simulator executor to use with this webadmin management handler.
     */
    private final UIMRExecutor executor;

    /**
     * The events queue to use with this webadmin management handler.
     */
    private final EventsQueue eventsQueue;

    /**
     * Creates a new {@code ManagementHandler} with specified executor and events queue.
     *
     * @param executor the {@code UIMRExecutor} to use to manage available runnables.
     * @param eventsQueue the {@code EventsQueue} to use for management.
     * @see UIMRExecutor
     * @see EventsQueue
     */
    public ManagementHandler(final UIMRExecutor executor, final EventsQueue eventsQueue) {
        this.executor = executor;
        this.eventsQueue = eventsQueue;
    }

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("Not done yet...");
    }
}
