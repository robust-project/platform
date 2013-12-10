package pl.swmind.robust.gibbs.buffer;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pl.swmind.robust.gibbs.buffer.impl.DefaultGibbsSamplerBufferStorageImpl;

import java.util.LinkedList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for simple App.
 */
public class DefaultGibbsSamplerBufferStorageTest {

    public static final Number TEST_VALUE = 123;
    public static final double DEFAULT_VALUE = -1.0;
    private GibbsSamplerBufferStorage bufferStorage;
    private final String ID_1 = "ID1";
    private final int BUFFER_SIZE = 12;
    private List<Double> list1;

    {
        list1 = new LinkedList<Double>();
        for (int i = 0; i < BUFFER_SIZE; i++){
            list1.add(new Double(i));
        }
    }

    @BeforeMethod
    private void setUp(){
        bufferStorage = new DefaultGibbsSamplerBufferStorageImpl();
        bufferStorage.setBufferSize(BUFFER_SIZE);
    }

    @Test
    public void testStoreSnapshots() throws GibbsSamplerBufferException {

        for (Double d : list1){
            bufferStorage.storeSnapshot(ID_1,d);
        }
    }

    @Test
    public void testGetNonCompleteSnapshots() throws GibbsSamplerBufferException {

        bufferStorage.storeSnapshot(ID_1, TEST_VALUE);

        List<Number> match = new LinkedList<Number>();
        while (match.size() < BUFFER_SIZE -1){
            match.add(DEFAULT_VALUE);
        }
        match.add(TEST_VALUE);

        List<Number> result = bufferStorage.getLastSnapshots().get(ID_1);

        for (int i = 0;  i < match.size(); i++){
            assertEquals(result.get(i), match.get(i));
        }

    }


    @Test
    public void testStoreAndReceiveSnapshots() throws GibbsSamplerBufferException {

        for (Double d : list1){
            bufferStorage.storeSnapshot(ID_1,d);
        }

        List<Number> list = (List<Number>) bufferStorage.getLastSnapshots().get(ID_1);
        assertNotNull(list);
        assertEquals(BUFFER_SIZE, list.size());
        for (int i = 0;  i < list.size(); i++){
            assertEquals(list1.get(i), list.get(i));
        }
    }

    @Test
    public void testStoreAndReceiveSnapshotsOverPeriod() throws GibbsSamplerBufferException {

        for (Double d : list1){
            bufferStorage.storeSnapshot(ID_1,d);
        }

        List<Number> list1 = (List<Number>) bufferStorage.getLastSnapshots().get(ID_1);

        Double nextValue = new Double(list1.size());
        bufferStorage.storeSnapshot(ID_1,nextValue);

        List<Number> list2 = (List<Number>) bufferStorage.getLastSnapshots().get(ID_1);

        assertNotNull(list2);
        assertEquals(BUFFER_SIZE, list2.size());

        for (int i = 0;  i < list2.size(); i++){
            assertEquals(new Double(i + 1), list2.get(i));
        }
    }

}
