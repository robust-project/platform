package pl.swmind.robust.gibbs.buffer;

import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import java.util.List;
import java.util.Map;

/**
 * The interface for DJstBuffer module which saves the values and returns them on demand.
 */
public interface DJstBufferStorage {

    /**
     * Stores single twitterPost in storage for current period.
     * @param twitterPost post to store
     * @throws DJstBufferException
     */
    void storeTweet(TwitterPost twitterPost) throws DJstBufferException;


    /**
     * Returns the list of all twitter posts from the last period, and then removes them from storage.
     * @return the list of tweets
     * @throws DJstBufferException
     */
    List<TwitterPost> getLastTweets() throws DJstBufferException;


}
