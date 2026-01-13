public class ErrorException extends Exception {

    /**
     * Constructs a new ErrorException with the specified detail message.
     * 
     * @param message The error message to be displayed to the user.
     */
    public ErrorException(String message) {
        super(message);
    }
}