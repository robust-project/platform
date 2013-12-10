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
import org.zeromq.ZMQ;
import java.util.List;
import java.util.ArrayList;

public class StreamViewer {

    public static void main(final String[] args) {
        if( args.length == 0 ) {
            System.err.println("Usage: [connect_string] <num_payloads> <envelope_key>");
            System.exit(1);
        }

        // Parse arguments
        final String connectString = args[0];
        int numPayloads = 1;
        if( args.length > 1 )
            numPayloads = Integer.parseInt(args[1]);
        String envelopeKey = "";
        if( args.length > 2 )
            envelopeKey = args[2];

        // Show details
        System.err.println("ZMQ version  : " + ZMQ.getVersionString());
        System.err.println("Connect to   : '" + connectString + "'");
        System.err.println("# payloads   : " + numPayloads);
        System.err.println("Envelope key : '" + envelopeKey + "'");
        System.err.println("--------------------------------------------------------");

        // Initialize ZeroMQ
        final ZMQ.Context context = ZMQ.context(1);
        final ZMQ.Socket socket = context.socket(ZMQ.SUB);
        socket.connect(connectString);
        socket.subscribe(envelopeKey.getBytes());

        // Main loop
        while( true ) {
             final List<String> receivedEnvelopeContents = new ArrayList<>();

             // Receive envelope key and payloads
             final byte[] receivedEnvelopeKey = socket.recv(0);
             for( int i=0; i<numPayloads; i++ )
                 receivedEnvelopeContents.add(new String(socket.recv(0)));

             System.out.println("{" + new String(receivedEnvelopeKey) + "}" + receivedEnvelopeContents);
        }
    }
}
