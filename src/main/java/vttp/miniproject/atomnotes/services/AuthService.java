package vttp.miniproject.atomnotes.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.models.SignUpForm;
import vttp.miniproject.atomnotes.models.User;
import vttp.miniproject.atomnotes.repositories.AuthRepo;

@Service
public class AuthService {
    
    @Autowired
    private AuthRepo authRepo;

    public boolean isUniqueUsername(String username) {
        return !authRepo.isUserExist(username);
    }

    public void createAccount(SignUpForm signUpForm) {

        User user = new User();

        // Generate a unique id for user
        String id = UUID.randomUUID().toString();
        long createdEpochTime = Instant.now().toEpochMilli();

        // Set information for user;
        user.setId(id);
        user.setUsername(signUpForm.getUsername());
        user.setPassword(signUpForm.getPassword());
        user.setEmail(signUpForm.getEmail());
        user.setCreatedEpochTime(createdEpochTime);

        authRepo.createAccount(user);
    }

    public boolean isValidLogin(String inputUsername, String inputPassword) {
        
        // User does not exist
        if (!authRepo.isUserExist(inputUsername)) {
            return false;
        }

        // User exist -> check password and username matches
        else {
            String password = authRepo.getUserPassword(inputUsername);

            if (password.equals(inputPassword)) {
                return true;
            }

            else {
                return false;
            }
        }
    }
}
