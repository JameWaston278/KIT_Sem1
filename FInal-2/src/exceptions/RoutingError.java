package exceptions;

/**
 * The RoutingError enum represents different types of routing errors that can
 * occur during the route planning process in the ski resort system. Each error
 * type has an associated message that can be used to provide more information
 * about the error.
 *
 * @author udqch
 */
public enum RoutingError {
    /** Error indicating that the start node for route planning is invalid. */
    INVALID_START_NODE("The start node must be a talstation lift."),
    /** Error indicating that the end node for route planning is invalid. */
    INVALID_TIME_CONSTRAINTS("Start time must be before end time, and both times must be non-null."),
    /** Error indicating that the end node for route planning is invalid. */
    INVALID_END_NODE("The end node must be a ski slope or a destination."),
    /** Error indicating that the graph for route planning is not loaded. */
    GRAPH_NOT_LOADED("No graph loaded. Please load a graph before planning a route."),
    /** Error indicating that no valid route was found for the given constraints. */
    NO_ROUTE_FOUND("No valid route found for the given constraints."),
    /** Error indicating that no active route is available. */
    NO_ACTIVE_ROUTE("No active route. Please plan a route first."),
    /** Error indicating that the user has already reached the destination. */
    NO_PENDING_STEP("No pending step. Place call \'next\' before taking a step."),
    /**
     * Error indicating that an error occurred while calculating the time for the
     * next step.
     */
    ERROR_TIME_CALCULATING("An error occurred while calculating time for the next step."),
    /** Error indicating that no next step is available. */
    NO_NEXT_STEP("No next step available. Place call \'next\' before taking a step.");

    private final String message;

    RoutingError(String message) {
        this.message = message;
    }

    /**
     * Returns the error message associated with this routing error.
     *
     * @return the error message for this routing error
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the error message associated with this parsing error, formatted
     * with the provided arguments.
     * 
     * @param args the arguments to format the error message with
     * @return the formatted error message for this parsing error
     */
    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
