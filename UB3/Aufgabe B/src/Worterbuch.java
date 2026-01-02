import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * this program is an online German-English dictionary, which has most of basic
 * features of an online dictionary like translate a word/ a sentence (word by
 * word), seach for vocabulary. In addition to some existing words this program
 * allows user to add new words or remove a existing one.
 * 
 * @author udqch
 * @since 16.12.2025
 */
public class Worterbuch {
    /**
     * function determines if the inputed command valid and the format of
     * corresponding command correct (like "add" must be followed by a word) are.
     * 
     * @param command inputed command, which need to be validated
     * @return true, if inputed command meets standard
     */
    public static boolean isValidCommand(String command) {
        if (command == null) {
            return false;
        }

        String[] parts = command.split(" ");
        return switch (parts[0].toLowerCase()) {
            case "add" -> parts.length == 3;
            case "remove" -> parts.length == 2;
            case "print" -> parts.length == 1 || (parts.length == 2 && parts[1].length() == 1);
            case "translate" -> parts.length >= 2;
            default -> false;
        };
    }

    /**
     * main-function of the program. This function's mission is reading existing
     * list of words and then stores it in an efficient data structure in order to
     * be easily accessed and edited.
     * 
     * @param args inputed path of file
     * @throws IOException if reading the word list fails
     */
    public static void main(String[] args) throws IOException {
        Dictionary dict = new Dictionary();
        List<String> inputList;

        if (args.length == 0) {
            System.out.println("ERROR: Too less arguments.");
        } else if (args.length > 1) {
            System.out.println("ERROR: Too much arguments.");
        } else {
            File fi = new File(args[0]);
            if (fi.exists() && !fi.isDirectory()) {
                inputList = Files.readAllLines(Path.of(args[0]));
                // push list into dictionary
                for (String element : inputList) {
                    String[] splitString = element.split(" - ");
                    if (splitString.length == 2) {
                        dict.add(splitString[0], splitString[1]);
                    } else {
                        System.out.println("ERROR: Invalid argument in file. Please correct your argument's pattern.");
                        break;
                    }
                }
            } else {
                System.out.println("ERROR: File not found.");
            }
        }

        // main program, get inputs from user and process them
        try (Scanner input = new Scanner(System.in)) {
            do {
                String command = input.nextLine();
                if (command.equals("quit")) {
                    break;
                } else {
                    if (isValidCommand(command)) {
                        dict.execute(command);
                    } else {
                        System.out.println("ERROR: Invalid command. Please try again.");
                    }
                }
            } while (true);
        }

    }
}