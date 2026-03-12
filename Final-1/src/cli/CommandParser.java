package cli;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ai.Controller;
import exceptions.GameLogicException;
import exceptions.InvalidCommandException;
import model.Game;
import model.Position;
import model.Unit;
import utils.DisplayFormat;

/**
 * The CommandParser class is responsible for parsing and executing user
 * commands in
 * the command-line interface (CLI) of the game.
 * It interacts with the Game model and the AI Controller to process user inputs
 * and update the game state accordingly.
 * 
 * @author udqch
 */
public class CommandParser {
    private static final String REGEX_WHITESPACE = "\\s+";

    private final Game game;
    private final Controller aiController;
    private final Scanner scanner;
    private Position selectedPosition;
    private boolean isRunning;
    private boolean isCompactMode = false;

    @FunctionalInterface
    private interface CommandHandler {
        List<String> execute(String[] parts) throws GameLogicException, InvalidCommandException;
    }

    private final Map<Command, CommandHandler> commandHandlers = new EnumMap<>(Command.class);

    /**
     * Constructs a new CliParser with the specified game and AI controller.
     *
     * @param game         The game instance to interact with.
     * @param aiController The AI controller for handling AI moves.
     */
    public CommandParser(Game game, Controller aiController) {
        this.game = game;
        this.aiController = aiController;
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
     * @throws GameLogicException If an error occurs during game logic execution.
     */
    public void start() throws GameLogicException {
        try (scanner) {
            System.out.println(CliMessages.WELCOME_MSG);
            List<String> startLogs = game.startTurn(game.getCurrentTurn());
            printLogs(startLogs);

            // Main game loop
            while (isRunning && !game.isGameOver()) {
                // Check if it's the AI's turn and let it make a move
                if (game.getCurrentTurn().equals(game.getEnemy())) {
                    try {
                        List<String> aiLogs = aiController.playTurn();
                        printLogs(aiLogs);
                    } catch (GameLogicException e) {
                        System.out.println(CliMessages.formatError(CliMessages.ERR_AI_CRASH, e.getMessage()));
                    }
                    continue; // AI has made its move, go to the next iteration of the loop
                }

                // Read user input
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue; // Skip empty input
                }

                processCommand(input);
            }
        }
    }

    private void processCommand(String input) {
        String[] parts = input.split(REGEX_WHITESPACE);
        Command command = Command.fromString(parts[0]);

        if (command == null) {
            System.out.println(CliMessages.ERR_UNKNOWN_COMMAND);
            return;
        }

        try {
            CommandHandler handler = commandHandlers.get(command);
            if (handler != null) {
                List<String> logs = handler.execute(parts);
                printLogs(logs);
                if (command == Command.SELECT || command == Command.MOVE
                        || command == Command.FLIP || command == Command.BLOCK
                        || command == Command.PLACE) {
                    printBoardAndSelection();
                }
            }
        } catch (GameLogicException e) {
            System.out.println(CliMessages.formatError(e.getMessage()));
        } catch (InvalidCommandException | NumberFormatException e) {
            System.out.println(CliMessages.formatError(CliMessages.ERR_INVALID_ARGS));
        }
    }

    // --- COMMAND HANDLERS ---

    private List<String> handleSelect(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 2);
        this.selectedPosition = Position.fromString(parts[1]);

        return List.of();
    }

    private List<String> handleBoard(String[] parts) throws InvalidCommandException {
        requireArgs(parts, 1);
        CommandBoard.printBoard(game.getBoard(), game.getPlayer(), this.selectedPosition, isCompactMode);
        return List.of();
    }

    private List<String> handleMove(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 2);
        checkIfSelected();

        Position targetPos = Position.fromString(parts[1]);
        List<String> logs = game.executeMove(game.getPlayer(), this.selectedPosition, targetPos);
        this.selectedPosition = targetPos;

        return logs;
    }

    private List<String> handleFlip(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 1);
        checkIfSelected();
        List<String> logs = game.executeFlip(game.getPlayer(), this.selectedPosition);
        return logs;
    }

    private List<String> handleBlock(String[] parts) throws GameLogicException, InvalidCommandException {
        requireArgs(parts, 1);

        checkIfSelected();
        List<String> logs = game.executeBlock(game.getPlayer(), this.selectedPosition);
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
            throw new InvalidCommandException();
        }

        checkIfSelected();
        List<Integer> indices = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            indices.add(Integer.valueOf(parts[i]));
        }
        return game.executePlace(game.getPlayer(), indices, this.selectedPosition);
    }

    private List<String> handleState(String[] parts) throws InvalidCommandException {
        requireArgs(parts, 1);
        return CommandState.execute(game, this.selectedPosition, isCompactMode);
    }

    private List<String> handleYield(String[] parts) throws GameLogicException, InvalidCommandException {
        List<String> logs = CommandYield.execute(parts, game);
        this.selectedPosition = null;
        return logs;
    }

    // --- HELPER METHODS ---

    private void checkIfSelected() throws GameLogicException {
        if (this.selectedPosition == null) {
            throw new GameLogicException(CliMessages.ERR_NO_SELECTION);
        }
    }

    private void requireArgs(String[] parts, int exactLength) throws InvalidCommandException {
        if (parts.length != exactLength) {
            throw new InvalidCommandException();
        }
    }

    private void printLogs(List<String> logs) {
        for (String log : logs) {
            System.out.println(log);
        }
    }

    private void printBoardAndSelection() {
        // Always print the board and selected unit info after each command
        CommandBoard.printBoard(game.getBoard(), game.getPlayer(), this.selectedPosition, isCompactMode);
        if (this.selectedPosition != null) {
            Unit selectedUnit = game.getBoard().getUnitAt(this.selectedPosition);
            List<String> showLogs = CommandShow.generateInfo(selectedUnit, game.getCurrentTurn());
            printLogs(showLogs);
        }
    }
}
