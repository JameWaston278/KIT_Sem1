package domain.graph;

import java.time.LocalTime;

/**
 * Class representing a lift in a ski area.
 * Each lift has a type, operating hours, ride time, queue time, and an
 * indicator if it's a talstation.
 * 
 * @author udqch
 */
public class Lift extends Node {

    private final LiftType type;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final int rideTime; // Time taken to ride the lift in minutes
    private final int queueTime; // Average time spent in the queue in minutes
    private final boolean isTalstation; // Indicates if the lift is a talstation (base station)

    /**
     * Constructor to initialize the lift with the specified parameters.
     *
     * @param id           the unique identifier for the lift
     * @param type         the type of the lift
     * @param startTime    the start time of the lift's operating hours
     * @param endTime      the end time of the lift's operating hours
     * @param rideTime     the time taken to ride the lift in minutes
     * @param queueTime    the average time spent in the queue in minutes
     * @param isTalstation indicates if the lift is a talstation (base station)
     */
    public Lift(String id, LiftType type, LocalTime startTime, LocalTime endTime,
            int rideTime, int queueTime, boolean isTalstation) {
        super(id);
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.rideTime = rideTime;
        this.queueTime = queueTime;
        this.isTalstation = isTalstation;
    }

    // --- GETTERS ---
    /**
     * Returns the type of the lift.
     *
     * @return the type of the lift
     */
    public LiftType getType() {
        return type;
    }

    /**
     * Returns the start time of the lift's operating hours.
     *
     * @return the start time of the lift's operating hours
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the lift's operating hours.
     *
     * @return the end time of the lift's operating hours
     */
    public LocalTime getEndTime() {
        return endTime;
    }

    /**
     * Returns the time taken to ride the lift in minutes.
     *
     * @return the time taken to ride the lift in minutes
     */
    public int getRideTime() {
        return rideTime;
    }

    /**
     * Returns the average time spent in the queue in minutes.
     *
     * @return the average time spent in the queue in minutes
     */
    public int getQueueTime() {
        return queueTime;
    }

    /**
     * Returns whether the lift is a talstation (base station).
     *
     * @return true if the lift is a talstation, false otherwise
     */
    public boolean isTalstation() {
        return isTalstation;
    }

}
