import ai.AIPlayer;
import cli.GameCLI;
import exceptions.FatalSetupException;
import exceptions.GameConfigurationException;
import exceptions.GameLogicException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import message.FatalError;
import model.Game;
import model.Team;
import model.UnitTemplate;
import utils.FileParser;
import utils.GameConstants;

/**
 * The Main class serves as the entry point for the game application. It is
 * responsible for parsing command-line arguments, validating them, and setting
 * up the game environment based on the provided configurations. The main method
 * orchestrates the flow of the program, handling exceptions that may arise
 * during setup and execution.
 *
 * @author udqch
 */
public final class Main {

    private static final String ARG_SEPARATOR = "=";

    private static final String KEY_SEED = "seed";
    private static final String KEY_BOARD = "board";
    private static final String KEY_UNITS = "units";
    private static final String KEY_DECK = "deck";
    private static final String KEY_DECK1 = "deck1";
    private static final String KEY_DECK2 = "deck2";
    private static final String KEY_TEAM1 = "team1";
    private static final String KEY_TEAM2 = "team2";
    private static final String KEY_VERBOSITY = "verbosity";
    private static final Set<String> ALLOWED_KEYS = Set.of(
            KEY_SEED, KEY_BOARD, KEY_UNITS, KEY_DECK, KEY_DECK1,
            KEY_DECK2, KEY_TEAM1, KEY_TEAM2, KEY_VERBOSITY);

    private static final String VERBOSITY_ALL = "all";
    private static final String VERBOSITY_COMPACT = "compact";

    private static final String DEFAULT_PLAYER_NAME = "Player";
    private static final String DEFAULT_ENEMY_NAME = "Enemy";

    private Main() {
        // Private constructor to prevent instantiation of this utility class
    }

    /**
     * The main method is the entry point of the application. It processes
     * command-line arguments, validates them, and initializes the game. If any
     * fatal errors occur during setup, it catches the exceptions and prints
     * appropriate error messages.
     *
     * @param args Command-line arguments passed to the program.
     */
    public static void main(String[] args) {
        try {
            Map<String, String> argsMap = parseArguments(args);
            validateArguments(argsMap);
            setupAndRunGame(argsMap);
        } catch (FatalSetupException e) {
            System.out.println(GameConstants.ERROR_PREFIX + e.getMessage());
        }
    }

    private static Map<String, String> parseArguments(String[] args) throws FatalSetupException {
        Map<String, String> argsMap = new HashMap<>();
        for (String arg : args) {
            String[] parts = arg.split(ARG_SEPARATOR, 2);
            if (parts.length != 2 || argsMap.containsKey(parts[0])) {
                throw new FatalSetupException(FatalError.INVALID_ARGUMENT_FORMAT.get());
            }
            argsMap.put(parts[0], parts[1]);
        }
        return argsMap;
    }

    // Validates the presence and correctness of required and optional arguments.
    // Throws FatalSetupException if any validation fails.
    private static void validateArguments(Map<String, String> argsMap) throws FatalSetupException {
        // Check for unrecognized keys
        for (String key : argsMap.keySet()) {
            if (!ALLOWED_KEYS.contains(key)) {
                throw new FatalSetupException(FatalError.INVALID_ARGUMENT_FORMAT.get());
            }
        }

        // Check for mandatory arguments
        if (!argsMap.containsKey(KEY_SEED) || !argsMap.containsKey(KEY_UNITS)) {
            throw new FatalSetupException(FatalError.MISSING_MANDATORY_ARGUMENT.get());
        }

        boolean hasDeck = argsMap.containsKey(KEY_DECK);
        boolean hasDeck1 = argsMap.containsKey(KEY_DECK1);
        boolean hasDeck2 = argsMap.containsKey(KEY_DECK2);
        if (hasDeck == hasDeck1 || hasDeck1 != hasDeck2) {
            throw new FatalSetupException(FatalError.INVALID_DECK_CONFIGURATION.get());
        }
    }

