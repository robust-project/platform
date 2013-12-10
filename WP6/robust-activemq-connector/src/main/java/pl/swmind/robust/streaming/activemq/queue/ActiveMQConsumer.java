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
package pl.swmind.robust.streaming.activemq.queue;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.queue.RobustConsumer;
import pl.swmind.robust.streaming.topic.RobustMessageListener;

import javax.jms.*;

/**
 * Implementation of RobustConsumer using ActiveMQ.
 * @author rafal
 */
public class ActiveMQConsumer implements RobustConsumer{

    private static final Logger log = Logger.getLogger(ActiveMQConsumer.class);

    private static final ConnectionFactory DEFAULT_CONNECTION_FACTORY = new ActiveMQConnectionFactory(
                ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD,
                ActiveMQConnection.DEFAULT_BROKER_URL);


    private Connection connection;
    private Session session;
    private Destination destination;
    private static boolean transacted = false;

    /**
     * Default constructor with default queue robust queue and ActiveMQ connectionFactory.
     * @throws RobustStreamingException
     */
    public ActiveMQConsumer() throws RobustStreamingException{
        this(ActiveMQProducer.DEFAULT_QUEUE, DEFAULT_CONNECTION_FACTORY);
    }

    /**
     * Constructor with default ActiveMQ connectionFactory and specified queue name.
     * @param queueName the name of queue from where messages are consumed.
     * @throws RobustStreamingException
     */
    public ActiveMQConsumer(String queueName) throws RobustStreamingException{
        this(queueName, DEFAULT_CONNECTION_FACTORY);
    }


    /**
     * Constructor.
     * @param queueName the name of queue from where messages are consumed.
     * @param connectionFactory for the consumer
     * @throws RobustStreamingException
     */
    public ActiveMQConsumer(String queueName, ConnectionFactory connectionFactory) throws RobustStreamingException{

        log.info("Consumer Connection Factory: " + connectionFactory.toString());
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(queueName);

        } catch (JMSException e) {
            log.error(e.getMessage());
            throw new RobustStreamingException(e);
        }
    }


    @Override
    public void start(RobustMessageListener messageListener) throws RobustStreamingException {

        log.info("Starting the robust consumer...");
        try{
            // FIX-ME for what is this connection here ?
            connection = DEFAULT_CONNECTION_FACTORY.createConnection();
            connection.start();
            MessageConsumer consumer = session.createConsumer(destination);
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            log.error(e.getMessage());
            throw new RobustStreamingException(e);
        }

    }
}
