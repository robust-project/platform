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

import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;

import org.codehaus.jackson.map.ObjectMapper;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import java.io.IOException;

public class StreamMessageListener implements MessageListener {
    private volatile long numMessages = 0;
    private volatile long numValidMessages = 0;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BlockingQueue<Event> eventsQueue;

    public StreamMessageListener(final BlockingQueue<Event> eventsQueue) {
        this.eventsQueue = eventsQueue;
    }

    public long getNumMessages() {
        return numMessages;
    }

    public long getNumValidMessages() {
        return numValidMessages;
    }

    @Override
    public void onMessage(final Message message) {
        numMessages++;
        try {
            if( !(message instanceof TextMessage) )
                throw new FeederException("Received message is not an instance of TextMessage");

            final TextMessage txtMessage = (TextMessage)message;
            if( txtMessage.getJMSType() == null )
                throw new FeederException("Received message does not have a JMSType attribute set");
            if( !txtMessage.propertyExists("timestamp") )
                throw new FeederException("Received message does not contain a 'timestamp' property");

            eventsQueue.put(objectMapper.readValue(txtMessage.getText(), Event.class));
            numValidMessages++;
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( FeederException e ) {
            e.printStackTrace();
        }
        catch( JMSException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
