package pl.swmind.robust.gibbs.buffer;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerBufferStorageImpl;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerCounter;
import pl.swmind.robust.gibbs.buffer.impl.DemoGibbsSamplerBufferImpl;
import pl.swmind.robust.gibbs.buffer.mocks.TestGibbsSamplerMessageListener;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.*;
import java.util.concurrent.TimeUnit;


public class DemoLongGibbsSamplerBufferTest {


    private static final int TOTAL_PERIODS = 3;
    private static final String ID_1 = "long1";
    private static final String ID_2 = "long2";
    private DemoGibbsSamplerBufferImpl gibbsSamplerBuffer;
    private GibbsSamplerBufferStorage gibbsSamplerBufferStorage;
    private GibbsSamplerTotalPeriodListener gibbsSamplerTotalPeriodListener;
    private DefaultGibbsSamplerCounter gibbsSamplerCounter;
    private SnapshotPeriod snapshotPeriod;


    @BeforeClass
    private void setUp(){
        gibbsSamplerCounter = new DefaultGibbsSamplerCounter();
        snapshotPeriod = new SnapshotPeriod();
        snapshotPeriod.setDuration(1);
        snapshotPeriod.setTimeUnit(TimeUnit.MINUTES);
        gibbsSamplerBuffer = new DemoGibbsSamplerBufferImpl(snapshotPeriod, TOTAL_PERIODS, gibbsSamplerCounter);
        gibbsSamplerBuffer.setStartDate(new Date());
        gibbsSamplerBufferStorage = new DefaultGibbsSamplerBufferStorageImpl();
        gibbsSamplerBuffer.setBufferStorage(gibbsSamplerBufferStorage);
        gibbsSamplerTotalPeriodListener = new TestGibbsSamplerMessageListener();
        gibbsSamplerBuffer.setGibbsSamplerTotalPeriodListener(gibbsSamplerTotalPeriodListener);

    }

    @Test
    public void testStoreSnapshots() throws GibbsSamplerBufferException, InterruptedException {

        List<Number> historicalDataList = new LinkedList<Number>();
        Number[] array = {1.1,2.2};
        historicalDataList = Arrays.asList(array);
        Map<String, List<Number>> historicalMap = new HashMap<String, List<Number>>();
        historicalMap.put(ID_2, historicalDataList);

        gibbsSamplerBuffer.initWithHistoricalData(historicalMap);
        gibbsSamplerBuffer.start();

        Date date = new Date();

        Thread.sleep(2);

        for (int i = 0; i < 5; i++){
            gibbsSamplerBuffer.buffer(ID_1, i, new Date(date.getTime() + i * 1000));
        }

        date = new Date(date.getTime() + 60000);

        for (int i = 0; i < 4; i++){
            gibbsSamplerBuffer.buffer(ID_2, i, new Date(date.getTime() + i * 10));
        }

        for (int i = 0; i < 4; i++){
            gibbsSamplerBuffer.buffer(ID_1, i, new Date(date.getTime() + i* 100));
        }

        date = new Date(date.getTime() + 120000);

        for (int i = 0; i < 3; i++){
            gibbsSamplerBuffer.buffer(ID_1, i, new Date(date.getTime() + 10));
        }

        for (int i = 0; i < 4; i++){
            gibbsSamplerBuffer.buffer(ID_2, i, new Date(date.getTime() + i * 100));
        }

        date = new Date(date.getTime() + 180000);

        for (int i = 0; i < 2; i++){
            gibbsSamplerBuffer.buffer(ID_1, i, new Date(date.getTime() + 100));
        }

        date = new Date(date.getTime() + 60000);

        for (int i = 0; i < 1; i++){
            gibbsSamplerBuffer.buffer(ID_1, i, new Date(date.getTime() + 100));
        }


    }

    @AfterClass
    public void tearDown(){
        gibbsSamplerBuffer.stop();
    }



}
