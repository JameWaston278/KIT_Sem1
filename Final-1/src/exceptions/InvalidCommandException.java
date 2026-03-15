package exceptions;

/**
 * The InvalidCommandException class is a custom exception that extends the
 * standard Exception class. It is used to represent errors that occur when a
 * user inputs an invalid command in the command-line interface (CLI) of the
 * game. This exception can be thrown with a specific error message to provide
 * feedback to the user about the nature of the invalid command.
 * 
 * @author udqch
 */
public class InvalidCommandException extends Exception {

    /**
     * Default constructor for the InvalidCommandException class.
     */
    public InvalidCommandException() {
    }

    /**
     * Constructor for the InvalidCommandException class, which takes a custom
     * error message as a parameter.
     * 
     * @param message The error message describing the reason for the exception.
     */
    public InvalidCommandException(String message) {
        super(message);
    }
}