public class GameException extends Exception {
    private final boolean isError;

    /**
     * Constructs a new ErrorException with the specified detail message.
     * 
     * @param message The error message to be displayed to the user.
     */
    public GameException(String message) {
        super(message);
        this.isError = true;
    }

    public GameException(String message, boolean isError) {
        super(message);
        this.isError = isError;
    }

    public boolean isError() {
        return this.isError;
    }
}
