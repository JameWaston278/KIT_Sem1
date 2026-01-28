package kit.edu.kastel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Predicate;

/**
 * The main application logic controller.
 * Manages the collection of all tasks and user-defined lists.
 * Executes business logic commands.
 *
 * @author udqch
 */
public class Procrastinot {
    private final Map<Integer, Task> allTasks;
    private final Map<String, TaskList> userLists;

    /** Initialize databases. */
    public Procrastinot() {
        this.allTasks = new TreeMap<>();
        this.userLists = new TreeMap<>();
    }

    // --- BUSINESS METHODS ---

    // Methods for tasks
    /**
     * Creates and adds a new task to the system.
     *
     * @param name     The name of the task.
     * @param priority The priority (can be null).
     * @param date     The deadline date (can be null).
     * @return Success message string.
     * @throws SystemException If parameters are invalid.
     */
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
     * @throws SystemException If tasks not found or assignment invalid.
     */
    public String assignSubtask(int idChild, int idParent) throws SystemException {
        Task child = getTask(idChild);
        Task parent = getTask(idParent);
        parent.assignSubtask(child);
        return SystemMessage.SUCCESS_ASSIGN.format(child.getName(), parent.getName());
    }

    /**
     * Toggles the completion status of a task.
     *
     * @param id The ID of the task.
     * @return Success message string indicating number of affected tasks.
     * @throws SystemException If task not found.
     */
    public String toggle(int id) throws SystemException {
        Task task = getTask(id);
        int subCount = Math.max(0, task.toggle() - 1);
        return SystemMessage.SUCCESS_TOGGLE.format(task.getName(), subCount);
    }

    /**
     * Changes the deadline of a task.
     *
     * @param id   The ID of the task.
     * @param date The new deadline date.
     * @return Success message string.
     * @throws SystemException If task not found.
     */
    public String changeDeadline(int id, LocalDate date) throws SystemException {
        Task task = getTask(id);
        task.setDeadline(date);
        return SystemMessage.SUCCESS_CHANGE_VALUES.format(task.getName(), TaskFormatter.formatNullValue(date));
    }

    /**
     * Changes the priority of a task.
     *
     * @param id       The ID of the task.
     * @param priority The new priority (can be null).
     * @return Success message string.
     * @throws SystemException If task not found.
     */
    public String changePriority(int id, Priority priority) throws SystemException {
        Task task = getTask(id);
        task.setPriority(priority);
        return SystemMessage.SUCCESS_CHANGE_VALUES.format(task.getName(), TaskFormatter.formatNullValue(priority));
    }

    /**
     * Deletes a task and its subtasks (soft delete).
     *
     * @param id The ID of the task.
     * @return Success message string.
     * @throws SystemException If task not found.
     */
    public String deleteTask(int id) throws SystemException {
        Task task = getTask(id);
        if (task.isDeleted()) {
            throw new SystemException(SystemMessage.TASK_DELETED.format());
        }
        int subCount = Math.max(0, task.setDeletedState(true) - 1);
        return SystemMessage.SUCCESS_DELETE.format(task.getName(), subCount);
    }

    /**
     * Restores a deleted task.
     *
     * @param id The ID of the task.
     * @return Success message string.
     * @throws SystemException If task not found.
     */
    public String restoreTask(int id) throws SystemException {
        Task task = getTask(id);
        Task parent = task.getParent();
        if (parent != null) {
            parent.addSubtask(task);
        }
        int subCount = Math.max(0, task.setDeletedState(false) - 1);
        return SystemMessage.SUCCESS_RESTORE.format(task.getName(), subCount);
    }

    /**
     * Shows all root tasks.
     * Uses LAX mode to show everything.
     * 
     * @return Success message string.
     * 
     */
    public String show() {
        return executeSearch(task -> task.getParent() == null, TaskSearcher.SearchMode.LAX);
    }

    /**
     * Shows a specific task and its subtasks.
     * Uses LAX mode.
     * 
     * @param id The id of task.
     * @return Success message string.
     */
    public String show(int id) {
        return executeSearch(task -> task.getId() == id, TaskSearcher.SearchMode.LAX);
    }

    /**
     * Shows all incomplete tasks.
     * Custom logic: Show if !Done OR hasActiveDescendant.
     * 
     * @return Success message string.
     */
    public String todo() {
        List<Task> roots = new ArrayList<>();

        for (Task task : allTasks.values()) {
            if (!task.isDeleted() && task.getParent() == null) {
                if (!task.isDone() || TaskUtils.hasActiveDescendant(task)) {
                    roots.add(task);
                }
            }
        }
        return TaskFormatter.formatTree(roots,
                task -> !task.isDone() || TaskUtils.hasActiveDescendant(task));
    }

