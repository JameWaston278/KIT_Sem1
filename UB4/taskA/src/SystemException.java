/**
 * Custom exception class for Post Office application errors.
 * Used to propagate logic errors (like invalid input, unauthorized access) to
 * the UI.
 */
public class SystemException extends Exception {

    /**
     * Constructs a new ErrorException with the specified detail message.
     * 
     * @param message The error message to be displayed to the user.
     */
    public SystemException(String message) {
        super(message);
    }
}