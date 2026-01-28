import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * Main controller class. Manages tasks, lists and executes commands.
 * Handles the storage and business logic for the application.
 *
 * @author udqch
 */
public class Procrastinot {
    private final Map<Integer, Task> allTasks = new LinkedHashMap<>();
    private final Map<String, TaskList> userLists = new TreeMap<>();

    /** Initializes the task and list databases. */
    public Procrastinot() {
    }

    // --- TASK OPERATIONS ---

    /**
     * Creates and adds a new task to the system.
     *
     * @param name     The name of the task.
     * @param priority The priority (can be null).
     * @param date     The deadline date (can be null).
     * @return Success message string.
     * @throws SystemException If invalid parameters provided (though currently
     *                         handled internally).
     */
    public String addTask(String name, Priority priority, LocalDate date) throws SystemException {
        Task newTask = new Task(name);
        if (priority != null) {
            newTask.setPriority(priority);
        }
        if (date != null) {
            try {
                newTask.setDeadline(date);
            } catch (SystemException ignored) {
                // Should not happen for new tasks
            }
        }
        allTasks.put(newTask.getId(), newTask);
        return SystemMessage.SUCCESS_ADD_TASK.format(newTask.getId(), name);
    }

    /**
     * Adds a tag to an existing task.
     *
     * @param id  The ID of the task.
     * @param tag The tag to add.
     * @return Success message string.
     * @throws SystemException If task not found or tag exists.
     */
    public String tagTask(int id, String tag) throws SystemException {
        Task task = getTask(id);
        task.addTag(tag);
        return SystemMessage.SUCCESS_TAG.format(task.getName(), tag);
    }

    /**
     * Assigns a child task to a parent task.
     *
     * @param idChild  The ID of the subtask.
     * @param idParent The ID of the parent task.
     * @return Success message string.
     * @throws SystemException If cycle detected or invalid assignment.
     */
    public String assignSubtask(int idChild, int idParent) throws SystemException {
        Task child = getTask(idChild);
        Task parent = getTask(idParent);
        parent.assignSubtask(child);
        return SystemMessage.SUCCESS_ASSIGN.format(child.getName(), parent.getName());
    }

    /**
     * Toggles the completion status of a task and its descendants.
     *
     * @param id The ID of the task.
     * @return Success message string with affected count.
     * @throws SystemException If task not found.
     */
    public String toggle(int id) throws SystemException {
        Task task = getTask(id);
        int subCount = Math.max(0, task.setDoneState(!task.isDone()) - 1);
        return SystemMessage.SUCCESS_TOGGLE.format(task.getName(), subCount);
    }

    /**
     * Updates the deadline of a task.
     *
     * @param id   The ID of the task.
     * @param date The new deadline.
     * @return Success message string.
     * @throws SystemException If task not found or deleted.
     */
    public String changeDeadline(int id, LocalDate date) throws SystemException {
        Task task = getTask(id);
        task.setDeadline(date);
        return SystemMessage.SUCCESS_CHANGE_VALUES.format(task.getName(), TaskFormatter.formatNullValue(date));
    }

    /**
     * Updates the priority of a task.
     *
     * @param id       The ID of the task.
     * @param priority The new priority.
     * @return Success message string.
     * @throws SystemException If task not found or deleted.
     */
    public String changePriority(int id, Priority priority) throws SystemException {
        Task task = getTask(id);
        task.setPriority(priority);
        return SystemMessage.SUCCESS_CHANGE_VALUES.format(task.getName(), TaskFormatter.formatNullValue(priority));
    }

    /**
     * Soft deletes a task and its descendants.
     *
     * @param id The ID of the task.
     * @return Success message string.
     * @throws SystemException If task is already deleted.
     */
    public String deleteTask(int id) throws SystemException {
        Task task = getTask(id);
        if (task.isDeleted()) {
            throw new SystemException(SystemMessage.TASK_DELETED.format());
        }
        int subCount = Math.max(0, task.setDeleteState(true) - 1);
        return SystemMessage.SUCCESS_DELETE.format(task.getName(), subCount);
    }

    /**
     * Restores a task, handling orphans and updating list order.
     *
     * @param id The ID of the task.
     * @return Success message string.
     * @throws SystemException If task is already active.
     */
    public String restoreTask(int id) throws SystemException {
        Task task = getTask(id);
        if (!task.isDeleted()) {
            throw new SystemException(SystemMessage.TASK_ACTIVE.format());
        }

        int subCount = Math.max(0, task.setDeleteState(false) - 1);
        Task parent = task.getParent();

        if (parent != null) {
            if (parent.isDeleted()) {
                task.detachFromParent();
            } else {
                parent.moveSubtaskToEnd(task);
            }
        }

        allTasks.put(id, allTasks.remove(id)); // Move to end of main map
        moveTaskToEndOfUserLists(task); // Move to end of user lists
        return SystemMessage.SUCCESS_RESTORE.format(task.getName(), subCount);
    }

    // --- SEARCH & VIEW OPERATIONS ---

    /**
     * Shows all root tasks using LAX mode.
     *
     * @return Formatted tree string.
     */
    public String show() {
        return executeSearch(task -> task.getParent() == null, TaskSearcher.SearchMode.LAX);
    }

    /**
     * Shows a specific task tree using LAX mode.
     *
     * @param id The ID of the root task to show.
     * @return Formatted tree string.
     */
    public String show(int id) {
        return executeSearch(task -> task.getId() == id, TaskSearcher.SearchMode.LAX);
    }

