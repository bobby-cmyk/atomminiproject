package vttp.miniproject.atomnotes.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.repositories.ConnectorRepo;
import vttp.miniproject.atomnotes.repositories.TaskRepo;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ConnectorRepo connectorRepo;

    private final Logger logger = Logger.getLogger(TaskService.class.getName());

    public void addTask(String username, Task task) {
        String taskId = UUID.randomUUID().toString();
        long addedEpochTime = Instant.now().toEpochMilli();

        task.setId(taskId);
        task.setAddedEpochTime(addedEpochTime);

        taskRepo.addTask(task);
        connectorRepo.addTaskId(username, taskId);
    }

    public Task getTask(String taskId) {

        Task task = taskRepo.getTask(taskId);

        logger.info(task.getAddedDateTime().toString());

        return task;
    }

    public List<Task> getAllSortedTasks(String user) {

        Set<String> taskIds = connectorRepo.getUserTaskIds(user);

        List<Task> tasks = new ArrayList<>(taskRepo.getTasks(taskIds));

        // Sort reverse, descending from latest to oldest
        List<Task> sortedTasks = tasks.stream()
            .sorted((t1, t2) -> Long.compare(t2.getAddedEpochTime(), t1.getAddedEpochTime()))
            .collect(Collectors.toList());
            
        return sortedTasks;
    }

    public void deleteTask(String username, String taskId) {
        taskRepo.deleteTask(taskId);
        connectorRepo.deleteTaskId(username, taskId);
    }

    public void updateTask(Task task) {

        // Update epoch time
        long addedEpochTime = Instant.now().toEpochMilli();
        task.setAddedEpochTime(addedEpochTime);

        taskRepo.updateTask(task);
    }
}
