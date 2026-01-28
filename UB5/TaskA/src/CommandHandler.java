package kit.edu.kastel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Handles user input and executes corresponding commands on the application.
 * Parses arguments and dispatches actions to Procrastinot.
 *
 * @author udqch
 */
public final class CommandHandler {
    private static final String SEPARATOR = "\\s+";
    private static final String DECIMAL = "\\d+";

    private final Procrastinot app;
    private final Scanner scanner;
    private final Map<String, Command> commands = new HashMap<>();
    private boolean isRunning = true;

    /**
     * Functional interface for commands taking an integer ID.
     */
    @FunctionalInterface
    private interface ActionInt {
        String apply(int id) throws SystemException;
    }

    /**
     * Functional interface for commands taking a string argument.
     */
    @FunctionalInterface
    private interface ActionStr {
        String apply(String s) throws SystemException;
    }

    /**
     * Constructs a new CommandHandler.
     *
     * @param app     The main application logic.
     * @param scanner The scanner for reading user input.
     */
    public CommandHandler(Procrastinot app, Scanner scanner) {
        this.app = app;
        this.scanner = scanner;
        registerAllCommands();
    }

    /**
     * Registers all available commands and their handlers into the map.
     */
    private void registerAllCommands() {
        commands.put(CommandType.QUIT.getName(), args -> {
            this.isRunning = false;
            return null;
        });

        // Command by ID input
        registerIntCmd(CommandType.TOGGLE.getName(), app::toggle);
        registerIntCmd(CommandType.DELETE.getName(), app::deleteTask);
        registerIntCmd(CommandType.RESTORE.getName(), app::restoreTask);

        // Command by String input
        registerStrCmd(CommandType.ADD_LIST.getName(), app::createList);
        registerStrCmd(CommandType.FIND.getName(), app::find);
        registerStrCmd(CommandType.TAGGED_WITH.getName(), tag -> app.hasTag(InputValidator.validateTag(tag)));

        // Command by date
        registerStrCmd(CommandType.UPCOMING.getName(), dateStr -> {
            LocalDate startDate = InputValidator.parseDate(dateStr);
            LocalDate endDate = startDate.plusDays(6);
            // Logic: Deadline >= startDate && Deadline <= endDate
            return app.searchTime(date -> !date.isBefore(startDate) && !date.isAfter(endDate));
        });
        registerStrCmd(CommandType.BEFORE.getName(), dateStr -> {
            LocalDate startDate = InputValidator.parseDate(dateStr);
            return app.searchTime(date -> !date.isAfter(startDate));
        });

        // Complex commands, need helper methods
        commands.put(CommandType.ADD.getName(), this::handleAdd);
        commands.put(CommandType.TAG.getName(), this::handleTag);
        commands.put(CommandType.ASSIGN.getName(), this::handleAssign);
        commands.put(CommandType.CHANGE_DATE.getName(), this::handleChangeDate);
        commands.put(CommandType.CHANGE_PRIORITY.getName(), this::handleChangePriority);
        commands.put(CommandType.LIST.getName(), this::handleShowList);
        commands.put(CommandType.BETWEEN.getName(), this::handleBetween);

        commands.put(CommandType.SHOW.getName(),
                args -> (args.length == 0) ? app.show() : app.show(InputValidator.parseId(args[0])));
        commands.put(CommandType.TODO.getName(), args -> app.todo());
        commands.put(CommandType.DUPLICATES.getName(), args -> app.duplicates());
    }

    /**
     * Starts the command processing loop.
     * Reads lines from scanner until "quit" is issued.
     *
     * @throws SystemException If a system error occurs.
     */
    public void run() throws SystemException {
        while (this.isRunning && scanner.hasNextLine()) {
            String inputLine = scanner.nextLine();
            handleInput(inputLine);
        }
    }

