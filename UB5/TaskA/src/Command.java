@FunctionalInterface
public interface Command {
    String execute(String[] args) throws SystemException;
}
