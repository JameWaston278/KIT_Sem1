public class ErrorException extends Exception {
    private final boolean isError;

    /**
     * Constructs a new ErrorException with the specified detail message.
     * 
     * @param message The error message to be displayed to the user.
     */
    public ErrorException(String message) {
        super(message);
        this.isError = true;
    }

    public ErrorException(String message, boolean isError) {
        super(message);
        this.isError = false;
    }

    public boolean isError() {
        return this.isError;
    }
}
