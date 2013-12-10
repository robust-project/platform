package pl.swmind.robust.gibbs.buffer.impl;



import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.DJstBufferException;
import pl.swmind.robust.gibbs.buffer.DJstBufferStorage;
import pl.swmind.robust.gibbs.buffer.DJstSnapshotEndListener;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;
import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import java.util.concurrent.TimeUnit;


import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class DJstBufferTest {

    private DJstBufferImpl buffer = new DJstBufferImpl();
    private DJstBufferStorage db;
    private DJstSnapshotEndListener listener;
    private SnapshotPeriod period = new SnapshotPeriod(TimeUnit.SECONDS, 2);

    private TwitterPost tweet1;
    private TwitterPost tweet2;
    private TwitterPost tweet3;

    private int periodInMillis = (int) period.getSnapshotPeriodInMillis();


    @BeforeClass
    private void setUp(){

        buffer.setSnapshotPeriod(period);

        tweet1 = new TwitterPost();
        tweet1.setContent("SOME TWEET1 CONTENT");

        tweet2 = new TwitterPost();
        tweet2.setContent("SOME TWEET2 CONTENT");

        tweet3 = new TwitterPost();
        tweet3.setContent("SOME TWEET2 CONTENT");
    }

    @BeforeMethod
    private void setUpMethod() throws DJstBufferException {
        db = mock(DJstBufferStorage.class);
        listener = mock(DJstSnapshotEndListener.class);
        buffer.setBufferStorage(db);
        buffer.setDjstSnapshotEndlistener(listener);
        buffer.start();
    }

    @AfterMethod
    private void tearDown(){
        buffer.stop();
    }

    @Test
    public void bufferOneTweetTest() throws DJstBufferException {
        buffer.buffer(tweet1);
        verify(db,timeout(periodInMillis).times(1)).storeTweet(tweet1);
    }

    @Test
    public void bufferThreeTweetsTest() throws DJstBufferException {
        buffer.buffer(tweet1);
        buffer.buffer(tweet2);
        buffer.buffer(tweet3);
        verify(db,timeout(periodInMillis).times(1)).storeTweet(tweet1);
        verify(db,timeout(periodInMillis).times(1)).storeTweet(tweet2);
        verify(db,timeout(periodInMillis).times(1)).storeTweet(tweet3);
    }

    @Test
    public void bufferThreeTweetsInTwoPeriodsTest() throws DJstBufferException, InterruptedException {
        buffer.buffer(tweet1);
        buffer.buffer(tweet2);
        Thread.sleep(2000);
        buffer.buffer(tweet3);
        verify(db,timeout(periodInMillis).times(1)).storeTweet(tweet1);
        verify(db,timeout(periodInMillis).times(1)).storeTweet(tweet2);

        int lessThen2Periods = 2 * periodInMillis - 100;

        verify(db,timeout(lessThen2Periods).times(1)).storeTweet(tweet3);
        verify(db,timeout(lessThen2Periods).times(1)).getLastTweets();
        verify(listener,timeout(lessThen2Periods).times(1)).onMessage(anyList());
    }

}
