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
import org.springframework.stereotype.Component;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.topic.RobustMessageListener;
import pl.swmind.robust.streaming.topic.RobustSubscriber;

import javax.jms.*;

/**
 * User: rafal
 * Date: 14.05.12
 */
@Component
public class ActiveMQSubscriber implements RobustSubscriber {
    private static final ConnectionFactory DEFAULT_CONNECTION_FACTORY = new ActiveMQConnectionFactory(
            ActiveMQConnection.DEFAULT_USER,
            ActiveMQConnection.DEFAULT_PASSWORD,
            ActiveMQConnection.DEFAULT_BROKER_URL);

    private static final Logger log = Logger.getLogger(RobustSubscriber.class);
    private static final String DEAFAULT_URL = ActiveMQConnection.DEFAULT_BROKER_URL;
    private static final String DEAFAULT_USER_NAME = "Admin";
    private static final String DEAFAULT_USER_PASSWORD = "Password";


    private Connection connection;
    private String topicName;


    // FIX-ME those three constructors are almost the same ...
    /**
     * Constructor of ActiveMQSubscriber.
     * @param topicName the name of the topic where messages are published
     * @param url where the topic is exposed
     * @param userName the name of the user where loggin
     * @param userPassword the password of loggin user
     * @throws RobustStreamingException
     */
    public ActiveMQSubscriber(String topicName, String url, String userName, String userPassword) throws RobustStreamingException {
        this.topicName = topicName;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        try {
            connection = connectionFactory.createConnection(userName, userPassword);
            connection.start();

        } catch (JMSException e) {
            log.error(e.getMessage());
            throw new RobustStreamingException(e);
        }

    }
    public ActiveMQSubscriber(String topicName, ConnectionFactory connectionFactory ) throws RobustStreamingException {
        this.topicName = topicName;
        connectionFactory = DEFAULT_CONNECTION_FACTORY;
        try {
            connection = connectionFactory.createConnection();
            connection.start();

        } catch (JMSException e) {
            log.error(e.getMessage());
            throw new RobustStreamingException(e);
        }

    }
    /**
     * Constructor of ActiveMQSubscriber with default ActiveMQ Topics url and specified topic name.
     * @param topicName the name of the topic where messages are published
     * @throws RobustStreamingException
     */
    public ActiveMQSubscriber(String topicName,String userName, String userPassword) throws RobustStreamingException {
        this(topicName, DEAFAULT_URL,userName,userPassword);


        this.topicName = topicName;
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(DEAFAULT_URL);
        try {
            connection = connectionFactory.createConnection(userName, userPassword);
            connection.start();

        } catch (JMSException e) {
            log.error(e.getMessage());
            throw new RobustStreamingException(e);
        }

    }

    /**
     * Constructor of ActiveMQSubscriber with default ActiveMQ Topics url and default robust topic name.
     * @throws RobustStreamingException
     */
    public ActiveMQSubscriber() throws RobustStreamingException {
        this(ActiveMQPublisher.DEFAULT_TOPIC_NAME, DEAFAULT_URL,DEAFAULT_USER_NAME,DEAFAULT_USER_PASSWORD);
    }

    class RobustMessageListenerWrapper implements MessageListener {
        private RobustMessageListener robustMessageListener;

        public RobustMessageListenerWrapper(RobustMessageListener robustMessageListener) {
            this.robustMessageListener = robustMessageListener;
        }

        @Override
        public void onMessage(Message message){

            try {
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    robustMessageListener.onMessage(text);
                } else {
                    log.error("Message won't be processed, only TextMessages are allowed : " + message.toString() );
                }
            } catch (JMSException e) {
                log.error("Caught:" + e);
                e.printStackTrace();
            }

        }
    }

    @Override
    public void subscribe(RobustMessageListener robustMessageListener) throws RobustStreamingException {

        MessageListener listener = robustMessageListener;
        Session session = null;
        try {
            session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic(topicName);
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(listener);
        } catch (JMSException e) {
            throw new RobustStreamingException(e);
        }
    }

    // FIX-ME commented code on SVN ?
//    public void setTopicName(String topicName) {
//        this.topicName = topicName;
//    }


    @Override
    public void close() throws RobustStreamingException {
        try {
            connection.close();
        } catch (JMSException e) {
            throw new RobustStreamingException(e);
        }
    }
}
