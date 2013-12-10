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
import pl.swmind.robust.streaming.activemq.topic.ActiveMQPublisher;
import pl.swmind.robust.streaming.activemq.topic.ActiveMQSubscriber;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.topic.RobustMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import static org.mockito.Mockito.*;



    // FIX-ME more descriptive test function names
public class ActiveMQTopicTest {
    // FIX-ME make them private
    static ActiveMQPublisher publisher = null;
    static ActiveMQSubscriber subscriber = null;
    static BrokerService brokerSvc = null;
    private static final String TEST_MESSAGE = "TEST_MESSAGE";

    @BeforeClass
    public static void setUp() throws Exception {

        brokerSvc = new BrokerService();
        brokerSvc.setBrokerName("TestBroker");
        brokerSvc.addConnector("tcp://localhost:61616");
        brokerSvc.start();

        publisher = new ActiveMQPublisher();
        subscriber = new ActiveMQSubscriber();

    }

    @Test
    public void publishTest() throws RobustStreamingException {
        publisher.publish(TEST_MESSAGE);
    }

        // FIX-ME subscriber tests can be done in a loop
    @Test
    public void subscriberTest() throws RobustStreamingException {

        RobustMessageListener messageListener = mock(RobustMessageListener.class);
        subscriber.subscribe(messageListener);

        publisher.publish(TEST_MESSAGE);
        verify(messageListener, timeout(2000).times(1)).onMessage(argThat(new HasProperText()));

    }


    @Test
    public void subscriberNoMessageTest() throws RobustStreamingException {

        RobustMessageListener messageListener = mock(RobustMessageListener.class);
        subscriber.subscribe(messageListener);

        verify(messageListener, timeout(2000).times(0)).onMessage(argThat(new HasProperText()));
    }


    @Test
    public void subscriber3MessagesTest() throws RobustStreamingException {

        RobustMessageListener messageListener = mock(RobustMessageListener.class);
        subscriber.subscribe(messageListener);

        publisher.publish(TEST_MESSAGE);
        publisher.publish(TEST_MESSAGE);
        publisher.publish(TEST_MESSAGE);


        verify(messageListener, timeout(3000).times(3)).onMessage(argThat(new HasProperText()));

    }

    @AfterClass
    public void tearDown() throws Exception {
        brokerSvc.stop();

    }

    private class HasProperText extends ArgumentMatcher<Message> {
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

}



