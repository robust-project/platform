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
 * WebAdmin service Monitoring handler class.
 *
 * <p>This class handles all web traffic needed for the webadmin monitoring interface.</p>
 * <p>To get status data from the simulator components, this class makes use of Java Beans conventions.</p>
 *
 * <p><b>WARNING: VERY BASIC STAGE AT THE MOMENT!</b></p>
 *
 * @since 3.0
 */
public class MonitoringHandler extends AbstractHandler {
    /**
     * The simulator executor to use with this webadmin monitoring handler.
     */
    private final UIMRExecutor executor;

    /**
     * The events queue to use with this webadmin monitoring handler.
     */
    private final EventsQueue eventsQueue;

    /**
     * Creates a new {@code MonitoringHandler} with specified executor and events queue.
     *
     * @param executor the {@code UIMRExecutor} to use to get information for runnables.
     * @param eventsQueue the {@code EventsQueue} to use to get information for the events queue.
     * @see UIMRExecutor
     * @see EventsQueue
     */
    public MonitoringHandler(final UIMRExecutor executor, final EventsQueue eventsQueue) {
        this.executor = executor;
        this.eventsQueue = eventsQueue;
    }

    /** {@inheritDoc} */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/plain; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        {
            response.getWriter().println("[ EXECUTOR ]");
            for( final UIMRRunnable runnable : executor.getRunnables() )
                response.getWriter().println(runnable.getName());
            response.getWriter().println("");
        }
        {
            final List<UIMRRunnable> plugins = executor.getRunnables("sourceplugin");
            for( final UIMRRunnable plugin : plugins ) {
                response.getWriter().println("[ SOURCE PLUGIN: " + plugin.getClass().getName() + " ]");
                response.getWriter().println(formatObject(plugin));
                response.getWriter().println("");
            }
        }
        {
            response.getWriter().println("[ EVENTS QUEUE ]");
            response.getWriter().println("size = " + eventsQueue.size());
            response.getWriter().println("remainingCapacity = " + eventsQueue.remainingCapacity());
            response.getWriter().println("");
        }
        {
            response.getWriter().println("[ STREAMER ]");
            final StreamerRunnable streamer = (StreamerRunnable)executor.getRunnable("streamer");
            response.getWriter().println(formatObject(streamer));
            response.getWriter().println("");
        }
        {
            final List<UIMRRunnable> plugins = executor.getRunnables("deliveryplugin");
            for( final UIMRRunnable plugin : plugins ) {
                response.getWriter().println("[ DELIVERY PLUGIN: " + plugin.getClass().getName() + " ]");
                response.getWriter().println(formatObject(plugin));
                response.getWriter().println("");
            }
        }
    }

    /**
     * Formats property descriptors.
     */
    private String formatPropertyDescriptor(final PropertyDescriptor pd, final Object object) {
        try {
            final Object value = pd.getReadMethod().invoke(object);
            String formattedValue;

            if( value == null ) formattedValue = "null";
            else if( value instanceof Object[] )
                formattedValue = Arrays.toString((Object[])value);
            else formattedValue = value.toString();

            return pd.getName() + " = " + formattedValue;
        }
        catch( IllegalAccessException e ) {
            return pd.getName() + " = (couldn't get value: " + e.getMessage() + ")";
        }
        catch( InvocationTargetException e ) {
            return pd.getName() + " = (couldn't get value: " + e.getMessage() + ")";
        }
    }

    /**
     * Formats objects.
     */
    private String formatObject(final Object object) {
        final StringBuilder sb = new StringBuilder();
        try {
            final BeanInfo info = Introspector.getBeanInfo(object.getClass());
            for( final PropertyDescriptor pd : info.getPropertyDescriptors() ) {
                if( pd.getName().equals("name") || pd.getName().equals("class") ) continue;
                if( pd.getReadMethod() == null ) continue;
                   sb.append(formatPropertyDescriptor(pd, object) + "\n");
            }
            sb.deleteCharAt(sb.length()-1);
        }
        catch( IntrospectionException e ) {
            return "(couldn't instrospect object: " + e.getMessage() + ")";
        }
        return sb.toString();
    }
}
