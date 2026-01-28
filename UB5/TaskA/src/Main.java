package kit.edu.kastel;

import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The entry point of the Procrastinot application.
 * Initializes the application logic and starts the command handler.
 *
 * @author udqch
 */
public final class Main {
    /** Private constructor. */
    private Main() {
    }

    /**
     * Main method to start the application.
     * Can read from a file (for testing) or standard input (for interactive use).
     *
     * @param args Command line arguments (not used).
     * @throws SystemException       If a system error occurs during initialization.
     * @throws FileNotFoundException If the input file is not found (when testing
     *                               with files).
     */
    public static void main(String[] args) throws SystemException, FileNotFoundException {
        Procrastinot app = new Procrastinot();

        // Uncomment for interactive mode:
        try (Scanner scanner = new Scanner(System.in)) {
            CommandHandler shell = new CommandHandler(app, scanner);
            shell.run();
        }

        // Test mode using file input:
        // Scanner scanner = new Scanner(new File("test.txt"));
        // CommandHandler shell = new CommandHandler(app, scanner);
        // shell.run();
    }
}