package vttp.miniproject.atomnotes.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.services.GenService;
import vttp.miniproject.atomnotes.services.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final Logger logger = Logger.getLogger(TaskController.class.getName());

    @Autowired
    private TaskService taskSvc;

    @Autowired
    private GenService genSvc;

    @GetMapping("/all")
    public ModelAndView getHome(
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();

        String user = sess.getAttribute("user").toString();

        List<Task> tasks = taskSvc.getAllSortedTasks(user);

        mav.addObject("tasks", tasks);
        mav.setViewName("task-all");
        
        return mav;
    }

    @GetMapping("/new")
    public ModelAndView getNewTaskForm(
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();
        
        if (sess.getAttribute("user") == null) {

            logger.info("Unauthenticated vistor. Redirected to login page.");

            mav.setViewName("redirect:/login");

            return mav;
        }
        
        Task task = new Task();
        
        logger.info("Creating new task");

        mav.addObject("task", task);
        mav.setViewName("task-new");
        
        return mav;  
    }
 
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

        String imageUrl = genSvc.retrieveImageUrl(task.getContent());

        task.setImageUrl(imageUrl);

        taskSvc.addTask(user, task);

        logger.info("User: %s added a new task".formatted(user));

        mav.setViewName("redirect:/task/all");

        return mav;
    }

    
    @PostMapping("/delete")
    public ModelAndView deleteTask(
        HttpSession sess,
        @RequestBody MultiValueMap<String, String> values
    ) 
    {
        ModelAndView mav = new ModelAndView();

        String user = sess.getAttribute("user").toString();

        String taskId = values.getFirst("taskId");

        taskSvc.deleteTask(user, taskId);

        logger.info("User: %s deleted a task id: %s".formatted(user, taskId));

        mav.setViewName("redirect:/task/all");

        return mav;
    }

    @PostMapping("/new/gensubtasks")
    public ModelAndView generateSubtasksNew(
        @Valid Task task,
        BindingResult bindings
    )
    {
        ModelAndView mav = new ModelAndView();

        // Syntax validation for task form
        if (bindings.hasErrors()) {

            mav.setViewName("task-new");

            return mav;
        }

        String content = task.getContent();

        logger.info("Generating subtasks for task: %s".formatted(content));

        List<String> subtasks = genSvc.generateSubtasks(content);

        task.setSubtasks(subtasks);

        mav.setViewName("task-new");

        return mav;
    }
        
    @PostMapping("/edit/gensubtasks")
    public ModelAndView generateSubtaskEdit(
        @Valid Task task,
        BindingResult bindings
    )
    {
        ModelAndView mav = new ModelAndView();

        // Syntax validation for task form
        if (bindings.hasErrors()) {

            mav.setViewName("task-edit");

            return mav;
        }

        String content = task.getContent();

        logger.info("Generating subtasks for task: %s".formatted(content));

        List<String> subtasks = genSvc.generateSubtasks(content);

        task.setSubtasks(subtasks);

        mav.setViewName("task-edit");
        
        return mav;
    }

    @GetMapping("/edit")
    public ModelAndView getEditTaskForm(
        @RequestParam("taskId") String taskId,
        HttpSession sess
    ) 
    {
        ModelAndView mav = new ModelAndView();
        
        if (sess.getAttribute("user") == null) {

            logger.info("Unauthenticated vistor. Redirected to login page.");

            mav.setViewName("redirect:/login");

            return mav;
        }

        // Get task info
        Task task = taskSvc.getTask(taskId);
        
        logger.info("Editing task: %s".formatted(taskId));

        mav.addObject("task", task);
        mav.setViewName("task-edit");
        
        return mav;  
    }

    @PostMapping("/update")
    public ModelAndView updateTask(
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

        taskSvc.updateTask(task);

        logger.info("Task: %s is updated.".formatted(task.getId()));

        mav.setViewName("redirect:/task/all");

        return mav;
    }
}
