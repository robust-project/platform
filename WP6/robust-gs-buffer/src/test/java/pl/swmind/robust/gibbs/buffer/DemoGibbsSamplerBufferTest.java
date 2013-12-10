package pl.swmind.robust.gibbs.buffer;

import org.mockito.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerBufferStorageImpl;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerCounter;
import pl.swmind.robust.gibbs.buffer.impl.DemoGibbsSamplerBufferImpl;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;


public class DemoGibbsSamplerBufferTest {

    private static final int TOTAL_PERIOD = 1;
    private static final int DELAY = 5;
    private Date date1;
    private Date date2;
    private Date date3;


    private static final String ID = "id";
    private DemoGibbsSamplerBufferImpl gibbsSamplerBuffer;
    private GibbsSamplerBufferStorage gibbsSamplerBufferStorage;
    private GibbsSamplerTotalPeriodListener gibbsSamplerTotalPeriodListener;
    private DefaultGibbsSamplerCounter gibbsSamplerCounter;
    private SnapshotPeriod snapshotPeriod;



    @BeforeMethod
    private void setUp() throws GibbsSamplerBufferException {
        gibbsSamplerCounter = new DefaultGibbsSamplerCounter();
        snapshotPeriod = new SnapshotPeriod();
        snapshotPeriod.setDuration(1);
        snapshotPeriod.setTimeUnit(TimeUnit.SECONDS);
        gibbsSamplerBuffer = new DemoGibbsSamplerBufferImpl(snapshotPeriod, TOTAL_PERIOD, gibbsSamplerCounter);
        gibbsSamplerBuffer.setStartDate(new Date());
        gibbsSamplerBufferStorage = new DefaultGibbsSamplerBufferStorageImpl();
        gibbsSamplerBuffer.setBufferStorage(gibbsSamplerBufferStorage);
        gibbsSamplerTotalPeriodListener = mock(GibbsSamplerTotalPeriodListener.class);
        gibbsSamplerBuffer.setGibbsSamplerTotalPeriodListener(gibbsSamplerTotalPeriodListener);
        gibbsSamplerBuffer.start();
        date1 = new Date();
        date2 = new Date(date1.getTime() + DELAY);
        date3 = new Date(date1.getTime() + 1000);

    }

    @Test
    public void testStoreSnapshots() throws GibbsSamplerBufferException, InterruptedException {
        Double value = 1.1;

        List<Object> list = new LinkedList<Object>();
        list.add(value);

        List<Number> expectedList = new LinkedList<Number>();
        expectedList.add(gibbsSamplerCounter.count(list));
        gibbsSamplerBuffer.buffer(ID, value, date1);
        gibbsSamplerBuffer.buffer(ID, value, date3);

        Map<String, List<Number>> expectedMap = new HashMap<String, List<Number>>();
        expectedMap.put(ID, expectedList);

        verify(gibbsSamplerTotalPeriodListener,timeout(2000).times(1)).onMessage(eq(expectedMap), Matchers.anyLong());

    }

    @Test
    public void testStore3SnapshotsInOneTotalPeriod() throws GibbsSamplerBufferException, InterruptedException {
        Double value1 = 1.4;
        Double value2 = 1.5;
        Double value3 = 1.6;

        List<Number> list = new LinkedList<Number>();
        list.add(value1);
        list.add(value2);

        List<Number> expectedList = new LinkedList<Number>();
        expectedList.add(gibbsSamplerCounter.count(list));
        gibbsSamplerBuffer.buffer(ID, value1, date1);
        gibbsSamplerBuffer.buffer(ID, value2, date2);
        gibbsSamplerBuffer.buffer(ID, value3, date3);

        Map<String, List<Number>> expectedMap = new HashMap<String, List<Number>>();
        expectedMap.put(ID, expectedList);

        verify(gibbsSamplerTotalPeriodListener,timeout(2000).times(1)).onMessage(eq(expectedMap), Matchers.anyLong());

    }

    @AfterMethod
    public void tearDown(){
        gibbsSamplerBuffer.stop();
    }

}
