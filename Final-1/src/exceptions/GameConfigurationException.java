package exceptions;

/**
 * The GameConfigurationException class is a custom exception that extends the
 * standard Exception class. It is used to represent errors that occur during
 * the configuration phase of the game, such as invalid settings, missing
 * configuration files, or other issues related to the initial setup of the
 * game.
 * 
 * @author udqch
 */
public class GameConfigurationException extends Exception {

    /**
     * Constructor for the GameConfigurationException class, which takes a custom
     * error message as a parameter.
     * 
     * @param message The error message describing the reason for the exception.
     */
    public GameConfigurationException(String message) {
        super(message);
    }
}
