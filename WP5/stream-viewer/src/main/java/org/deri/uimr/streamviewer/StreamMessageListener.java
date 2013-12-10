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

import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamMessageListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(StreamMessageListener.class);

    private volatile long num_messages = 0;
    private volatile long num_text_messages = 0;

    public long getNumMessages() {
        return num_messages;
    }

    public long getNumTextMessages() {
        return num_text_messages;
    }

    @Override
    public void onMessage(final Message message) {
        num_messages++;
        if (!(message instanceof TextMessage))
            logger.warn("Received message is not an instance of TextMessage.");
        try {
            final TextMessage text_message = (TextMessage)message;
            System.out.format("Message type '%s' @ %d: %s%n",
                              text_message.getJMSType(),
                              text_message.getJMSTimestamp(),
                              text_message.getText());
            num_text_messages++;
        }
        catch (JMSException e) {
            logger.error("While receiving message.", e);
        }
    }
}
