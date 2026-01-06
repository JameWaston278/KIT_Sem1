import java.util.Scanner;

public class Postamt {
    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            do {
                String rawCommand = input.nextLine();
                Command cmd = new Command(rawCommand);
                if (cmd.isValid()) {
                    if (cmd.getName().equals("quit")) {
                        break;
                    } else {
                        cmd.execute();
                    }
                } else {
                    System.out.println("ERROR, incorrect input format.");
                }
            } while (true);
        }
    }
}