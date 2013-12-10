package pl.swmind.robust.gibbs.buffer;

import java.util.List;
import java.util.Map;

/**
 * The interface for GibbsSamplerBuffer module which saves the values and returns them on demand.
 */
public interface GibbsSamplerBufferStorage {

    /**
     * Stores value in the storage with specified id.
     * @param id id of the value
     * @param value object to store
     * @param <E> type of the object - value of snapshot - to store
     * @throws GibbsSamplerBufferException
     */
    <E extends Number> void storeSnapshot(String id, E value) throws GibbsSamplerBufferException;

    /**
     * Gets the map of list with last x of snapshot objects, where x is the number set in  setBufferSize method.
     * @param <E> type of the value object
     * @return map of snapshot values for each id
     * @throws GibbsSamplerBufferException
     */
    <E extends Number> Map<String,List<E>> getLastSnapshots() throws GibbsSamplerBufferException;

    /**
     * Sets the buffer size - the number of snapshots that should be returned (=TotalPeriod)
     * @param size
     */
    void setBufferSize(int size);
}
