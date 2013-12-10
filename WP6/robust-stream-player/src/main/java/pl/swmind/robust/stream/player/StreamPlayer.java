package pl.swmind.robust.stream.player;

import org.apache.log4j.Logger;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.model.RobustStreamMessage;
import pl.swmind.robust.streaming.model.StreamDao;
import pl.swmind.robust.streaming.topic.RobustPublisher;

import java.util.List;

public class StreamPlayer {
    private Logger logger = Logger.getLogger(StreamPlayer.class);
    private Double speedUp;
    private StreamDao streamDao;
    private int maxMessageLogSize = 15;

    public void setSpeedUp(Double speedUp) {
        this.speedUp = speedUp;
    }

    public void setStreamDao(StreamDao streamDao) {
        this.streamDao = streamDao;
    }

    public void play(RobustPublisher publisher) throws RobustStreamingException {
        logger.info("Stream player started");

        List<RobustStreamMessage> messages = streamDao.getData();

        logger.info(String.format("%d robust objects found", messages.size()));

        if (messages != null && !messages.isEmpty()){
            publishMessages(messages, publisher);
        }

        publisher.close();
        logger.info("StreamPlayer stopped");
    }

    private void publishMessages(List<RobustStreamMessage> messages, RobustPublisher publisher) {
        long lastTimestamp = messages.get(0).createTimestamp();

        for (RobustStreamMessage message : messages.subList(1,messages.size())){
            long timestamp = message.createTimestamp();
            Double waitTimeDouble = (double)(timestamp - lastTimestamp) / speedUp;
            long waitTime = waitTimeDouble.longValue();


            logger.info("Waiting " + waitTime + " ms for sending the post");

            try {
                Thread.sleep(waitTime);
                logger.info("Sending: " + message.toString().substring(maxMessageLogSize));
                publisher.publish(message);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }catch (RobustStreamingException e) {
                e.printStackTrace();
            }
            lastTimestamp = timestamp;
        }
    }
}
