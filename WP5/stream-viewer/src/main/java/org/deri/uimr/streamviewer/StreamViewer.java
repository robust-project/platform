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
package org.deri.uimr.streamviewer;

import javax.jms.ConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.MessageConsumer;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamViewer {
    private static final Logger logger = LoggerFactory.getLogger(StreamViewer.class);

    public static void main(final String[] args) {
        // Parse program arguments
        if( args.length < 2 ) {
            logger.error("Usage: <Broker_URL> <Topic_Name>");
            System.exit(-1);
        }
        final String broker_url = args[0];
        final String topic_name = args[1];

        // Initialize client
        logger.info("Connecting to '{}', topic '{}' ...", broker_url, topic_name);
        try {
            // Create connection
            final ConnectionFactory factory = new ActiveMQConnectionFactory(broker_url);
            final Connection connection = factory.createConnection();
            connection.start();

            // Subscribe to topic
            final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            final Topic topic = session.createTopic(topic_name);
            final MessageConsumer consumer = session.createConsumer(topic);
            final StreamMessageListener listener = new StreamMessageListener();
            consumer.setMessageListener(listener);

            // Wait for user to stop
            logger.info("Receiving messages, press ENTER key to stop.");
            System.in.read();
            consumer.close();
            session.close();
            connection.close();
            logger.info("Messages received: {}, text messages: {}",
                listener.getNumMessages(), listener.getNumTextMessages());
        }
        catch (Exception e) {
            logger.error("While listening for messages.", e);
            System.exit(-1);
        }

        // Stop the viewer and shutdown
        logger.info("Terminated.");
    }
}
