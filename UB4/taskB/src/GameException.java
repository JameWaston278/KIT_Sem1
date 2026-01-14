/**
 * Represents exceptions specific to the game logic.
 * Can represent both errors (rule violations) and status updates (game over).
 *
 * @author udqch
 * @version 1.0
 */
public class GameException extends Exception {
    
    private final boolean isError;

    /**
     * Constructs a standard error exception.
     * @param message The error message.
     */
    public GameException(String message) {
        super(message);
        this.isError = true;
    }

    /**
     * Constructs an exception with a specific type flag.
     * @param message The message (error or status).
     * @param isError true if this is an error, false if it's a status notification.
     */
    public GameException(String message, boolean isError) {
        super(message);
        this.isError = isError;
    }

    /**
     * Checks if this exception represents an error or a status message.
     * @return true if it is an error.
     */
    public boolean isError() {
        return this.isError;
    }
}