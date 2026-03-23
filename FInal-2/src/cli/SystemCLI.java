package cli;

import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

import exceptions.CLIError;
import exceptions.InvalidCommandException;
import exceptions.SkiException;

/****
 * The SystemCLI class is responsible for handling the command-line interface
 * (CLI)
 * of the ski resort system. It processes user commands, validates input, and
 * executes the corresponding actions based on the defined commands. The CLI
 * supports various commands such as loading a graph, listing nodes, setting
 * preferences, and planning routes.
 *
 * @author udqch
 */
public class SystemCLI {
    private static final String REGEX_WHITESPACE = "\\s+";
    private final Scanner scanner;

    private boolean isRunning;

    @FunctionalInterface
    private interface CommandHandler {
        void execute(String[] parts) throws SkiException;
    }

    private final Map<Command, CommandHandler> commandHandlers = new EnumMap<>(Command.class);

    /**
     * Constructs a new SystemCLI instance and initializes the command handlers.
     *
     * @throws InvalidCommandException if there is an error initializing the command
     *                                 handlers
     */
    public SystemCLI() throws InvalidCommandException {
        this.scanner = new Scanner(System.in);

        this.isRunning = true;

        // Initialize command handlers
        commandHandlers.put(Command.QUIT, parts -> {
            requireArgs(parts, 1);
            this.isRunning = false;
        });
        commandHandlers.put(Command.LOAD, this::handleLoad);
        commandHandlers.put(Command.LIST, this::handleList);
        commandHandlers.put(Command.SET, this::handleSet);
        commandHandlers.put(Command.LIKE, this::handleLike);
        commandHandlers.put(Command.DISLIKE, this::handleDislike);
        commandHandlers.put(Command.PLAN, this::handlePlan);
        commandHandlers.put(Command.ABORT, this::handleAbort);
        commandHandlers.put(Command.NEXT, this::handleNext);
        commandHandlers.put(Command.TAKE, this::handleTake);
        commandHandlers.put(Command.ALTERNATIVE, this::handleAlternative);
        commandHandlers.put(Command.SHOW, this::handleShow);
    }

    /**
     * Starts the command-line interface and processes user input until the user
     * decides to quit.
     *
     * @throws SkiException if there is an error processing a command
     */
    public void start() throws SkiException {
        while (isRunning) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            processCommand(input);
        }
    }

    private void processCommand(String input) throws SkiException {
        String[] parts = input.split(REGEX_WHITESPACE);
        Command command = Command.fromString(parts[0]);
        try {
            if (command == null) {
                throw new InvalidCommandException(CLIError.UNKNOWN_COMMAND.getMessage(parts[0]));
            }

            CommandHandler handler = commandHandlers.get(command);
            if (handler != null) {
                handler.execute(parts);
            }
        } catch (SkiException e) {
            System.out.println(e.getMessage());
        }
    }

    private void requireArgs(String[] parts, int expected) throws InvalidCommandException {
        if (parts.length != expected) {
            throw new InvalidCommandException(
                    CLIError.INVALID_ARGS.getMessage(parts[0], expected - 1, parts.length - 1));
        }
    }
}
