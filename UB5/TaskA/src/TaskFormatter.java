
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class TaskFormatter {
    // ---CONSTANTS---
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

    public static final Comparator<Task> TASK_COMPARATOR = Comparator
            .<Task, Priority>comparing(task -> task.getPriority(),
                    Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingInt(task -> task.getId());

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

    public static String formatTree(List<Task> taskToPrint, Predicate<Task> condition) {
        if (taskToPrint.isEmpty()) {
            return null;
        }

        taskToPrint.sort(TASK_COMPARATOR);

        StringBuilder result = new StringBuilder();
        for (Task task : taskToPrint) {
            appendRecursive(result, task, 0, condition);
        }

        return result.toString();
    }

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
