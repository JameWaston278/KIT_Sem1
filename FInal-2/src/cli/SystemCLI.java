package cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import core.SkiEngine;
import domain.graph.Difficulty;
import domain.graph.SkiGraph;
import domain.graph.Surface;
import domain.skier.Goal;
import domain.skier.Preference;
import domain.skier.Skill;
import exceptions.CommandError;
import exceptions.CommandException;
import exceptions.ParseException;
import exceptions.SkiException;
import io.MermaidParser;
import utils.EnumParser;
import utils.GraphFormatter;

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

    private static final String MSG_ROUTE_PLANNED = "route planned";
    private static final String MSG_ROUTE_ABORTED = "route aborted";
    private static final String MSG_ROUTE_FINISHED = "route finished!";
    private static final String MSG_NO_ALTERNATIVE = "no alternative found";
    private static final String MSG_AVOIDED = "avoided %s";
    private static final String MSG_FAILED_LOAD_GRAPH = "failed to load graph from file: %s";

    private enum LoadTarget {
        /** The target for loading an area */
        area
    }

    private enum ListTarget {
        /** The target for listing lifts */
        lifts,
        /** The target for listing slopes */
        slopes
    }

    private enum SetTarget {
        /** The target for setting skill level */
        skill,
        /** The target for setting goal */
        goal
    }

    private enum PreferenceTarget {
        /** The target for resetting preferences */
        preferences
    }

    private enum ShowTarget {
        /** The target for showing the current route */
        route
    }

    private final SkiEngine engine;
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
     * @throws CommandException if there is an error initializing the command
     *                          handlers
     */
    public SystemCLI() throws CommandException {
        this.engine = new SkiEngine();
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
        commandHandlers.put(Command.LIKE, parts -> this.handlePreference(parts, Preference.LIKE));
        commandHandlers.put(Command.DISLIKE, parts -> this.handlePreference(parts, Preference.DISLIKE));
        commandHandlers.put(Command.RESET, this::handleReset);
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
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                processCommand(input);
            } catch (SkiException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void processCommand(String input) throws SkiException {
        String[] parts = input.split(REGEX_WHITESPACE);

        Command command = Command.fromString(parts[0])
                .orElseThrow(() -> new CommandException(CommandError.UNKNOWN_COMMAND.getMessage(parts[0])));
        if (command != Command.TAKE && command != Command.ALTERNATIVE && command != Command.NEXT) {
            engine.resetPendingState();
        }

        CommandHandler handler = commandHandlers.get(command);
        if (handler != null) {
            handler.execute(parts);
        }
    }

    private void handleLoad(String[] parts) throws SkiException {
        // Expected format: load area <file_path>
        requireArgs(parts, 3);
        LoadTarget target = parseEnumArgs(parts[0], parts[1], LoadTarget.class);

        if (target == LoadTarget.area) {
            String filePath = parts[2];
            try {
                List<String> rawContents = Files.readAllLines(Path.of(filePath));

                MermaidParser parser = new MermaidParser();
                SkiGraph graph = parser.parse(rawContents);
                engine.setGraph(graph);
            } catch (IOException e) {
                throw new ParseException(MSG_FAILED_LOAD_GRAPH.formatted(e.getMessage()));
            }
        }
    }

    private void handleList(String[] parts) throws SkiException {
        // Expected format: list lifts|slopes
        requireArgs(parts, 2);
        ListTarget target = parseEnumArgs(parts[0], parts[1], ListTarget.class);

        String output = (target == ListTarget.lifts)
                ? GraphFormatter.listLifts(engine.getGraph())
                : GraphFormatter.listPistes(engine.getGraph());

        if (!output.isEmpty()) {
            System.out.println(output);
        }
    }

    private void handleSet(String[] parts) throws SkiException {
        // Expected format: set skill|goal <value>
        requireArgs(parts, 3);
        SetTarget target = parseEnumArgs(parts[0], parts[1], SetTarget.class);

        if (target == SetTarget.skill) {
            Skill skill = parseEnumArgs(parts[0], parts[2], Skill.class);
            engine.setSkill(skill);
        } else {
            Goal goal = parseEnumArgs(parts[0], parts[2], Goal.class);
            engine.setGoal(goal);
        }
    }

    private void handlePreference(String[] parts, Preference preference) throws SkiException {
        // Expected format: like|dislike <difficulty>|<surface>
        requireArgs(parts, 2);

        // First try parsing as Difficulty
        Optional<Difficulty> difficultyOpt = EnumParser.parseEnum(Difficulty.class, parts[1]);
        if (difficultyOpt.isPresent()) {
            engine.setPreference(difficultyOpt.get(), preference);
            return;
        }
        // Then try parsing as Surface
        Optional<Surface> surfaceOpt = EnumParser.parseEnum(Surface.class, parts[1]);
        if (surfaceOpt.isPresent()) {
            engine.setPreference(surfaceOpt.get(), preference);
            return;
        }

        // If neither parsing succeeded, throw an error
        throw new CommandException(CommandError.INVALID_ARGUMENT.getMessage(parts[0], parts[1]));
    }

    private void handleReset(String[] parts) throws SkiException {
        // Expected format: reset preferences
        requireArgs(parts, 2);
        PreferenceTarget target = parseEnumArgs(parts[0], parts[1], PreferenceTarget.class);

        if (target == PreferenceTarget.preferences) {
            engine.resetPreferences();
        }
    }

    private void handlePlan(String[] parts) throws SkiException {
        // Expected format: plan <id> <startTime> <endTime>
        requireArgs(parts, 4);

        String startNodeId = parts[1];
        try {
            LocalTime startTime = LocalTime.parse(parts[2]);
            LocalTime endTime = LocalTime.parse(parts[3]);

            engine.planRoute(startNodeId, startTime, endTime);
            System.out.println(MSG_ROUTE_PLANNED);
        } catch (DateTimeParseException e) {
            throw new CommandException(CommandError.INVALID_TIME_FORMAT.getMessage());
        }
    }

    private void handleAbort(String[] parts) throws SkiException {
        // Expected format: abort
        requireArgs(parts, 1);
        engine.resetEngine();
        System.out.println(MSG_ROUTE_ABORTED);
    }

    private void handleNext(String[] parts) throws SkiException {
        // Expected format: next
        requireArgs(parts, 1);
        String nextStepId = engine.showNextStep();
        if (nextStepId != null) {
            System.out.println(nextStepId);
        } else {
            System.out.println(MSG_ROUTE_FINISHED);
        }
    }

    private void handleTake(String[] parts) throws SkiException {
        // Expected format: take
        requireArgs(parts, 1);
        engine.takeNextStep();
    }

    private void handleAlternative(String[] parts) throws SkiException {
        // Expected format: alternative
        requireArgs(parts, 1);
        String avoidedId = engine.findAlternativeRoute();
        if (avoidedId != null) {
            System.out.println(MSG_AVOIDED.formatted(avoidedId));
        } else {
            System.out.println(MSG_NO_ALTERNATIVE);
        }
    }

    private void handleShow(String[] parts) throws SkiException {
        // Expected format: show preferences
        requireArgs(parts, 2);
        ShowTarget target = parseEnumArgs(parts[0], parts[1], ShowTarget.class);

        if (target == ShowTarget.route) {
            System.out.println(engine.showCurrentRoute());
        }
    }

    // --- HELPER METHODS ---

    // Validates that the number of arguments provided matches the expected count.
    private void requireArgs(String[] parts, int expected) throws CommandException {
        if (parts.length != expected) {
            throw new CommandException(
                    CommandError.INVALID_NUMBER_ARGS.getMessage(parts[0], expected - 1, parts.length - 1));
        }
    }

    /// Parses an argument as an enum value and provides detailed error messages if
    /// parsing fails.
    private <T extends Enum<T>> T parseEnumArgs(String command, String args, Class<T> enumClass)
            throws CommandException {
        return EnumParser.parseEnum(enumClass, args)
                .orElseThrow(() -> new CommandException(
                        CommandError.INVALID_ARGUMENT.getMessage(command, args)));
    }
}
