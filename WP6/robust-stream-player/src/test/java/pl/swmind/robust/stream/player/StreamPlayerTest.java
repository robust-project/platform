package pl.swmind.robust.stream.player;


import org.junit.Before;
import org.junit.Test;
import pl.swmind.robust.stream.sap.dto.Message;
import pl.swmind.robust.streaming.activemq.topic.ActiveMQPublisher;
import pl.swmind.robust.streaming.exception.RobustStreamingException;
import pl.swmind.robust.streaming.model.RobustStreamMessage;
import pl.swmind.robust.streaming.model.StreamDao;
import pl.swmind.robust.streaming.topic.RobustPublisher;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamPlayerTest {
    private StreamPlayer streamPlayer;

    private StreamDao streamDao;
    private double speedUp = 100.0;

    @Before
    public void init(){
        streamDao = mock(StreamDao.class);
        streamPlayer = new StreamPlayer();
        streamPlayer.setSpeedUp(speedUp);
        streamPlayer.setStreamDao(streamDao);
    }

    @Test
    public void playTest(){
        List<RobustStreamMessage> messages = new LinkedList<RobustStreamMessage>();
        Message message1 = mock(Message.class);
        manageMessageMock(messages,10L,"Mocked message1", message1);
        Message message2 = mock(Message.class);
        manageMessageMock(messages,11L,"Mocked message2", message2);

        when(streamDao.getData()).thenReturn(messages);

        try {
            RobustPublisher publisher = mock(ActiveMQPublisher.class);
            publisher.publish(message1);
            publisher.publish(message2);

            streamPlayer.play(publisher);
        } catch (RobustStreamingException e) {
            e.printStackTrace();
        }
    }

    private void manageMessageMock(List<RobustStreamMessage> messages, long timestamp, String toStringMessage, RobustStreamMessage message){
        when(message.createTimestamp()).thenReturn(timestamp);
        when(message.toString()).thenReturn("Mocked message1");
        messages.add(message);
    }
}
