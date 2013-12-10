package pl.swmind.robust.stream.gibbs.dao;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import pl.swmind.robust.stream.gibbs.dto.Message;
import pl.swmind.robust.streaming.model.RobustStreamMessage;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


// tests real DB access - use it if you wish to check it
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/gibbs-db-context.xml"})
public class SAPDBTest {
    @Autowired
    GibbsDao streamDao;

    private Logger logger = Logger.getLogger(SAPDBTest.class);

    @Test
    public void testNotNull(){
        assertNotNull(streamDao);
    }

    @Test
    public void testDbAccess() throws InterruptedException {
        int noOfMessages = 5;
        List<RobustStreamMessage> messages = streamDao.getData();

        assertEquals(noOfMessages, messages.size());

        for(RobustStreamMessage streamMessage:messages){
            assertTrue(streamMessage instanceof Message);
            Message message = (Message) streamMessage;
            logger.info(message);
        }
    }
}
