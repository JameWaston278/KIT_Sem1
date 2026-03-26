package domain.routing;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import domain.graph.Lift;
import domain.graph.Node;
import exceptions.RoutingError;
import exceptions.RoutingException;

/**
 * The RoutePlaner class is responsible for planning the best route for a skier
 * based on their profile, starting node, and time constraints. It uses a
 * depth-first search (DFS) approach to explore all possible routes and utilizes
 * the TimeCalculator to determine the time spent on each node.
 *
 * @author udqch
 */
public class RoutePlanner {
    private final TimeCalculator timeCalculator;

    /**
     * Constructs a new RoutePlaner with the given TimeCalculator.
     *
     * @param timeCalculator the TimeCalculator used to calculate time spent on
     *                       nodes during route planning
     */
    public RoutePlanner(TimeCalculator timeCalculator) {
        this.timeCalculator = timeCalculator;
    }

    /**
     * Plans the best route for a skier based on the provided RouteRequest. It
     * validates the inputs and then initiates a routing session to find the best
     * route.
     *
     * @param request the RouteRequest containing the starting node, time
     *                constraints,
     *                skier profile, and graph information
     * @return an Optional containing the best Route if found, or an empty Optional
     *         if no valid route exists
     * @throws RoutingException if the input validation fails or if any errors occur
     *                          during route planning
     */
    public Optional<Route> planRoute(RouteRequest request) throws RoutingException {
        validateInputs(request.startNode(), request.startTime(), request.endTime());

        RoutingSession session = new RoutingSession(request);
        return session.findRoute(request.startNode(), request.startTime());
    }

    private void validateInputs(Node startNode, LocalTime startTime, LocalTime endTime) throws RoutingException {
        if (!(startNode instanceof Lift) || !((Lift) startNode).isTalstation()) {
            throw new RoutingException(RoutingError.INVALID_START_NODE.getMessage());
        }
        if (startTime == null || endTime == null || endTime.isBefore(startTime)) {
            throw new RoutingException(RoutingError.INVALID_TIME_CONSTRAINTS.getMessage());
        }
    }

    // Inner class to manage the state of the routing session, including the current
    // best route and the comparator for route evaluation
    private class RoutingSession {
        private final RouteRequest request;
        private final RouteComparator comparator;
        private Route bestRoute = null;

        RoutingSession(RouteRequest request) {
            this.request = request;
            this.comparator = new RouteComparator(request.skier());
        }

        Optional<Route> findRoute(Node startNode, LocalTime startTime) {
            List<Node> currentPath = new ArrayList<>();
            currentPath.add(startNode);
            dfs(startNode, startTime, currentPath);
            return Optional.ofNullable(bestRoute);
        }

        private void dfs(Node currentNode, LocalTime currentTime, List<Node> currentPath) {
            // Check if the current node is a goal node (talstation lift) and if the current
            // path is valid
            if (currentNode.equals(request.endNode()) && currentPath.size() > 1) {
                updateBestRoute(currentPath, currentTime);
            }

            for (Node neighbor : request.graph().getAdjacencyNodes(currentNode)) {
                if (request.forbiddenNodes().contains(neighbor)) {
                    continue; // Skip forbidden nodes
                }

                // Calculate the time spent on the neighbor node based on the skier's profile
                // and the current time
                Optional<Long> timeSpent = timeCalculator.calculateTimeSpent(neighbor, currentTime, request.skier());

                if (timeSpent.isPresent()) {
                    LocalTime nextTime = currentTime.plusSeconds(timeSpent.get());

                    // Only continue DFS if the next time is before the end time
                    if (!nextTime.isAfter(request.endTime())) {
                        currentPath.add(neighbor);
                        dfs(neighbor, nextTime, currentPath);
                        currentPath.remove(currentPath.size() - 1);
                    }
                }
            }
        }

        // Helper method to update the best route if the current route is better than
        // the best route found so far
        private void updateBestRoute(List<Node> currentPath, LocalTime currentTime) {
            int currentScore = request.skier().getGoal().calculateUtility(currentPath);
            long duration = Duration.between(request.startTime(), currentTime).getSeconds();

            if (this.bestRoute != null && currentScore < this.bestRoute.getScore()) {
                return; // Current route is worse than the best route, no need to compare further
            }

            Route potentialRoute = new Route(
                    new ArrayList<>(currentPath), request.startTime(), duration, currentScore);
            if (this.bestRoute == null || comparator.compare(potentialRoute, this.bestRoute) > 0) {
                this.bestRoute = potentialRoute; // Update best route if the potential route is better
            }
        }
    }
}