    /**
     * Shows tasks that are incomplete or have active descendants.
     *
     * @return Formatted tree string.
     */
    public String todo() {
        Predicate<Task> isActive = t -> !t.isDone() || TaskUtils.hasActiveDescendant(t);
        List<Task> roots = new ArrayList<>();
        for (Task task : allTasks.values()) {
            if (!task.isDeleted() && task.getParent() == null && isActive.test(task)) {
                roots.add(task);
            }
        }
        return TaskFormatter.formatTree(roots, isActive);
    }

    /**
     * Finds tasks by name using STRICT mode (hides children of done parents).
     *
     * @param name The substring to search for.
     * @return Formatted tree string.
     */
    public String find(String name) {
        return executeSearch(t -> t.getName().contains(name)
                && (t.getParent() == null || !t.getParent().isDone()), TaskSearcher.SearchMode.STRICT);
    }

    /**
     * Finds tasks by tag using LAX mode.
     *
     * @param tag The tag to search for.
     * @return Formatted tree string.
     */
    public String hasTag(String tag) {
        return executeSearch(t -> t.getTags().contains(tag), TaskSearcher.SearchMode.LAX);
    }

    /**
     * Finds tasks matching a date condition using LAX mode.
     *
     * @param condition The date predicate.
     * @return Formatted tree string.
     */
    public String searchTime(Predicate<LocalDate> condition) {
        return executeSearch(t -> t.getDeadline() != null && condition.test(t.getDeadline()),
                TaskSearcher.SearchMode.LAX);
    }

    /**
     * Finds duplicate tasks based on name and deadline.
     *
     * @return String listing duplicates.
     */
    public String duplicates() {
        return DuplicatesFinder.findDuplicates(this.allTasks);
    }

    // --- LIST OPERATIONS ---

    /**
     * Creates a new task list.
     *
     * @param name The unique name of the list.
     * @return Success message string.
     * @throws SystemException If list name already exists.
     */
    public String createList(String name) throws SystemException {
        if (userLists.containsKey(name)) {
            throw new SystemException(SystemMessage.LIST_EXISTS.format(name));
        }
        userLists.put(name, new TaskList(name));
        return SystemMessage.SUCCESS_CREATE_LIST.format(name);
    }

    /**
     * Adds a tag to a list.
     *
     * @param name The name of the list.
     * @param tag  The tag to add.
     * @return Success message string.
     * @throws SystemException If list not found.
     */
    public String tagList(String name, String tag) throws SystemException {
        getList(name).addNewTag(tag);
        return SystemMessage.SUCCESS_TAG.format(name, tag);
    }

    /**
     * Assigns a task to a list.
     *
     * @param id   The ID of the task.
     * @param name The name of the list.
     * @return Success message string.
     * @throws SystemException If task or list not found.
     */
    public String assignToList(int id, String name) throws SystemException {
        Task task = getTask(id);
        TaskList list = getList(name);
        list.addNewTask(task);
        return SystemMessage.SUCCESS_ASSIGN.format(task.getName(), list.getName());
    }

    /**
     * Shows all active tasks in a list.
     *
     * @param name The name of the list.
     * @return Formatted tree string.
     * @throws SystemException If list not found.
     */
    public String showList(String name) throws SystemException {
        List<Task> activeTasks = new ArrayList<>();
        for (Task task : getList(name).getTasks()) {
            if (!task.isDeleted()) {
                activeTasks.add(task);
            }
        }
        return TaskFormatter.formatTree(activeTasks, task -> true);
    }

    // --- HELPERS ---

    /**
     * Retrieves a task by ID.
     *
     * @param id The task ID.
     * @return The task object.
     * @throws SystemException If task not found.
     */
    private Task getTask(int id) throws SystemException {
        Task task = allTasks.get(id);
        if (task == null) {
            throw new SystemException(SystemMessage.TASK_NOT_FOUND.format(id));
        }
        return task;
    }

    /**
     * Retrieves a list by name.
     *
     * @param name The list name.
     * @return The TaskList object.
     * @throws SystemException If list not found.
     */
    private TaskList getList(String name) throws SystemException {
        TaskList list = userLists.get(name);
        if (list == null) {
            throw new SystemException(SystemMessage.LIST_NOT_FOUND.format(name));
        }
        return list;
    }

    /**
     * Executes a search and formats the result.
     *
     * @param criteria The filtering criteria.
     * @param mode     The search mode (STRICT/LAX).
     * @return Formatted result string.
     */
    private String executeSearch(Predicate<Task> criteria, TaskSearcher.SearchMode mode) {
        Predicate<Task> visibilityFilter = TaskSearcher.createVisibilityFilter(allTasks, criteria, mode);
        List<Task> visibleTasks = new ArrayList<>();
        for (Task task : allTasks.values()) {
            if (!task.isDeleted() && visibilityFilter.test(task)) {
                visibleTasks.add(task);
            }
        }
        return TaskFormatter.formatTree(TaskUtils.filterRoots(visibleTasks), visibilityFilter);
    }

    /**
     * Moves a task to the end of all user lists containing it.
     * Used during restore to ensure correct display order.
     *
     * @param task The task to move.
     */
    private void moveTaskToEndOfUserLists(Task task) {
        for (TaskList list : userLists.values()) {
            list.moveTaskToEnd(task);
        }
    }
}