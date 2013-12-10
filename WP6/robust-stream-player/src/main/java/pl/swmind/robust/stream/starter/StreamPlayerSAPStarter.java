package pl.swmind.robust.stream.starter;


import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pl.swmind.robust.stream.player.StreamPlayer;
import pl.swmind.robust.stream.sap.dao.SAPDao;
import pl.swmind.robust.streaming.model.StreamDao;


public class StreamPlayerSAPStarter extends Starter {
    private static Logger logger = Logger.getLogger(StreamPlayerSAPStarter.class);
    private static SessionFactory sessionFactory;

    public StreamPlayerSAPStarter(String[] args) {
        super(args);
    }

    public static void main(String[] args){
        StreamPlayerSAPStarter starter = new StreamPlayerSAPStarter(args);

        ApplicationContext context = new ClassPathXmlApplicationContext("/META-INF/spring/player-context.xml");
        sessionFactory = (SessionFactory) context.getBean("sessionFactory");

        StreamDao sapDao = starter.getDao();
        StreamPlayer player = starter.getPlayer(sapDao);

        starter.start(player);
    }

    @Override
    public StreamDao getDao() {
        SAPDao sapDao = new SAPDao();
        sapDao.setNoOfMessages(noOfMessages);
        sapDao.setFromDate(fromDate);
        sapDao.setToDate(toDate);
        sapDao.setSessionFactory(sessionFactory);
        return sapDao;
    }
}
