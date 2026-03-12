package cli;

import java.util.ArrayList;
import java.util.List;

import model.Game;
import model.Position;
import model.Team;
import model.Unit;

/**
 * CommandState is responsible for generating and printing the overall state of
 * the game.
 * It coordinates printing the header, the board, and the selected unit's info.
 * 
 * @author udqch
 */
final class CommandState {

    // --- FORMAT CONSTANTS ---
    private static final int TOTAL_LINE_WIDTH = 31;
    private static final String LEFT_PADDING = "  ";
    private static final String SPACE = " ";

    // String formats for Team Stats
    private static final String FORMAT_LP = "%d/8000 LP";
    private static final String FORMAT_DC = "DC: %d/40";
    private static final String FORMAT_BC = "BC: %d/5";

    private CommandState() {
        // Private constructor to prevent instantiation
    }

    /**
     * Executes the state command by printing the header and the board directly,
     * then returning the selected unit's info to the caller.
     * 
     * @param game          The current game state, used to extract information for
     *                      the
     *                      header and board.
     * @param selectedPos   The position of the selected unit.
     * @param isCompactMode Whether to print the board in compact mode.
     * @return A list of strings representing the unit info logs.
     * 
     */
    static List<String> execute(Game game, Position selectedPos, boolean isCompactMode) {

        // 1. Generate and print the header logs directly
        List<String> headerLogs = generateHeader(game);
        for (String line : headerLogs) {
            System.out.println(line);
        }

        // Print the game board
        CommandBoard.printBoard(game.getBoard(), game.getPlayer(), selectedPos, isCompactMode);

        // Return the unit info logs (CommandShow) to the CliParser to print
        if (selectedPos != null) {
            Unit selectedUnit = game.getBoard().getUnitAt(selectedPos);
            return CommandShow.generateInfo(selectedUnit, game.getCurrentTurn());
        }

        return List.of();
    }

    // --- HELPER METHODS ---

    private static List<String> generateHeader(Game game) {
        Team p1 = game.getPlayer();
        Team p2 = game.getEnemy();
        List<String> logs = new ArrayList<>();

        // Line 1: Player Names
        logs.add(formatAlignedLine(p1.getName(), p2.getName()));

        // Line 2: Life Points (LP)
        String lp1 = String.format(FORMAT_LP, p1.getLp());
        String lp2 = String.format(FORMAT_LP, p2.getLp());
        logs.add(formatAlignedLine(lp1, lp2));

        // Line 3: Deck Count (DC)
        String dc1 = String.format(FORMAT_DC, p1.getDeckSize());
        String dc2 = String.format(FORMAT_DC, p2.getDeckSize());
        logs.add(formatAlignedLine(dc1, dc2));

        // Line 4: Board Count (BC)
        String bc1 = String.format(FORMAT_BC, countBoardUnits(p1));
        String bc2 = String.format(FORMAT_BC, countBoardUnits(p2));
        logs.add(formatAlignedLine(bc1, bc2));

        return logs;
    }

    private static String formatAlignedLine(String leftText, String rightText) {
        int spacesNeeded = TOTAL_LINE_WIDTH - LEFT_PADDING.length() - leftText.length() - rightText.length();
        spacesNeeded = Math.max(1, spacesNeeded); // Ensure at least one space between left and right text
        return LEFT_PADDING + leftText + SPACE.repeat(spacesNeeded) + rightText;
    }

    /**
     * Counts the number of units on the board for a given team (excluding the
     * King).
     */
    private static int countBoardUnits(Team team) {
        int count = 0;
        for (Unit unit : team.getActiveUnits()) {
            if (!unit.isKing()) {
                count++;
            }
        }
        return count;
    }
}