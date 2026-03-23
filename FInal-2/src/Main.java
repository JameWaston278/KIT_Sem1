
import domain.graph.SkiGraph;
import exceptions.ParseException;
import io.MermaidParser;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java Main <mermaid_file_path>");
            return;
        }

        String filePath = args[0];
        MermaidParser parser = new MermaidParser();
        try {
            SkiGraph graph = parser.parse(filePath);
        } catch (ParseException e) {
            System.err.println("Error parsing the mermaid file: " + e.getMessage());
        }
    }
}