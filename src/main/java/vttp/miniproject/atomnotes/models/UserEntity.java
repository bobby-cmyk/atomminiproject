package vttp.miniproject.atomnotes.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserEntity implements UserDetails {

    @Id
    private String id;
    private long createdEpochTime;
    private String username;
    private String password;
    private String email;
    private String role;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public long getCreatedEpochTime() {return createdEpochTime;}
    public void setCreatedEpochTime(long createdEpochTime) {this.createdEpochTime = createdEpochTime;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}
    
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
    
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getRole() {return role;}
    public void setRole(String role) {this.role = role;}

    public LocalDateTime getCreatedDateTime() {
        Instant instant = Instant.ofEpochMilli(createdEpochTime);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    public static UserEntity mapToUserEntity(Map<Object, Object> userMap) {
        UserEntity user = new UserEntity();

        user.setId(null);
        user.setCreatedEpochTime(Long.parseLong((String) userMap.get("createdEpochTime")));
        user.setUsername((String) userMap.get("username"));
        user.setPassword((String) userMap.get("password"));
        user.setEmail((String) userMap.get("email"));
        user.setRole((String) userMap.get("role"));
        
        return user;
    }

    // Assuming that each user only have a single role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.isEmpty()) {
            // Return an empty list if no role is set
            return Collections.emptyList();
        }
        // Convert the single role String (e.g., "ROLE_USER") to a GrantedAuthority
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    // Not actively managed
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    @Override
    public String toString() {
        return "UserEntity [id=" + id + ", createdEpochTime=" + createdEpochTime + ", username=" + username
                + ", password=" + password + ", email=" + email + ", role=" + role + "]";
    }
}