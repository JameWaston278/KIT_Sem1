
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Utility class responsible for searching tasks and determining their
 * visibility
 * based on specific criteria and modes.
 * Implements a "Mark-and-Sweep" approach to handle complex hierarchy visibility
 * rules.
 *
 * @author udqch
 */
public final class TaskSearcher {

    /**
     * Defines how the search should handle task expansion (children visibility).
     */
    public enum SearchMode {
        /**
         * Strict mode (e.g., for 'find').
         * Only expands children if the parent is NOT done.
         * Enforces the "Collapse Done Tasks" rule.
         */
        STRICT,

        /**
         * Lax mode (e.g., for 'upcoming', 'tagged-with').
         * Always expands children to show full context, regardless of Done state.
         */
        LAX
    }

    /** Private constructor. */
    private TaskSearcher() {

    }

    /**
     * Creates a visibility filter (Predicate) based on the search criteria.
     * The filter returns true if a task should be displayed in the result tree.
     *
     * @param allTasks The map of all tasks in the system.
     * @param criteria The condition to find "Direct Hits" (e.g., name matches, tag
     *                 matches).
     * @param mode     The expansion mode (STRICT or LAX).
     * @return A Predicate that returns true if a task ID is in the visible set.
     */
    public static Predicate<Task> createVisibilityFilter(Map<Integer, Task> allTasks,
            Predicate<Task> criteria,
            SearchMode mode) {
        Set<Integer> visibleIds = new HashSet<>();

        // 1. Identify "Direct Hits"
        for (Task task : allTasks.values()) {
            if (!task.isDeleted() && criteria.test(task)) {
                if (mode == SearchMode.STRICT && task.getParent() != null && task.getParent().isDone()) {
                    continue;
                }
                visibleIds.add(task.getId());

                // 2. Expand Down (Mark Descendants)
                boolean shouldExpand = false;
                if (mode == SearchMode.LAX) {
                    shouldExpand = true;
                } else if (mode == SearchMode.STRICT && !task.isDone()) {
                    shouldExpand = true;
                }

                if (shouldExpand) {
                    markDescendants(task, visibleIds);
                }
            }
        }

        // Return the filter
        return task -> visibleIds.contains(task.getId());
    }

    /**
     * Recursively marks all non-deleted descendants of a task as visible.
     *
     * @param task       The parent task.
     * @param visibleIds The set of visible IDs to update.
     */
    private static void markDescendants(Task task, Set<Integer> visibleIds) {
        for (Task sub : task.getSubtasks()) {
            if (!sub.isDeleted()) {
                visibleIds.add(sub.getId());
                markDescendants(sub, visibleIds);
            }
        }
    }
}