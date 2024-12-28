package vttp.miniproject.atomnotes.controllers;

import static vttp.miniproject.atomnotes.controllers.TaskController.getUserId;
import static vttp.miniproject.atomnotes.controllers.TaskController.getUsername;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vttp.miniproject.atomnotes.models.AuthUserDetails;
import vttp.miniproject.atomnotes.models.DeleteConfirmation;
import vttp.miniproject.atomnotes.models.UserStats;
import vttp.miniproject.atomnotes.services.StatsService;

@Controller
@RequestMapping
public class ProfileController {

    @Autowired
    private StatsService statsSvc;

    private final Logger logger = Logger.getLogger(ProfileController.class.getName());
    
    @GetMapping("/profile")
    public ModelAndView profilePage(
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) 
    {
        ModelAndView mav = new ModelAndView();

        String userId = getUserId(authUser, oAuth2User);
        String username = getUsername(authUser, oAuth2User);

        UserStats userStats = statsSvc.getUserStats(userId);

        logger.info("User: %s loaded user stats".formatted(userId));
        
        mav.addObject("userStats", userStats);
        mav.addObject("username", username);
        mav.addObject("deleteConfirmation", new DeleteConfirmation());
        mav.setViewName("profile");
        
        return mav;
    }
}
