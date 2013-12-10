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

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Session;
import javax.jms.MessageProducer;
import javax.jms.Topic;
import javax.jms.Message;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * ActiveMQ broker delivery plugin which publishes events to a ActiveMQ Topic.
 *
 * <p>This plugin uses a properties file to specify ActiveMQ parameters to use. See {@link #DEFAULT_PROPERTIES_FILENAME}.</p>
 * <p>It uses two properties: {@code broker_url} with a default value of {@code tcp://localhost:61616} and {@code topic_name} with a default value of {@code StreamSimulator}. The first parameter is used to tell which ActiveMQ broker to use for publishing events, and the second is to specify which Topic in that broker to use.</p>
 *
 * <p>This plugin delivers events using a {@code TextMessage} with the following structure:</p>
 *
 * <ol>
 * <li>First, the message's {@code JMSType} property is set using the event type (from {@link Event#getType()}.</li>
 * <li>Secondly, the event timestamp is added as a {@code long}-typed property (from {@link Event#getTimestamp()}.</li>
 * <li>And finally the string representation (using {@code toString()} method) of the event's content is used as the {@code TextMessage}'s payload.</li>
 * </ol>
 *
 * @author Hugo Hromic
 * @since 3.0
 * @see DeliveryPlugin
 */
public class ActiveMQDeliveryPlugin extends AbstractDeliveryPlugin {
    /**
     * Default plugin properties file name.
     *
     * <p>Value: {@code activemqdeliveryplugin.properties}.</p>
     */
    public static final String DEFAULT_PROPERTIES_FILENAME = "activemqdeliveryplugin.properties";

    /**
     * The number of delivered events for this delivery plugin.
     */
    private volatile long numberDeliveredEvents = 0;

    /**
     * The number of failed events for this delivery plugin.
     */
    private volatile long numberFailedEvents = 0;

    /**
     * The ActiveMQ connection factory for this delivery plugin.
     */
    private ActiveMQConnectionFactory factory;

    /**
     * The ActiveMQ connection for this delivery plugin.
     */
    private Connection connection;

    /**
     * The ActiveMQ session for this delivery plugin.
     */
    private Session session;

    /**
     * The ActiveMQ Topic to deliver events for this delivery plugin.
     */
    private Topic topic;

    /**
     * The ActiveMQ producer (publisher) for this delivery plugin.
     */
    private MessageProducer producer;

    /**
     * Creates a new {@code ActiveMQDeliveryPlugin} with specified name.
     *
     * @param name the runnable name for this delivery plugin.
     */
    public ActiveMQDeliveryPlugin(final String name) {
        super(name);

        // Get ActiveMQ parameters from properties
        final SmartProperties properties = new SmartProperties();
        properties.loadFromFile(DEFAULT_PROPERTIES_FILENAME);
        final String brokerURL = properties.getString("broker_url", "tcp://localhost:61616");
        final String topicName = properties.getString("topic_name", "StreamSimulator");

        // Initialize ActiveMQ
        try {
            factory = new ActiveMQConnectionFactory(brokerURL);
            connection = factory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            connection.start();
            Logger.log("ActiveMQ Broker URL: " + brokerURL);
            Logger.log("ActiveMQ Topic Name: " + topicName);
        }
        catch( JMSException e ) {
            Logger.log("Can't initialize ActiveMQ: " + e.getMessage());
            factory = null;
            connection = null;
            session = null;
            topic = null;
            producer = null;
        }
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
     * Gets the ActiveMQ broker URL for this delivery plugin.
     *
     * @return the ActiveMQ broker URL for this delivery plugin or {@code null} if not initialized.
     */
    public String getBrokerURL() {
        if( factory == null )
            return null;
        return factory.getBrokerURL();
    }

    /**
     * Gets the ActiveMQ Topic Name for this delivery plugin.
     *
     * @return the ActiveMQ Topic Name for this delivery plugin or {@code null} if not initialized.
     */
    public String getTopicName() {
        if( topic == null )
            return null;
        try {
            return topic.getTopicName();
        }
        catch( JMSException e ) {
            Logger.log("Can't get ActiveMQ Topic Name:" + e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>This delivery plugin can deliver only if ActiveMQ was correctly initialized.</p>
     *
     * @return {@code true} if the plugin can deliver events or {@code false} if ActiveMQ couldn't be initialized.
     */
    @Override
    public boolean canDeliver(final Event event) {
        if( producer == null )
            return false;
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        if( producer == null )
            return;
        Logger.log("Started.");

        // Loop until interrupted
        while( !Thread.currentThread().isInterrupted() ) {
            try {
                // Wait for events to be delivered
                final Event event = deliveryQueue.take();
                final Message message = session.createTextMessage(event.getContent().toString());
                message.setJMSType(event.getType());
                message.setLongProperty("timestamp", event.getTimestamp());
                producer.send(message);
                numberDeliveredEvents++;
            }
            catch( JMSException e ) {
                Logger.log("Can't send ActiveMQ message: " + e.getMessage());
                numberFailedEvents++;
                continue;
            }
            catch( InterruptedException e ) {
                Logger.log("Interrupted.");
                break;
            }
        }

        // Terminated.
        try {
            connection.stop();
            connection.close();
        }
        catch( JMSException e ) {
            Logger.log("Can't stop/close ActiveMQ: " + e.getMessage());
        }
        Logger.log("Terminated.");
    }
}
