package pl.swmind.robust.gibbs.buffer;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Interface for buffer component in Gibbs Sampler module.
 */
public interface GibbsSamplerBuffer {

    /**
     * Buffers the value of the E type in the GS buffer.
     * @param id id of the value (says to which snapshot value should be add)
     * @param value object to buffer
     * @param <E> type of the object to buffer
     * @throws GibbsSamplerBufferException
     */
    <E extends Number> void buffer(String id, E value) throws GibbsSamplerBufferException;


    /**
     * Buffers the value of the E type in the GS buffer in Demo Mode. In normal mode the date is ignored.
     * @param id id of the value (says to which snapshot value should be add)
     * @param value object to buffer
     * @param timestamp value timestamp for demo mode
     * @param <E> type of the object to buffer
     * @throws GibbsSamplerBufferException
     */
    <E extends Number> void buffer(String id, E value, Date timestamp) throws GibbsSamplerBufferException;


   <E extends Number> boolean initWithHistoricalData(Map<String, List<E>> historicalData) throws GibbsSamplerBufferException;

    void start() throws GibbsSamplerBufferException;

    /**
     * Stops the buffer - the periodic tasks after each snapshot is collected are stopped.
     */
    void stop();



}
