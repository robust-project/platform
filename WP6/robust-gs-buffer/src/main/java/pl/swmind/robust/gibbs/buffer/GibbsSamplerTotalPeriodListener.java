package pl.swmind.robust.gibbs.buffer;


import java.util.List;
import java.util.Map;

/**
 * Interface for GibbsSamplerBuffer module which starts after each snapshot when number of snapshots >= totalPeriod.
 */
public interface GibbsSamplerTotalPeriodListener {




    /**
     * Starts after each snapshot when number of snapshots >= totalPeriod
     * @param snapshotDataMap map of list of snapshot values where the key is id
     * @param snapshotStartTimestamp timestamp of snapshot beginning
     */
    void onMessage(Map<String,List<Number>> snapshotDataMap, long snapshotStartTimestamp);

}
