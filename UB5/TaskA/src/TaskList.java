import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaskList {
    private final String name;
    private final List<Task> tasks = new ArrayList<>();
    private final Set<String> tags = new TreeSet<>();

    public TaskList(String name) {
        this.name = name;
    }

    // --- BUSINESS METHODS ---

    public void addNewTag(String tag) throws SystemException {
        if (this.tags.contains(tag)) {
            throw new SystemException(SystemMessage.TAG_EXISTS.format(tag));
        }
        this.tags.add(tag);
    }

    public void addNewTask(Task task) throws SystemException {
        if (this.tasks.contains(task)) {
            throw new SystemException(SystemMessage.TASK_IN_LIST.format());
        }
        this.tasks.add(task);
    }

    // --- GETTERS ---

    public String getName() {
        return this.name;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(this.tasks);
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(this.tags);
    }
}
