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

import deri.uimr.streamsim.plugins.SourcePlugin;
import deri.uimr.streamsim.plugins.DeliveryPlugin;

import deri.uimr.streamsim.webadmin.MonitoringHandler;
import deri.uimr.streamsim.webadmin.ManagementHandler;

import deri.uimr.utilities.Logger;
import deri.uimr.utilities.concurrent.UIMRExecutor;

import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.TimeUnit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

/**
 * The main simulator class.
 *
 * @since 1.0
 */
public class Simulator {
    /**
     * A {@code SimulatorProperties} singleton instance.
     */
    private final static SimulatorProperties properties = SimulatorProperties.getInstance();

    /**
     * This is called when starting the simulator.
     *
     * @param args arguments given when starting the program.
     */
    public static void main(final String [] args) {
        // Welcome information
        Logger.log("Welcome to " + Simulator.class.getPackage().getImplementationTitle()
                   + " version " + Simulator.class.getPackage().getImplementationVersion() + " !");
        Logger.log("This software is part of the ROBUST project. More info: http://robust-project.eu");
        Logger.log("Copyright(c) " + Simulator.class.getPackage().getImplementationVendor());

        // Configure logging
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "WARN");

        // Create the events queue
        final EventsQueue eventsQueue = new PriorityEventsQueue(properties.getEventsQueueSize());
        Logger.log("Events queue size: " + eventsQueue.remainingCapacity());

        // Instantiate source plugins
        final List<SourcePlugin> sourcePluginInstances = new ArrayList<SourcePlugin>();
        {
            final String[] plugins = properties.getSourcePlugins();
            for( int i=0; i<plugins.length; i++ ) {
                final String className = plugins[i];
                try {
                    final Constructor constructor = Class.forName(className).getDeclaredConstructor(String.class, EventsQueue.class);
                    sourcePluginInstances.add((SourcePlugin)constructor.newInstance("sourceplugin" + i, eventsQueue));
                }
                catch( ClassCastException e ) {
                    Logger.log("Source plugin not compatible: " + className);
                }
                catch( ClassNotFoundException e ) {
                    Logger.log("Source plugin class not found: " + className);
                }
                catch( NoSuchMethodException e ) {
                    Logger.log("Invalid source plugin constructor for: " + className);
                }
                catch( IllegalAccessException e ) {
                    Logger.log("Invalid source plugin constructor for: " + className);
                }
                catch( InstantiationException e ) {
                    Logger.log("Can't create a SourcePlugin instance for: " + className);
                }
                catch( InvocationTargetException e ) {
                    Logger.log("Can't create a SourcePlugin instance for: " + className);
                }
            }
        }
        Logger.log("Loaded " + sourcePluginInstances.size() + " source plugin(s).");

        // Instantiate delivery plugins
        final List<DeliveryPlugin> deliveryPluginInstances = new ArrayList<DeliveryPlugin>();
        {
            final String[] plugins = properties.getDeliveryPlugins();
            for( int i=0; i<plugins.length; i++ ) {
                final String className = plugins[i];
                try {
                    final Constructor constructor = Class.forName(className).getDeclaredConstructor(String.class);
                    deliveryPluginInstances.add((DeliveryPlugin)constructor.newInstance("deliveryplugin" + i));
                }
                catch( ClassCastException e ) {
                    Logger.log("Delivery plugin not compatible: " + className);
                }
                catch( ClassNotFoundException e ) {
                    Logger.log("Delivery plugin class not found: " + className);
                }
                catch( NoSuchMethodException e ) {
                    Logger.log("Invalid delivery plugin constructor for: " + className);
                }
                catch( IllegalAccessException e ) {
                    Logger.log("Invalid delivery plugin constructor for: " + className);
                }
                catch( InstantiationException e ) {
                    Logger.log("Can't create a DeliveryPlugin instance for: " + className);
                }
                catch( InvocationTargetException e ) {
                    Logger.log("Can't create a DeliveryPlugin instance for: " + className);
                }
            }
        }
        Logger.log("Loaded " + deliveryPluginInstances.size() + " delivery plugin(s).");

        // Create runnable executor
        final UIMRExecutor executor = new UIMRExecutor(1 + sourcePluginInstances.size() + deliveryPluginInstances.size());

        // Execute the source plugins
        for( final SourcePlugin sourcePluginInstance : sourcePluginInstances )
            executor.execute(sourcePluginInstance, true);

        // Execute the delivery plugins
        for( final DeliveryPlugin deliveryPluginInstance : deliveryPluginInstances )
            executor.execute(deliveryPluginInstance, true);

        // Execute the streamer runnable
        executor.execute(new StreamerRunnable("streamer", eventsQueue, deliveryPluginInstances));

        // Create the server for webadmin interface
        if( properties.isWebAdminEnabled() )
            try {
                System.setProperty("org.eclipse.jetty.LEVEL", "WARN");
                final Server server = new Server();
                final Connector connector = new SelectChannelConnector();
                connector.setHost(properties.getWebAdminHost());
                connector.setPort(properties.getWebAdminPort());
                server.addConnector(connector);

                // Configure monitoring context
                final ContextHandler monitoringContext = new ContextHandler();
                monitoringContext.setContextPath("/monitoring");
                monitoringContext.setClassLoader(Thread.currentThread().getContextClassLoader());
                monitoringContext.setHandler(new MonitoringHandler(executor, eventsQueue));

                // Configure management context
                final ContextHandler managementContext = new ContextHandler();
                managementContext.setContextPath("/management");
                managementContext.setClassLoader(Thread.currentThread().getContextClassLoader());
                managementContext.setHandler(new ManagementHandler(executor, eventsQueue));

                // Start the webadmin interface server with the monitoring and management contexts
                final ContextHandlerCollection contexts = new ContextHandlerCollection();
                contexts.setHandlers(new ContextHandler[]{monitoringContext, managementContext});
                server.setHandler(contexts);
                server.start();
                Logger.log("WebAdmin URL: http://" + properties.getWebAdminHost() + ":" + properties.getWebAdminPort());
            }
            catch( Exception e ) {
                Logger.log("Could not start WebAdmin server: " + e.getMessage());
            }
        else Logger.log("WebAdmin is disabled.");

        // Wait for the executor to finish
        executor.shutdown();
        while( !Thread.currentThread().isInterrupted() )
            try {
                if( executor.awaitTermination(1L, TimeUnit.MINUTES) )
                    break;
            }
            catch( InterruptedException e ) {
                Logger.log("Interrupted.");
                break;
            }

        // Terminated
        Logger.log("Terminated.");
    }
}
