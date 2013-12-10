package pl.swmind.robust.gibbs.buffer;


import java.util.List;

/**
 * Interface for GibbsSamplerBuffer module which starts when each snapshot period ends.
 */
public interface GibbsSamplerCounter {

    /**
     * Count the final value of the all snapshot values.
     * @param list of the values buffered for the one snapshot.
     * @return the counted value
     */
    Number count(List<?> list);

}
