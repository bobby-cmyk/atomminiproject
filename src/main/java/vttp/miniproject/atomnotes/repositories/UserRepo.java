package vttp.miniproject.atomnotes.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import vttp.miniproject.atomnotes.models.UserEntity;

@Repository
public class UserRepo {
    
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    private static final String USER_KEY_PREFIX = "user:";
    private static final String EMAIL_KEY_PREFIX = "emailToUserId:";
    private static final String USERNAME_KEY_PREFIX = "usernameToUserId:";

    // hset "user:userId" id : idValue email : emailValue ...
    // set "emailToUserId:userEmail"
    // set "usernameToUserId:userUsername"
    public void createUser(UserEntity user) {

        HashOperations<String, String, String> hashOps = template.opsForHash();
        ValueOperations<String, String> valueOps = template.opsForValue();

        // Add user details into a redis hash 
        Map<String, String> values = new HashMap<>();
        values.put("id", user.getId());
        values.put("createdEpochTime", String.valueOf(user.getCreatedEpochTime()));
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());

        hashOps.putAll(USER_KEY_PREFIX + user.getId(), values);

        // Add email and username mapping to userId
        valueOps.set(EMAIL_KEY_PREFIX + user.getEmail(), user.getId());
        valueOps.set(USERNAME_KEY_PREFIX + user.getUsername(), user.getId());
    }

    // exist "emailToUserId:email"
    public boolean isEmailTaken(String email) {
        return template.hasKey(EMAIL_KEY_PREFIX + email);
    }

    // exist "usernameToUserId:username"
    public boolean isUsernameTaken(String username) {
        return template.hasKey(USERNAME_KEY_PREFIX + username);
    }

    // get "emailToUserId:email"
    public String getIdFromEmail(String email) {
        return template.opsForValue().get(EMAIL_KEY_PREFIX + email);
    }

    // get "usernameToUserId:username"
    public String getIdFromUsername(String username) {
        return template.opsForValue().get(USERNAME_KEY_PREFIX + username);
    }

    // hgetall "user:userId"
    public UserEntity getUserEntity(String userId) {
        
        Map<Object, Object> userMap = template.opsForHash().entries(USER_KEY_PREFIX + userId);
        UserEntity user = UserEntity.mapToUserEntity(userMap);
        
        return user;
    }
}
