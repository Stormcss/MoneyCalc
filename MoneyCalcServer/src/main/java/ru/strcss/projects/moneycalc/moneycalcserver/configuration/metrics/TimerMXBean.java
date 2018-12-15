package ru.strcss.projects.moneycalc.moneycalcserver.configuration.metrics;

/**
 * Created by Stormcss
 * Date: 12.12.2018
 */
public interface TimerMXBean {
    /**
     * The total time of recorded events.
     */
    double getTotalTime();

    /**
     * The distribution average for all recorded events
     */
    double getMean();

    /**
     * The maximum time of a single event
     */
    double getMax();

    /**
     * The number of times that stop has been called on this timer
     */
    long getCount();
}
