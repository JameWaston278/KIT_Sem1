package cli;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

import ai.AIPlayer;
import exceptions.GameLogicException;
import exceptions.InvalidCommandException;
import message.CliMessages;
import model.Game;
import model.Position;
import model.Unit;
import utils.DisplayFormat;
import utils.GameConstants;

/**
 * The CliParser class is responsible for parsing and executing user
 * commands in the command-line interface (CLI) of the game.
 * It interacts with the Game model and the AI Controller to process user inputs
 * and update the game state accordingly.
 * 
 * @author udqch
 */
public class GameCLI {
    private static final String REGEX_WHITESPACE = "\\s+";
    private static final String INPUT_REMINDER = "> ";
    private final Game game;
    private final AIPlayer aiController;
    private final Scanner scanner;

    private Position selectedPosition;
    private boolean isCompactMode;
    private char[] customSymbols;
    private boolean isRunning;

    @FunctionalInterface
    private interface CommandHandler {
        List<String> execute(String[] parts) throws GameLogicException, InvalidCommandException;
    }

    private final Map<Command, CommandHandler> commandHandlers = new EnumMap<>(Command.class);

    /**
     * Constructs a new CliParser with the specified game and AI controller.
     *
     * @param game          The game instance to interact with.
     * @param aiController  The AI controller for handling AI moves.
     * @param isCompactMode Whether to use compact mode for board display.
     * @param customSymbols The custom symbols for the board.
     */
    public GameCLI(Game game, AIPlayer aiController, boolean isCompactMode, char[] customSymbols) {
        this.game = game;
        this.aiController = aiController;
        this.isCompactMode = isCompactMode;
        this.customSymbols = customSymbols;

        this.scanner = new Scanner(System.in);
        this.isRunning = true;
        this.selectedPosition = null;

        // Initialize command handlers
        commandHandlers.put(Command.SELECT, this::handleSelect);
        commandHandlers.put(Command.BOARD, this::handleBoard);
        commandHandlers.put(Command.MOVE, this::handleMove);
        commandHandlers.put(Command.FLIP, this::handleFlip);
        commandHandlers.put(Command.BLOCK, this::handleBlock);
        commandHandlers.put(Command.HAND, this::handleHand);
        commandHandlers.put(Command.PLACE, this::handlePlace);
        commandHandlers.put(Command.SHOW, this::handleShow);
        commandHandlers.put(Command.YIELD, this::handleYield);
        commandHandlers.put(Command.STATE, this::handleState);
        commandHandlers.put(Command.QUIT, parts -> {
            this.isRunning = false;
            return List.of();
        });
    }

