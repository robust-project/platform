package pl.swmind.robust.gibbs.buffer.impl;

import org.apache.log4j.Logger;
import org.hibernate.annotations.Synchronize;
import pl.swmind.robust.gibbs.buffer.DJstBufferException;
import pl.swmind.robust.gibbs.buffer.DJstBufferStorage;
import pl.swmind.robust.sioc.twitter.mapping.TwitterPost;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default DJstBufferStorage which uses the db to store the snapshots values.
 */
@Repository
@Transactional
public class DefaultDJstBufferStorageImpl implements DJstBufferStorage {

    private static final Logger log = Logger.getLogger(DefaultDJstBufferStorageImpl.class);

    @Autowired
    private SessionFactory sessionFactory;


    private Session currentSession(){
      return sessionFactory.getCurrentSession();
    }

    /**
     * Save given tweet to database.
     * @param twitterPost
     * @throws DJstBufferException 
     */
    @Override
    public void storeTweet(TwitterPost twitterPost) throws DJstBufferException {
      log.debug("saving TwitterPost to database");
      currentSession().save(twitterPost);
    }

    /**
     * Get all tweets from database and then remove them.
     * @return
     * @throws DJstBufferException 
     */
    @Override
    public List<TwitterPost> getLastTweets() throws DJstBufferException {
      Session session = currentSession();
      
      log.debug("loading TwitterPosts from database");
      List<TwitterPost> results = session.createCriteria(TwitterPost.class).list();
      
      log.debug("removing TwitterPosts from database");
      session.createQuery("delete from " + TwitterPost.class.getSimpleName()).executeUpdate();
      
      return results;
    }
}
