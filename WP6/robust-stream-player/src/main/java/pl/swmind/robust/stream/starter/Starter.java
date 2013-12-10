package pl.swmind.robust.stream.starter;


import org.apache.log4j.Logger;
import pl.swmind.robust.stream.player.StreamPlayer;
import pl.swmind.robust.streaming.activemq.topic.ActiveMQPublisher;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.model.StreamDao;
import pl.swmind.robust.streaming.topic.RobustPublisher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Starter {
    private Logger logger = Logger.getLogger(Starter.class);
    protected Double speedUp;
    protected Date fromDate;
    protected Date toDate;
    protected Integer noOfMessages = Integer.MAX_VALUE;
    protected String topicName;
    protected String url;

    public Starter(String[] args) {
        if(args.length < 6){
            logger.error("[ERROR] Arguments usage:\n speedUp,\n from date,\n to date,\n date format,\n topic name,\n broker url,\n number of messages (optional)");
            System.exit(-1);
        }

        speedUp = Double.parseDouble(args[0]);
        String fromDateStr = args[1];
        String toDateStr = args[2];
        String dateFormatStr = args[3];
        topicName = args[4];
        url = args[5];
        if (args.length > 6 ){
            noOfMessages = Integer.parseInt(args[6]);
        }

        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr, Locale.UK);
        try {
            fromDate = dateFormat.parse(fromDateStr);
            toDate = dateFormat.parse(toDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Double getSpeedUp() {
        return speedUp;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public Integer getNoOfMessages() {
        return noOfMessages;
    }

    public StreamPlayer getPlayer(StreamDao sapDao){
        StreamPlayer player = new StreamPlayer();
        player.setSpeedUp(speedUp);
        player.setStreamDao(sapDao);

        return player;
    }

    public void start(StreamPlayer player){
        try {
            RobustPublisher publisher = new ActiveMQPublisher(topicName,url);
            player.play(publisher);
        } catch (RobustStreamingException e) {
            e.printStackTrace();
        }
    }

    abstract public StreamDao getDao();

}