    /**
     * Finds tasks containing a specific name substring.
     * Uses STRICT mode: Children of DONE tasks are hidden unless they match
     * directly.
     *
     * @param name The name to search for.
     * @return Formatted string tree.
     */
    public String find(String name) {
        // Criteria: Name matches AND (Parent is null OR Parent is not Done)
        // This preserves the "Hide children of Done parents" rule for direct hits
        Predicate<Task> criteria = task -> task.hasName(name)
                && (task.getParent() == null || !task.getParent().isDone());

        return executeSearch(criteria, TaskSearcher.SearchMode.STRICT);
    }

    /**
     * Finds tasks by a specific tag.
     * Uses LAX mode: Shows full context (children) even if parent is Done.
     *
     * @param tag The tag to search for.
     * @return Formatted string tree.
     */
    public String hasTag(String tag) {
        return executeSearch(task -> task.hasTag(tag), TaskSearcher.SearchMode.LAX);
    }

    /**
     * Filters tasks based on a date condition (deadline).
     * Uses LAX mode: Shows full context for time-planning purposes.
     *
     * @param dateCondition The predicate for the deadline date.
     * @return Formatted string tree.
     */
    public String searchTime(Predicate<LocalDate> dateCondition) {
        return executeSearch(
                task -> task.getDeadline() != null && dateCondition.test(task.getDeadline()),
                TaskSearcher.SearchMode.LAX);
    }

    /**
     * Identifies duplicate tasks based on name and deadline.
     *
     * @return Formatted string of duplicate IDs.
     */
    public String duplicates() {
        return DuplicatesFinder.findDuplicates(this.allTasks);
    }

    // Methods for List

    /**
     * Creates a new task list.
     *
     * @param name The unique name of the list.
     * @return Success message string.
     * @throws SystemException If list name already exists.
     */
    public String createList(String name) throws SystemException {
        // validate the uniqueness of list
        if (userLists.containsKey(name)) {
            throw new SystemException(SystemMessage.LIST_EXISTS.format(name));
        }

        TaskList newList = new TaskList(name);
        userLists.put(name, newList);
        return SystemMessage.SUCCESS_CREATE_LIST.format(name);
    }

    /**
     * Adds a tag to a task list.
     *
     * @param name The name of the list.
     * @param tag  The tag to add.
     * @return Success message string.
     * @throws SystemException If list not found or tag exists.
     */
    public String tagList(String name, String tag) throws SystemException {
        TaskList list = getList(name);
        list.addNewTag(tag);
        return SystemMessage.SUCCESS_TAG.format(name, tag);
    }

    /**
     * Assigns a task to a list.
     *
     * @param id   The ID of the task.
     * @param name The name of the list.
     * @return Success message string.
     * @throws SystemException If task/list not found.
     */
    public String assignToList(int id, String name) throws SystemException {
        Task task = getTask(id);
        TaskList list = getList(name);
        list.addNewTask(task);
        return SystemMessage.SUCCESS_ASSIGN.format(task.getName(), list.getName());
    }

    /**
     * Shows all tasks in a specific list.
     * 
     * @param name The name of list.
     * @return Success message string.
     * @throws SystemException If list not found.
     */
    public String showList(String name) throws SystemException {
        List<Task> taskInList = getList(name).getTasks();

        List<Task> activeTasks = new ArrayList<>();
        for (Task task : taskInList) {
            if (!task.isDeleted()) {
                activeTasks.add(task);
            }
        }
        return TaskFormatter.formatTree(activeTasks, task -> true);
    }

    // --- HELPER METHODS ---
    /**
     * Retrieves a task by ID.
     *
     * @param id The task ID.
     * @return The task object.
     * @throws SystemException If not found.
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
     * @throws SystemException If not found.
     */
    private TaskList getList(String name) throws SystemException {
        TaskList list = userLists.get(name);
        if (list == null) {
            throw new SystemException(SystemMessage.LIST_NOT_FOUND.format(name));
        }
        return list;
    }

    /**
     * Helper method to execute search and format results.
     *
     * @param criteria The condition to find tasks.
     * @param mode     The search mode (STRICT or LAX).
     * @return The formatted string output.
     */
    private String executeSearch(Predicate<Task> criteria, TaskSearcher.SearchMode mode) {
        // 1. Create the smart visibility filter
        Predicate<Task> visibilityFilter = TaskSearcher.createVisibilityFilter(allTasks, criteria, mode);

        // 2. Collect all visible tasks
        List<Task> visibleTasks = new ArrayList<>();
        for (Task task : allTasks.values()) {
            if (!task.isDeleted() && visibilityFilter.test(task)) {
                visibleTasks.add(task);
            }
        }

        // 3. Filter roots and format
        // We pass 'visibilityFilter' as the childCondition to TaskFormatter
        List<Task> roots = TaskUtils.filterRoots(visibleTasks);
        return TaskFormatter.formatTree(roots, visibilityFilter);
    }
}
