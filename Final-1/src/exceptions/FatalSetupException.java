package exceptions;

public class FatalSetupException extends Exception {
    public FatalSetupException(ErrorMessage message) {
        super(message.get());
    }
}
