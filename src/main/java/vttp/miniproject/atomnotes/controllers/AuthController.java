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
import vttp.miniproject.atomnotes.services.AuthService;

@Controller
@RequestMapping
public class AuthController {

    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private AuthService authSvc;

    @GetMapping("/login")
    public ModelAndView getLogin(
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();

        if (sess.getAttribute("user") != null) {
            mav.setViewName("redirect:/");
            return mav;
        }

        LoginForm loginForm = new LoginForm();

        logger.info("User reached login page.");

        mav.addObject("loginForm", loginForm);
        mav.setViewName("login");

        return mav;
    }

    @PostMapping("/auth")
    public ModelAndView postLoginForm(
        @Valid LoginForm loginForm,
        BindingResult bindings,
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();

        // Syntax validation for form fields
        if (bindings.hasErrors()) {

            logger.info("Invalid login form submitted: %s".formatted(loginForm));

            mav.setViewName("login");

            return mav;
        }

        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        
        // Invalid credentails
        if (!authSvc.isValidLogin(username, password)) {

            logger.info("Invalid credentials. Failed login attempt.");

            ObjectError err = new ObjectError("globalError", "Invalid username or password");

            bindings.addError(err);

            mav.setViewName("login");

            return mav;
        }

        logger.info("User: %s is successfully authenticated. Redirected to home page".formatted(username));

        sess.setAttribute("user", username);
        
        mav.setViewName("redirect:/");
        
        return mav;
    }

    @GetMapping("/signup")
    public ModelAndView getSignUp(
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();

        if (sess.getAttribute("user") != null) {
            mav.setViewName("redirect:/");
            return mav;
        }

        SignUpForm signUpForm = new SignUpForm();

        logger.info("User reached sign up page");

        mav.addObject("signUpForm", signUpForm);
        mav.setViewName("sign-up");

        return mav;
    }

    @PostMapping("/createaccount")
    public ModelAndView postSignUp(
        @Valid SignUpForm signUpForm,
        BindingResult bindings
    ) 
    {
        ModelAndView mav = new ModelAndView();

        // Syntax validation for form fields
        if (bindings.hasErrors()) {

            logger.info("Invalid sign up form submitted: %s".formatted(signUpForm));

            mav.setViewName("sign-up");

            return mav;
        }

        String username = signUpForm.getUsername();

        // Check if username has been taken already
        if (!authSvc.isUniqueUsername(username)) {
            
            logger.info("Username: %s has been taken".formatted(username));

            ObjectError err = new ObjectError("globalError", "Username has been taken");

            bindings.addError(err);

            mav.setViewName("sign-up");

            return mav;
        }

        logger.info("Creating account: %s".formatted(username));

        authSvc.createAccount(signUpForm);
        
        logger.info("Account: %s is successfully created!".formatted(username));
        
        mav.setViewName("redirect:/login");

        return mav;
    }

    @PostMapping("/logout")
    public ModelAndView logoutUser(
        HttpSession sess
    ) {
        ModelAndView mav = new ModelAndView();

        sess.invalidate();

        mav.setViewName("redirect:/login");
        
        return mav;
    }
}
