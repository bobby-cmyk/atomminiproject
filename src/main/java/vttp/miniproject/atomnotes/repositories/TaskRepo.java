package vttp.miniproject.atomnotes.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.miniproject.atomnotes.models.Task;

@Repository
public class TaskRepo {
    
    @Autowired @Qualifier("redisTemplate2")
    private RedisTemplate<String, String> template;

    // hgetall taskId
    public Task getTask(String taskId) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> taskMap = hashOps.entries(taskId);

        return Task.mapToTask(taskMap);
    }

    // hsetall 
    public void addTask(Task task) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> values = new HashMap<>();

        String taskId = task.getId();

        values.put("id", taskId);
        values.put("content", task.getContent());
        values.put("subtasks", task.getSubtasksString());

        hashOps.putAll(taskId, values);
    }

}
