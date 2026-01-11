import java.util.Scanner;

public class PostOfficeUI {
    @FunctionalInterface
    private interface CommandAction {
        void execute(String[] args) throws ErrorException;
    }

    private final PostOffice postOffice;
    private boolean isRunning;

    public PostOfficeUI(PostOffice postOffice) {
        this.postOffice = postOffice;
        this.isRunning = true;
    }

    public void run() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (isRunning && scanner.hasNextLine()) {
                String inputCommand = scanner.nextLine();
                try {
                    parseCommand(inputCommand);
                } catch (ErrorException e) {
                    System.out.println("ERROR, " + e.getMessage());
                }
            }
        }
    }

    public void checkNoParams(String rawParams) throws ErrorException {
        if (!rawParams.isEmpty()) {
            throw new ErrorException("incorrect input format, this command does not accept any parameters.");
        }
    }

    public void parseCommand(String input) throws ErrorException {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];

        String rawParams = (parts.length > 1) ? parts[1] : "";

        switch (commandName) {
            case "add-customer" -> addCustomer(rawParams);
            case "add-mailman" -> addMailman(rawParams);
            case "add-agent" -> addAgent(rawParams);
            case "authenticate" -> authenticate(rawParams);
            case "logout" -> logout(rawParams);
            case "send-mail" -> sendMail(rawParams);
            case "get-mail" -> getMail(rawParams);
            case "list-mail" -> listMail(rawParams);
            case "list-price" -> listPrice(rawParams);
            case "reset-pin" -> resetPin(rawParams);
            case "quit" -> {
                parseArguments(rawParams, 0);
                this.isRunning = false;
            }
            default -> throw new ErrorException("unknown command.");
        }
    }

    private String[] parseArguments(String rawParams, int expectedCount) throws ErrorException {
        if (expectedCount == 0) {
            if (!rawParams.isEmpty()) {
                throw new ErrorException("incorrect input format, this command does not accept any parameters.");
            } else {
                return new String[0];
            }
        }

        if (rawParams.isEmpty()) {
            throw new ErrorException(
                    "invalid number of arguments. Expected " + expectedCount + " but got 0");
        }

        String[] args = rawParams.split(";", -1);
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        if (args.length != expectedCount) {
            throw new ErrorException(
                    "invalid number of arguments. Expected " + expectedCount + " but got " + args.length);
        }
        return args;
    }

    private void processCommand(String rawParams, int expectedArgs, CommandAction action) throws ErrorException {
        String[] args = parseArguments(rawParams, expectedArgs);

        action.execute(args);
    }

    private void addCustomer(String rawParams) throws ErrorException {
        processCommand(rawParams, 5,
                args -> postOffice.addCustomer(args[0], args[1], args[2], args[3], args[4]));
        System.out.println("OK");
    }

    private void addMailman(String rawParams) throws ErrorException {
        processCommand(rawParams, 4, args -> postOffice.addMailman(args[0], args[1], args[2], args[3]));
        System.out.println("OK");
    }

    private void addAgent(String rawParams) throws ErrorException {
        processCommand(rawParams, 4, args -> postOffice.addAgent(args[0], args[1], args[2], args[3]));
        System.out.println("OK");
    }

    private void authenticate(String rawParams) throws ErrorException {
        processCommand(rawParams, 2, args -> postOffice.authenticate(args[0], args[1]));
        System.out.println("OK");
    }

    private void logout(String rawParams) throws ErrorException {
        processCommand(rawParams, 0, args -> postOffice.logout());
        System.out.println("OK");
    }

    private void sendMail(String rawParams) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(rawParams, 2, args -> postOffice.sendMail(args[0], args[1], currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(rawParams, 3, args -> postOffice.sendMail(args[0], args[1], args[2]));
        } else {
            throw new ErrorException(
                    "operation failed for unauthorized role.");
        }
        System.out.println("OK");
    }

    private void getMail(String rawParams) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(rawParams, 0, args -> postOffice.getMail(currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(rawParams, 1, args -> postOffice.getMail(args[0]));
        } else {
            throw new ErrorException(
                    "operation failed for unauthorized role.");
        }
        System.out.println("OK");
    }

    private void listMail(String rawParams) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(rawParams, 0, args -> postOffice.listMail(currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(rawParams, 1, args -> postOffice.listMail(args[0]));
        } else {
            throw new ErrorException(
                    "operation failed for unauthorized role.");
        }
    }

    private void listPrice(String rawParams) throws ErrorException {
        User currentUser = postOffice.getCurrentUser();
        if (currentUser instanceof Customer) {
            processCommand(rawParams, 0, args -> postOffice.listPrice(currentUser.getID()));
        } else if (currentUser instanceof Employee) {
            processCommand(rawParams, 1, args -> postOffice.listPrice(args[0]));
        } else {
            throw new ErrorException(
                    "operation failed for unauthorized role.");
        }
    }

    private void resetPin(String rawParams) throws ErrorException {
        processCommand(rawParams, 3, args -> postOffice.resetPin(args[0], args[1], args[2]));
        System.out.println("OK");
    }
}
