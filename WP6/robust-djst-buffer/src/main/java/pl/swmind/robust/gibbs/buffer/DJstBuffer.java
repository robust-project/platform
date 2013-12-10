package pl.swmind.robust.gibbs.buffer;

import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for buffer component in dJst module.
 */
public interface DJstBuffer {


    void buffer(TwitterPost twitterPost) throws DJstBufferException;


    /**
     * Starts the buffer, method must be invoked otherwise buffer won't run the DJstSnapshotEndListener.
     * @throws DJstBufferException
     */
    void start() throws DJstBufferException;

    /**
     * Stops the buffer - the periodic tasks after each snapshot is collected are stopped.
     */
    void stop();



}