    /**
     * Processes a single line of input.
     *
     * @param input The raw input string.
     * @throws SystemException If the command is unknown or arguments are invalid.
     */
    public void handleInput(String input) throws SystemException {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] parts = input.trim().split(SEPARATOR);
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command cmd = commands.get(commandName);
        if (cmd != null) {
            try {
                String result = cmd.execute(args);
                if (result != null) {
                    System.out.println(result);
                }
            } catch (SystemException e) {
                System.out.println(SystemMessage.ERROR_PREFIX + e.getMessage());
            }
        } else {
            throw new SystemException(SystemMessage.UNREGISTERED_COMMAND.format());
        }
    }

    /**
     * Checks if the handler is still running.
     * 
     * @return true if running, false if quit.
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    // --- HELPER METHODS ---

    /**
     * Validates the number of arguments.
     *
     * @param args     The arguments array.
     * @param expected The expected minimum number of arguments.
     * @throws SystemException If arguments are fewer than expected.
     */
    private void checkArgLength(String[] args, int expected) throws SystemException {
        if (args.length < expected) {
            throw new SystemException(SystemMessage.INVALID_ARGUMENTS.format(expected));
        }
    }

    /**
     * Helper to register a command that takes an integer ID.
     *
     * @param name   The command name.
     * @param action The action to execute.
     */
    private void registerIntCmd(String name, ActionInt action) {
        commands.put(name, args -> {
            checkArgLength(args, 1);
            return action.apply(InputValidator.parseId(args[0]));
        });
    }

    /**
     * Helper to register a command that takes a string argument.
     *
     * @param name   The command name.
     * @param action The action to execute.
     */
    private void registerStrCmd(String name, ActionStr action) {
        commands.put(name, args -> {
            checkArgLength(args, 1);
            return action.apply(args[0]);
        });
    }

    /**
     * Handles the 'add' command logic including optional parameters.
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If parameters are invalid.
     */
    private String handleAdd(String[] args) throws SystemException {
        if (args.length < 1) {
            throw new SystemException(SystemMessage.INVALID_ARGUMENTS.format());
        }
        String name = InputValidator.validateName(args[0]);
        Priority priority = null;
        LocalDate date = null;

        for (int i = 1; i < args.length; i++) {
            try {
                priority = InputValidator.parsePriority(args[i]);
            } catch (SystemException e1) {
                try {
                    date = InputValidator.parseDate(args[i]);
                } catch (SystemException e2) {
                    throw new SystemException(SystemMessage.INVALID_PARAMETER.format(args[i]));
                }
            }
        }
        return app.addTask(name, priority, date);
    }

    /**
     * Handles the 'tag' command logic (for Task or List).
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If validation fails.
     */
    private String handleTag(String[] args) throws SystemException {
        checkArgLength(args, 2);
        String tag = InputValidator.validateTag(args[1]);
        String id = args[0];

        if (id.matches(DECIMAL)) {
            return app.tagTask(InputValidator.parseId(id), tag);
        } else {
            return app.tagList(InputValidator.validateListName(args[0]), tag);
        }
    }

    /**
     * Handles the 'assign' command logic (for Task or List).
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If validation fails.
     */
    private String handleAssign(String[] args) throws SystemException {
        checkArgLength(args, 2);
        int assignedId = InputValidator.parseId(args[0]);
        String targetId = args[1];
        if (targetId.matches(DECIMAL)) {
            return app.assignSubtask(assignedId, InputValidator.parseId(targetId));
        } else {
            return app.assignToList(assignedId, InputValidator.validateListName(args[1]));
        }
    }

    /**
     * Handles the 'change-date' command logic (for Task or List).
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If validation fails.
     */
    private String handleChangeDate(String[] args) throws SystemException {
        checkArgLength(args, 2);
        return app.changeDeadline(InputValidator.parseId(args[0]), InputValidator.parseDate(args[1]));
    }

    /**
     * Handles the 'change-priority' command logic (for Task or List).
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If validation fails.
     */
    private String handleChangePriority(String[] args) throws SystemException {
        checkArgLength(args, 1);
        Priority priority = (args.length > 1) ? InputValidator.parsePriority(args[1]) : null;
        return app.changePriority(InputValidator.parseId(args[0]), priority);
    }

    /**
     * Handles the 'between' command logic (for Task or List).
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If validation fails.
     */
    private String handleBetween(String[] args) throws SystemException {
        checkArgLength(args, 2);
        LocalDate start = InputValidator.parseDate(args[0]);
        LocalDate end = InputValidator.parseDate(args[1]);
        return app.searchTime(date -> !date.isBefore(start) && !date.isAfter(end));
    }

    /**
     * Handles the 'show' command logic (for List).
     *
     * @param args Command arguments.
     * @return Result string.
     * @throws SystemException If validation fails.
     */
    private String handleShowList(String[] args) throws SystemException {
        checkArgLength(args, 1);
        return app.showList(InputValidator.validateListName(args[0]));
    }
}
