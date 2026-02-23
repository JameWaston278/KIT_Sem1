
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class to find duplicate tasks.
 * Duplicates are defined as tasks with the same name and compatible deadlines.
 *
 * @author udqch
 */
public final class DuplicatesFinder {
    private static final String DELIMITER = ", ";

    /** Private constructor. */
    private DuplicatesFinder() {
    }

    /**
     * Finds duplicates across all tasks in the system.
     *
     * @param allTasks The map of all tasks.
     * @return A formatted string listing IDs of duplicate tasks.
     */
    public static String findDuplicates(Map<Integer, Task> allTasks) {
        Set<Integer> duplicateIds = new TreeSet<>();
        List<Task> taskList = new ArrayList<>(allTasks.values());

        for (int i = 0; i < taskList.size(); i++) {
            for (int j = i + 1; j < taskList.size(); j++) {
                Task t1 = taskList.get(i);
                Task t2 = taskList.get(j);

                if (isDuplicate(t1, t2)) {
                    duplicateIds.add(t1.getId());
                    duplicateIds.add(t2.getId());
                }
            }
        }

        if (duplicateIds == null || duplicateIds.isEmpty()) {
            return SystemMessage.TASK_DUPLICATE.format(0);
        }

        StringBuilder result = new StringBuilder();
        int count = 0;
        for (Integer id : duplicateIds) {
            result.append(id);
            count++;
            if (count < duplicateIds.size()) {
                result.append(DELIMITER);
            }
        }
        String idListStr = result.toString();
        return SystemMessage.TASK_DUPLICATE.format(duplicateIds.size(), idListStr);
    }

    /**
     * Checks duplicate between 2 tasks.
     * 
     * @param t1 Tasks 1.
     * @param t2 Tasks 2.
     * @return true if two tasks duplicate.
     */
    private static boolean isDuplicate(Task t1, Task t2) {
        if (!t1.getName().equals(t2.getName())) {
            return false;
        }

        LocalDate deadline1 = t1.getDeadline();
        LocalDate deadline2 = t2.getDeadline();
        return !(deadline1 != null && deadline2 != null
                && !deadline1.equals(deadline2));
    }
}
