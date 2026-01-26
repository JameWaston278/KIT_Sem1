import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DuplicatesFinder {
    private static final String DELIMITER = ", ";

    public static String findDuplicates(Map<Integer, Task> allTasks) {
        Set<Integer> dupicateIds = new TreeSet<>();
        List<Task> taskList = new ArrayList<>(allTasks.values());

        for (int i = 0; i < taskList.size(); i++) {
            for (int j = i + 1; j < taskList.size(); j++) {
                Task t1 = taskList.get(i);
                Task t2 = taskList.get(j);

                if (isDuplicate(t1, t2)) {
                    dupicateIds.add(t1.getId());
                    dupicateIds.add(t2.getId());
                }
            }
        }

        if (dupicateIds.isEmpty()) {
            return SystemMessage.NO_DUPLICATE.format();
        }
        String idListStr = dupicateIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(DELIMITER));
        return SystemMessage.TASK_DUPLICATE.format(dupicateIds.size(), idListStr);
    }

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
