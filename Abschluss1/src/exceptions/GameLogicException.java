package exceptions;

public class GameLogicException extends Exception {
    public GameLogicException(String message) {
        super(message);
    }

    public GameLogicException(ErrorMessage message, Object... args) {
        super(message.format(args));
    }
}
