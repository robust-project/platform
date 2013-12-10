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
package pl.swmind.robust.streaming.queue;

import pl.swmind.robust.streaming.exception.RobustStreamingException;

/**
 * Interface for queue producers for Robust Streaming Architecture.
 */
public interface RobustProducer {

    /**
     * Sends the messages to the queue.
     * @param message
     * @throws RobustStreamingException
     */
    void send(String message) throws RobustStreamingException;

    /**
     * Sends the messages to the queue.
     * @param message
     * @throws RobustStreamingException
     */
    void send(String message, String type, long timestamp) throws RobustStreamingException;

}
