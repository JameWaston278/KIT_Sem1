package exceptions;

/**
 * The RoutingException class represents exceptions that occur during the
 * routing process in the ski resort system. It extends the SkiException class
 * and is used to indicate errors related to route planning, time calculations,
 * or other routing-related issues.
 *
 * @author udqch
 */
public class RoutingException extends SkiException {

    private static final String ERROR_PREFIX = "(routing error) ";

    /**
     * Constructs a new RoutingException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public RoutingException(String message) {
        super(ERROR_PREFIX + message);
    }

}
