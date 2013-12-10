/*
 * Any use, copying, modification, distribution and selling of this software
 * and its documentation for any purposes without SoftwareMind's written permission
 * is hereby prohibited
 */
package pl.swmind.robust.gibbs.buffer.impl;

import java.util.List;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.DJstBufferException;
import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeClass;

import javax.annotation.Resource;

/**
 *
 * @author pifu
 */

@ContextConfiguration(locations={"classpath:META-INF/spring/test-application-context.xml"})
public class DefaultDJstBufferStorageImplTest extends AbstractTestNGSpringContextTests{

    @Resource
    private DefaultDJstBufferStorageImpl db;

    @BeforeClass
    private void setUp() {
        //ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/test-application-context.xml");
        //db = context.getBean(DefaultDJstBufferStorageImpl.class);
    }

    @Test
    public void storeAndLoadTest() throws DJstBufferException {
        TwitterPost tweet = new TwitterPost();
        tweet.setContent("SOME TWEET CONTENT");
        db.storeTweet(tweet);

        List<TwitterPost> tweets = db.getLastTweets();

        assertTrue(tweets.size() == 1);
        assertEquals(tweet.getContent(), tweets.get(0).getContent());

        tweets = db.getLastTweets();
        assertTrue(tweets.isEmpty());
    }
}
