package pl.swmind.robust.gibbs.buffer.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import pl.swmind.robust.gibbs.buffer.*;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.*;

/**
 *  Abstract class for GibbsSamplerBuffer implementations.
 */
public abstract class AbstractGibbsSamplerBuffer implements GibbsSamplerBuffer{

    protected static final Logger log = Logger.getLogger(GibbsSamplerBuffer.class);
    protected int totalPeriods;
    protected SnapshotPeriod snapshotPeriod;
    protected GibbsSamplerCounter gibbsSamplerCounter;
    protected GibbsSamplerTotalPeriodListener gibbsSamplerTotalPeriodListener;
    protected Map<String,List<?>> snapshotsElements;
    protected Set<String> ids;
    protected boolean bufferStarted = false;
    protected boolean historicalDataLoaded = false;
    protected long lastSnapshotStartTimestamp;
    @Autowired
    protected GibbsSamplerBufferStorage bufferStorage;


    protected  AbstractGibbsSamplerBuffer(SnapshotPeriod snapshotPeriod, int totalPeriods, GibbsSamplerCounter gibbsSamplerCounter){
        snapshotsElements = new HashMap<String, List<?>>();
        ids = new HashSet<String>();
        this.snapshotPeriod = snapshotPeriod;
        this.gibbsSamplerCounter = gibbsSamplerCounter;
        this.totalPeriods = totalPeriods;
        log.info("Timer scheduled: " + snapshotPeriod.getTimeUnitName() + " x "+ snapshotPeriod.getDuration());
        log.info("Total period:    " + totalPeriods);
    }

    protected <E extends Number> void storeSnapshot(String id, E value) throws GibbsSamplerBufferException {
        bufferStarted = true;
        log.info("Storing snapshot with id=" + id + ", value=" + value + " in buffer.");
        bufferStorage.storeSnapshot(id,value);
    }


    public void setTotalPeriods(int totalPeriods) {
        this.totalPeriods = totalPeriods;
    }

    public void setBufferStorage(GibbsSamplerBufferStorage bufferStorage) {
        this.bufferStorage = bufferStorage;
        this.bufferStorage.setBufferSize(totalPeriods);
    }

    public void setGibbsSamplerTotalPeriodListener(GibbsSamplerTotalPeriodListener gibbsSamplerTotalPeriodListener) {
        this.gibbsSamplerTotalPeriodListener = gibbsSamplerTotalPeriodListener;
    }

    public <E extends Number> boolean initWithHistoricalData(Map<String, List<E>> historicalData) throws GibbsSamplerBufferException {
        if (!bufferStarted){
            for (String id : historicalData.keySet()){
                ids.add(id);
                List<E> list = historicalData.get(id);

                if (list.size() > totalPeriods){
                    list = list.subList(list.size() - totalPeriods, list.size());
                }
                for(Number value : list){
                    bufferStorage.storeSnapshot(id, value);
                }
                List<E> elements = new LinkedList<E>();
                snapshotsElements.put(id, elements);


            }
            historicalDataLoaded = true;
            //TODO fixme
            return historicalDataLoaded;
        }

        else{
            log.info("Historical data can't be initialized because buffer is already running...");
            return false;
        }
    }

    public void start() throws GibbsSamplerBufferException {
        if (historicalDataLoaded){
            Map<String,List<Number>> lastSnapshots = bufferStorage.getLastSnapshots();
            lastSnapshotStartTimestamp = new Date().getTime() - snapshotPeriod.getSnapshotPeriodInMillis();
            gibbsSamplerTotalPeriodListener.onMessage(lastSnapshots, lastSnapshotStartTimestamp);
            lastSnapshotStartTimestamp = new Date().getTime();
        }

    }


}
