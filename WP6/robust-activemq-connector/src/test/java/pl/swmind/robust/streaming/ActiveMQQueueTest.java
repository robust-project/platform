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
package pl.swmind.robust.streaming;

import org.apache.activemq.broker.BrokerService;
import org.mockito.ArgumentMatcher;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.swmind.robust.streaming.activemq.queue.ActiveMQConsumer;
import pl.swmind.robust.streaming.activemq.queue.ActiveMQProducer;
import pl.swmind.robust.streaming.commons.RobustEventType;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.topic.RobustMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import static org.mockito.Mockito.*;


public class ActiveMQQueueTest {

    static ActiveMQConsumer consumer = null;
    static ActiveMQProducer producer = null;
    static BrokerService brokerSvc = null;

    private static final String TEST_MESSAGE = "testMessage";


    @BeforeClass
    public static void setUp() throws Exception {

        brokerSvc = new BrokerService();
        brokerSvc.setBrokerName("TestBroker");
        brokerSvc.addConnector("tcp://localhost:61616");
        brokerSvc.start();

        consumer = new ActiveMQConsumer();
        producer = new ActiveMQProducer();

    }

    @Test
    public void sendTest() throws RobustStreamingException {

        producer.send(TEST_MESSAGE);


    }

    @Test(dependsOnMethods = "sendTest")
    public void consumeTest() throws RobustStreamingException {

        RobustMessageListener messageListener = mock(RobustMessageListener.class);
        consumer.start(messageListener);


        class HasProperText extends ArgumentMatcher<Message> {
            public boolean matches(Object message) {

                boolean result = false;
                try {
                    result = ((TextMessage) message).getText().equals(TEST_MESSAGE);
                } catch (JMSException e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        verify(messageListener,timeout(2000).times(1)).onMessage(argThat(new HasProperText()));

    }

    @AfterClass
    public void tearDown() throws Exception {
        brokerSvc.stop();

    }

}
