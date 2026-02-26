package model;

import java.util.ArrayList;
import java.util.List;

import exceptions.ErrorMessage;
import exceptions.GameLogicException;

public record Position(int col, int row) {
    public static final char FIRST_COL = 'A';
    public static final char LAST_COL = 'G';
    public static final char FIRST_ROW = '1';
    public static final char LAST_ROW = '7';

    // --- PARSE COORDINATES ---
    public static Position fromString(String posString) throws GameLogicException {
        if (posString == null || posString.length() != 2) {
            throw new GameLogicException(ErrorMessage.INVALID_COORDINATES.format(posString));
        }

        char columnChar = Character.toUpperCase(posString.charAt(0));
        char rowChar = posString.charAt(1);
        if (columnChar < FIRST_COL || columnChar > LAST_COL || rowChar < FIRST_ROW || rowChar > LAST_ROW) {
            throw new GameLogicException(ErrorMessage.OUT_OF_BOUNDS.format(posString));
        }

        int colIndex = columnChar - FIRST_COL;
        int rowIndex = Character.getNumericValue(rowChar) - 1;
        return new Position(colIndex, rowIndex);
    }

    @Override
    public String toString() {
        char colChar = (char) (FIRST_COL + col);
        char rowChar = (char) (FIRST_ROW + row);
        return "" + colChar + rowChar;
    }

    // --- NEIGHBORS ---
    public List<Position> getNeighbors(int[][] directions) {
        List<Position> neighbors = new ArrayList<>();
        for (int[] dir : directions) {
            int newCol = this.col + dir[0];
            int newRow = this.row + dir[1];
            if (newCol >= 0 && newCol < Board.BOARD_SIZE && newRow >= 0 && newRow < Board.BOARD_SIZE) {
                neighbors.add(new Position(newCol, newRow));
            }
        }
        return neighbors;
    }
}