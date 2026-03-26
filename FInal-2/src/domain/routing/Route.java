package domain.routing;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import domain.graph.Node;

/**
 * The Route class represents a planned route for a skier, consisting of a list
 * of nodes (pistes and lifts), the total duration of the route in minutes, and
 * a score that reflects how well the route matches the skier's preferences.
 *
 * @author udqch
 */
public class Route {
    private final List<Node> path;
    private final LocalTime startTime;
    private final long duration;
    private final int score;

    /**
     * Constructs a new Route instance with the specified nodes, total duration, and
     * score.
     *
     * @param path      the list of nodes that make up the route
     * @param startTime the start time of the route
     * @param duration  the total duration of the route in seconds
     * @param score     the score reflecting how well the route matches the
     *                  skier's preferences
     */
    public Route(List<Node> path, LocalTime startTime, long duration, int score) {
        this.path = path;
        this.startTime = startTime;
        this.duration = duration;
        this.score = score;
    }

    // --- GETTERS ---

    /**
     * Returns an unmodifiable list of nodes that make up the route.
     *
     * @return an unmodifiable list of nodes in the route
     */
    public List<Node> getRoute() {
        return Collections.unmodifiableList(path);
    }

    /**
     * Returns the start time of the route.
     *
     * @return the start time of the route
     */
    public LocalTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the total duration of the route in seconds.
     *
     * @return the total duration of the route in seconds
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Returns the score reflecting how well the route matches the skier's
     * preferences.
     *
     * @return the score of the route
     */
    public int getScore() {
        return score;
    }
}
