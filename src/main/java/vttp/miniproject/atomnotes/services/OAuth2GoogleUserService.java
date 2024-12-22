package vttp.miniproject.atomnotes.services;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.models.UserEntity;
import vttp.miniproject.atomnotes.repositories.UserRepo;

@Service
public class OAuth2GoogleUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

    @Autowired
    private UserRepo userRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String sub = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // Check if this googleAccount has been stored in redis

        // If there's existing account, update the details if there's changes in google
        // Create an account if new login with Google
        if (!userRepo.isIdTaken(sub)) {
            UserEntity user = new UserEntity();

            long createdEpochTime = Instant.now().toEpochMilli();

            // Set information for user;
            user.setId(sub);
            user.setUsername(name);
            user.setPassword("GOOGLE_DUMMY_PASSWORD");
            user.setEmail(email);
            user.setCreatedEpochTime(createdEpochTime);
            user.setRole("USER");
            user.setCreateMethod("google");

            System.out.printf("\nUser: %s\n", user);

            userRepo.createUser(user);
        }
        return oAuth2User;
    }
    
}
