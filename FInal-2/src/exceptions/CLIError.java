package exceptions;

/****
 * Enum representing different types of command-line interface (CLI) errors that
 * can occur when processing user commands. Each error type has an associated
 * message that can be used to provide more information about the error.
 *
 * @author udqch
 */
public enum CLIError {
    INVALID_ARGS("Invalid number of arguments for command %s. Expected %d but got %d."),
    UNKNOWN_COMMAND("Unknown command: %s."),

    private final String message;

    CLIError(String message) {
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
