package exceptions;

/****
 * Enum representing different types of command-line interface (CLI) errors that
 * can occur when processing user commands. Each error type has an associated
 * message that can be used to provide more information about the error.
 *
 * @author udqch
 */
public enum CommandError {

    /** Error indicating that a required argument for a command is missing. */
    UNKNOWN_COMMAND("Unknown command: %s."),
    /**
     * Error indicating that the number of arguments provided for a command is
     * invalid.
     */
    INVALID_NUMBER_ARGS("Invalid number of arguments for command %s. Expected %d but got %d."),
    /** Error indicating that an argument provided for a command is invalid. */
    INVALID_ARGUMENT("Invalid argument for command %s: %s."),
    /**
     * Error indicating that a time argument provided for a command is in an invalid
     * format.
     */
    INVALID_TIME_FORMAT("Invalid time format. Expected format is HH:mm.");

    private final String message;

    CommandError(String message) {
        this.message = message;
    }

    /**
     * Returns the error message associated with this CLI error.
     * 
     * @return the error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the error message associated with this CLI error, formatted with the
     * provided arguments.
     * 
     * @param args the arguments to format the error message with
     * @return the formatted error message
     */
    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
