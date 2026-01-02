import java.util.Arrays;

public class PlayingField {
    int[][] field = new int[6][7];

    // initialize field with values 0
    public PlayingField() {
        for (int[] row : field) {
            Arrays.fill(row, 0);
        }
    }

    public boolean validate(String colString) {
        if (colString.length() > 1) {
            InOutput.error = "Too large number";
            return false;
        }
        int col = Integer.parseInt(colString) - 1;
        // valid column?
        if (col < 0 || col >= field[0].length) {
            InOutput.error = "Current column is unavailable. Please try again!";
            return false;
        }

        // full column?
        if (field[0][col] != 0) {
            InOutput.error = "Current column was full. Please select a different column!";
            return false;
        }

        return true;
    }

    // full Field? -> yes, game end
    public boolean isFull() {
        for (int firstRow : field[0]) {
            if (firstRow == 0) {
                return false;
            }
        }
        InOutput.error = "Field was full. Game end";
        return true;
    }

    // player is winner?
    public boolean isWinner(int player) {
        int rows = field.length, cols = field[0].length, win = InOutput.winCondition;

        // horizontal
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c <= cols - win; c++) {
                boolean all = true;
                for (int k = 0; k < win; k++) {
                    if (field[r][c + k] != player) {
                        all = false;
                        break;
                    }
                }
                if (all)
                    return true;
            }
        }

        // vertical
        for (int r = 0; r <= rows - win; r++) {
            for (int c = 0; c < cols; c++) {
                boolean all = true;
                for (int k = 0; k < win; k++) {
                    if (field[r + k][c] != player) {
                        all = false;
                        break;
                    }
                }
                if (all)
                    return true;
            }
        }

        // down-right diagonal
        for (int r = 0; r <= rows - win; r++) {
            for (int c = 0; c <= cols - win; c++) {
                boolean all = true;
                for (int k = 0; k < win; k++) {
                    if (field[r + k][c + k] != player) {
                        all = false;
                        break;
                    }
                }
                if (all)
                    return true;
            }
        }

        // up-right diagonal
        for (int r = win - 1; r < rows; r++) {
            for (int c = 0; c <= cols - win; c++) {
                boolean all = true;
                for (int k = 0; k < win; k++) {
                    if (field[r - k][c + k] != player) {
                        all = false;
                        break;
                    }
                }
                if (all)
                    return true;
            }
        }

        return false;
    }

    public void dropStone(int col, int stone) {
        for (int row = field.length - 1; row >= 0; row--) {
            if (field[row][col] == 0) {
                field[row][col] = stone;
                break;
            }
        }
    }

    public void display() {
        for (int[] row : field) {
            System.out.print(" |");
            for (int element : row) {
                if (element == 0) {
                    System.out.print(" |");
                } else {
                    System.out.print(InOutput.stone[element - 1] + "|");
                }
            }
            System.out.println();
        }
    }
}
