package vttp.miniproject.atomnotes.controllers;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping
public class IndexController {

    // Initialise a logger for IndexController
    private final Logger logger = Logger.getLogger(IndexController.class.getName());
    
    // GET method for the index page/homepage
    @GetMapping
    public ModelAndView getIndex(
        HttpSession sess
    ) {
        ModelAndView mav = new ModelAndView();

        // Check if there are any user attribute in session
        // If there are no user in session...
        /*
        
            if (sess.getAttribute("user") == null) {

            logger.info("Unauthenticated vistor. Redirected to login page.");

            // Set view to redirect back to login
            mav.setViewName("redirect:/login");

            return mav;
        }
         */
        

        //String user = sess.getAttribute("user").toString();

        //List<Task> tasks = taskSvc.getAllSortedTasks(user);

        //String schedule = "Hi aiken";
        
        // taskSvc.getSchedule(tasks);

        //mav.addObject("schedule", schedule);
        mav.setViewName("redirect:/task/all");

        return mav;
    }
}
