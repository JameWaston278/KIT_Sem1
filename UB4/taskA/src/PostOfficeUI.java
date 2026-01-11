import java.util.Scanner;

/**
 * Handles the User Interface (Command Line Interface) for the Post Office.
 * Parses user input, executes commands, and displays results.
 */
public class PostOfficeUI {

    /**
     * Functional interface for executing a command after parsing arguments.
     * This helps reduce code duplication in command handlers.
     */
    @FunctionalInterface
    private interface CommandAction {
        /**
         * Executes a specific command logic.
         *
         * @param args The parsed arguments array.
         * @throws ErrorException If the command execution fails.
         */
        void execute(String[] args) throws ErrorException;
    }

    // --- FIELDS ---
    private final PostOffice postOffice;
    private boolean isRunning;

    // --- CONSTRUCTOR ---

    /**
     * Initializes the UI with a PostOffice instance.
     *
     * @param postOffice The logic controller to interact with.
     */
    public PostOfficeUI(PostOffice postOffice) {
        this.postOffice = postOffice;
        this.isRunning = true;
    }

    // --- MAIN LOOP ---

    /**
     * Starts the main event loop to process user input.
     * Runs until 'quit' command is issued.
     */
    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (isRunning && scanner.hasNextLine()) {
                String inputCommand = scanner.nextLine();
                try {
                    dispatchCommand(inputCommand);
                } catch (ErrorException e) {
                    System.out.println("ERROR, " + e.getMessage());
                }
            }
        }
    }

    // --- COMMAND DISPATCHER ---

    /**
     * Parses the raw input string and dispatches it to the appropriate handler.
     *
     * @param input The full line of text entered by the user.
     * @throws ErrorException If the command is unknown or invalid.
     */
    public void dispatchCommand(String input) throws ErrorException {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];
        String commandArgs = (parts.length > 1) ? parts[1] : "";

        switch (commandName) {
            case "add-customer" -> addCustomer(commandArgs);
            case "add-mailman" -> addMailman(commandArgs);
            case "add-agent" -> addAgent(commandArgs);
            case "authenticate" -> authenticate(commandArgs);
            case "logout" -> logout(commandArgs);
            case "send-mail" -> sendMail(commandArgs);
            case "get-mail" -> getMail(commandArgs);
            case "list-mail" -> listMail(commandArgs);
            case "list-price" -> listPrice(commandArgs);
            case "reset-pin" -> resetPin(commandArgs);
            case "quit" -> {
                parseArguments(commandArgs, 0);
                this.isRunning = false;
            }
            default -> throw new ErrorException("unknown command.");
        }
    }

    // --- SPECIFIC COMMAND HANDLERS ---

    /**
     * Handler for 'add-customer' command.
     * Expected params: FirstName;LastName;Username;Password;IDCard
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If arguments are invalid.
     */
    private void addCustomer(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 5,
                args -> postOffice.addCustomer(args[0], args[1], args[2], args[3], args[4]));
        System.out.println("OK");
    }

    /**
     * Handler for 'add-mailman' command.
     * Expected params: FirstName;LastName;personnelId;Password
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If arguments are invalid.
     */
    private void addMailman(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 4,
                args -> postOffice.addMailman(args[0], args[1], args[2], args[3]));
        System.out.println("OK");
    }

    /**
     * Handler for 'add-agent' command.
     * Expected params: FirstName;LastName;personnelId;Password
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If arguments are invalid.
     */
    private void addAgent(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 4,
                args -> postOffice.addAgent(args[0], args[1], args[2], args[3]));
        System.out.println("OK");
    }

    /**
     * Handler for 'authenticate' command.
     * Expected params: Username;Password
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If login fails.
     */
    private void authenticate(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 2,
                args -> postOffice.authenticate(args[0], args[1]));
        System.out.println("OK");
    }

    /**
     * Handler for 'logout' command.
     * Expected params: None.
     *
     * @param commandArgs The raw parameter string (should be empty).
     * @throws ErrorException If logout fails or params exist.
     */
    private void logout(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 0,
                args -> postOffice.logout());
        System.out.println("OK");
    }

    /**
     * Handler for 'reset-pin' command.
     * Expected params: TargetUser;IDCard;NewPassword
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If reset fails.
     */
    private void resetPin(String commandArgs) throws ErrorException {
        processCommand(commandArgs, 3,
                args -> postOffice.resetPin(args[0], args[1], args[2]));
        System.out.println("OK");
    }

    /**
     * Handles sending mail. Logic differs based on user role.
     * Customers send to target (2 args), Employees send on behalf of sender (3
     * args).
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If role is unauthorized or params invalid.
     */
    private void sendMail(String commandArgs) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(commandArgs, 2,
                    args -> postOffice.sendMail(args[0], args[1], currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(commandArgs, 3,
                    args -> postOffice.sendMail(args[0], args[1], args[2]));
        } else {
            throw new ErrorException("unauthorized role.");
        }
        System.out.println("OK");
    }

    /**
     * Handles getting mail.
     * Customers get their own mail (0 args), Employees get mail for a target (1
     * arg).
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If role is unauthorized or params invalid.
     */
    private void getMail(String commandArgs) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(commandArgs, 0,
                    args -> postOffice.getMail(currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(commandArgs, 1,
                    args -> postOffice.getMail(args[0]));
        } else {
            throw new ErrorException("unauthorized role.");
        }
        System.out.println("OK");
    }

    /**
     * Handles listing mail content. Does NOT print "OK" because it prints a list.
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If role is unauthorized.
     */
    private void listMail(String commandArgs) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(commandArgs, 0, args -> postOffice.listMail(currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(commandArgs, 1, args -> postOffice.listMail(args[0]));
        } else {
            throw new ErrorException("unauthorized role.");
        }
    }

    /**
     * Handles listing mail price history. Does NOT print "OK".
     *
     * @param commandArgs The raw parameter string.
     * @throws ErrorException If role is unauthorized.
     */
    private void listPrice(String commandArgs) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(commandArgs, 0, args -> postOffice.listPrice(currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(commandArgs, 1, args -> postOffice.listPrice(args[0]));
        } else {
            throw new ErrorException("unauthorized role.");
        }
    }

    // --- PRIVATE HELPERS ---

    /**
     * Helper to parse arguments and execute an action.
     *
     * @param commandArgs  The string containing parameters separated by semicolons.
     * @param expectedArgs The expected number of parameters.
     * @param action       The lambda function containing the logic to execute.
     * @throws ErrorException If argument parsing or execution fails.
     */
    private void processCommand(String commandArgs, int expectedArgs, CommandAction action) throws ErrorException {
        String[] args = parseArguments(commandArgs, expectedArgs);
        action.execute(args);
    }

    /**
     * Parses the parameter string into an array, checking for validity.
     *
     * @param commandArgs   Raw parameter string (e.g., "Arg1; Arg2").
     * @param expectedCount How many arguments are expected.
     * @return An array of trimmed argument strings.
     * @throws ErrorException If format is invalid or count is wrong.
     */
    private String[] parseArguments(String commandArgs, int expectedCount) throws ErrorException {
        // Case: No arguments expected (e.g., logout, quit)
        if (expectedCount == 0) {
            if (!commandArgs.isEmpty()) {
                throw new ErrorException("this command accepts no parameters.");
            } else {
                return new String[0];
            }
        }

        // Case: Arguments expected but input is empty
        if (commandArgs.isEmpty()) {
            throw new ErrorException("invalid number of arguments. Expected " + expectedCount + " but got 0");
        }

        // Logic: Split by semicolon, preserve empty strings (limit = -1) to detect
        // errors like "a;;"
        String[] args = commandArgs.split(";", -1);

        // Logic: Trim whitespace around parameters (e.g., " Max " -> "Max")
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }

        if (args.length != expectedCount) {
            throw new ErrorException(
                    "invalid number of arguments. Expected " + expectedCount + " but got " + args.length);
        }
        return args;
    }
}