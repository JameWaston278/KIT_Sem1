package kit.edu.kastel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class for formatting tasks into strings for display.
 * Handles indentation, tree structure, and sorting.
 *
 * @author udqch
 */
public final class TaskFormatter {

    /**
     * Comparator to sort tasks first by priority (HI > MD > LO > None),
     * then by ID (ascending).
     */
    public static final Comparator<Task> TASK_COMPARATOR = (t1, t2) -> {
        int p1 = getPriorityRank(t1.getPriority());
        int p2 = getPriorityRank(t2.getPriority());

        int priorityResult = Integer.compare(p1, p2);
        if (priorityResult != 0) {
            return priorityResult;
        }
        return 0;
    };
    // ---CONSTANTS---
    private static final String NONE = "NONE";
    private static final String INDENT_UNIT = "  ";
    private static final String NEWLINE = "\n";
    private static final String CHECKBOX_DONE = "- [x] ";
    private static final String CHECKBOX_TODO = "- [ ] ";
    private static final String PRIORITY_OPEN = " [";
    private static final String PRIORITY_CLOSE = "]";
    private static final String SEPARATOR = ":";
    private static final String TAG_OPEN = " (";
    private static final String TAG_CLOSE = ")";
    private static final String TAG_DELIMITER = ", ";
    private static final String DEADLINE_PREFIX = " --> ";

    /** Private constructor. */
    private TaskFormatter() {
    }

    /**
     * Helper method to get priority rank.
     * 
     * @param p The input priority.
     * @return The rank of priority.
     */
    private static int getPriorityRank(Priority p) {
        if (p == null) {
            return 3;
        }
        return switch (p.name()) {
            case "HI" -> 0;
            case "MD" -> 1;
            case "LO" -> 2;
            default -> 3;
        };
    }

    /**
     * Formats a nullable object to "NONE" or its string representation.
     *
     * @param value The object to format.
     * @return The formatted string.
     */
    public static String formatNullValue(Object value) {
        return (value == null) ? NONE : value.toString();
    }

    /**
     * Formats a single task line with indentation.
     *
     * @param task  The task to format.
     * @param level The indentation level.
     * @return The formatted string.
     */
    public static String formatSingleTask(Task task, int level) {
        StringBuilder result = new StringBuilder();

        result.append(INDENT_UNIT.repeat(level))
                .append(task.isDone() ? CHECKBOX_DONE : CHECKBOX_TODO);

        result.append(task.getName());

        Priority priority = task.getPriority();
        if (priority != null) {
            result.append(PRIORITY_OPEN)
                    .append(priority)
                    .append(PRIORITY_CLOSE);
        }

        Set<String> tags = task.getTags();
        LocalDate deadline = task.getDeadline();
        result.append(!tags.isEmpty() || deadline != null ? SEPARATOR : "");

        if (!tags.isEmpty()) {
            result.append(TAG_OPEN)
                    .append(String.join(TAG_DELIMITER, tags))
                    .append(TAG_CLOSE);
        }

        if (deadline != null) {
            result.append(DEADLINE_PREFIX).append(deadline.toString());
        }
        result.append(NEWLINE);

        return result.toString();
    }

    /**
     * Formats a list of tasks into a hierarchical tree structure.
     *
     * @param taskToPrint List of tasks to print.
     * @param condition   Predicate to filter which subtasks to display.
     * @return The full formatted tree string.
     */
    public static String formatTree(List<Task> taskToPrint, Predicate<Task> condition) {
        if (taskToPrint.isEmpty()) {
            return null;
        }

        taskToPrint.sort(TASK_COMPARATOR);

        StringBuilder result = new StringBuilder();
        for (Task task : taskToPrint) {
            appendRecursive(result, task, 0, condition);
        }

        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    /**
     * Recursive method accesses subtasks of task.
     * 
     * @param result    String of result.
     * @param task      Current task.
     * @param level     Level of task in hierarchical tree strutre.
     * @param condition Predicate to filter.
     */
    private static void appendRecursive(StringBuilder result, Task task, int level, Predicate<Task> condition) {
        result.append(formatSingleTask(task, level));

        List<Task> subtasks = new ArrayList<>(task.getSubtasks());
        subtasks.sort(TASK_COMPARATOR);

        for (Task subtask : subtasks) {
            if (!subtask.isDeleted() && condition.test(subtask)) {
                appendRecursive(result, subtask, level + 1, condition);
            }
        }
    }

}
