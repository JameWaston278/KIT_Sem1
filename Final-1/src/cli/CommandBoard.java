package cli;

import model.Board;
import model.Position;
import model.Team;
import model.Unit;
import utils.GameConstants;

/**
 * Utility class for printing the game board to the console.
 * Handles both compact and detailed views, as well as highlighting the selected
 * position.
 * 
 * @author udqch
 */
final class CommandBoard {

    // --- STRINGS & FORMATS ---
    private static final String COLUMN_NUMBERS = "    A   B   C   D   E   F   G  ";
    private static final String ROW_PADDING = "  ";
    private static final String EMPTY_CELL = "   "; // 3 spaces to match the width of a unit cell

    // --- BORDER SYMBOLS (Ký hiệu viền) ---
    private static final char CORNER_DEFAULT = '+';
    private static final char CORNER_SELECTED = '#';
    private static final char HORZ_DEFAULT = '-';
    private static final char HORZ_SELECTED = '=';
    private static final char VERT_DEFAULT = '|';
    private static final char VERT_SELECTED = 'N';

    // --- UNIT SYMBOLS (Ký hiệu lính) ---
    private static final char TEAM_PLAYER = 'x';
    private static final char TEAM_ENEMY = 'y';
    private static final char PREFIX_UNMOVED = '*';
    private static final char PREFIX_MOVED = ' ';
    private static final char SUFFIX_BLOCKING = 'b';
    private static final char SUFFIX_NORMAL = ' ';

    private CommandBoard() {
        // Private constructor to prevent instantiation
    }

    /**
     * Prints the game board to the console.
     *
     * @param board       The game board to print.
     * @param playerTeam  The team of the current player.
     * @param selectedPos The position of the currently selected unit.
     * @param isCompact   Whether to print a compact view.
     */
    static void printBoard(Board board, Team playerTeam, Position selectedPos, boolean isCompact) {
        for (int row = GameConstants.BOARD_ROWS - 1; row >= 0; row--) {
            if (!isCompact) {
                printHorizontalBorder(row, selectedPos);
            }
            if (row > 0) {
                printContentRow(board, playerTeam, row - 1, selectedPos);
            }
        }
        System.out.println(COLUMN_NUMBERS);
    }

    private static void printHorizontalBorder(int row, Position selectedPos) {
        System.out.print(ROW_PADDING);

        for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
            System.out.print(getCornerSymbol(row, col, selectedPos));

            if (col < GameConstants.BOARD_COLS - 1) {
                char h = getHorizontalSymbol(row, col, selectedPos);
                // In 3 ký tự ngang cho khớp độ rộng của 1 ô
                System.out.print("" + h + h + h);
            }
        }
        System.out.println();
    }

    private static void printContentRow(Board board, Team playerTeam, int row, Position selectedPos) {
        System.out.print((row + 1) + " ");

        for (int col = 0; col < GameConstants.BOARD_COLS; col++) {
            System.out.print(getVerticalSymbol(row, col, selectedPos));

            if (col < GameConstants.BOARD_COLS - 1) {
                Unit unit = board.getUnitAt(new Position(row, col));
                System.out.print(formatUnit(unit, playerTeam));
            }
        }
        System.out.println();
    }

    // --- FORMAT UNIT ---

    private static String formatUnit(Unit unit, Team playerTeam) {
        if (unit == null) {
            return EMPTY_CELL;
        }

        boolean isOwn = unit.getOwner().equals(playerTeam);
        char teamChar = isOwn ? TEAM_PLAYER : TEAM_ENEMY;

        if (unit.isKing()) {
            teamChar = Character.toUpperCase(teamChar);
        }

        char prefix = !unit.hasMoved() ? PREFIX_UNMOVED : PREFIX_MOVED;
        char suffix = unit.isBlocking() ? SUFFIX_BLOCKING : SUFFIX_NORMAL;

        return "" + prefix + teamChar + suffix;
    }

    // --- SYMBOL RESOLVERS ---

    private static char getCornerSymbol(int row, int col, Position selectedPos) {
        if (selectedPos != null) {
            boolean isRowMatch = (selectedPos.row() == row || selectedPos.row() == row - 1);
            boolean isColMatch = (selectedPos.col() == col || selectedPos.col() == col - 1);

            if (isRowMatch && isColMatch) {
                return CORNER_SELECTED;
            }
        }
        return CORNER_DEFAULT;
    }

    private static char getHorizontalSymbol(int row, int col, Position selectedPos) {
        if (selectedPos != null) {
            if ((selectedPos.row() == row && selectedPos.col() == col)
                    || (selectedPos.row() == row - 1 && selectedPos.col() == col)) {
                return HORZ_SELECTED;
            }
        }
        return HORZ_DEFAULT;
    }

    private static char getVerticalSymbol(int row, int col, Position selectedPos) {
        if (selectedPos != null) {
            if ((selectedPos.row() == row && selectedPos.col() == col)
                    || (selectedPos.row() == row && selectedPos.col() == col - 1)) {
                return VERT_SELECTED;
            }
        }
        return VERT_DEFAULT;
    }
}