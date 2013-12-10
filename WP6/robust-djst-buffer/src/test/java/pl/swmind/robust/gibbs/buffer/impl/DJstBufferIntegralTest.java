package pl.swmind.robust.gibbs.buffer.impl;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.DJstBufferException;
import pl.swmind.robust.gibbs.buffer.DJstSnapshotEndListener;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;
import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ContextConfiguration(locations={"classpath:META-INF/spring/test-application-context.xml"})
public class DJstBufferIntegralTest extends AbstractTestNGSpringContextTests{

    @Resource
    private DefaultDJstBufferStorageImpl db;

    private DJstBufferImpl buffer = new DJstBufferImpl();

    private DJstSnapshotEndListener listener = mock(DJstSnapshotEndListener.class);
    private SnapshotPeriod period = new SnapshotPeriod(TimeUnit.SECONDS, 2);

    private TwitterPost tweet1;
    private TwitterPost tweet2;
    private TwitterPost tweet3;

    private int periodInMillis = (int) period.getSnapshotPeriodInMillis();

    @BeforeClass
    private void setUp(){
        buffer.setBufferStorage(db);
        buffer.setDjstSnapshotEndlistener(listener);
        buffer.setSnapshotPeriod(period);

        tweet1 = new TwitterPost();
        tweet1.setContent("SOME TWEET1 CONTENT");

        tweet2 = new TwitterPost();
        tweet2.setContent("SOME TWEET2 CONTENT");

        tweet3 = new TwitterPost();
        tweet3.setContent("SOME TWEET3 CONTENT");

    }

    @BeforeMethod
    private void setUpMethod() throws DJstBufferException {
       buffer.start();
    }

    @AfterMethod
    private void tearDown(){
       buffer.stop();
    }

    @Test
    public void bufferThreeTweetsInTwoPeriodsTest() throws DJstBufferException, InterruptedException {

        buffer.buffer(tweet1);
        buffer.buffer(tweet2);
        Thread.sleep(periodInMillis);
        buffer.buffer(tweet3);

        List<TwitterPost> expectedList = new LinkedList<TwitterPost>();
        expectedList.add(tweet1);
        expectedList.add(tweet2);

        verify(listener, timeout(2 * periodInMillis - 100).times(1)).onMessage(expectedList);

        expectedList = new LinkedList<TwitterPost>();
        expectedList.add(tweet3);

        verify(listener, timeout(3 * periodInMillis - 100).times(1)).onMessage(expectedList);

    }



}
