package vttp.miniproject.atomnotes.repositories;

import java.time.Duration;
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

    private final String USER_PREFIX = "user:";
    private final String EMAIL_PREFIX = "emailToUserId:";
    private final String USERNAME_PREFIX = "usernameToUserId:";
    private final String API_PREFIX = "apiToken:";

    // hset "user:userId" id : idValue email : emailValue ...
    // set "emailToUserId:userEmail"
    // set "usernameToUserId:userUsername"
    public void createUser(UserEntity user) {

        HashOperations<String, String, String> hashOps = template.opsForHash();
        ValueOperations<String, String> valueOps = template.opsForValue();

        // Add user details into a redis hash 
        Map<String, String> values = new HashMap<>();
        values.put("id", user.getId());
        values.put("createdTime", String.valueOf(user.getCreatedTime()));
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("role", user.getRole());
        values.put("createMethod", user.getCreateMethod());

        hashOps.putAll(USER_PREFIX + user.getId(), values);

        // If account created via username or email
        if (user.getCreateMethod().equals("default")) {
            // Add email and username mapping to userId
            valueOps.set(EMAIL_PREFIX + user.getEmail(), user.getId());
            valueOps.set(USERNAME_PREFIX + user.getUsername(), user.getId());
        }  
    }

    // exists "emailToUserId:email"
    public boolean isEmailTaken(String email) {
        return template.hasKey(EMAIL_PREFIX + email);
    }

    // exists "usernameToUserId:username"
    public boolean isUsernameTaken(String username) {
        return template.hasKey(USERNAME_PREFIX + username);
    }

    // get "emailToUserId:email"
    public String getIdFromEmail(String email) {
        return template.opsForValue().get(EMAIL_PREFIX + email);
    }

    // get "usernameToUserId:username"
    public String getIdFromUsername(String username) {
        return template.opsForValue().get(USERNAME_PREFIX + username);
    }

    // hgetall "user:userId"
    public UserEntity getUserEntity(String userId) {
        
        Map<Object, Object> userMap = template.opsForHash().entries(USER_PREFIX + userId);
        UserEntity user = UserEntity.mapToUserEntity(userMap);
        
        return user;
    }

    // exists "user:userId"
    public boolean isIdTaken(String userId) {
        return template.hasKey(USER_PREFIX + userId);
    }

    // set "token: userId" token ex 86,400
    public void storeUserApiToken(String userId, String token) {
        
        ValueOperations<String, String> valueOps = template.opsForValue();

        valueOps.set(API_PREFIX + token, userId, Duration.ofHours(24));
    }

    // get "apiToken:token"
    public String getIdFromToken(String token) {
        return template.opsForValue().get(API_PREFIX + token);
    }

    // exists "apiToken:token"
    public boolean tokenExist(String token) {
        return template.hasKey(API_PREFIX +token);
    }

}
