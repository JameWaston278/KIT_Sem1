import java.util.ArrayList;
import java.util.List;

public class TaskUtils {
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

    public static List<Task> filterRoots(List<Task> inputList) {
        List<Task> rootTasks = new ArrayList<>();
        for (Task task : inputList) {
            if (!TaskUtils.hasAncestorInList(task, inputList)) {
                rootTasks.add(task);
            }
        }
        return rootTasks;
    }
}
