package vttp.miniproject.atomnotes.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.miniproject.atomnotes.models.Task;

@Repository
public class TaskRepo {
    
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    // hgetall taskId
    public Task getTask(String taskId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> taskMap = hashOps.entries(taskId);

        return Task.mapToTask(taskMap);
    }

    public List<Task> getTasks(Set<String> taskIds) {
        
        // Comment: using arraylist to sort them by date when displaying
        List<Task> tasks = new ArrayList<>();

        HashOperations<String, String, String> hashOps = template.opsForHash();

        for (String taskId : taskIds) {

            Task task = Task.mapToTask(hashOps.entries(taskId));

            tasks.add(task);
        }

        return tasks;
    }

    // hsetall taskId key1 value1 key2 value2 ... 
    public void addTask(Task task) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> values = new HashMap<>();

        String taskId = task.getId();

        values.put("id", taskId);
        values.put("addedEpochTime", Long.toString(task.getAddedEpochTime()));
        values.put("content", task.getContent());
        values.put("subtasks", task.getSubtasksString());
        values.put("imageUrl", task.getImageUrl());

        hashOps.putAll(taskId, values);
    }

    // del taskId
    public void deleteTask(String taskId) {
        
        template.delete(taskId);
    }

    // hsetall taskId key1 value1 key2 value2 ... 
    public void updateTask(Task task) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> values = new HashMap<>();

        String taskId = task.getId();

        values.put("id", taskId);
        values.put("addedEpochTime", Long.toString(task.getAddedEpochTime()));
        values.put("content", task.getContent());
        values.put("subtasks", task.getSubtasksString());
        values.put("imageUrl", task.getImageUrl());

        //Overwrite
        hashOps.putAll(taskId, values);
    }

}
