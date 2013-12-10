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

public class Event {
    public Long id = null;
    public Long user_id = null;
    public Long conversation_id = null;
    public Long community_id = null;
    public String subject = null;
    public String content = null;
    public Long timestamp = null;
    public String msgid = null;
    public Long reply_to = null;

    @Override
    public String toString() {
        return String.format("<Event @ %d: id=%d, user_id=%d, conversation_id=%d, community_id=%d, reply_to=%d, subject=%s>",
                             timestamp, id, user_id, conversation_id, community_id, reply_to, subject);
    }

    public boolean valid() {
        if( id == null ) return false;
        if( user_id == null ) return false;
        if( conversation_id == null ) return false;
        if( community_id == null ) return false;
        if( subject == null ) return false;
        if( content == null ) return false;
        if( timestamp == null ) return false;
        if( msgid == null ) return false;
        return true;
    }
}
