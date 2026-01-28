
/**
 * Functional interface representing an executable command.
 *
 * @author udqch
 */
@FunctionalInterface
public interface Command {
    /**
     * Execute commands with arguments.
     * 
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If meets exception.
     */
    String execute(String[] args) throws SystemException;
}
