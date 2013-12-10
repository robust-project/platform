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
package deri.uimr.ufstreamfeeder;

import deri.uimr.utilities.Logger;

import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.MessageConsumer;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class StreamFeeder {
    private final static FeederProperties properties = FeederProperties.getInstance();

    public static void main(final String[] args) {
        // Welcome banner
        Logger.log(String.format("Welcome to %s version %s !", StreamFeeder.class.getPackage().getImplementationTitle(),
                          StreamFeeder.class.getPackage().getImplementationVersion()));
        Logger.log(String.format("Copyright(c) %s", StreamFeeder.class.getPackage().getImplementationVendor()));

        // Configure logging
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "WARN");

        // Main program control
        try {
            // Create a blocking queue for events
            final BlockingQueue<Event> eventsQueue = new LinkedBlockingQueue<Event>();

            // Create a feeding thread
            Logger.log("Starting feeding runnable ...");
            final FeedingRunnable feedingRunnable = new FeedingRunnable("feeding", eventsQueue);
            final Thread feedingThread = new Thread(feedingRunnable, feedingRunnable.getName());
            feedingThread.start();

            // Create an ActiveMQ connection
            Logger.log(String.format("Connecting to '%s' ...", properties.getBrokerURL()));
            final ConnectionFactory amqFactory = new ActiveMQConnectionFactory(properties.getBrokerURL());
            final Connection amqConnection = amqFactory.createConnection();
            amqConnection.start();

            // Subscribe to the ActiveMQ topic
            Logger.log(String.format("Subscribing to topic '%s' ...", properties.getTopicName()));
            final Session amqSession = amqConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final Topic amqTopic = amqSession.createTopic(properties.getTopicName());
            final MessageConsumer amqConsumer = amqSession.createConsumer(amqTopic);
            final StreamMessageListener streamListener = new StreamMessageListener(eventsQueue);
            amqConsumer.setMessageListener(streamListener);

            // Create a servlet server connector
            final Connector servletServerConnector = new SelectChannelConnector();
            servletServerConnector.setHost(properties.getServletServerHost());
            servletServerConnector.setPort(properties.getServletServerPort());

            // Create a servlet server with configured connectors
            final Server servletServer = new Server();
            servletServer.setConnectors(new Connector[]{ servletServerConnector });

            // Create/configure the servlet server context handlers
            final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");
            servletContextHandler.addServlet(new ServletHolder(new ControlServlet(feedingRunnable)), "/control");

            // Start the servlet server with configured context handlers
            Logger.log(String.format("Starting the servlet server at http://%s:%s ...",
                       properties.getServletServerHost(), properties.getServletServerPort()));
            final ContextHandlerCollection servletServerContexts = new ContextHandlerCollection();
            servletServerContexts.setHandlers(new ContextHandler[]{ servletContextHandler });
            servletServer.setHandler(servletServerContexts);
            servletServer.start();

            // Wait for user to stop the service
            Logger.log("Stream feeder running. Press ENTER key to stop.");
            System.in.read();
            servletServer.stop();
            feedingThread.interrupt();
            amqConsumer.close();
            amqSession.close();
            amqConnection.close();

            // Show final statistics
            Logger.log(String.format("Messages received: %d, valid: %d. Events committed: %d",
                       streamListener.getNumMessages(), streamListener.getNumValidMessages(),
                       feedingRunnable.getNumCommitted()));
        }
        catch( Exception e ) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Stop the viewer and shutdown
        Logger.log("Terminated.");
    }
}
