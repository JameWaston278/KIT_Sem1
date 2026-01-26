import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SystemException, FileNotFoundException {
        Procrastinot app = new Procrastinot();
        // try (Scanner scanner = new Scanner(System.in)) {
        // CommandHandler shell = new CommandHandler(app, scanner);
        // shell.run();
        // }

        Scanner scanner = new Scanner(new File("test.txt"));
        CommandHandler shell = new CommandHandler(app, scanner);
        shell.run();
    }
}
