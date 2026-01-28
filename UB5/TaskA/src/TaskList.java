package kit.edu.kastel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a user-defined list of tasks.
 * A list has a name and contains references to tasks.
 *
 * @author udqch
 */
public class TaskList {
    private final String name;
    private final List<Task> tasks = new ArrayList<>();
    private final Set<String> tags = new TreeSet<>();

    /**
     * Creates a new task list.
     * 
     * @param name The unique name of the list.
     */
    public TaskList(String name) {
        this.name = name;
    }

    // --- BUSINESS METHODS ---

    /**
     * Adds a tag to the list.
     * 
     * @param tag The tag to add.
     * @throws SystemException If tag exists.
     */
    public void addNewTag(String tag) throws SystemException {
        if (this.tags.contains(tag)) {
            throw new SystemException(SystemMessage.TAG_EXISTS.format(tag));
        }
        this.tags.add(tag);
    }

    /**
     * Adds a task to the list.
     * 
     * @param task The task object.
     * @throws SystemException If task is already in the list.
     */
    public void addNewTask(Task task) throws SystemException {
        if (this.tasks.contains(task)) {
            throw new SystemException(SystemMessage.TASK_IN_LIST.format());
        }
        this.tasks.add(task);
    }

    // --- GETTERS ---

    /**
     * Returns the list name.
     * 
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the tasks in list.
     * 
     * @return The list.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    /**
     * Returns the list tags.
     * 
     * @return The tags.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.tags);
    }
}
