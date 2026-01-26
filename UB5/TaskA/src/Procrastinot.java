
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

public class Procrastinot {

    private final Map<Integer, Task> allTasks;
    private final Map<String, TaskList> userLists;

    public Procrastinot() {
        this.allTasks = new TreeMap<>();
        this.userLists = new TreeMap<>();
    }

    // --- BUSINESS METHODS ---

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

    public String tagTask(int id, String tag) throws SystemException {
        Task task = getTask(id);
        task.addTag(tag);
        return SystemMessage.SUCCESS_TAG.format(task.getName(), tag);
    }

    public String assignSubtask(int idChild, int idParent) throws SystemException {
        Task child = getTask(idChild);
        Task parent = getTask(idParent);
        parent.addSubtask(child);
        return SystemMessage.SUCCESS_ASSIGN.format(child.getName(), parent.getName());
    }

    public String toggle(int id) throws SystemException {
        Task task = getTask(id);
        int subCount = Math.max(0, task.toggle() - 1);
        return SystemMessage.SUCCESS_TOGGLE.format(task.getName(), subCount);
    }

    public String changeDeadline(int id, LocalDate date) throws SystemException {
        Task task = getTask(id);
        task.setDeadline(date);
        return SystemMessage.SUCCESS_CHANGE_VALUES.format(task.getName(), formatNullValue(date));
    }

    public String changePriority(int id, Priority priority) throws SystemException {
        Task task = getTask(id);
        task.setPriority(priority);
        return SystemMessage.SUCCESS_CHANGE_VALUES.format(task.getName(), formatNullValue(priority));
    }

    public String deleteTask(int id) throws SystemException {
        Task task = getTask(id);
        int subCount = Math.max(0, task.setDeletedState(true) - 1);
        return SystemMessage.SUCCESS_DELETE.format(task.getName(), subCount);
    }

    public String restoreTask(int id) throws SystemException {
        Task task = getTask(id);
        Task parent = task.getParent();

        if (parent != null && parent.isDeleted()) {
            task.detachFromParent();
            parent = null;
        }
        int subCount = Math.max(0, task.setDeletedState(false) - 1);
        if (parent != null) {
            parent.addSubtask(task);
        }
        return SystemMessage.SUCCESS_RESTORE.format(task.getName(), subCount);
    }

    public String show() {
        return searchWithCondition(task -> task.getParent() == null, child -> true);
    }

    public String show(int id) {
        return searchWithCondition(task -> task.getParent() == null && task.getId() == id, child -> true);
    }

    public String todo() {
        return searchWithCondition(task -> task.getParent() == null && (!task.isDone() || hasActiveDescendant(task)),
                child -> !child.isDone() || hasActiveDescendant(child));
    }

    public String hasTag(String tag) {
        return searchWithCondition(task -> task.hasTag(tag), child -> true);
    }

    public String find(String name) {
        return searchWithCondition(task -> task.hasName(name), child -> true);
    }

    public String searchTime(Predicate<LocalDate> condition) {
        return searchWithCondition(task -> task.getDeadline() != null && condition.test(task.getDeadline()),
                child -> true);
    }

    public String duplicates() {
        return DuplicatesFinder.findDuplicates(this.allTasks);
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

    public String tagList(String name, String tag) throws SystemException {
        TaskList list = getList(name);
        list.addNewTag(tag);
        return SystemMessage.SUCCESS_TAG.format(name, tag);
    }

    public String assignToList(int id, String name) throws SystemException {
        Task task = getTask(id);
        TaskList list = getList(name);
        list.addNewTask(task);
        return SystemMessage.SUCCESS_ASSIGN.format(task.getName(), list.getName());
    }

    public String showList(String name) throws SystemException {
        List<Task> taskInList = getList(name).getTasks();

        List<Task> activeTasks = new ArrayList<>();
        for (Task task : taskInList) {
            if (!task.isDeleted()) {
                activeTasks.add(task);
            }
        }
        if (activeTasks.isEmpty()) {
            return SystemMessage.LIST_EMPTY.format(name);
        }
        return TaskFormatter.formatTree(activeTasks, task -> true);
    }

    // --- HELPER METHODS ---

    private Task getTask(int id) throws SystemException {
        Task task = allTasks.get(id);
        if (task == null) {
            throw new SystemException(SystemMessage.TASK_NOT_FOUND.format(id));
        }
        return task;
    }

    private TaskList getList(String name) throws SystemException {
        TaskList list = userLists.get(name);
        if (list == null) {
            throw new SystemException(SystemMessage.LIST_NOT_FOUND.format(name));
        }
        return list;
    }

    private String formatNullValue(Object value) {
        return (value == null) ? "NONE" : value.toString();
    }

    private String searchWithCondition(Predicate<Task> searchCondition, Predicate<Task> childCondition) {
        // search all tasks matches the condition
        List<Task> allMatches = new ArrayList<>();
        for (Task task : allTasks.values()) {
            if (!task.isDeleted() && searchCondition.test(task)) {
                allMatches.add(task);
            }
        }

        if (allMatches.isEmpty()) {
            return SystemMessage.NO_MATCHED.format();
        }
        // Logic: remove child if its parent is also in list
        List<Task> displayRoots = TaskUtils.filterRoots(allMatches);
        return TaskFormatter.formatTree(displayRoots, childCondition);
    }

    private boolean hasActiveDescendant(Task task) {
        for (Task sub : task.getSubtasks()) {
            if (sub.isDeleted())
                continue;

            if (!sub.isDone())
                return true;
            if (hasActiveDescendant(sub))
                return true;
        }
        return false;
    }
}
