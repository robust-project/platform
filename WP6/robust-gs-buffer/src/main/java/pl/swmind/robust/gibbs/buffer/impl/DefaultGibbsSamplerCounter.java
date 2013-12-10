package pl.swmind.robust.gibbs.buffer.impl;

import pl.swmind.robust.gibbs.buffer.GibbsSamplerCounter;

import java.util.List;

/**
 *  Default implementation of GibbsSamplerCounter that returns the number of buffered events in one snapshot.
 */
public class DefaultGibbsSamplerCounter implements GibbsSamplerCounter{


    @Override
    public Double count(List<?> list) {

        if (null == list || list.isEmpty()){
            return new Double(0);
        }
        return new Double(list.size());

    }
}


