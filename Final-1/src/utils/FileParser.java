package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import exceptions.GameConfigurationException;
import message.ConfigError;
import model.UnitTemplate;

/**
 * Utility class for reading and parsing files related to the game setup.
 * 
 * @author udqch
 */
public final class FileParser {

    private static final String UNIT_SEPERATOR = ";";
    private static final int EXPECTED_UNIT_PARTS = 4;
    private static final int EXPECTED_BOARD_SYMBOLS = 29;

    private FileParser() {
        // Private constructor to prevent instantiation
    }

    /**
     * Reads a file and prints its contents to the console.
     * 
     * @param filePath the path to the file to read
     * @return a list of lines read from the file
     * @throws IOException if an I/O error occurs reading from the file or a
     *                     malformed or unmappable byte sequence is read
     */
    public static List<String> readAndPrintFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        lines.forEach(System.out::println);
        return lines;
    }

    /**
     * Parses a list of strings into a list of UnitTemplate objects.
     * Each line is expected to be in the format: "qualifier;role;atk;def".
     * 
     * @param lines the list of strings to parse
     * @return a list of UnitTemplate objects parsed from the input lines
     * @throws GameConfigurationException if any line does not contain exactly 4
     *                                    parts
     */
    public static List<UnitTemplate> parseUnits(List<String> lines) throws GameConfigurationException {
        List<UnitTemplate> templates = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(UNIT_SEPERATOR);
            if (parts.length != EXPECTED_UNIT_PARTS) {
                throw new GameConfigurationException(ConfigError.INVALID_UNIT_TEMPLATE.get());
            }

            String qualifier = parts[0].trim();
            String role = parts[1].trim();
            int atk = parseToInt(parts[2]);
            int def = parseToInt(parts[3]);
            templates.add(new UnitTemplate(qualifier, role, atk, def));
        }
        return templates;
    }

    /**
     * Parses a list of strings representing a deck configuration into a list of
     * UnitTemplate objects.
     * Each line is expected to contain a non-negative integer representing the
     * count
     * of units of the corresponding template.
     * The total count of units must be exactly 40.
     * 
     * @param lines     the list of strings to parse
     * @param templates the list of UnitTemplate objects corresponding to the lines
     *                  in the deck configuration
     * @return a list of UnitTemplate objects representing the parsed deck
     * @throws GameConfigurationException if the number of lines does not match the
     *                                    number of templates, if any count is
     *                                    negative, or if the total count does not
     *                                    equal 40
     */
    public static List<UnitTemplate> parseDeck(List<String> lines, List<UnitTemplate> templates)
            throws GameConfigurationException {
        if (lines.size() != templates.size()) {
            throw new GameConfigurationException(ConfigError.DECK_LINES_MISMATCH.get());
        }

        List<UnitTemplate> deck = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            int count = parseToInt(lines.get(i));
            if (count < 0) {
                throw new GameConfigurationException(ConfigError.UNIT_COUNT_NEGATIVE.get());
            }
            // Add the specified number of units of the corresponding template to the deck
            deck.addAll(Collections.nCopies(count, templates.get(i)));
        }

        if (deck.size() != GameConstants.INITIAL_DECK_SIZE) {
            throw new GameConfigurationException(ConfigError.INVALID_DECK_SIZE.get());
        }
        return deck;
    }

    /**
     * Parses a list of strings representing the board symbol configuration into an
     * array of characters.
     * The input is expected to contain exactly one line with exactly 29 characters.
     * 
     * @param lines the list of strings to parse
     * @return an array of characters representing the parsed board symbols
     * @throws GameConfigurationException if the number of lines is not 1 or if the
     *                                    line does not contain exactly 29
     *                                    characters
     */
    public static char[] parseBoardSymbol(List<String> lines) throws GameConfigurationException {
        if (lines.size() != 1) {
            throw new GameConfigurationException(ConfigError.INVALID_BOARD_LINES.get());
        }

        String symbols = lines.get(0);
        if (symbols.length() != EXPECTED_BOARD_SYMBOLS) {
            throw new GameConfigurationException(ConfigError.INVALID_BOARD_SYMBOLS.get());
        }
        return symbols.toCharArray();
    }

    // --- HELPER METHODS ---

    private static int parseToInt(String str) {
        return Integer.parseInt(str.trim());
    }
}
