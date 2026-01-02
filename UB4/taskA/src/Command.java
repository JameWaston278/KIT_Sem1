import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// class process raw input and check if it is valid to use
public class Command {
    private String name;
    private String rawInput;
    private List<String> arguments;
    private static final Map<String, Integer> EXPECTED_COMMAND = Map.ofEntries(Map.entry("add-customer", 5),
            Map.entry("add-mailman", 2), Map.entry("add-agent", 2), Map.entry("authenticate", 2),
            Map.entry("logout", 0), Map.entry("send-mail", 3), Map.entry("get-mail", 1),
            Map.entry("list-mail", 1), Map.entry("list-price", 1), Map.entry("reset-pin", 2), Map.entry("quit", 0));

    public Command(String[] args) {
        this.name = args[0];
        this.rawInput = args[1];
        this.arguments = new ArrayList<>(Arrays.asList(rawInput.split(";")));
    }

    public boolean isValid() {
        if (name == null) {
            return false;
        }
        if (!EXPECTED_COMMAND.containsKey(name)) {
            return false;
        } else {
            if (arguments.size() != EXPECTED_COMMAND.get(name)) {
                return false;
            }
        }
        return true;
    }
}