    private static void setupAndRunGame(Map<String, String> argsMap) throws FatalSetupException {
        try {
            long seed = Long.parseLong(argsMap.get(KEY_SEED));
            Random random = new Random(seed);

            char[] customSymbols = loadCustomSymbols(argsMap);
            List<UnitTemplate> templates = loadUnitTemplates(argsMap);
            List<List<UnitTemplate>> decks = loadDecks(argsMap, templates);

            Collections.shuffle(decks.get(0), random);
            Collections.shuffle(decks.get(1), random);

            String[] teamNames = loadTeamName(argsMap);
            boolean isCompact = loadVerbosity(argsMap);

            Team team1 = new Team(teamNames[0], decks.get(0));
            Team team2 = new Team(teamNames[1], decks.get(1));

            Game game = new Game(team1, team2);
            AIPlayer aiPlayer = new AIPlayer(game, team2, random);
            GameCLI parser = new GameCLI(game, aiPlayer, isCompact, customSymbols);

            parser.start();

        } catch (IOException e) {
            throw new FatalSetupException(FatalError.FILE_ERROR.get() + e.getMessage());

        } catch (GameConfigurationException | GameLogicException e) {
            // Error related to game setup or logic (e.g., invalid unit templates, deck
            // issues)
            throw new FatalSetupException(e.getMessage());

        } catch (NumberFormatException e) {
            // Error related to parsing the seed value
            throw new FatalSetupException(FatalError.INVALID_NUMBER_FORMAT.get());
        }
    }

    // --- PARSER METHODS ---

    private static char[] loadCustomSymbols(Map<String, String> argsMap)
            throws IOException, GameConfigurationException {
        if (argsMap.containsKey(KEY_BOARD)) {
            List<String> rawBoard = FileParser.readAndPrintFile(argsMap.get(KEY_BOARD));
            return FileParser.parseBoardSymbol(rawBoard);
        }
        return null; // Return null if no custom symbols are specified
    }

    private static List<UnitTemplate> loadUnitTemplates(Map<String, String> argsMap)
            throws IOException, GameConfigurationException {
        List<String> rawTemplates = FileParser.readAndPrintFile(argsMap.get(KEY_UNITS));
        return FileParser.parseUnits(rawTemplates);
    }

    private static List<List<UnitTemplate>> loadDecks(Map<String, String> argsMap, List<UnitTemplate> templates)
            throws IOException, GameConfigurationException {
        List<UnitTemplate> team1Deck;
        List<UnitTemplate> team2Deck;

        if (argsMap.containsKey(KEY_DECK)) {
            List<String> rawDeck = FileParser.readAndPrintFile(argsMap.get(KEY_DECK));
            team1Deck = FileParser.parseDeck(rawDeck, templates);
            team2Deck = FileParser.parseDeck(rawDeck, templates);
        } else {
            List<String> rawDeck1 = FileParser.readAndPrintFile(argsMap.get(KEY_DECK1));
            team1Deck = FileParser.parseDeck(rawDeck1, templates);

            List<String> rawDeck2 = FileParser.readAndPrintFile(argsMap.get(KEY_DECK2));
            team2Deck = FileParser.parseDeck(rawDeck2, templates);
        }
        return List.of(team1Deck, team2Deck);
    }

    private static String[] loadTeamName(Map<String, String> argsMap)
            throws FatalSetupException, GameConfigurationException {
        String team1Name = argsMap.getOrDefault(KEY_TEAM1, DEFAULT_PLAYER_NAME);
        String team2Name = argsMap.getOrDefault(KEY_TEAM2, DEFAULT_ENEMY_NAME);
        if (team1Name.equals(team2Name)) {
            throw new FatalSetupException(FatalError.DUPLICATE_TEAM_NAMES.get());
        }
        if (team1Name.length() > 14 || team2Name.length() > 14) {
            throw new FatalSetupException(FatalError.TEAM_NAME_TOO_LONG.get());
        }
        return new String[] { team1Name, team2Name };
    }

    private static boolean loadVerbosity(Map<String, String> argsMap) throws FatalSetupException {
        if (!argsMap.containsKey(KEY_VERBOSITY)) {
            return false; // Default to compact if verbosity is not specified
        }

        String status = argsMap.get(KEY_VERBOSITY);
        if (!status.equals(VERBOSITY_ALL) && !status.equals(VERBOSITY_COMPACT)) {
            throw new FatalSetupException(FatalError.INVALID_VERBOSITY_LEVEL.get());
        }
        return VERBOSITY_COMPACT.equals(argsMap.get(KEY_VERBOSITY));
    }
}
