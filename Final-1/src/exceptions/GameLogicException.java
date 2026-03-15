package exceptions;

import message.ErrorMessage;

/**
 * The GameLogicException class is a custom exception that extends the standard
 * Exception class. It is used to represent errors that occur during the game
 * logic, such as invalid moves, incorrect game state transitions, or other
 * issues related to the rules and mechanics of the game. This exception can be
 * thrown with a specific error message or an ErrorMessage enum value for more
 * structured error handling.
 * 
 * @author udqch
 */
public class GameLogicException extends Exception {
    /**
     * Constructor for the GameLogicException class, which takes a custom error
     * message as a parameter.
     * 
     * @param message The error message describing the reason for the exception.
     */
    public GameLogicException(String message) {
        super(message);
    }

    /**
     * Constructor for the GameLogicException class, which takes an ErrorMessage
     * enum value and optional arguments to format the message.
     * 
     * @param message The ErrorMessage enum value representing the error type.
     * @param args    Optional arguments to format the error message.
     */
    public GameLogicException(ErrorMessage message, Object... args) {
        super(message.format(args));
    }
}
