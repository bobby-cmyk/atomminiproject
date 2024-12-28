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

        // Set a random id
        String taskId = UUID.randomUUID().toString();
        task.setId(taskId);
        
        // Set created time and last updated time 
        long createdTime = Instant.now().toEpochMilli();
        task.setCreatedTime(createdTime);
        task.setLastUpdatedTime(createdTime);

        taskRepo.createTask(task, userId);
    }

    public void completeTask(String taskId, String userId) {
        taskRepo.completeTask(taskId, userId);
    }

    public void updateTask(Task task) {

        // Update last updated time
        long currentTime = Instant.now().toEpochMilli();
        task.setLastUpdatedTime(currentTime);

        taskRepo.updateTask(task);
    }

    public Task getCurrentTask(String taskId) {

        Task task = taskRepo.getCurrentTask(taskId);

        return task;
    }

    public List<Task> getAllSortedCurrentTasks(String userId) {

        List<Task> tasks = taskRepo.getAllCurrentTasks(userId);

        // Sort reverse, descending from latest to oldest
        List<Task> sortedTasks = tasks.stream()
            // Most updated to least updated
            .sorted((t1, t2) -> Long.compare(t2.getLastUpdatedTime(), t1.getLastUpdatedTime()))
            // Priority tasks at the top
            .sorted((t1, t2) -> Boolean.compare(t2.isPriority(), t1.isPriority()))
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
            .sorted((t1, t2) -> Long.compare(t2.getCompletedTime(), t1.getCompletedTime()))
            .collect(Collectors.toList());

        return sortedTasks;
    }

    public void clearAllCompletedTasks(String userId) {

        taskRepo.clearAllCompletedTasks(userId);

    }
}
