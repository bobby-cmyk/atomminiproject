package vttp.miniproject.atomnotes.repositories;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class ConnectorRepo {
    
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    // smembers username taskId
    public Set<String> getUserTaskIds(String username) {
        
        SetOperations<String, String> setOps = template.opsForSet();

        Set<String> taskIds = setOps.members(username);

        return taskIds;
    }

    // srem username taskId
    public void removeTaskId(String username, String taskId) {

        SetOperations<String, String> setOps = template.opsForSet();

        setOps.remove(username, taskId);
    }

    // sadd username taskId
    public void addTaskId(String username, String taskId) {

        SetOperations<String, String> setOps = template.opsForSet();

        setOps.add(username, taskId);
    }

    // srem username taskId
    public void deleteTaskId(String username, String taskId) {

        SetOperations<String, String> setOps = template.opsForSet();

        setOps.remove(username, taskId);
    }
}
