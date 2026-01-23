
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Task {
    // Identity
    private static int idCounter = 1;
    private final int id;
    private String name;

    // Relations
    private final List<Task> subtasks = new ArrayList<>();
    private Task parent;

    // State
    private boolean isDone = false;
    private boolean isDeleted = false;

    // Optional
    private LocalDate deadline;
    private Priority priority;
    private final Set<String> tags = new TreeSet<>();

    public Task(String name, Priority priority, LocalDate deadline) {
        this.id = idCounter++;
        this.name = name;
        this.priority = priority;
        this.deadline = deadline;
    }

    // Getter

    public int getId() {
        return this.id;
    }

    public Set<String> getTags() {
        return this.tags;
    }

    // Setter

    public void addNewTag(String tag) throws SystemException {
        if (this.tags.contains(tag)) {
            throw new SystemException(SystemMessage.TAG_EXISTS.format(tag));
        }
        this.tags.add(tag);
    }
}