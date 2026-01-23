
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class TaskManager {

    private final Map<Integer, Task> allTasks;
    private final Map<String, TaskList> userLists;

    public TaskManager() {
        this.allTasks = new TreeMap<>();
        this.userLists = new TreeMap<>();
    }

    public void addTask(String name, Priority priority, LocalDate deadline) {
        Task newTask = new Task(name, priority, deadline);
        int id = newTask.getId();
        allTasks.put(id, newTask);
    }

    public void addNewList(TaskList list) throws SystemException {
        String name = list.getNameList();
        for (String element : userLists.keySet()) {
            if (name.equals(element)) {
                throw new SystemException(SystemMessage.LIST_EXISTS.format(name));
            }
        }
        userLists.put(name, list);
    }

    public void tagTask(int id, String tag) throws SystemException {
        Task currentTask = allTasks.get(id);
        if (allTasks.get(id) == null) {
            throw new SystemException(SystemMessage.TASK_NOT_FOUND.format(id));
        }
        currentTask.addNewTag(tag);
    }

    public void tagList(String name, String tag) throws SystemException {
        TaskList currentList = userLists.get(name);
        if (userLists.get(name) == null) {
            throw new SystemException(SystemMessage.LIST_NOT_FOUND.format(name));
        }
        currentList.addNewTag(tag);
    }
}
