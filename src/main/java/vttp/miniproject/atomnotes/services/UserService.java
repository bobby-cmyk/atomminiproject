package vttp.miniproject.atomnotes.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.models.AuthUserDetails;
import vttp.miniproject.atomnotes.models.SignUpForm;
import vttp.miniproject.atomnotes.models.UserEntity;
import vttp.miniproject.atomnotes.repositories.UserRepo;

@Service
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void createUser(SignUpForm signUpForm) {

        UserEntity user = new UserEntity();

        // Generate a unique id for user
        String id = UUID.randomUUID().toString();
        long createdTime = Instant.now().toEpochMilli();

        // Set information for user;
        user.setId(id);
        user.setUsername(signUpForm.getUsername());
        user.setPassword(passwordEncoder.encode(signUpForm.getPassword()));
        user.setEmail(signUpForm.getEmail());
        user.setCreatedTime(createdTime);
        user.setRole("USER");
        user.setCreateMethod("default");

        userRepo.createUser(user);
    }

    public boolean isEmailTaken(String email) {
        return userRepo.isEmailTaken(email);
    }

    public boolean isUsernameTaken(String username) {
        return userRepo.isUsernameTaken(username);
    }


    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        
        String userId = null;
        if (input.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            // treat as email
            userId = userRepo.getIdFromEmail(input);
        }

        else {
            // treat as username
            userId = userRepo.getIdFromUsername(input);
        }

        if (userId == null) {
            throw new UsernameNotFoundException("User not found: " + input);
        }

        UserEntity user = userRepo.getUserEntity(userId);

        return new AuthUserDetails(
            user.getId(),
            user.getUsername(),
            user.getPassword(),  
            List.of(() -> "ROLE_" + user.getRole()));
    }
}
