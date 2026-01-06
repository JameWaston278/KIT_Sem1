import java.util.Scanner;

public class PostOfficeUI {
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
                } catch (ErrorExeption e) {
                    System.out.println("ERROR, " + e.getMessage());
                }
            }
        }
    }

    public void checkNoParams(String rawParams) throws ErrorExeption {
        if (!rawParams.isEmpty()) {
            throw new ErrorExeption("incorrect input format, this command does not accept any parameters.");
        }
    }

    public void parseCommand(String input) throws ErrorExeption {
        String[] parts = input.split(" ", 2);
        String commandName = parts[0];

        String paramsRaw = (parts.length > 1) ? parts[1] : "";

        switch (commandName) {
            case "add-customer" -> addCustomer(paramsRaw);
            case "quit" -> {
                checkNoParams(paramsRaw);
                this.isRunning = false;
            }
            default -> throw new ErrorExeption("unkonwn command.");
        }
    }

    private String[] parseArguments(String rawParams, int expectedCount) throws ErrorExeption {
        if (expectedCount == 0 && !rawParams.isEmpty()) {
            throw new ErrorExeption("incorrect input format, this command does not accept any parameters.");
        }

        String[] args = rawParams.split(";", -1);
        if (args.length != expectedCount) {
            throw new ErrorExeption(
                    "invalid number of arguments. Expected " + expectedCount + " but got " + args.length);
        }

        return args;
    }

    private void addCustomer(String rawParams) throws ErrorExeption {
        String[] args = parseArguments(rawParams, 5);

        postOffice.addCustomer(args[0], args[1], args[2], args[3], args[4]);

        System.out.println("OK");
    }
}
