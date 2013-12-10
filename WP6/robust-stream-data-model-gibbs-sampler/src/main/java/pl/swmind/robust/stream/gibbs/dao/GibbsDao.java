package pl.swmind.robust.stream.gibbs.dao;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import pl.swmind.robust.stream.gibbs.dto.Message;
import pl.swmind.robust.streaming.model.RobustStreamMessage;
import pl.swmind.robust.streaming.model.StreamDao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GibbsDao implements StreamDao {
    private SessionFactory sessionFactory;
    private Date fromDate;
    private Date toDate;
    private int noOfMessages;

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

//    public void setFromDate(String fromDate) throws ParseException {
//        this.fromDate = parseDate(fromDate);
//    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

//    public void setToDate(String toDate) throws ParseException {
//        this.toDate = parseDate(toDate);
//    }

    public void setNoOfMessages(int noOfMessages) {
        this.noOfMessages = noOfMessages;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public List<RobustStreamMessage> getData() {
        Session session = sessionFactory.openSession();

        Timestamp fromTimestamp = new Timestamp(fromDate.getTime());
        Timestamp toTimestamp = new Timestamp(toDate.getTime());

        Criteria criteria = session.createCriteria(Message.class, "m")
                .setMaxResults(noOfMessages)
                .createAlias("m.messagepoint", "mp", CriteriaSpecification.INNER_JOIN)
                .createAlias("m.thread", "t", CriteriaSpecification.INNER_JOIN)
                .add(Restrictions.between("timestamp", fromTimestamp, toTimestamp))
                .addOrder(Order.asc("timestamp"))
                ;

        List<RobustStreamMessage> messages = criteria.list();
        session.close();

        return messages;
    }

    private Date parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.UK);
        Date date = (Date)sdf.parse(dateString);
        return date;
    }
}

