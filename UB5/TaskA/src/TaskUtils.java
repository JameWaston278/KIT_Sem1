package kit.edu.kastel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility class providing static helper methods for Task operations.
 * Handles ancestry checks and list filtering for tree structures.
 *
 * @author udqch
 */
public final class TaskUtils {

    /** Private constructor. */
    private TaskUtils() {
    }

    /**
     * Checks if a potential ancestor is indeed an ancestor of the target task.
     * Used to prevent circular dependencies (cycles) in the task tree.
     *
     * @param potentialAncestor The task suspected to be the ancestor.
     * @param target            The task to check (the potential descendant).
     * @return true if potentialAncestor is found in target's parent hierarchy.
     */
    public static boolean isAncestor(Task potentialAncestor, Task target) {
        Task current = target.getParent();
        while (current != null) {
            if (current.equals(potentialAncestor)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }

    /**
     * Checks if any ancestor of the given task is present in the provided list.
     * Used to determine if a task should be displayed as a root in a filtered view.
     *
     * @param task The task to check.
     * @param list The list of tasks containing potential ancestors.
     * @return true if an ancestor is found in the list.
     */
    public static boolean hasAncestorInList(Task task, List<Task> list) {
        Task parent = task.getParent();
        while (parent != null) {
            if (list.contains(parent)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    /**
     * Filters a list of tasks to return only the "visual roots".
     * A visual root is a task whose parent is NOT present in the input list.
     * This is essential for printing correct indentation trees.
     *
     * @param inputList The raw list of tasks (e.g., search results).
     * @return A list of tasks that serve as roots for the display tree.
     */
    public static List<Task> filterRoots(List<Task> inputList) {
        List<Task> rootTasks = new ArrayList<>();
        for (Task task : inputList) {
            // If the task has NO ancestor in the current list, it is a root for this
            // specific view
            if (!TaskUtils.hasAncestorInList(task, inputList)) {
                rootTasks.add(task);
            }
        }
        return rootTasks;
    }

    /**
     * Checks if a task has any active (not done, not deleted) descendants.
     * Used for the 'todo' command logic.
     *
     * @param task The task to check.
     * @return true if active descendants exist.
     */
    public static boolean hasActiveDescendant(Task task) {
        for (Task sub : task.getSubtasks()) {
            if (sub.isDeleted()) {
                continue;
            }
            if (!sub.isDone() || hasActiveDescendant(sub)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any ancestor matches the condition and is active.
     * 
     * @param task      Current task.
     * @param condition The condition.
     * @return true if pass.
     */
    public static boolean hasActiveMatchingAncestor(Task task, Predicate<Task> condition) {
        return searchAncestor(task, t -> !t.isDone() && condition.test(t));
    }

    /**
     * Checks if any ancestor matches the condition (Ignores Done state).
     * Used for Time-based searches where we want to see full context even if tasks
     * are done.
     * 
     * @param task      Current task.
     * @param condition The condition.
     * @return true if pass.
     */
    public static boolean hasMatchingAncestor(Task task, Predicate<Task> condition) {
        return searchAncestor(task, condition);
    }

    // --- HELPER METHODS ---
    /**
     * Generic helper to traverse up the parent chain.
     * * @param task The starting task.
     * 
     * @param matcher The combined condition to check.
     * @return true if any ancestor matches.
     */
    private static boolean searchAncestor(Task task, Predicate<Task> matcher) {
        Task current = task.getParent();
        while (current != null) {
            // Luôn luôn kiểm tra isDeleted() ở đây để tránh lặp lại ở các hàm gọi
            if (!current.isDeleted() && matcher.test(current)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}