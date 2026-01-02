import java.util.Objects;
import java.util.Scanner;

public class taskD {
    public static void main(String[] args) {
        try (Scanner game = new Scanner(System.in)) {

            String player1 = game.nextLine();
            String player2 = game.nextLine();
            game.close();

            player1 = player1.toLowerCase();
            player2 = player2.toLowerCase();

            switch (player1) {
                case "scissors" -> {
                    if (Objects.equals(player2, "paper"))
                        System.out.println("Winner: scissors!");
                    else if (Objects.equals(player2, "rock"))
                        System.out.println("Winner: rock!");
                    else
                        System.out.println("No winner...");
                    break;
                }
                case "rock" -> {
                    if (Objects.equals(player2, "scissors"))
                        System.out.println("Winner: rock!");
                    else if (Objects.equals(player2, "paper"))
                        System.out.println("Winner: paper!");
                    else
                        System.out.println("No winner...");
                    break;
                }
                default -> {
                    if (Objects.equals(player2, "rock"))
                        System.out.println("Winner: paper!");
                    else if (Objects.equals(player2, "scissors"))
                        System.out.println("Winner: scissors!");
                    else
                        System.out.println("No winner...");
                    break;
                }
            }
        }
    }
}
