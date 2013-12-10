/** 
*Copyright 2013 Software Mind SA
*
*Licensed under the Apache License, Version 2.0 (the "License");
*you may not use this file except in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing, software
*distributed under the License is distributed on an "AS IS" BASIS,
*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*See the License for the specific language governing permissions and
*limitations under the License.
*/
package pl.swmind.robust.streaming.activemq.topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import pl.swmind.robust.streaming.commons.Constants;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.topic.RobustPublisher;

import javax.jms.*;
import java.io.Serializable;
import java.util.Date;

// FIX-ME almost the same as subscriber - extract abstraction
/**
 * Implementation of RobustPublisher.
 */
public class ActiveMQPublisher implements RobustPublisher {

    private static final ConnectionFactory DEFAULT_CONNECTION_FACTORY = new ActiveMQConnectionFactory(
            ActiveMQConnection.DEFAULT_USER,
            ActiveMQConnection.DEFAULT_PASSWORD,
            ActiveMQConnection.DEFAULT_BROKER_URL);


    private static final Logger log = Logger.getLogger(RobustPublisher.class);
    //TODO remove in next version
    private static final String DEFAULT_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    static final String DEFAULT_TOPIC_NAME = "ROBUST_TOPIC";
    private static final String DEFAULT_USER_NAME = "Admin";

    private static final String DEFAULT_USER_PASSWORD = "Password";
    private Topic topic;
    private MessageProducer producer;
    private Session session;


    private Connection connection;

    // FIX-ME those 3 constructors are almost the same...
    /**
     * Constructor of ActiveMQPublisher
     * @param topicName the name of the topic where messages are published
     * @param url where the topic is exposed
     * @param userName the name of the user where loggin
     * @param userPassword the password of loggin user
     * @throws RobustStreamingException
     */
    public ActiveMQPublisher(String topicName, String url, String userName, String userPassword) throws RobustStreamingException{
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        connection = null;
        try {
            connection = connectionFactory.createConnection(userName , userPassword);
            connection.start();
            session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            producer = session.createProducer(topic);

        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }
    }

    public ActiveMQPublisher(String topicName, ConnectionFactory connectionFactory) throws RobustStreamingException{
        connectionFactory = DEFAULT_CONNECTION_FACTORY;
        connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            producer = session.createProducer(topic);

        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }
    }



    /**
     * Constructor of ActiveMQPublisher with default ActiveMQ Topics url and specified topic name.
     * @param topicName name of the topic where messages are published.
     * @throws RobustStreamingException
     */
    public ActiveMQPublisher(String topicName,String userName, String userPassword) throws RobustStreamingException{
        this(topicName, DEFAULT_URL,userName, userPassword);
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(DEFAULT_URL);
        connection = null;
        try {
            connection = connectionFactory.createConnection(userName , userPassword);
            connection.start();
            session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
            producer = session.createProducer(topic);

        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }
    }

    /**
     * Constructor of ActiveMQPublisher with default ActiveMQ Topics url and default robust topic name.
     * @throws RobustStreamingException
     */
    public ActiveMQPublisher() throws RobustStreamingException{
        this(DEFAULT_TOPIC_NAME, DEFAULT_URL, DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD);
    }


    @Override
    public void publish(String text) throws RobustStreamingException {
        TextMessage message = null;
        try {
            message = session.createTextMessage();
            message.setText(text);
            producer.send(message);
            log.debug("Message sent" + message.getText());
        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }

    }

    @Override
    public void publish(String text, String type, long timestamp) throws RobustStreamingException {
        TextMessage message = null;
        try {
            message = session.createTextMessage();
            message.setText(text);
            message.setJMSType(type);
            message.setLongProperty(Constants.TIMESTAMP_PROPERTY_NAME, timestamp);
            producer.send(message);
            log.debug("Message sent" + message.getText());
        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }
    }

    @Override
    public void close() throws RobustStreamingException {
        try {
            connection.close();
        } catch (JMSException e) {
            throw new RobustStreamingException(e);
        }
    }

    @Override
    public void publish(Serializable serializable) throws RobustStreamingException {
        ObjectMessage message = null;
        try {
            message = session.createObjectMessage(serializable);
            message.setObject(serializable);
            producer.send(message);
            log.debug("Message sent " + message.getObject());
        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }
    }

    @Override
    public void publish(Serializable serializable, String type, long timestamp) throws RobustStreamingException {
        ObjectMessage message = null;
        try {
            message = session.createObjectMessage(serializable);
            message.setObject(serializable);
            message.setJMSType(type);
            message.setLongProperty(Constants.TIMESTAMP_PROPERTY_NAME, timestamp);
            producer.send(message);
            log.debug("Message sent " + message.getObject());
        } catch (JMSException e) {
            log.error("Robust Streaming Exception " + e.getMessage());
            throw new RobustStreamingException(e);
        }
    }
}





