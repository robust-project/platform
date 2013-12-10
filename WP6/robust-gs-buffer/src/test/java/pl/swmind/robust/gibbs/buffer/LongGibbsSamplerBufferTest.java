package pl.swmind.robust.gibbs.buffer;

import org.testng.annotations.*;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerBufferStorageImpl;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerCounter;
import pl.swmind.robust.gibbs.buffer.impl.GibbsSamplerBufferImpl;
import pl.swmind.robust.gibbs.buffer.mocks.TestGibbsSamplerMessageListener;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;


public class LongGibbsSamplerBufferTest {


    private static final int TOTAL_PERIODS = 3;
    private static final String ID1 = "id_long1";
    private static final String ID2 = "id_long2";
    private GibbsSamplerBufferImpl gibbsSamplerBuffer;
    private GibbsSamplerBufferStorage gibbsSamplerBufferStorage;
    private GibbsSamplerTotalPeriodListener gibbsSamplerTotalPeriodListener;
    private DefaultGibbsSamplerCounter gibbsSamplerCounter;
    private SnapshotPeriod snapshotPeriod;


    @BeforeClass
    private void setUp(){
        gibbsSamplerCounter = new DefaultGibbsSamplerCounter();
        snapshotPeriod = new SnapshotPeriod();
        snapshotPeriod.setDuration(15);
        snapshotPeriod.setTimeUnit(TimeUnit.SECONDS);
        gibbsSamplerBuffer = new GibbsSamplerBufferImpl(snapshotPeriod, TOTAL_PERIODS, gibbsSamplerCounter);
        gibbsSamplerBufferStorage = new DefaultGibbsSamplerBufferStorageImpl();
        gibbsSamplerBuffer.setBufferStorage(gibbsSamplerBufferStorage);
        gibbsSamplerTotalPeriodListener = new TestGibbsSamplerMessageListener();
        gibbsSamplerBuffer.setGibbsSamplerTotalPeriodListener(gibbsSamplerTotalPeriodListener);

    }

   //@Test
    public void testStoreSnapshots() throws GibbsSamplerBufferException, InterruptedException {

       List<Number> historicalDataList;
       List<Number> historicalDataList2;

       Number[] array = {1.1,2.2};
       Number[] array2 = {3.3};
       historicalDataList = Arrays.asList(array);
       historicalDataList2 = Arrays.asList(array2);
       Map<String, List<Number>> historicalMap = new HashMap<String, List<Number>>();
       historicalMap.put(ID2, historicalDataList);
       historicalMap.put(ID1, historicalDataList2);

       gibbsSamplerBuffer.initWithHistoricalData(historicalMap);
       gibbsSamplerBuffer.start();

        for (int a = 0; a < 4; a++){

            for (int i = 0; i < 10 - a; i++){
                gibbsSamplerBuffer.buffer(ID1, i);
            }

            if ( a > 0 && a < 3){
                for (int i = 0; i < 10 - a; i++){
                    gibbsSamplerBuffer.buffer(ID2, i);
                }
            }
            Thread.sleep(snapshotPeriod.getSnapshotPeriodInMillis());
        }
    }

    @AfterClass
    public void tearDown(){
        gibbsSamplerBuffer.stop();
    }



}
