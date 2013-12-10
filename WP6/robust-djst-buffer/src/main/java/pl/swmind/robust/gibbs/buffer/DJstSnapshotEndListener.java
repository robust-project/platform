package pl.swmind.robust.gibbs.buffer;


import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import java.util.List;
import java.util.Map;

/**
 * Interface for DJstBuffer module which starts after each snapshot.
 */
public interface DJstSnapshotEndListener {

    /**
     * This method is invoked after the complete period for the list with the all tweets from finished period.
     * @param twitterPosts list of all tweets from remaining period.
     */
    void onMessage(List<TwitterPost> twitterPosts);

}
