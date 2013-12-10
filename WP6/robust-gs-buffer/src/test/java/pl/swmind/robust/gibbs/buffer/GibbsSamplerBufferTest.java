package pl.swmind.robust.gibbs.buffer;

import org.mockito.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerBufferStorageImpl;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerCounter;
import pl.swmind.robust.gibbs.buffer.impl.GibbsSamplerBufferImpl;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class GibbsSamplerBufferTest {

    private static final int TOTAL_PERIOD = 1;
    private static final int HISTORICAL_DATA = 1;
    private static final String ID = "id";
    private GibbsSamplerBufferImpl gibbsSamplerBuffer;
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
        gibbsSamplerBuffer = new GibbsSamplerBufferImpl(snapshotPeriod, TOTAL_PERIOD, gibbsSamplerCounter);
        gibbsSamplerBufferStorage = new DefaultGibbsSamplerBufferStorageImpl();
        gibbsSamplerBuffer.setBufferStorage(gibbsSamplerBufferStorage);

        gibbsSamplerBuffer.start();
        gibbsSamplerTotalPeriodListener = mock(GibbsSamplerTotalPeriodListener.class);
        gibbsSamplerBuffer.setGibbsSamplerTotalPeriodListener(gibbsSamplerTotalPeriodListener);

    }

    @Test
    public void testStoreSnapshots() throws GibbsSamplerBufferException, InterruptedException {
        Double value = 1.1;

        List<Double> list = new LinkedList<Double>();
        list.add(value);

        Map<String, List<Number>> expectedMap = new HashMap<String, List<Number>>();

        List<Number> expectedList = new LinkedList<Number>();
        expectedList.add(gibbsSamplerCounter.count(list));
        expectedMap.put(ID, expectedList);
        gibbsSamplerBuffer.buffer(ID, value);
        verify(gibbsSamplerTotalPeriodListener,timeout(1000).times(1)).onMessage(eq(expectedMap), Matchers.anyLong());

    }

    @Test
    public void testStore3SnapshotsInOneTotalPeriod() throws GibbsSamplerBufferException, InterruptedException {
        Double value1 = 1.1;
        Double value2 = 1.2;
        Double value3 = 1.3;

        List<Number> list = new LinkedList<Number>();
        list.add(value1);
        list.add(value2);
        list.add(value3);

        List<Number> expectedList = new LinkedList<Number>();
        expectedList.add(gibbsSamplerCounter.count(list));
        gibbsSamplerBuffer.buffer(ID, value1);
        gibbsSamplerBuffer.buffer(ID, value2);
        gibbsSamplerBuffer.buffer(ID, value3);

        Map<String, List<Number>> expectedMap = new HashMap<String, List<Number>>();
        expectedMap.put(ID, expectedList);

        verify(gibbsSamplerTotalPeriodListener,timeout(1000).times(1)).onMessage(eq(expectedMap), Matchers.anyLong());
    }


    @AfterMethod
    public void tearDown(){
        gibbsSamplerBuffer.stop();
    }

}
