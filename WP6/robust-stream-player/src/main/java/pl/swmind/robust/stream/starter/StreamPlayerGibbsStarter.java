package pl.swmind.robust.stream.starter;


import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.swmind.robust.stream.gibbs.dao.GibbsDao;
import pl.swmind.robust.stream.player.StreamPlayer;
import pl.swmind.robust.streaming.model.StreamDao;

public class StreamPlayerGibbsStarter extends Starter {
    private static Logger logger = Logger.getLogger(StreamPlayerGibbsStarter.class);
    private static SessionFactory sessionFactory;

    public StreamPlayerGibbsStarter(String[] args) {
        super(args);
    }

    public static void main(String[] args){
        StreamPlayerGibbsStarter starter = new StreamPlayerGibbsStarter(args);

        ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/player-context.xml");
                sessionFactory = (SessionFactory) context.getBean("gibbsSessionFactory");

        StreamDao sapDao = starter.getDao();
        StreamPlayer player = starter.getPlayer(sapDao);

        starter.start(player);
    }

    @Override
    public StreamDao getDao() {
        GibbsDao gibbsDao = new GibbsDao();
        gibbsDao.setNoOfMessages(noOfMessages);
        gibbsDao.setFromDate(fromDate);
        gibbsDao.setToDate(toDate);
        gibbsDao.setSessionFactory(sessionFactory);
        return gibbsDao;
    }
}
