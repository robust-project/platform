package pl.swmind.robust.gibbs.buffer.impl;

import org.apache.log4j.Logger;
import pl.swmind.robust.gibbs.buffer.*;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;
import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import java.util.*;


public class DJstBufferImpl implements DJstBuffer {

    private TimerTask timertask;
    private Timer timer;
    protected static final Logger log = Logger.getLogger(DJstBuffer.class);
    protected SnapshotPeriod snapshotPeriod;
    protected DJstSnapshotEndListener djstSnapshotEndlistener;
    private DJstBufferStorage bufferStorage;

    public void setBufferStorage(DJstBufferStorage bufferStorage) {
        this.bufferStorage = bufferStorage;
    }

    public void setSnapshotPeriod(SnapshotPeriod snapshotPeriod) {
        this.snapshotPeriod = snapshotPeriod;
    }

    public void setDjstSnapshotEndlistener(DJstSnapshotEndListener djstSnapshotEndlistener) {
        this.djstSnapshotEndlistener = djstSnapshotEndlistener;
    }

    private synchronized DJstBufferStorage getBufferStorage(){
        return bufferStorage;
    }

    @Override
    public void buffer(TwitterPost twitterPost) throws DJstBufferException {
        getBufferStorage().storeTweet(twitterPost);
    }

    public void start() throws DJstBufferException {

        timertask = new TimerTask() {
            @Override
            public void run() {

                try {
                    List<TwitterPost> posts = getBufferStorage().getLastTweets();
                    djstSnapshotEndlistener.onMessage(posts);
                } catch (DJstBufferException e) {
                    //TODO
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        };

        timer = new Timer();
        long periodInMillis = snapshotPeriod.getSnapshotPeriodInMillis();
        timer.schedule(timertask, periodInMillis, periodInMillis);
        log.info("Timer scheduled: " + snapshotPeriod.getDuration() + " x "+ snapshotPeriod.getTimeUnitName());
    }

    @Override
    public void stop() {
        log.info("Stopping timer...");
        timer.cancel();
    }


}

