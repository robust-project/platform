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
package pl.swmind.robust.streaming.topic;

import pl.swmind.robust.streaming.exception.RobustStreamingException;

/**
 * Common interface for Subscribers in Robust Streaming Architecture.
 * @author Rafal Janik
 */
public interface RobustSubscriber {

   /**
     * Subscribes to the topic.
     * @param listener  - implementation of  RobustMessageListener which describes how message should be handled.
     * @throws RobustStreamingException
     */
    void subscribe(RobustMessageListener listener) throws RobustStreamingException;

    /**
     * Closes the connection to the topic.
     * @throws RobustStreamingException
     */
    void close() throws RobustStreamingException;

}
