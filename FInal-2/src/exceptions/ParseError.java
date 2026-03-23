package exceptions;

/**
 * Enum representing different types of parsing errors that can occur when
 * constructing a ski graph. Each error type has an associated message that
 * can be used to provide more information about the error.
 * 
 * @author udqch
 */
public enum ParseError {

    CANNOT_READ_FILE("Cannot read the input file %s."),
    INVALID_FILE("The input file must not be empty and start with \"graph\"."),
    INVALID_FORMAT("Invalid %s format in %s."),
    UNRECOGNIZED_NODE("Unrecognized node in connection %s and %s."),

    /** Error indicating that a line in the input file is malformed. */
    EXISTING_NODE("Node with ID %s already exists."),
    SELF_LOOP("A node cannot have an edge to itself."),

    NO_PISTE("The graph must contain at least one piste."),
    NO_TALSTATION("The graph must contain at least one talstation."),

    INVALID_CONNECTION("Invalid connection between same node type: %s and %s."),

    EMPTY_GRAPH("The graph cannot be empty."),
    NOT_CONNECTED("The graph must be fully connected.");

    private final String message;

    ParseError(String message) {
        this.message = message;
    }

    /**
     * Returns the error message associated with this parsing error.
     * 
     * @return the error message for this parsing error
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
