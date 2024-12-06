package vttp.miniproject.atomnotes.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class User {

    private String id;
    private long createdEpochTime;
    private String username;
    private String password;
    private String email;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public long getCreatedEpochTime() {
        return createdEpochTime;
    }

    public void setCreatedEpochTime(long createdEpochTime) {
        this.createdEpochTime = createdEpochTime;
    }

    public LocalDateTime getCreatedDateTime() {
        
        Instant instant = Instant.ofEpochMilli(createdEpochTime);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        return localDateTime;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}