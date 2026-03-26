package core;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import domain.graph.Difficulty;
import domain.graph.Node;
import domain.graph.SkiGraph;
import domain.graph.Surface;
import domain.routing.Route;
import domain.routing.RoutePlanner;
import domain.routing.RouteRequest;
import domain.routing.TimeCalculator;
import domain.skier.Goal;
import domain.skier.Preference;
import domain.skier.SkierProfile;
import domain.skier.Skill;
import exceptions.RoutingError;
import exceptions.RoutingException;

/**
 * The SkiEngine class is responsible for managing the state of the ski resort
 * system, including the ski graph, skier profile, route planning, and current
 * route information.
 * It provides methods to initialize the system, plan routes, and update the
 * current route based on user interactions and time constraints.
 *
 * @author udqch
 */
public class SkiEngine {
    private SkiGraph graph;
    private final SkierProfile skier;
    private final RoutePlanner planner;

    private LocalTime sessionEndTime;
    private Route currentRoute;
    private int currentStepIndex;
    private LocalTime currentTime;
    private Node pendingNode;
    private LocalTime pendingTime;

    /**
     * Constructs a new SkiEngine instance with an empty skier profile and a route
     * planner. The ski graph will be initialized separately using the
     * initializeGraph method.
     */
    public SkiEngine() {
        this.skier = new SkierProfile();
        this.planner = new RoutePlanner(new TimeCalculator());
    }

    /**
     * Sets the skill level of the skier.
     *
     * @param skill the skill level to set
     * @throws RoutingException if an error occurs while setting the skill level
     */
    public void setSkill(Skill skill) throws RoutingException {
        this.skier.setSkill(skill);
        replanDynamic();
    }

    /**
     * Sets the goal of the skier.
     *
     * @param goal the goal to set
     * @throws RoutingException if an error occurs while setting the goal
     */
    public void setGoal(Goal goal) throws RoutingException {
        this.skier.setGoal(goal);
        replanDynamic();
    }

    /**
     * Sets the preference for a specific difficulty level.
     *
     * @param difficulty the difficulty level to set the preference for
     * @param preference the preference to set
     * @throws RoutingException if an error occurs while setting the preference
     */
    public void setPreference(Difficulty difficulty, Preference preference) throws RoutingException {
        this.skier.setDifficultyPreference(difficulty, preference);
        replanDynamic();
    }

    /**
     * Sets the preference for a specific surface type.
     *
     * @param surface    the surface type to set the preference for
     * @param preference the preference to set
     * @throws RoutingException if an error occurs while setting the preference
     */
    public void setPreference(Surface surface, Preference preference) throws RoutingException {
        this.skier.setSurfacePreference(surface, preference);
        replanDynamic();
    }

    /**
     * Resets all preferences of the skier to their default values.
     *
     * @throws RoutingException if an error occurs while resetting the preferences
     */
    public void resetPreferences() throws RoutingException {
        this.skier.resetPreferences();
        replanDynamic();

    }

    /**
     * Plans a route for the skier based on the provided start node, start time,
     * and end time. The method uses the route planner to find a suitable route
     * that meets the skier's preferences and constraints.
     *
     * @param startNodeId the ID of the starting node for the route
     * @param startTime   the starting time for the route
     * @param endTime     the ending time for the route
     * @throws RoutingException if an error occurs during route planning, such as
     *                          invalid input or no valid route found
     */
    public void planRoute(String startNodeId, LocalTime startTime, LocalTime endTime) throws RoutingException {
        requireActiveRoute();
        Node startNode = graph.getNodeById(startNodeId);
        if (startNode == null) {
            throw new RoutingException(RoutingError.INVALID_START_NODE.getMessage());
        }

        // Create a route request and use the planner to find a route
        RouteRequest request = new RouteRequest(graph, skier, startNode, startNode, startTime, endTime);
        Optional<Route> foundRoute = planner.planRoute(request);
        if (foundRoute.isEmpty()) {
            throw new RoutingException(RoutingError.NO_ROUTE_FOUND.getMessage());
        }

        // If a route is found, set it as the current route and initialize the state
        this.sessionEndTime = endTime;
        this.currentRoute = foundRoute.get();
        this.currentStepIndex = 0;
        this.currentTime = startTime;
        this.pendingNode = null;
        this.pendingTime = null;
    }

    /**
     * Resets the ski engine to its initial state.
     */
    public void resetEngine() {
        this.currentRoute = null;
        this.currentStepIndex = 0;
        this.currentTime = null;
        this.pendingNode = null;
        this.pendingTime = null;
    }

    /**
     * Shows the next step in the route.
     *
     * @return the next node in the route, or null if the route is complete
     * @throws RoutingException if an error occurs while showing the next step
     */
    public String showNextStep() throws RoutingException {
        requireActiveRoute();
        List<Node> route = currentRoute.getRoute();
        if (currentStepIndex >= route.size() - 1) {
            return null; // No more steps, route is complete
        }

        this.pendingNode = route.get(currentStepIndex);

        TimeCalculator timeCalculator = new TimeCalculator();
        long timeSpent = timeCalculator.calculateTimeSpent(pendingNode, currentTime, skier)
                .orElseThrow(() -> new RoutingException(RoutingError.ERROR_TIME_CALCULATING.getMessage()));
        this.pendingTime = currentTime.plusSeconds(timeSpent);

        return pendingNode.getId();
    }

