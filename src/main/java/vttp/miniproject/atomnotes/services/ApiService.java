package vttp.miniproject.atomnotes.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.repositories.UserRepo;

@Service
public class ApiService {

    @Autowired
    private UserRepo userRepo;
    
    public String generateApiToken(String userId) {

        // Will only be shown once 

        String token = UUID.randomUUID().toString();

        userRepo.storeUserApiToken(userId, token);
         
        return token;
    }

    public String retrieveByToken(String token) {

        String userId = userRepo.getIdFromToken(token);

        return userId;
    }

    public boolean tokenExist(String token) {

        return userRepo.tokenExist(token);
    }
}
