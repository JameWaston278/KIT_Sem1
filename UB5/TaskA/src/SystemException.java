package kit.edu.kastel;

/**
 * Custom exception class for system errors.
 * Used to propagate logic errors (like invalid input or illegal state) to the
 * UI layer.
 * 
 * @author udqch
 */
public class SystemException extends Exception {

    /**
     * Constructs a new SystemException with the specified detail message.
     * 
     * @param message The error message to be displayed to the user.
     */
    public SystemException(String message) {
        super(message);
    }
}