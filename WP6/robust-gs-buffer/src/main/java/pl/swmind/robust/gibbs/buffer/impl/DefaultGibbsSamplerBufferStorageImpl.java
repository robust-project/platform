package pl.swmind.robust.gibbs.buffer.impl;

import org.apache.log4j.Logger;
import pl.swmind.robust.gibbs.buffer.GibbsSamplerBufferException;
import pl.swmind.robust.gibbs.buffer.GibbsSamplerBufferStorage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Default GibbsSamplerBufferStorage which uses the map to store the snapshots values.
 */
public class DefaultGibbsSamplerBufferStorageImpl implements GibbsSamplerBufferStorage{

    private static final Logger log = Logger.getLogger(DefaultGibbsSamplerBufferStorageImpl.class);

    private final int FIRST_ELEMENT_INDEX = 0;

    private int bufferSize;

    private Map<String,List<?>> storage = new HashMap<String,List<?>>();
    private static final Double DEFAULT_VALUE = -1.0;

    public <E extends Number> void storeSnapshot(String id, E value) throws GibbsSamplerBufferException {
        log.debug("Storing snapshot id,value:" + id + "," +value);
        if (null == id || id.isEmpty()){
            throw new GibbsSamplerBufferException("Id can't be null or empty.");
        }

        if (storage.containsKey(id)){
            List<E> list = (List<E>) storage.get(id);
            boolean successesAdded = list.add(value);
        } else {
            log.debug("New id=" + id + ", creating list for snapshots...");
            List<E> newList = new LinkedList<E>();
            boolean successesAdded = newList.add(value);
            storage.put(id, newList);
        }

    }

    private <E extends Number> List<E> getLastSnapshots(String id) throws GibbsSamplerBufferException {

        List<E> list = (List<E>) storage.get(id);

        List<E> requestedList = new LinkedList<E>(list);
        if (list.size() >= bufferSize){
            list.remove(FIRST_ELEMENT_INDEX);

        }
        return requestedList;
    }


    public  Map<String,List<Number>> getLastSnapshots() throws GibbsSamplerBufferException {

        Map<String,List<Number>>  requestedMap = new HashMap<String, List<Number>>();

        for(String key : storage.keySet()){
            List<Number> list =  getLastSnapshots(key);
            log.debug(key + "    " + list.size());
            while (list.size() < bufferSize){
                list.add(FIRST_ELEMENT_INDEX, DEFAULT_VALUE);
            }

            requestedMap.put(key, list.subList(FIRST_ELEMENT_INDEX, list.size()));
        }

        return requestedMap;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

//    private <E> List<E> initListForNewId() {
//        List<E> list = new LinkedList<E>();
//        for (int i = 0; i < bufferSize -1; i ++){
//            list.add(null);
//        }
//        return list;
//    }

}
