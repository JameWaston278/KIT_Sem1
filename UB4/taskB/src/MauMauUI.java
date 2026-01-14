import java.util.Scanner;

public class MauMauUI {
    /**
     * Functional interface for executing a command after parsing arguments.
     * This helps reduce code duplication in command handlers.
     */
    @FunctionalInterface
    private interface CommandAction {
        /**
         * Executes a specific command logic.
         *
         * @param args The parsed arguments array.
         * @throws ErrorException If the command execution fails.
         */
        void execute(String[] args) throws ErrorException;
    }

    private final MauMauGame game;

    public MauMauUI(MauMauGame game) {
        this.game = game;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (game.isRunning() && scanner.hasNextLine()) {
                String inputCommmand = scanner.nextLine();
                try {
                    dispatchCommand(inputCommmand);
                } catch (ErrorException e) {
                    if (e.isError()) {
                        System.out.println("Error, " + e.getMessage());
                    } else {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    public void dispatchCommand(String input) throws ErrorException {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];
        String commandArgs = (parts.length > 1) ? parts[1] : "";

        if (game.endRound()) {
            switch (commandName) {
                case "start" -> start(commandArgs);
                case "quit" -> quit(commandArgs);
                default -> throw new ErrorException(GameMessage.UNKNOWN_COMMAND.format());
            }

        } else {
            switch (commandName) {
                case "show" -> show(commandArgs);
                case "discard" -> discard(commandArgs);
                case "pick" -> pick(commandArgs);
                case "quit" -> quit(commandArgs);
                default -> throw new ErrorException(GameMessage.UNKNOWN_COMMAND.format());
            }
        }

    }

    private void start(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 1, args -> game.start(Integer.parseInt(args[0])));
    }

    private void quit(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 0, args -> game.quit());
    }

    private void show(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 1, args -> {
            if (args[0].equals("game")) {
                game.showGame();
            } else {
                game.show(convertToInt(args[0]));
            }
        });
    }

    private void discard(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 2, args -> game.discard(convertToInt(args[0]), args[1]));
    }

    private void pick(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 1, args -> game.pick(convertToInt(args[0])));
    }

    private int convertToInt(String input) throws ErrorException {
        if (!input.matches("\\d+")) {
            throw new ErrorException(GameMessage.REQUIRE_INTEGER.format());
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new ErrorException(GameMessage.REQUIRE_INTEGER.format());
        }
    }

    /**
     * Helper to parse arguments and execute an action.
     *
     * @param commandArgs  The string containing parameters separated by semicolons.
     * @param expectedArgs The expected number of parameters.
     * @param action       The lambda function containing the logic to execute.
     * @throws ErrorException If argument parsing or execution fails.
     */
    private void processCommand(String commandArgs, int expectedArgs, CommandAction action) throws ErrorException {
        String[] args = parseArguments(commandArgs, expectedArgs);
        action.execute(args);
    }

    /**
     * Parses the parameter string into an array, checking for validity.
     *
     * @param commandArgs   Raw parameter string (e.g., "Arg1; Arg2").
     * @param expectedCount How many arguments are expected.
     * @return An array of trimmed argument strings.
     * @throws ErrorException If format is invalid or count is wrong.
     */
    private String[] parseArguments(String commandArgs, int expectedCount) throws ErrorException {
        // Case: No arguments expected (e.g., logout, quit)
        if (expectedCount == 0) {
            if (!commandArgs.isEmpty()) {
                throw new ErrorException("this command accepts no parameters.");
            } else {
                return new String[0];
            }
        }

        // Case: Arguments expected but input is empty
        if (commandArgs.isEmpty()) {
            throw new ErrorException("invalid number of arguments. Expected " + expectedCount + " but got 0");
        }

        // Logic: Split by semicolon, preserve empty strings (limit = -1) to detect
        // errors like "a;;"
        String[] args = commandArgs.trim().split("\\s+");

        // Logic: Trim whitespace around parameters (e.g., " Max " -> "Max")
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }

        if (args.length != expectedCount) {
            throw new ErrorException(
                    "invalid number of arguments. Expected " + expectedCount + " but got " + args.length);
        }
        return args;
    }
}
