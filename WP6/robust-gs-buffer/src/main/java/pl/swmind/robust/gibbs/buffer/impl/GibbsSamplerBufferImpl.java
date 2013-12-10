package pl.swmind.robust.gibbs.buffer.impl;

import pl.swmind.robust.gibbs.buffer.*;
import pl.swmind.robust.gibbs.buffer.utils.SnapshotPeriod;

import java.util.*;


public class GibbsSamplerBufferImpl extends AbstractGibbsSamplerBuffer {

    private TimerTask timertask;
    private Timer timer;
;


    public GibbsSamplerBufferImpl(SnapshotPeriod snapshotPeriod, final int totalPeriods, final GibbsSamplerCounter gibbsSamplerCounter){
        super(snapshotPeriod, totalPeriods, gibbsSamplerCounter);
    }

    public void start() throws GibbsSamplerBufferException {
        super.start();
        timertask = new TimerTask() {
            @Override
            public void run() {

                for(String id : snapshotsElements.keySet()){
                    List<?> list =  snapshotsElements.get(id);
                    Number result = gibbsSamplerCounter.count(list);
                    if (null != list){
                        list.clear();
                    }
                    try {
                        storeSnapshot(id, result);
                    } catch (GibbsSamplerBufferException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                }


                Map<String,List<Number>> lastSnapshots = null;
                try {
                    lastSnapshots = bufferStorage.getLastSnapshots();
                } catch (GibbsSamplerBufferException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                }
                gibbsSamplerTotalPeriodListener.onMessage(lastSnapshots, lastSnapshotStartTimestamp);
                lastSnapshotStartTimestamp = new Date().getTime();
            }
        };

        timer = new Timer();
        long periodInMillis = snapshotPeriod.getSnapshotPeriodInMillis();
        timer.schedule(timertask, periodInMillis, periodInMillis);
        log.info("Timer scheduled: " + snapshotPeriod.getDuration() + " x "+ snapshotPeriod.getTimeUnitName());
    }


    public <E extends Number> void buffer(String id, E value) throws GibbsSamplerBufferException {

        log.info("Buffering: " + id + ", " + value);

        if (snapshotsElements.containsKey(id)){
            List<E> elements = (List<E>) snapshotsElements.get(id);
            elements.add(value);

        } else{
            List<E> elements = new LinkedList<E>();
            elements.add(value);
            snapshotsElements.put(id, elements);
        }
    }

    @Override
    public <E extends Number> void buffer(String id, E value, Date timestamp) throws GibbsSamplerBufferException {
        log.info("param date ignored - this param works only in demo mode...");
        buffer(id, value);
    }

    @Override
    public void stop() {
        log.info("Stopping timer...");
        timer.cancel();
    }


}

