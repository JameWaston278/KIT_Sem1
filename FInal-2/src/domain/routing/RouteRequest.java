package domain.routing;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

import domain.graph.Node;
import domain.graph.SkiGraph;
import domain.skier.SkierProfile;

/**
 * The RouteRequest record encapsulates all the necessary information for
 * planning a route for a skier, including the ski graph, skier profile,
 * starting node, time constraints, and any forbidden nodes.
 *
 * @param graph          the ski graph representing the ski area
 * @param skier          the skier's profile containing their preferences and
 *                       goals
 * @param startNode      the starting node (must be a talstation lift)
 * @param endNode        the destination node (must be a talstation lift)
 * @param startTime      the time when the skier starts on the route
 * @param endTime        the time by which the skier must reach a goal node
 * @param forbiddenNodes a set of nodes that should be avoided during route
 *                       planning (can be null or empty if no nodes are to be
 *                       avoided)
 * 
 * @author udqch
 */
public record RouteRequest(
        SkiGraph graph,
        SkierProfile skier,
        Node startNode,
        Node endNode,
        LocalTime startTime,
        LocalTime endTime,
        Set<Node> forbiddenNodes) {

    /**
     * Constructs a new RouteRequest with the given parameters, using an empty set
     * for forbidden nodes if none are provided.
     *
     * @param graph     the ski graph representing the ski area
     * @param skier     the skier's profile containing their preferences and goals
     * @param startNode the starting node (must be a talstation lift)
     * @param endNode   the destination node (must be a talstation lift)
     * @param startTime the time when the skier starts on the route
     * @param endTime   the time by which the skier must reach a goal node
     */
    public RouteRequest(
            SkiGraph graph, SkierProfile skier, Node startNode, Node endNode, LocalTime startTime, LocalTime endTime) {
        this(graph, skier, startNode, endNode, startTime, endTime, Collections.emptySet());
    }

    /**
     * Compact constructor that validates the inputs and ensures that forbiddenNodes
     * is not null.
     */
    public RouteRequest {
        if (forbiddenNodes == null) {
            forbiddenNodes = Collections.emptySet();
        }
    }
}