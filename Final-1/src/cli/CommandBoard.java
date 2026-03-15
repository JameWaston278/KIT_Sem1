package cli;

import model.Board;
import model.Game;
import model.Position;
import model.Unit;
import utils.GameConstants;

/**
 * Utility class for printing the game board to the console.
 * Handles both compact and detailed views, as well as highlighting the selected
 * position using either default symbols or a custom 29-character symbol set.
 * 
 * @author udqch
 */
public final class CommandBoard {

    private static final char NULL = '\0'; // Sentinel value for "no symbol"

    // --- STRINGS & FORMATS ---
    private static final String COLUMN_NUMBERS = "    A   B   C   D   E   F   G  ";
    private static final String ROW_PADDING = "  ";
    private static final String EMPTY_CELL = "   ";

    // --- DEFAULT BORDER SYMBOLS ---
    private static final char CORNER_DEFAULT = '+';
    private static final char CORNER_SELECTED = '#';
    private static final char HORZ_DEFAULT = '-';
    private static final char HORZ_SELECTED = '=';
    private static final char VERT_DEFAULT = '|';
    private static final char VERT_SELECTED = 'N';

    // --- UNIT SYMBOLS ---
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
     * Prints the game board to the console with appropriate formatting. It
     * highlights
     * the selected position and uses either default symbols or custom symbols for
     * the borders.
     * 
     * @param game          The game instance containing the board and current team
     * @param selectedPos   The currently selected position on the board (can be
     *                      null).
     * @param isCompact     Whether to print in compact mode (borders only between
     *                      rows
     * @param customSymbols An optional array of 29 custom symbols for borders and
     *                      corners. If null, default symbols are used.
     */
    public static void printBoard(Game game, Position selectedPos, boolean isCompact,
            char[] customSymbols) {
        for (int row = GameConstants.BOARD_ROWS; row >= 0; row--) {
            if (!isCompact) {
                printHorizontalBorder(row, selectedPos, customSymbols);
            }
            if (row > 0) {
                printContentRow(game, row - 1, selectedPos, customSymbols);
            }
        }
        System.out.println(COLUMN_NUMBERS);
    }

    private static void printHorizontalBorder(int row, Position selectedPos, char[] c) {
        System.out.print(ROW_PADDING);

        for (int col = 0; col <= GameConstants.BOARD_COLS; col++) {
            System.out.print(getCornerSymbol(row, col, selectedPos, c));

            if (col < GameConstants.BOARD_COLS) {
                char h = getHorizontalSymbol(row, col, selectedPos, c);
                System.out.print("" + h + h + h);
            }
        }
        System.out.println();
    }

    private static void printContentRow(Game game, int row, Position selectedPos, char[] c) {
        Board board = game.getBoard();
        System.out.print((row + 1) + " ");

        for (int col = 0; col <= GameConstants.BOARD_COLS; col++) {
            System.out.print(getVerticalSymbol(row, col, selectedPos, c));

            if (col < GameConstants.BOARD_COLS) {
                Unit unit = board.getUnitAt(new Position(col, row));
                System.out.print(formatUnit(unit, game));
            }
        }
        System.out.println();
    }

    // --- FORMAT UNIT ---

    private static String formatUnit(Unit unit, Game game) {
        if (unit == null) {
            return EMPTY_CELL;
        }

        boolean isPlayer = unit.getOwner().equals(game.getTeam1());
        char teamChar = isPlayer ? TEAM_PLAYER : TEAM_ENEMY;

        if (unit.isKing()) {
            teamChar = Character.toUpperCase(teamChar);
        }

        boolean canMoveThisTurn = unit.getOwner().equals(game.getCurrentTurn()) && !unit.hasMoved();
        char prefix = canMoveThisTurn ? PREFIX_UNMOVED : PREFIX_MOVED;
        char suffix = unit.isBlocking() ? SUFFIX_BLOCKING : SUFFIX_NORMAL;

        return "" + prefix + teamChar + suffix;
    }

    // --- SYMBOL RESOLVERS ---

    private static char getHorizontalSymbol(int row, int col, Position selectedPos, char[] c) {
        boolean selected = selectedPos != null && selectedPos.col() == col
                && (selectedPos.row() == row || selectedPos.row() == row - 1);

        if (c == null) {
            return selected ? HORZ_SELECTED : HORZ_DEFAULT;
        }
        return selected ? c[23] : c[8]; // 'x' or 'i'
    }

    private static char getVerticalSymbol(int row, int col, Position selectedPos, char[] c) {
        boolean selected = selectedPos != null && selectedPos.row() == row
                && (selectedPos.col() == col || selectedPos.col() == col - 1);

        if (c == null) {
            return selected ? VERT_SELECTED : VERT_DEFAULT;
        }
        return selected ? c[24] : c[9]; // 'y' or 'j'
    }

    private static char getCornerSymbol(int row, int col, Position selectedPos, char[] c) {
        // Check if the corner itself is selected (any of the 4 adjacent cells is
        // selected)
        boolean selTL = selectedPos != null && selectedPos.col() == col - 1 && selectedPos.row() == row;
        boolean selTR = selectedPos != null && selectedPos.col() == col && selectedPos.row() == row;
        boolean selBL = selectedPos != null && selectedPos.col() == col - 1 && selectedPos.row() == row - 1;
        boolean selBR = selectedPos != null && selectedPos.col() == col && selectedPos.row() == row - 1;

        if (c == null) {
            return (selTL || selTR || selBL || selBR) ? CORNER_SELECTED : CORNER_DEFAULT;
        }

        char symbol = getOuterCornerSymbol(row, col, selTL, selTR, selBL, selBR, c);
        if (symbol != NULL) {
            return symbol;
        }

        symbol = getEdgeCornerSymbol(row, col, selTL, selTR, selBL, selBR, c);
        if (symbol != NULL) {
            return symbol;
        }

        return getInnerCornerSymbol(selTL, selTR, selBL, selBR, c);
    }

    // Outer corner symbols are determined by whether the selected cell is in the
    // opposite corner (e.g. for top-left corner, check bottom-right cell)
    // (tl=top-left, tr=top-right, bl=bottom-left, br=bottom-right)
    private static char getOuterCornerSymbol(int row, int col, boolean tl, boolean tr, boolean bl, boolean br,
            char[] c) {
        if (row == GameConstants.BOARD_ROWS && col == 0) {
            return br ? c[11] : c[0];
        }
        if (row == GameConstants.BOARD_ROWS && col == GameConstants.BOARD_COLS) {
            return bl ? c[12] : c[1];
        }
        if (row == 0 && col == 0) {
            return tr ? c[13] : c[2];
        }
        if (row == 0 && col == GameConstants.BOARD_COLS) {
            return tl ? c[14] : c[3];
        }
        return NULL; // Sentinel value
    }

    // Edge corner symbols are determined by the adjacent cell in the opposite
    // direction (e.g. for a top edge, check the bottom cell)
    // (tl=top-left, tr=top-right, bl=bottom-left, br=bottom-right)
    private static char getEdgeCornerSymbol(int row, int col, boolean tl, boolean tr, boolean bl, boolean br,
            char[] c) {
        if (row == GameConstants.BOARD_ROWS) {
            return bl ? c[15] : (br ? c[16] : c[4]);
        }
        if (row == 0) {
            return tl ? c[19] : (tr ? c[20] : c[6]);
        }
        if (col == 0) {
            return tr ? c[21] : (br ? c[22] : c[7]);
        }
        if (col == GameConstants.BOARD_COLS) {
            return tl ? c[17] : (bl ? c[18] : c[5]);
        }
        return NULL; // Sentinel value
    }

    // Inner corner symbols are determined solely by which of the 4 adjacent cells
    // is selected (tl=top-left, tr=top-right, bl=bottom-left, br=bottom-right)
    private static char getInnerCornerSymbol(boolean tl, boolean tr, boolean bl, boolean br, char[] c) {
        if (tl) {
            return c[25];
        }
        if (tr) {
            return c[26];
        }
        if (bl) {
            return c[27];
        }
        if (br) {
            return c[28];
        }
        return c[10];
    }
}