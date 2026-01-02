
import java.util.Scanner;

public class ConnectFour {

    public static void main(String[] args) {
        // set up for the game
        InOutput game = new InOutput();
        PlayingField field = new PlayingField();

        game.getTotalPlayers(args);
        if (InOutput.error.equals("none")) {
            field.display();
            // Game Start
            try (Scanner input = new Scanner(System.in)) {
                int turn = 1;
                int player = 1;

                // player's choice
                while (true) {
                    System.out.println(turn + ". Zug, Spieler " + player + ":");
                    String command = input.nextLine();
                    if (command.equals("quit")) {
                        break;
                    } else {
                        while (field.validate(command) == false) {
                            if (command.equals("quit")) {
                                break;
                            }
                            System.out.println("ERROR: " + InOutput.error);
                            System.out.println(turn + ". Zug, Spieler " + player + ":");
                            command = input.nextLine();
                        }
                        if (command.equals("quit")) {
                            break;
                        }
                        field.dropStone(Integer.parseInt(command) - 1, player);
                        field.display();
                        boolean winner = field.isWinner(player);
                        if (field.isFull() == false && winner == false) {
                            // switch player
                            turn++;
                            if (player == game.totalPlayers) {
                                player = 1;
                            } else {
                                player++;
                            }
                        } else {
                            if (winner == true) {
                                System.out.println("Sieger: Spieler " + player);
                            } else {
                                System.out.println("Unentschieden!");
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}