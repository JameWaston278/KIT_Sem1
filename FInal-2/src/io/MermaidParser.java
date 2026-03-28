package io;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import domain.graph.Difficulty;
import domain.graph.Lift;
import domain.graph.LiftType;
import domain.graph.Node;
import domain.graph.Piste;
import domain.graph.SkiGraph;
import domain.graph.Surface;
import exceptions.ParseError;
import exceptions.ParseException;
import utils.EnumParser;

/****
 * The MermaidParser class is responsible for parsing a Mermaid graph definition
 * from a specified file and constructing a SkiGraph object representing the ski
 * resort. The input file should follow a specific format where nodes represent
 * lifts and pistes, and edges represent connections between them. The parser
 * validates the input format and constructs the graph accordingly, throwing
 * exceptions if any errors are encountered during parsing.
 *
 * @author udqch
 */
public class MermaidParser {

    private static final String GRAPH_START = "graph";
    private static final Pattern TRANSIT_LIFT_PATTERN = Pattern.compile(
            "^(\\w+)\\s*\\[\\[\\1<br\\s*/>([A-Z]+);\\s*(\\d{2}:\\d{2});\\s*(\\d{2}:\\d{2});\\s*(\\d+);\\s*(\\d+)\\]\\]$");
    private static final Pattern REGULAR_LIFT_PATTERN = Pattern.compile(
            "^(\\w+)\\s*\\[\\1<br\\s*/>([A-Z]+);\\s*(\\d{2}:\\d{2});\\s*(\\d{2}:\\d{2});\\s*(\\d+);\\s*(\\d+)\\]$");
    private static final Pattern PISTE_PATTERN = Pattern.compile(
            "^(\\w+)\\s*\\(\\[\\1<br\\s*/>([A-Z]+);\\s*([A-Z]+);\\s*(\\d+);\\s*(\\d+)\\]\\)$");
    private static final Pattern EDGE_PATTERN = Pattern.compile(
            "^(\\w+)\\s*-->\\s*(\\w+)$");
    private static final String EDGE_CONNECTION = "-->";
    private static final String CONNECTION = "Connection";

    /**
     * Parses a Mermaid graph definition from the specified file and constructs a
     * SkiGraph object representing the ski resort. The input file should follow
     * a specific format where nodes represent lifts and pistes, and edges
     * represent connections between them.
     *
     * @param contents a list of strings representing the lines of the input file
     * @return a SkiGraph object representing the ski resort defined in the input
     *         file
     * @throws ParseException if there is an error reading the file or if the file
     *                        format is invalid
     */
    public SkiGraph parse(List<String> contents) throws ParseException {
        SkiGraph graph = new SkiGraph();
        List<String> edgeLines = new ArrayList<>();

        if (contents.isEmpty() || !contents.get(0).trim().equals(GRAPH_START)) {
            throw new ParseException(ParseError.INVALID_FILE.getMessage());
        }
        System.out.println(contents.get(0));

        for (int i = 1; i < contents.size(); i++) {
            System.out.println(contents.get(i));
            String line = contents.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }
            if (line.contains(EDGE_CONNECTION)) {
                edgeLines.add(line);
            } else {
                parseNode(line, graph);
            }
        }

        for (String line : edgeLines) {
            parseEdge(line, graph);
        }

        graph.validate();
        return graph;
    }

    private void parseNode(String line, SkiGraph graph) throws ParseException {
        Matcher transitMatcher = TRANSIT_LIFT_PATTERN.matcher(line);
        if (transitMatcher.matches()) {
            graph.addNode(createLift(transitMatcher, true));
            return;
        }

        Matcher regularMatcher = REGULAR_LIFT_PATTERN.matcher(line);
        if (regularMatcher.matches()) {
            graph.addNode(createLift(regularMatcher, false));
            return;
        }

        Matcher pisteMatcher = PISTE_PATTERN.matcher(line);
        if (pisteMatcher.matches()) {
            graph.addNode(createPiste(pisteMatcher));
            return;
        }

        // If the line doesn't match any known pattern, it's an invalid file format
        throw new ParseException(ParseError.INVALID_FORMAT.getMessage(Node.class, line));
    }

    private void parseEdge(String line, SkiGraph graph) throws ParseException {
        Matcher edgeMatcher = EDGE_PATTERN.matcher(line);
        if (edgeMatcher.matches()) {
            String fromId = edgeMatcher.group(1);
            String toId = edgeMatcher.group(2);

            Node fromNode = graph.getNodeById(fromId);
            Node toNode = graph.getNodeById(toId);
            if (fromNode == null || toNode == null) {
                throw new ParseException(ParseError.UNRECOGNIZED_NODE.getMessage(fromId, toId));
            }

            graph.addEdge(fromNode, toNode);
            return;
        }
        throw new ParseException(ParseError.INVALID_FORMAT.getMessage(CONNECTION, line));
    }

    private Lift createLift(Matcher matcher, boolean isTalstation) throws ParseException {
        try {
            return new Lift(
                    matcher.group(1),
                    EnumParser.parseEnum(LiftType.class, matcher.group(2))
                            .orElseThrow(() -> new ParseException(
                                    ParseError.INVALID_FORMAT.getMessage(LiftType.class, matcher.group(1)))),
                    LocalTime.parse(matcher.group(3)),
                    LocalTime.parse(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)),
                    Integer.parseInt(matcher.group(6)),
                    isTalstation);
        } catch (DateTimeParseException e) {
            throw new ParseException(ParseError.INVALID_FORMAT.getMessage(LocalTime.class, matcher.group(1)));
        } catch (NumberFormatException e) {
            throw new ParseException(ParseError.INVALID_FORMAT.getMessage(Integer.class, matcher.group(1)));
        }
    }

    private Piste createPiste(Matcher matcher) throws ParseException {
        try {
            return new Piste(
                    matcher.group(1),
                    EnumParser.parseEnum(Difficulty.class, matcher.group(2))
                            .orElseThrow(() -> new ParseException(
                                    ParseError.INVALID_FORMAT.getMessage(Difficulty.class, matcher.group(1)))),
                    EnumParser.parseEnum(Surface.class, matcher.group(3))
                            .orElseThrow(() -> new ParseException(
                                    ParseError.INVALID_FORMAT.getMessage(Surface.class, matcher.group(1)))),
                    Integer.parseInt(matcher.group(4)),
                    Integer.parseInt(matcher.group(5)));
        } catch (NumberFormatException e) {
            throw new ParseException(ParseError.INVALID_FORMAT.getMessage(Integer.class, matcher.group(1)));
        }
    }
}