    /**
     * Takes the next step in the route, updating the current time and step index.
     *
     * @throws RoutingException if an error occurs while taking the next step, such
     *                          as no pending step or no active route
     */
    public void takeNextStep() throws RoutingException {
        requireActiveRoute();
        if (pendingNode == null) {
            throw new RoutingException(RoutingError.NO_NEXT_STEP.getMessage());
        }

        this.currentStepIndex++;
        this.currentTime = this.pendingTime;
        this.pendingNode = null;
        this.pendingTime = null;
    }

    /**
     * Plans an alternative route that avoids the pending node. This method is
     * called when the skier wants to avoid the next step in the current route.
     *
     * @return the ID of the node that was avoided
     * @throws RoutingException if an error occurs while planning the alternative
     *                          route, such as no pending step or no active route
     */
    public String findAlternativeRoute() throws RoutingException {
        requireActiveRoute();
        if (pendingNode == null) {
            throw new RoutingException(RoutingError.NO_PENDING_STEP.getMessage());
        }
        if (currentStepIndex == 0) {
            throw new RoutingException(RoutingError.NO_NEXT_STEP.getMessage());
        }

        // Prepare the route request for the alternative route, treating the current
        // node as forbidden
        Node currentNode = currentRoute.getRoute().get(currentStepIndex - 1);
        Node destinationNode = currentRoute.getRoute().get(currentRoute.getRoute().size() - 1);

        Set<Node> forbiddenNodes = new HashSet<>();
        forbiddenNodes.add(pendingNode);

        RouteRequest request = new RouteRequest(
                this.graph, this.skier, currentNode, destinationNode, currentTime, sessionEndTime, forbiddenNodes);
        Optional<Route> alternativePath = planner.planRoute(request);

        if (alternativePath.isEmpty()) {
            return null; // No alternative route found, keep current route
        }

        String avoidedId = pendingNode.getId();
        stitchRoutes(alternativePath.get());
        return avoidedId;
    }

    /**
     * Shows the remaining route from the current step to the destination.
     *
     * @return a string representation of the remaining route, with node IDs
     *         separated by spaces
     * @throws RoutingException if an error occurs while showing the route, such as
     *                          no active route
     */
    public String showCurrentRoute() throws RoutingException {
        requireActiveRoute();

        StringBuilder result = new StringBuilder();
        List<Node> path = currentRoute.getRoute();
        for (int i = currentStepIndex; i < path.size(); i++) {
            result.append(path.get(i).getId());
            if (i < path.size() - 1) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    // --- HELPER METHODS ---

    private void requireActiveRoute() throws RoutingException {
        if (currentRoute == null) {
            throw new RoutingException(RoutingError.NO_ACTIVE_ROUTE.getMessage());
        }
    }

    private void replanDynamic() throws RoutingException {
        requireActiveRoute();

        Node currentNode = currentRoute.getRoute().get(Math.max(0, currentStepIndex - 1));
        Node destinationNode = currentRoute.getRoute().get(currentRoute.getRoute().size() - 1);

        RouteRequest request = new RouteRequest(
                this.graph, this.skier, currentNode, destinationNode, currentTime, sessionEndTime,
                Collections.emptySet());
        Optional<Route> newRouteOpt = planner.planRoute(request);

        if (newRouteOpt.isPresent()) {
            stitchRoutes(newRouteOpt.get());
        } else {
            this.currentRoute = null; // No valid route found, reset current route
            throw new RoutingException(RoutingError.NO_ROUTE_WITH_PREFERENCES.getMessage());
        }
    }

    // This method stitches a new path with the past path up to the current step
    private void stitchRoutes(Route newRoute) {
        // Stitch the new route with the past path up to the current step
        List<Node> pastPath = new ArrayList<>(
                currentRoute.getRoute().subList(0, Math.max(0, currentStepIndex - 1)));
        List<Node> stitchedPath = new ArrayList<>(pastPath);
        stitchedPath.addAll(newRoute.getRoute());

        // Recalculate the total duration and score for the new stitched route
        long pastDuration = Duration.between(currentRoute.getStartTime(), currentTime).getSeconds();
        long totalDuration = pastDuration + newRoute.getDuration();

        // Calculate the new score based on the skier's goal and the stitched path
        int newScore = skier.getGoal().calculateUtility(stitchedPath);

        // Update the current route with the new stitched path, start time, total
        // duration, and score
        this.currentRoute = new Route(stitchedPath, currentRoute.getStartTime(), totalDuration, newScore);

        // Reset pending state
        this.pendingNode = null;
        this.pendingTime = null;
    }

    // --- GETTERS ---

    /**
     * Returns the ski graph.
     *
     * @return the ski graph
     */
    public SkiGraph getGraph() {
        return graph;
    }

    /**
     * Returns the skier profile.
     *
     * @return the skier profile
     */
    public SkierProfile getSkier() {
        return skier;
    }

    // --- SETTERS ---
    /**
     * Sets the ski graph for the engine.
     *
     * @param graph the ski graph to set
     */
    public void setGraph(SkiGraph graph) {
        this.graph = graph;
    }
}
