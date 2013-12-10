package pl.swmind.robust.gibbs.buffer.utils;


import java.util.concurrent.TimeUnit;


/**
 * SnapshotPeriod - the default SnapshotPeriod is one day.
 */
public class SnapshotPeriod {

    private TimeUnit timeUnit = TimeUnit.HOURS;

    private long duration = 1;
    
    /**
     * Default constructor which sets the snapshot period to one day.
     */
    public SnapshotPeriod() {
        this.timeUnit = TimeUnit.DAYS;
        this.duration = 1;
    }
    
    /**
     * Constructor to set the time unit and the duration, which allows you to
     * specify, for example 3 days (unit = DAYS, duration = 3).
     * @param timeUnit The time unit, as defined by the TimeUnit enumerations.
     * @param duration The duration of the time unit.
     */
    public SnapshotPeriod(TimeUnit timeUnit, long duration) {
        this.timeUnit = timeUnit;
        this.duration = duration;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSnapshotPeriodInMillis(){
        return timeUnit.toMillis(duration);
    }

    public String getTimeUnitName(){
        return timeUnit.name();
    }

    public long getDuration(){
        return duration;
    }

}
