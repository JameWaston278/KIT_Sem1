package exceptions;

/**
 * The FatalSetupException class represents a specific type of exception that is
 * thrown when there is a critical error during the setup phase of the game.
 * This
 * exception indicates that the game cannot proceed due to an unrecoverable
 * issue
 * in the initial configuration or setup process.
 * 
 * @author udqch
 */
public class FatalSetupException extends Exception {
    /**
     * Constructor for the FatalSetupException class, which takes an ErrorMessage
     * object as a parameter. The error message is retrieved from the ErrorMessage
     * object and passed to the superclass constructor to create the exception with
     * a descriptive message.
     *
     * @param message The ErrorMessage object containing the error message to be
     *                associated with this exception.
     */
    public FatalSetupException(ErrorMessage message) {
        super(message.format());
    }
}
