package vttp.miniproject.atomnotes.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.services.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final Logger logger = Logger.getLogger(TaskController.class.getName());

    @Autowired
    private TaskService taskSvc;

    @GetMapping("/all")
    public ModelAndView getHome(
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();

        // TODO Consider using SecurityFilterChain
        // Redirect to login page if user is not logged in
        if (sess.getAttribute("user") == null) {

            logger.info("Unauthenticated vistor. Redirected to login page.");

            mav.setViewName("redirect:/login");

            return mav;
        }

        mav.setViewName("home");
        
        return mav;
    }

    @GetMapping("/new")
    public ModelAndView getTaskForm(
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();
        
        if (sess.getAttribute("user") == null) {

            logger.info("Unauthenticated vistor. Redirected to login page.");

            mav.setViewName("redirect:/login");

            return mav;
        }

        // Initialise new task
        Task task = new Task();
        
        logger.info("Creating new task");

        mav.addObject("task", task);
        mav.setViewName("task");
        
        return mav;  
    }

    /*
     * 
    
    @PostMapping("/add")
    public ModelAndView addTask(
        @Valid Task task,
        BindingResult bindings,
        HttpSession sess
    ) 
    {  
        ModelAndView mav = new ModelAndView();

        // Syntax validation for task form
        if (bindings.hasErrors()) {

            mav.setViewName("task");

            return mav;
        }

        String user = sess.getAttribute("user").toString();

        taskSvc.addTask(user, task);

        logger.info("User: %s added a new task".formatted(user));

        mav.setViewName("home");

        return mav;
    }

     */

    @PostMapping("/gensubtasks")
    public ModelAndView generateSubtasks(
        @Valid Task task,
        BindingResult bindings
    )
    {
        ModelAndView mav = new ModelAndView();

        // Syntax validation for task form
        if (bindings.hasErrors()) {

            mav.setViewName("task");

            return mav;
        }

        String content = task.getContent();

        logger.info("Generating subtasks for task: %s".formatted(content));

        List<String> subtasks = taskSvc.generateSubtasks(content);

        task.setSubtasks(subtasks);

        

        mav.setViewName("task");
        
        return mav;
    }
}
