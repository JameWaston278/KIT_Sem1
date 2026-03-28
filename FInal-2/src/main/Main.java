package main;

/**
 * Main class to start the Ski Route Management System.
 * 
 * @author udqch
 */
public final class Main {

    private Main() {
        // Private constructor to prevent instantiation
    }

    /**
     * The main method to start the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            cli.SystemCLI systemCLI = new cli.SystemCLI();
            systemCLI.start();
        } catch (exceptions.SkiException e) {
            System.out.println(e.getMessage());
        }
    }
}