    /**
     * Starts the CLI game loop, processing user commands until the game is over or
     * the user quits.
     *
     * @throws GameLogicException If any game logic errors occur during command
     *                            execution.
     */
    public void start() throws GameLogicException {
        try (scanner) {
            System.out.println(CliMessages.WELCOME_MSG.get());
            List<String> startLogs = game.startTurn(game.getCurrentTurn());
            printLogs(startLogs);

            // Main game loop
            while (isRunning && !game.isGameOver()) {
                // Check if it's the AI's turn and let it make a move
                if (game.getCurrentTurn().equals(aiController.getAiTeam())) {

                    if (game.isGameOver()) {
                        break;
                    }

                    playAITurn();
                    continue; // Skip user input when AI is playing
                }

                // Read user input (player's turn)
                System.out.print(INPUT_REMINDER);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue; // Skip empty input
                }
                processCommand(input);
            }
        }
    }

    private void playAITurn() {
        BiConsumer<List<String>, Position> aiStepCallback = (stepLogs, targetPos) -> {
            if (stepLogs != null && !stepLogs.isEmpty()) { // Print logs from the AI's step and update the board display
                for (String log : stepLogs) {
                    System.out.println(log);
                }
                if (targetPos != null) {
                    printBoardAndSelection(targetPos);
                }
            }
        };

        try {
            aiController.playTurn(aiStepCallback);
        } catch (GameLogicException e) {
            printError(CliMessages.format(CliMessages.AI_CRASH.get(), e.getMessage()));
            this.isRunning = false; // Stop the game if AI crashes
        }
    }

    private void processCommand(String input) {
        String[] parts = input.split(REGEX_WHITESPACE);
        Command command = Command.fromString(parts[0]);

        try {
            if (command == null) {
                throw new InvalidCommandException(CliMessages.UNKNOWN_COMMAND.get());
            }

            CommandHandler handler = commandHandlers.get(command);
            if (handler != null) {
                List<String> logs = handler.execute(parts);
                printLogs(logs);

                if (command == Command.SELECT || command == Command.MOVE
                        || command == Command.FLIP || command == Command.BLOCK
                        || command == Command.PLACE) {
                    printBoardAndSelection(this.selectedPosition);
                }
            }

        } catch (GameLogicException e) {
            // Logic errors: Invalid moves, trying to move opponent's unit, etc.
            printError(e.getMessage());

        } catch (InvalidCommandException e) {
            // Command format errors: Wrong number of arguments, invalid argument types,
            // etc.
            String errorMsg = (e.getMessage() != null) ? e.getMessage() : CliMessages.INVALID_ARGS.get();
            printError(errorMsg);

        } catch (NumberFormatException e) {
            // Handle cases where integer parsing fails (e.g., in "place" command)
            printError(CliMessages.INVALID_ARGS.get());
        }
    }

    // --- COMMAND HANDLERS ---

    private List<String> handleSelect(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 2);
        this.selectedPosition = parsePosition(parts[1]);

        return List.of();
    }

    private List<String> handleBoard(String[] parts) throws InvalidCommandException {
        requireArgs(parts, 1);
        CommandBoard.printBoard(game, this.selectedPosition, isCompactMode, customSymbols);
        return List.of();
    }

    private List<String> handleMove(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 2);
        checkIfSelected();

        Position targetPos = parsePosition(parts[1]);
        List<String> logs = game.executeMove(game.getCurrentTurn(), this.selectedPosition, targetPos);
        this.selectedPosition = targetPos;

        return logs;
    }

    private List<String> handleFlip(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 1);
        checkIfSelected();
        List<String> logs = game.executeFlip(game.getCurrentTurn(), this.selectedPosition);
        return logs;
    }

    private List<String> handleBlock(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 1);

        checkIfSelected();
        List<String> logs = game.executeBlock(game.getCurrentTurn(), this.selectedPosition);
        return logs;
    }

    private List<String> handleShow(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 1);
        checkIfSelected();
        return CommandShow.generateInfo(game.getBoard().getUnitAt(this.selectedPosition), game.getCurrentTurn());
    }

    private List<String> handleHand(String[] parts) throws InvalidCommandException {
        requireArgs(parts, 1);
        List<String> logs = new ArrayList<>();

        List<Unit> hand = game.getCurrentTurn().getHand();
        for (int i = 0; i < hand.size(); i++) {
            Unit unit = hand.get(i);
            String line = DisplayFormat.UNIT_ON_HAND.format(i + 1, unit.getName(), unit.getAtk(), unit.getDef());
            logs.add(line);
        }
        return logs;
    }

    private List<String> handlePlace(String[] parts) throws GameLogicException, InvalidCommandException {
        if (parts.length < 2) {
            throw new InvalidCommandException(CliMessages.INVALID_ARGS.get());
        }

        checkIfSelected();
        List<Integer> indices = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            indices.add(Integer.valueOf(parts[i]));
        }
        return game.executePlace(game.getCurrentTurn(), indices, this.selectedPosition);
    }

    private List<String> handleState(String[] parts) throws InvalidCommandException {
        requireArgs(parts, 1);
        return CommandState.execute(game, this.selectedPosition, isCompactMode, customSymbols);
    }

    private List<String> handleYield(String[] parts) throws GameLogicException, InvalidCommandException {
        List<String> logs = CommandYield.execute(parts, game);
        this.selectedPosition = null;
        return logs;
    }

    // --- HELPER METHODS ---

    private void checkIfSelected() throws InvalidCommandException {
        if (this.selectedPosition == null) {
            throw new InvalidCommandException(CliMessages.NO_SELECTION.get());
        }
    }

    private void requireArgs(String[] parts, int exactLength) throws InvalidCommandException {
        if (parts.length != exactLength) {
            throw new InvalidCommandException(CliMessages.INVALID_ARGS.get());
        }
    }

    private Position parsePosition(String posStr) throws GameLogicException {
        Position pos = Position.fromString(posStr);
        game.getBoard().isValid(pos); // Validate the position
        return pos;
    }

    private void printLogs(List<String> logs) {
        for (String log : logs) {
            System.out.println(log);
        }
    }

    private void printBoardAndSelection(Position selectedPos) {
        // Always print the board and selected unit info after each command
        CommandBoard.printBoard(game, selectedPos, isCompactMode, customSymbols);
        if (selectedPos != null) {
            Unit selectedUnit = game.getBoard().getUnitAt(selectedPos);
            List<String> showLogs = CommandShow.generateInfo(selectedUnit, game.getCurrentTurn());
            printLogs(showLogs);
        }
    }

    private void printError(String message) {
        System.out.println(GameConstants.ERROR_PREFIX + message);
    }
}
