package vttp.miniproject.atomnotes.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.services.TaskService;

@Controller
@RequestMapping
public class IndexController {

    @Autowired
    private TaskService taskSvc;

    private final Logger logger = Logger.getLogger(IndexController.class.getName());
    
    @GetMapping
    public ModelAndView getIndex(
        HttpSession sess
    ) {
        ModelAndView mav = new ModelAndView();

        if (sess.getAttribute("user") == null) {

            logger.info("Unauthenticated vistor. Redirected to login page.");

            mav.setViewName("redirect:/login");

            return mav;
        }

        //String user = sess.getAttribute("user").toString();

        //List<Task> tasks = taskSvc.getAllSortedTasks(user);

        String schedule = "Hi aiken";
        
        // taskSvc.getSchedule(tasks);

        mav.addObject("schedule", schedule);
        mav.setViewName("home");

        return mav;
    }
}
