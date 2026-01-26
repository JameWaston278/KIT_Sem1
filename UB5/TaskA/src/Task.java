
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    public Task(String name) {
        this.id = Task.idCounter++;
        this.name = name;
    }

    // --- BUSINESS METHODS ---

    // Methods for task

    public void assignSubtask(Task child) throws SystemException {
        if (this.equals(child)) {
            throw new SystemException(SystemMessage.ASSIGN_ITSELF.format());
        }
        // check: is child a ancestor of parent?
        if (TaskUtils.isAncestor(this, child)) {
            throw new SystemException(SystemMessage.ASSIGN_WITH_CYCLE.format());
        }
        this.addSubtask(child);
    }

    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    public void addTag(String tag) throws SystemException {
        if (hasTag(tag)) {
            throw new SystemException(SystemMessage.TAG_EXISTS.format(tag));
        }
        this.tags.add(tag);
    }

    public int toggle() throws SystemException {
        ensureTaskIsActive();
        boolean targetState = !this.isDone;
        return setStateRecursive(targetState);
    }

    public boolean hasName(String name) {
        return this.name.contains(name);
    }

    // Methods for subtask

    public void addSubtask(Task subtask) throws SystemException {
        subtask.detachFromParent();
        this.subtasks.add(subtask);
        subtask.setParent(this);
    }

    // --- GETTERS ---

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<Task> getSubtasks() {
        return Collections.unmodifiableList(this.subtasks);
    }

    public Task getParent() {
        return this.parent;
    }

    public boolean isDone() {
        return this.isDone;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    public LocalDate getDeadline() {
        return this.deadline;
    }

    public Priority getPriority() {
        return this.priority;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    // --- SETTERS ---

    protected void setParent(Task task) throws SystemException {
        this.parent = task;
    }

    public int setDeletedState(boolean targetState) throws SystemException {
        return setDeletedRecursive(targetState);
    }

    public void setDeadline(LocalDate date) throws SystemException {
        ensureTaskIsActive();
        this.deadline = date;
    }

    public void setPriority(Priority priority) throws SystemException {
        ensureTaskIsActive();
        this.priority = priority;
    }

    // --- HELPER METHODS ---

    private void ensureTaskIsActive() throws SystemException {
        if (this.isDeleted) {
            throw new SystemException(SystemMessage.TASK_DELETED.format());
        }
    }

    public void detachFromParent() {
        if (this.parent != null) {
            this.parent.subtasks.remove(this);
            this.parent = null;
        }
    }

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