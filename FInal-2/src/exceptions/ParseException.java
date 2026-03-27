package exceptions;

/**
 * Exception thrown when there is an error parsing the input data.
 * This exception is used to indicate issues such as invalid formats,
 * missing fields, or other problems encountered during the parsing process.
 * 
 * @author udqch
 */
public class ParseException extends SkiException {

    private static final String ERROR_PREFIX = "(parsing error) ";

    /**
     * Constructs a new ParseException with the specified error message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public ParseException(String message) {
        super(ERROR_PREFIX + message);
    }

}
