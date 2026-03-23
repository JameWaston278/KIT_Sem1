package exceptions;

/**
 * Exception thrown when an invalid command is encountered in the command-line
 * interface. This exception is used to indicate that the user has entered a
 * command that is not recognized or does not conform to the expected format.
 * 
 * @author udqch
 */
public class InvalidCommandException extends SkiException {
    private static final String ERROR_PREFIX = "invalid command: ";

    /**
     * Constructs a new InvalidCommandException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCommandException(String message) {
        super(ERROR_PREFIX + message);
    }
}
