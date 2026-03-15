package exceptions;

/**
 * The FatalSetupException class represents a specific type of exception that is
 * thrown when there is a critical error during the setup phase of the game.
 * This exception indicates that the game cannot proceed due to an unrecoverable
 * issue in the initial configuration or setup process.
 * 
 * @author udqch
 */
public class FatalSetupException extends Exception {
    /**
     * Constructor for the FatalSetupException class, which takes an ErrorMessage
     * object as a parameter. This constructor allows for the creation of a
     * FatalSetupException with a specific error message that can be used to provide
     * detailed information about the nature of the setup failure.
     *
     * @param message The string representing the specific error that
     *                occurred during setup.
     */
    public FatalSetupException(String message) {
        super(message);
    }
}
