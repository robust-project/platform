package pl.swmind.robust.gibbs.buffer.impl;

import pl.swmind.robust.gibbs.buffer.*;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.*;


public class DemoGibbsSamplerBufferImpl extends  AbstractGibbsSamplerBuffer {

    private Date nextSnapshotStartDate;
    private Date startDate;


    public DemoGibbsSamplerBufferImpl(SnapshotPeriod snapshotPeriod, int totalPeriods, GibbsSamplerCounter gibbsSamplerCounter){
        super(snapshotPeriod, totalPeriods, gibbsSamplerCounter);

        //setNextSnapshotStartDate();
    }

    @Override
    public <E extends Number> void buffer(String id, E value) throws GibbsSamplerBufferException {
        buffer(id, value, new Date());
    }

    public <E extends Number> void buffer(String id, E value, Date date) throws GibbsSamplerBufferException {

        if (date.getTime() < nextSnapshotStartDate.getTime()){
            log.debug("Buffering: " + id + ", " + value);



            if (snapshotsElements.containsKey(id)){
                List<E> elements = (List<E>) snapshotsElements.get(id);
                elements.add(value);
            } else{
                List<E> elements = new LinkedList<E>();
                elements.add(value);
                snapshotsElements.put(id, elements);
                ids.add(id);
            }
        } else{
            log.debug("Object to buffer from nextSnapshotPeriod, counting current snapshot value... id=" + id + ", value=" + value);
            for (String key : snapshotsElements.keySet()){
                List<?> list =  snapshotsElements.get(key);
                Number result = gibbsSamplerCounter.count(list);
                if (null != list){
                    list.clear();
                }
                try {
                    storeSnapshot(key, result);
                } catch (GibbsSamplerBufferException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
            }
            Map<String,List<Number>> lastSnapshots = bufferStorage.getLastSnapshots();
            gibbsSamplerTotalPeriodListener.onMessage(lastSnapshots, lastSnapshotStartTimestamp);
            lastSnapshotStartTimestamp = nextSnapshotStartDate.getTime();


            setNextSnapshotStartDate();
            buffer(id, value, date);
        }
    }

    @Override
    public void start() throws GibbsSamplerBufferException {
        if (startDate == null) {
            throw new GibbsSamplerBufferException("The given start date is NULL");
        }
        super.start();
        nextSnapshotStartDate = startDate;
        setNextSnapshotStartDate();
    }

    public void stop() {
        log.info("Stopping...");
    }

    private void setNextSnapshotStartDate(){
        nextSnapshotStartDate = new Date(nextSnapshotStartDate.getTime() + snapshotPeriod.getSnapshotPeriodInMillis());
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}

