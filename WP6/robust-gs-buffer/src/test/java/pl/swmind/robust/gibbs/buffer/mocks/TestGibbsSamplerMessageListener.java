package pl.swmind.robust.gibbs.buffer.mocks;

import org.apache.log4j.Logger;
import pl.swmind.robust.gibbs.buffer.GibbsSamplerTotalPeriodListener;

import java.util.List;
import java.util.Map;


public class TestGibbsSamplerMessageListener implements GibbsSamplerTotalPeriodListener {

    private Logger log = Logger.getLogger(TestGibbsSamplerMessageListener.class);

    @Override
    public void onMessage(Map<String,List<Number>> map, long snapshotStartTimestamp) {

        log.info("onMessage : map keyset size : " + map.keySet().size());
        log.info("onMessage snapsotTimestamp : " + snapshotStartTimestamp);

        for (String id : map.keySet()){

            log.info(System.currentTimeMillis() + " " + id);
            for (Object o : map.get(id)){
                log.info("element: " + (Double)o);
            }
        }
        log.info("onMessage stop");
    }


}
