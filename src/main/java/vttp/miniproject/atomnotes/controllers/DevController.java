package vttp.miniproject.atomnotes.controllers;

import static vttp.miniproject.atomnotes.controllers.TaskController.getUserId;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import vttp.miniproject.atomnotes.models.AuthUserDetails;
import vttp.miniproject.atomnotes.services.ApiService;

@Controller
@RequestMapping("/dev")
public class DevController {
    
    @Autowired
    private ApiService apiSvc;

    private final Logger logger = Logger.getLogger(DevController.class.getName());

    @GetMapping
    public ModelAndView getDevPage() {
        ModelAndView mav = new ModelAndView();

        logger.info("A visitor reached the dev page");

        mav.setViewName("dev.html");

        return mav;
    }

    @PostMapping("/token")
    public ModelAndView generateToken(
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) 
    {
        ModelAndView mav = new ModelAndView();

        String userId = getUserId(authUser, oAuth2User);

        String apiToken = apiSvc.generateApiToken(userId);

        logger.info("User: %s generated an API token".formatted(userId));
        
        mav.addObject("apiToken", apiToken);
        mav.setViewName("dev.html");

        return mav;
    }

    @GetMapping("/documentation")
    public ModelAndView getDocumentation() {
        ModelAndView mav = new ModelAndView();

        logger.info("A visitor reached the API documentation page");

        mav.setViewName("documentation.html");

        return mav;
    }
}
