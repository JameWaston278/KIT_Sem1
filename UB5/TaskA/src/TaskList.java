import java.util.ArrayList;
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

    // Getter

    public String getNameList() {
        return this.name;
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
