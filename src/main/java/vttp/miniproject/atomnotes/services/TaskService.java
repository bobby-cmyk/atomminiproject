package vttp.miniproject.atomnotes.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.repositories.TaskRepo;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    public void createTask(Task task, String userId) {

        String taskId = UUID.randomUUID().toString();
        task.setId(taskId);

        long createdTime = Instant.now().toEpochMilli();
        task.setCreatedTime(createdTime);
        task.setLastUpdatedTime(createdTime);

        taskRepo.createTask(task, userId);
    }

    public void completeTask(String taskId, String userId) {
        taskRepo.completeTask(taskId, userId);
    }

    public void updateTask(Task task) {
        long currentTime = Instant.now().toEpochMilli();
        task.setLastUpdatedTime(currentTime);

        taskRepo.updateTask(task);
    }

    public Task getTask(String taskId) {

        Task task = taskRepo.getTask(taskId);

        return task;
    }

    public List<Task> getAllSortedTasks(String userId) {

        List<Task> tasks = taskRepo.getAllTasks(userId);

        // Sort reverse, descending from latest to oldest
        List<Task> sortedTasks = tasks.stream()
            .sorted((t1, t2) -> Long.compare(t2.getLastUpdatedTime(), t1.getLastUpdatedTime()))
            .collect(Collectors.toList());
            
        return sortedTasks;
    }

    public Task getCompletedTask(String taskId) {

        Task task = taskRepo.getCompletedTask(taskId);

        return task;
    }

    public List<Task> getAllSortedCompletedTasks(String userId) {

        List<Task> tasks = taskRepo.getAllCompletedTasks(userId);

        // Sort reverse, descending from latest to oldest
        List<Task> sortedTasks = tasks.stream()
            .sorted((t1, t2) -> Long.compare(t2.getLastUpdatedTime(), t1.getLastUpdatedTime()))
            .collect(Collectors.toList());
            
        return sortedTasks;
    }

    public void clearAllCompletedTasks(String userId) {
        taskRepo.clearAllCompletedTasks(userId);
    }

    public Long numberOfTasks(String userId) {
        return taskRepo.numberOfTasks(userId);
    }

    public Long numberOfCompletedTasks(String userId) {
        return taskRepo.numberOfCompletedTasks(userId);
    }
}
