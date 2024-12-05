package vttp.miniproject.atomnotes.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import vttp.miniproject.atomnotes.models.User;

@Repository
public class AuthRepo {
    
    @Autowired @Qualifier("redisTemplate1")
    private RedisTemplate<String, String> template;

    // exist username
    public boolean isUserExist(String username) {
        return template.hasKey(username);
    }

    // hset username_key id id_value email email_value username username_value password password_value
    public void createAccount(User user) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        Map<String, String> values = new HashMap<>();
        values.put("id", user.getId());
        values.put("email", user.getEmail());
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());

        hashOps.putAll(user.getUsername(), values);
    }

    // hget username password
    public String getUserPassword(String username) {

        HashOperations<String, String, String> hashOps = template.opsForHash();

        return hashOps.get(username, "password");
    }
}
