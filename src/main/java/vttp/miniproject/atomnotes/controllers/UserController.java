package vttp.miniproject.atomnotes.controllers;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.miniproject.atomnotes.models.LoginForm;
import vttp.miniproject.atomnotes.models.SignUpForm;
import vttp.miniproject.atomnotes.services.UserService;

@Controller
@RequestMapping
public class UserController {

    private final Logger logger = Logger.getLogger(UserController.class.getName());

    @Autowired
    private UserService userSvc;

    @GetMapping("/login")
    public ModelAndView getLogin(
        HttpSession sess
    ) 
    {   
        ModelAndView mav = new ModelAndView();

        LoginForm loginForm = new LoginForm();

        logger.info("A visitor reached login page.");

        mav.addObject("loginForm", loginForm);
        mav.setViewName("login");

        return mav;
    }

    @GetMapping("/signup")
    public ModelAndView getSignUp() 
    {
        ModelAndView mav = new ModelAndView();

        SignUpForm signUpForm = new SignUpForm();

        logger.info("A visitor has reached sign up page");

        mav.addObject("signUpForm", signUpForm);
        mav.setViewName("signup");

        return mav;
    }

    @PostMapping("/signup")
    public ModelAndView postSignUp(
        @Valid SignUpForm signUpForm,
        BindingResult bindings
    ) 
    {
        ModelAndView mav = new ModelAndView();

        if (bindings.hasErrors()) {

            logger.info("Invalid sign up form submitted");

            mav.setViewName("signup");

            return mav;
        }

        // Get username and email to check if they have been taken
        String username = signUpForm.getUsername();
        String email = signUpForm.getEmail();

        if (userSvc.isUsernameTaken(username)) {
            
            logger.info("Username: %s has been taken".formatted(username));

            ObjectError err = new ObjectError("globalError", "Username has been taken");
            bindings.addError(err);

            mav.setViewName("signup");

            return mav;
        }

        if (userSvc.isEmailTaken(email)) {
            
            logger.info("Email: %s has been taken".formatted(email));

            ObjectError err = new ObjectError("globalError", "Email has been taken");
            bindings.addError(err);

            mav.setViewName("signup");

            return mav;
        }

        logger.info("Creating account: %s".formatted(username));

        // Create the user account
        userSvc.createUser(signUpForm);
        
        logger.info("Account: %s is successfully created!".formatted(username));
        
        // Set view to login page
        mav.setViewName("redirect:/login");

        return mav;
    }
}
