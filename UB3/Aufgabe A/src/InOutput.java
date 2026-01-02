public class InOutput {
    int totalPlayers;
    public static int winCondition;
    public static String[] stone = new String[7]; // array stores symbol of player's stone
    public static String error;

    // default values for variables
    public InOutput() {
        totalPlayers = 2;
        winCondition = 4;
        error = "none";
    }

    // identify number of players from input String
    public void getTotalPlayers(String[] args) {

        // default number of players
        if (args.length == 0) {
            stone[0] = "x";
            stone[1] = "o";
        } else if (args.length >= 1 && args.length <= 6) {// totalPlayers limit [1,6]?
            if (validate(args) == true) {
                totalPlayers = args.length;
                winCondition = Math.min(4, (int) Math.ceil(9.0 / totalPlayers));
                getStone(args);
            } else {
                System.out.println("ERROR: " + error);
            }
        } else {
            error = "Total players must be between 1 and 6. Please try again!";
            System.out.println("ERROR: " + error);
        }
    }

    public boolean validate(String[] stoneString) {
        // a symbol with >2 characters
        for (String s : stoneString) {
            if (s.length() >= 2) {
                error = "A stone of any players may not have more than two characters";
                return false;
            }
        }

        // identical symbol
        for (int i = 0; i < stoneString.length; i++) {
            for (int j = i + 1; j < stoneString.length; j++) {
                if (stoneString[i].equals(stoneString[j]) == true) {
                    error = "Two players may not have the same stone";
                    return false;
                }
            }
        }

        return true;
    }

    public void getStone(String[] stoneString) {
        int len = totalPlayers;
        System.arraycopy(stoneString, 0, stone, 0, len);
    }
}