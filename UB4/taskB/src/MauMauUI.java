import java.util.Scanner;

/**
 * Handles user interaction and command dispatching for the Mau-Mau game.
 * Parses inputs from the console and invokes the corresponding game logic.
 *
 * @author udqch
 * @version 1.0
 */
public class MauMauUI {

    /**
     * Functional interface for executing a command logic.
     */
    @FunctionalInterface
    private interface CommandAction {
        void execute(String[] args) throws GameException;
    }

    private final MauMauGame game;

    /**
     * Creates a new UI instance linked to a specific game engine.
     * 
     * @param game The MauMauGame instance.
     */
    public MauMauUI(MauMauGame game) {
        this.game = game;
    }

    /**
     * Starts the main input loop.
     * Reads commands from standard input until the game is terminated.
     */
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (game.isRunning() && scanner.hasNextLine()) {
                String inputCommand = scanner.nextLine();
                try {
                    dispatchCommand(inputCommand);
                } catch (GameException e) {
                    if (e.isError()) {
                        System.out.println("Error, " + e.getMessage());
                    } else {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Parses the raw input string and routes it to the appropriate command handler.
     *
     * @param input The raw input string from the user.
     * @throws GameException If the command is unknown or invalid.
     */
    public void dispatchCommand(String input) throws GameException {
        String[] parts = input.trim().split("\\s+", 2);
        String commandName = parts[0];
        String commandArgs = (parts.length > 1) ? parts[1] : "";

        // Global commands (Always allowed)
        if (commandName.equals("quit")) {
            quit(commandArgs);
            return;
        }
        if (commandName.equals("start")) {
            start(commandArgs);
            return;
        }

        // Game state restricted commands
        if (game.endMatch()) {
            throw new GameException(GameMessage.MATCH_ENDED.format());
        } else {
            switch (commandName) {
                case "show" -> show(commandArgs);
                case "discard" -> discard(commandArgs);
                case "pick" -> pick(commandArgs);
                default -> throw new GameException(GameMessage.UNKNOWN_COMMAND.format());
            }
        }
    }

    // =========================================================================
    // COMMAND HANDLERS
    // =========================================================================

    /**
     * Handles the 'start' command.
     * 
     * @param commandArgs The arguments (expected: seed).
     * @throws GameException If arguments are invalid.
     */
    private void start(String commandArgs) throws GameException {
        processCommand(commandArgs, 1, args -> game.start(convertToInt(args[0])));
    }

    /**
     * Handles the 'quit' command.
     * 
     * @param commandArgs The arguments (expected: none).
     * @throws GameException If arguments are present.
     */
    private void quit(String commandArgs) throws GameException {
        processCommand(commandArgs, 0, args -> game.quit());
    }

    /**
     * Handles the 'show' command.
     * 
     * @param commandArgs The arguments (expected: player ID or "game").
     * @throws GameException If arguments are invalid.
     */
    private void show(String commandArgs) throws GameException {
        processCommand(commandArgs, 1, args -> {
            if (args[0].equals("game")) {
                game.showGame();
            } else {
                game.show(convertToInt(args[0]));
            }
        });
    }

    /**
     * Handles the 'discard' command.
     * 
     * @param commandArgs The arguments (expected: player ID and card name).
     * @throws GameException If arguments are invalid.
     */
    private void discard(String commandArgs) throws GameException {
        processCommand(commandArgs, 2, args -> game.discard(convertToInt(args[0]), args[1]));
    }

    /**
     * Handles the 'pick' command.
     * 
     * @param commandArgs The arguments (expected: player ID).
     * @throws GameException If arguments are invalid.
     */
    private void pick(String commandArgs) throws GameException {
        processCommand(commandArgs, 1, args -> game.pick(convertToInt(args[0])));
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    /**
     * Converts a string input to an integer with validation.
     *
     * @param input The string to convert.
     * @return The integer value.
     * @throws GameException If the input is not a valid integer.
     */
    private int convertToInt(String input) throws GameException {
        if (!input.matches("\\d+")) {
            throw new GameException(GameMessage.REQUIRE_NUMBER.format());
        }
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new GameException(GameMessage.TOO_LARGE_NUMBER.format());
        }
    }

    /**
     * Orchestrates argument parsing and command execution using a lambda action.
     *
     * @param commandArgs  The raw argument string.
     * @param expectedArgs The number of expected arguments.
     * @param action       The logic to execute if parsing succeeds.
     * @throws GameException If parsing fails or execution errors occur.
     */
    private void processCommand(String commandArgs, int expectedArgs, CommandAction action) throws GameException {
        String[] args = parseArguments(commandArgs, expectedArgs);
        action.execute(args);
    }

    /**
     * Splits and validates the command arguments.
     *
     * @param commandArgs   The raw argument string.
     * @param expectedCount The exact number of arguments required.
     * @return An array of cleaned arguments.
     * @throws GameException If the argument count does not match.
     */
    private String[] parseArguments(String commandArgs, int expectedCount) throws GameException {
        if (expectedCount == 0) {
            if (!commandArgs.isEmpty()) {
                throw new GameException(GameMessage.NO_PARAMETER.format());
            }
            return new String[0];
        }

        if (commandArgs.isEmpty()) {
            throw new GameException(GameMessage.INVALID_ARGUMENT_COUNT.format(expectedCount, 0));
        }

        String[] args = commandArgs.trim().split("\\s+");

        if (args.length != expectedCount) {
            throw new GameException(GameMessage.INVALID_ARGUMENT_COUNT.format(expectedCount, args.length));
        }
        return args;
    }
}