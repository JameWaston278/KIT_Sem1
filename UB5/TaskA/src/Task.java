package kit.edu.kastel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a single task in the system.
 * A task can have subtasks, tags, a deadline, and a priority.
 * It maintains its state (done/deleted) and handles recursive operations on
 * subtasks.
 *
 * @author udqch
 */
public class Task {

    // Identity
    private static int idCounter = 1;
    private final int id;
    private final String name;

    // Relations
    private final List<Task> subtasks = new ArrayList<>();
    private Task parent = null;

    // State
    private boolean isDone = false;
    private boolean isDeleted = false;

    // Optional
    private LocalDate deadline;
    private Priority priority;
    private final Set<String> tags = new TreeSet<>();

    /**
     * Creates a new task with the given name and a unique ID.
     *
     * @param name The name of the task.
     */
    public Task(String name) {
        this.id = Task.idCounter++;
        this.name = name;
    }

    // --- BUSINESS METHODS ---

    /**
     * Assigns a child task to this task.
     * Checks for circular dependencies before assignment.
     *
     * @param child The subtask to assign.
     * @throws SystemException If the child is the task itself or an ancestor (cycle
     *                         detected).
     */
    public void assignSubtask(Task child) throws SystemException {
        if (this.equals(child)) {
            throw new SystemException(SystemMessage.ASSIGN_ITSELF.format());
        }
        if (TaskUtils.isAncestor(this, child)) {
            throw new SystemException(SystemMessage.ASSIGN_WITH_CYCLE.format());
        }
        ensureTaskIsActive();
        child.ensureTaskIsActive();
        this.addSubtask(child);
    }

    /**
     * Checks if the task has a specific tag.
     *
     * @param tag The tag to check.
     * @return true if the tag exists, false otherwise.
     */
    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    /**
     * Adds a tag to the task.
     *
     * @param tag The tag to add.
     * @throws SystemException If the tag already exists.
     */
    public void addTag(String tag) throws SystemException {
        if (hasTag(tag)) {
            throw new SystemException(SystemMessage.TAG_EXISTS.format(tag));
        }
        ensureTaskIsActive();
        this.tags.add(tag);
    }

    /**
     * Toggles the done state of the task and all its subtasks.
     *
     * @return The number of tasks affected (including subtasks).
     * @throws SystemException If the task is deleted.
     */
    public int toggle() throws SystemException {
        ensureTaskIsActive();
        boolean targetState = !this.isDone;
        return setStateRecursive(targetState);
    }

    /**
     * Checks if the task name contains the given string.
     *
     * @param name The substring to search for.
     * @return true if the name contains the input string.
     */
    public boolean hasName(String name) {
        return this.name.contains(name);
    }

    /**
     * Internal method to link a subtask.
     * Detaches the subtask from its old parent before adding.
     *
     * @param subtask The subtask to add.
     */
    public void addSubtask(Task subtask) {
        subtask.detachFromParent();
        this.subtasks.add(subtask);
        subtask.setParent(this);
    }

    // --- GETTERS ---

    /**
     * Returns the unique ID of the task.
     * 
     * @return The task ID.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the name of the task.
     * 
     * @return The task name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns an unmodifiable list of subtasks.
     * 
     * @return The list of subtasks.
     */
    public List<Task> getSubtasks() {
        return Collections.unmodifiableList(this.subtasks);
    }

    /**
     * Returns the parent task.
     * 
     * @return The parent task, or null if it is a root task.
     */
    public Task getParent() {
        return this.parent;
    }

    /**
     * Checks if the task is marked as done.
     * 
     * @return true if done, false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Checks if the task is marked as deleted.
     * 
     * @return true if deleted, false otherwise.
     */
    public boolean isDeleted() {
        return this.isDeleted;
    }

    /**
     * Returns the deadline of the task.
     * 
     * @return The deadline, or null if not set.
     */
    public LocalDate getDeadline() {
        return this.deadline;
    }

    /**
     * Returns the priority of the task.
     * 
     * @return The priority, or null if not set.
     */
    public Priority getPriority() {
        return this.priority;
    }

    /**
     * Returns an unmodifiable set of tags.
     * 
     * @return The set of tags.
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    // --- SETTERS ---

    /**
     * Sets the parent of this task.
     * Validated and called only by addSubtask logic.
     *
     * @param task The new parent task.
     */
    protected void setParent(Task task) {
        this.parent = task;
    }

    /**
     * Recursively sets the deleted state of the task and its subtasks.
     *
     * @param targetState true to delete, false to restore.
     * @return The number of tasks affected.
     */
    public int setDeletedState(boolean targetState) {
        return setDeletedRecursive(targetState);
    }

    /**
     * Sets the deadline of the task.
     *
     * @param date The new deadline date.
     * @throws SystemException If the task is deleted.
     */
    public void setDeadline(LocalDate date) throws SystemException {
        ensureTaskIsActive();
        this.deadline = date;
    }

    /**
     * Sets the priority of the task.
     *
     * @param priority The new priority.
     * @throws SystemException If the task is deleted.
     */
    public void setPriority(Priority priority) throws SystemException {
        ensureTaskIsActive();
        this.priority = priority;
    }

    // --- HELPER METHODS ---

    /**
     * Ensures the task is not deleted before performing modifications.
     *
     * @throws SystemException If the task is deleted.
     */
    private void ensureTaskIsActive() throws SystemException {
        if (this.isDeleted) {
            throw new SystemException(SystemMessage.TASK_DELETED.format());
        }
    }

    /**
     * Detaches this task from its current parent, if any.
     */
    public void detachFromParent() {
        if (this.parent != null) {
            this.parent.subtasks.remove(this);
            this.parent = null;
        }
    }

    /**
     * Toggle task and its relation recursively.
     * 
     * @param targetState The new state.
     * @return The number of affected subtasks.
     */
    private int setStateRecursive(boolean targetState) {
        if (this.isDeleted) {
            return 0;
        }
        this.isDone = targetState;
        int count = 1;

        for (Task subtask : subtasks) {
            count += subtask.setStateRecursive(targetState);
        }
        return count;
    }

    /**
     * Delete/ restore task and its relation recursively.
     * 
     * @param targetState The new state.
     * @return The number of affected subtasks.
     */
    private int setDeletedRecursive(boolean targetState) {
        if (this.isDeleted == targetState) {
            return 0;
        }
        this.isDeleted = targetState;
        int count = 1;

        for (Task subtask : subtasks) {
            count += subtask.setDeletedRecursive(targetState);
        }
        return count;
    }
}