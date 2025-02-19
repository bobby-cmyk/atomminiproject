package vttp.miniproject.atomnotes.repositories;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

import vttp.miniproject.atomnotes.models.Task;

@Repository
public class TaskRepo {
    
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    private String TASK_IDS_PREFIX="userTaskIds:";
    private String COMPLETED_TASK_IDS_PREFIX="userCompletedTaskIds:";
    private String TASK_PREFIX="task:";
    private String COMPLETED_TASK_PREFIX="completedTask:";

    // hset "task:taskId" idKey idValue contentKey contentValue ...
    // sadd "userTaskIds:userId" ["task1Id", "task2Id", ...]
    public void createTask(Task task, String userId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        String taskId = task.getId();

        Map<String, String> values = task.taskToMap();

        hashOps.putAll(TASK_PREFIX + taskId, values);

        SetOperations<String, String> setOps = template.opsForSet();
        
        setOps.add(TASK_IDS_PREFIX + userId, taskId);
    }

    // hgetall "task:taskId"
    // del "task:taskId"
    // hset "completedTask:taskId" idKey idValue contentKey contentValue ...
    // smove "userTaskIds:userId" taskId "completedTaskIds:usedId"
    public void completeTask(String taskId, String userId) {
        
        HashOperations<String, String, String> hashOps = template.opsForHash();

        // Get taskMap
        Map<String, String> taskMap = hashOps.entries(TASK_PREFIX + taskId);

        // Delete task from current tasks
        template.delete(TASK_PREFIX + taskId);

        long completedTime = Instant.now().toEpochMilli();

        // Add completed time
        taskMap.put("completedTime", Long.toString(completedTime));

        // Add task back as completed
        hashOps.putAll(COMPLETED_TASK_PREFIX + taskId, taskMap);

        SetOperations<String, String> setOps = template.opsForSet();

        // Move from current task ids set to completed task ids set
        setOps.move(TASK_IDS_PREFIX + userId, taskId, COMPLETED_TASK_IDS_PREFIX + userId);
    }

    // hset "task:taskId" idKey idValue contentKey contentValue ...
    public void updateTask(Task task) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        String taskId = task.getId();

        Map<String, String> values = task.taskToMap();

        //Overwrite
        hashOps.putAll(TASK_PREFIX + taskId, values);
    }

    // hgetall "task:taskId"
    public Task getCurrentTask(String taskId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> taskMap = hashOps.entries(TASK_PREFIX + taskId);

        return Task.mapToTask(taskMap);
    }

    // sget "userTaskIds:userId"
    // hgetall "task:taskId"
    public List<Task> getAllCurrentTasks(String userId) {
        
        SetOperations<String, String> setOps = template.opsForSet();

        Set<String> taskIds = setOps.members(TASK_IDS_PREFIX + userId);
        
        List<Task> tasks = new ArrayList<>();

        HashOperations<String, String, String> hashOps = template.opsForHash();

        for (String taskId : taskIds) {

            Task task = Task.mapToTask(hashOps.entries(TASK_PREFIX + taskId));

            tasks.add(task);
        }

        return tasks;
    }

    // hgetall "completedTask:taskId"
    public Task getCompletedTask(String taskId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> taskMap = hashOps.entries(COMPLETED_TASK_PREFIX + taskId);

        return Task.mapToTask(taskMap);
    }

    // sget "userCompletedTaskIds:userId"
    // hgetall "completedTask:taskId"
    public List<Task> getAllCompletedTasks(String userId) {
        
        SetOperations<String, String> setOps = template.opsForSet();

        Set<String> taskIds = setOps.members(COMPLETED_TASK_IDS_PREFIX + userId);
        
        List<Task> completedTasks = new ArrayList<>();

        HashOperations<String, String, String> hashOps = template.opsForHash();

        for (String taskId : taskIds) {

            Task task = Task.mapToTask(hashOps.entries(COMPLETED_TASK_PREFIX + taskId));

            completedTasks.add(task);
        }

        return completedTasks;
    }  

    // smembers "userCompletedTaskIds:userId"
    // del "completedTask:taskId"
    public void clearAllCompletedTasks(String userId) {

        SetOperations<String, String> setOps = template.opsForSet();
        Set<String> completedTaskIds = setOps.members(COMPLETED_TASK_IDS_PREFIX + userId);
    
        if (completedTaskIds != null && !completedTaskIds.isEmpty()) {
            for (String taskId : completedTaskIds) {
                template.delete(COMPLETED_TASK_PREFIX + taskId);
            }
        }

        template.delete(COMPLETED_TASK_IDS_PREFIX + userId);
    }

    // Assuming users wont have 2,147,483,647 tasks

    // ssize "userTaskIds:userId"
    public int numberOfCurrentTasks(String userId) {

        SetOperations<String, String> setOps = template.opsForSet();
        
        return Math.toIntExact(setOps.size(TASK_IDS_PREFIX + userId));
    }

    // sssize "userCompletedTaskIds:userId"
    public int numberOfCompletedTasks(String userId) {

        SetOperations<String, String> setOps = template.opsForSet();

        return Math.toIntExact(setOps.size(COMPLETED_TASK_IDS_PREFIX + userId));
    }
}
