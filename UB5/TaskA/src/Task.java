
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Task {
    // ---CONSTANTS---
    private static final String INDENT_UNIT = "  ";
    private static final String NEWLINE = "\n";
    private static final String CHECKBOX_DONE = "- [x] ";
    private static final String CHECKBOX_TODO = "- [ ] ";
    private static final String PRIORITY_OPEN = " [";
    private static final String PRIORITY_CLOSE = "]";
    private static final String SEPARATOR = ":";
    private static final String TAG_OPEN = " (";
    private static final String TAG_CLOSE = ")";
    private static final String TAG_DELIMITER = ", ";
    private static final String DEADLINE_PREFIX = " --> ";

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

    // ---BUSINESS METHODS---

    // Methods for task
    public void addTag(String tag) throws SystemException {
        if (this.tags.contains(tag)) {
            throw new SystemException(SystemMessage.TAG_EXISTS.format(tag));
        }
        this.tags.add(tag);
    }

    public int toggle() throws SystemException {
        ensureTaskIsActive();
        boolean targetState = !this.isDone;
        return setStateRecursive(targetState);
    }

    protected void detachFromParent() {
        if (this.parent != null) {
            this.parent.removeSubtask(this);
            this.parent = null;
        }
    }

    public boolean hasName(String name) {
        return this.name.contains(name);
    }

    public boolean hasTag(String tag) {
        return this.tags.contains(tag);
    }

    public String getDetails(int level) {
        StringBuilder result = new StringBuilder();

        result.append(INDENT_UNIT.repeat(level))
                .append(this.isDone ? CHECKBOX_DONE : CHECKBOX_TODO);

        result.append(this.name);

        if (this.priority != null) {
            result.append(PRIORITY_OPEN)
                    .append(this.priority)
                    .append(PRIORITY_CLOSE);
        }

        result.append(!tags.isEmpty() || this.deadline != null ? SEPARATOR : "");

        if (!tags.isEmpty()) {
            result.append(TAG_OPEN)
                    .append(String.join(TAG_DELIMITER, tags))
                    .append(TAG_CLOSE);
        }

        if (this.deadline != null) {
            result.append(DEADLINE_PREFIX).append(this.deadline);
        }
        result.append(NEWLINE);

        return result.toString();
    }

    // Methods for subtask

    public void addSubtask(Task subtask) throws SystemException {
        if (subtask.getParent() != null) {
            subtask.getParent().removeSubtask(subtask);
        }
        this.subtasks.add(subtask);
        subtask.setParent(this);
    }

    // ---GETTERS---

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
        return Collections.unmodifiableSet(this.tags);
    }

    // ---SETTERS---

    public void setDeadline(LocalDate date) throws SystemException {
        ensureTaskIsActive();
        this.deadline = date;
    }

    public void setPriority(Priority priority) throws SystemException {
        ensureTaskIsActive();
        this.priority = priority;
    }

    protected void setParent(Task task) throws SystemException {
        this.parent = task;
    }

    public int setDeletedState(boolean targetState) throws SystemException {
        return setDeletedRecursive(targetState);
    }

    // ---HELPER METHODS---

    private void ensureTaskIsActive() throws SystemException {
        if (this.isDeleted == true) {
            throw new SystemException(SystemMessage.TASK_DELETED.format());
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

    protected void removeSubtask(Task subtask) {
        this.subtasks.remove(subtask);
    }
}