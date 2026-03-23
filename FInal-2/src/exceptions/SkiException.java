package exceptions;

/**
 * Exception thrown when there is an error related to skiing operations.
 * This exception can be used to indicate various issues such as invalid
 * ski slope configurations, calculation errors, or other problems encountered
 * during skiing-related processes.
 * 
 * @author udqch
 */
public class SkiException extends Exception {

    private static final String ERROR_PREFIX = "Error, ";

    /**
     * Constructs a new SkiException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public SkiException(String message) {
        super(ERROR_PREFIX + message);
    }

}
