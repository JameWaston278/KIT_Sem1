
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Procrastinot {

    private final Map<Integer, Task> allTasks;
    private final Map<String, TaskList> userLists;

    public Procrastinot() {
        this.allTasks = new TreeMap<>();
        this.userLists = new TreeMap<>();
    }

    // ---BUSINESS METHODS---

    // Methods for Task

    public String addTask(String name, Priority priority, LocalDate date) throws SystemException {
        Task newTask = new Task(name);

        // set optional parameters
        if (priority != null) {
            newTask.setPriority(priority);
        }
        if (date != null) {
            newTask.setDeadline(date);
        }

        allTasks.put(newTask.getId(), newTask);
        return SystemMessage.SUCCESS_ADD_TASK.format(newTask.getId(), name);
    }

    public String show() {
        return searchWithConditon(task -> task.getParent() == null, child -> true);
    }

    public String todo() {
        return searchWithConditon(task -> task.getParent() == null && !task.isDone(), child -> !child.isDone());
    }

    public String hasTag(String tag) {
        return searchWithConditon(task -> task.hasTag(tag), child -> true);
    }

    public String find(String name) {
        return searchWithConditon(task -> task.hasName(name), child -> true);
    }

    public String searchTime(Predicate<LocalDate> condition) {
        return searchWithConditon(task -> condition.test(task.getDeadline()), child -> true);
    }

    public String duplicates() {
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
                .collect(Collectors.joining(", "));
        return SystemMessage.TASK_DUPLICATE.format(dupicateIds.size(), idListStr);
    }

    // Methods for List

    public String createList(String name) throws SystemException {
        // validate the uniqueness of list
        if (userLists.containsKey(name)) {
            throw new SystemException(SystemMessage.LIST_EXISTS.format(name));
        }

        TaskList newList = new TaskList(name);
        userLists.put(name, newList);
        return SystemMessage.SUCCESS_CREATE_LIST.format(name);
    }

    public String showList(String name) throws SystemException {
        List<Task> taskInList = getList(name).getTasks();

        List<Task> roots = new ArrayList<>();
        for (Task task : taskInList) {
            if (!hasAncestorInList(task, taskInList)) {
                roots.add(task);
            }
        }
        if (roots.isEmpty()) {
            return SystemMessage.LIST_EMPTY.format(name);
        }
        return buildTreeOutput(roots, task -> true);
    }

    // ---HELPER METHODS---

    private TaskList getList(String name) throws SystemException {
        TaskList list = userLists.get(name);
        if (list == null) {
            throw new SystemException(SystemMessage.LIST_NOT_FOUND.format(name));
        }
        return list;
    }

    private boolean hasAncestorInList(Task task, List<Task> list) {
        Task parent = task.getParent();
        while (parent != null) {
            if (list.contains(parent)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    private String buildTreeOutput(List<Task> taskToPrint, Predicate<Task> condition) {
        if (taskToPrint.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        for (Task task : taskToPrint) {
            appendRecursive(result, task, 0, condition);
        }

        return result.toString();
    }

    private void appendRecursive(StringBuilder result, Task task, int level, Predicate<Task> condition) {
        result.append(task.getDetails(level));
        for (Task subtask : task.getSubtasks()) {
            if (!subtask.isDeleted() && condition.test(subtask)) {
                appendRecursive(result, subtask, level + 1, condition);
            }
        }
    }

    private String searchWithConditon(Predicate<Task> searchCondition, Predicate<Task> childCondition) {
        // search all tasks matches the condition
        List<Task> allMatches = allTasks.values().stream()
                .filter(task -> !task.isDeleted() && searchCondition.test(task))
                .collect(Collectors.toList());

        if (allMatches.isEmpty()) {
            return SystemMessage.NO_MATCHED.format();
        }

        // Logic: remove child if its parent is also in list
        List<Task> displayRoots = allMatches.stream()
                .filter(task -> !hasAncestorInList(task, allMatches))
                .collect(Collectors.toList());

        return buildTreeOutput(displayRoots, childCondition);
    }

    private boolean isDuplicate(Task t1, Task t2) {
        if (!t1.getName().equals(t2.getName())) {
            return false;
        }

        boolean hasDeadline1 = (t1.getDeadline() != null);
        boolean hasDeadline2 = (t2.getDeadline() != null);
        return !(hasDeadline1 && hasDeadline2 && !t1.getDeadline().equals(t2.getDeadline()));
    }
}
