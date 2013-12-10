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

import deri.uimr.utilities.Logger;
import deri.uimr.utilities.concurrent.AbstractUIMRRunnable;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class FeedingRunnable extends AbstractUIMRRunnable {
    private final MySQLManager mySQLManager = MySQLManager.getInstance();
    private volatile long numCommitted = 0;
    private volatile double commitIntervalMillis = 30000.0;
    private final BlockingQueue<Event> eventsQueue;

    public FeedingRunnable(final String name, final BlockingQueue<Event> eventsQueue) {
        super(name);
        this.eventsQueue = eventsQueue;
    }

    public long getNumCommitted() {
        return numCommitted;
    }

    public void setCommitIntervalMillis(final double commitIntervalMillis) {
        this.commitIntervalMillis = commitIntervalMillis;
    }

    @Override
    public void run() {
        Logger.log("Started.");
        while( !Thread.currentThread().isInterrupted() )
            try {
                suspendRunnable(commitIntervalMillis);
                final Thread workerThread = new Thread(new WorkerRunnable(),
                                            String.format("%s-wrkr", getName()));
                workerThread.start();
            }
            catch( InterruptedException e ) {
                break;
            }
        Logger.log("Terminated.");
    }

    public class WorkerRunnable implements Runnable {
        @Override
        public void run() {
            final List<Event> eventsBuffer = new ArrayList<Event>();
            eventsQueue.drainTo(eventsBuffer);

            final long numCommittedRun = mySQLManager.insertEvents(eventsBuffer);
            numCommitted = numCommitted + numCommittedRun;

            Logger.log(String.format("%d event(s) committed.", numCommittedRun));
        }
    }
}
