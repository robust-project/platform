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
import pl.swmind.robust.streaming.commons.Constants;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.queue.RobustProducer;

import javax.jms.*;

/**
 * Implementation of RobustProducer using ActiveMQ.
 * @author rafal
 */
public class ActiveMQProducer implements RobustProducer{


    private static final Logger log = Logger.getLogger(ActiveMQProducer.class);

    private static final ConnectionFactory DEFAULT_CONNECTION_FACTORY = new ActiveMQConnectionFactory(
            ActiveMQConnection.DEFAULT_USER,
            ActiveMQConnection.DEFAULT_PASSWORD,
            ActiveMQConnection.DEFAULT_BROKER_URL);

    static final String DEFAULT_QUEUE = "ROBUST";

    private Connection connection;
    private Session session;
    private Destination destination;
    private boolean transacted = false;

    /**
     * Default constructor with default queue robust queue and ActiveMQ connectionFactory.
     * @throws RobustStreamingException
     */
    public ActiveMQProducer() throws RobustStreamingException {
        this(DEFAULT_QUEUE, DEFAULT_CONNECTION_FACTORY);
    }

    /**
     * Constructor with default ActiveMQ connectionFactory and specified queue name.
     * @param queueName the name of queue where messages are sent.
     * @throws RobustStreamingException
     */
    public ActiveMQProducer(String queueName) throws RobustStreamingException {
        this(queueName, DEFAULT_CONNECTION_FACTORY );
    }

    /**
     * Constructor.
     * @param queueName the name of queue from where messages are consumed.
     * @param connectionFactory for the producer
     * @throws RobustStreamingException
     */
    public ActiveMQProducer(String queueName, ConnectionFactory connectionFactory) throws RobustStreamingException {

        log.info("ActiveMQ producer started");
        try{
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(transacted, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue(queueName);
        }catch (JMSException ex){
            log.error(ex.getMessage());
            throw new RobustStreamingException(ex);
        }
        log.info("ActiveMQ connection initialized.");
    }



    @Override
      public void send(String s) throws RobustStreamingException {
          try{
              MessageProducer producer = session.createProducer(destination);
              producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
              TextMessage message = session.createTextMessage(s);
              log.debug("Sending message: " + message.getText());
              producer.send(message);
          }catch (JMSException ex){
              log.error(ex.getMessage());
              throw new RobustStreamingException(ex);
          }
      }

      @Override
      public void send(String text, String type, long timestamp) throws RobustStreamingException {
          try{
              MessageProducer producer = session.createProducer(destination);
              producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
              TextMessage message = session.createTextMessage(text);
              message.setJMSType(type);
              message.setLongProperty(Constants.TIMESTAMP_PROPERTY_NAME, timestamp);
              log.debug("Sending message: " + message.getText());
              producer.send(message);
          }catch (JMSException ex){
              log.error(ex.getMessage());
              throw new RobustStreamingException(ex);
          }
      }
}
