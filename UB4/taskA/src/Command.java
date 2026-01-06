import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

// class process raw input and check if it is valid to use
public class Command {
    private String name;
    private List<String> arguments;
    private String errorMessage;
    private static final Map<String, Set<Integer>> ARGS_RULES = Map.ofEntries(Map.entry("add-customer", Set.of(5)),
            Map.entry("add-mailman", Set.of(4)), Map.entry("add-agent", Set.of(4)),
            Map.entry("authenticate", Set.of(2)), Map.entry("logout", Set.of(0)), Map.entry("send-mail", Set.of(2, 3)),
            Map.entry("get-mail", Set.of(0, 1)), Map.entry("list-mail", Set.of(0, 1)),
            Map.entry("list-price", Set.of(0, 1)),
            Map.entry("reset-pin", Set.of(3)), Map.entry("quit", Set.of(0)));

    public Command(String input) {
        String[] parts = input.split(" ");
        this.name = parts[0];
        this.arguments = new ArrayList<>(Arrays.asList(parts[1].split(";")));
    }

    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return new ArrayList<>(arguments);
    }

    public boolean isValid() {
        if (name == null) {
            errorMessage = "this command does not exist.";
            return false;
        }
        Set<Integer> validSizes = ARGS_RULES.get(name);
        if (!validSizes.contains(arguments.size())) {
            errorMessage = "this command hasinvalid parameters.";
            return false;
        }
        return true;
    }
}
