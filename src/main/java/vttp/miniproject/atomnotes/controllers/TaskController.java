package vttp.miniproject.atomnotes.controllers;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vttp.miniproject.atomnotes.models.AuthUserDetails;
import vttp.miniproject.atomnotes.models.DeleteConfirmation;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.services.GenService;
import vttp.miniproject.atomnotes.services.StatsService;
import vttp.miniproject.atomnotes.services.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController {

    private final Logger logger = Logger.getLogger(TaskController.class.getName());

    @Autowired
    private TaskService taskSvc;

    @Autowired
    private GenService genSvc;

    @Autowired
    private StatsService statsSvc;

    @GetMapping("/all")
    public ModelAndView allTasks(
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User) {

        String userId = getUserId(authUser, oAuth2User);
        String username = getUsername(authUser, oAuth2User);
        
        ModelAndView mav = new ModelAndView();

        List<Task> tasks = taskSvc.getAllSortedCurrentTasks(userId);
        
        int numberOfTasks = statsSvc.numberOfCurrentTasks(userId);

        String quote = genSvc.getQuote();

        logger.info("User: %s loaded homepage".formatted(userId));

        mav.addObject("quote", quote);
        mav.addObject("username", username);
        mav.addObject("numberOfTasks", numberOfTasks);
        mav.addObject("tasks", tasks);
        mav.setViewName("task-all");
        
        return mav;
    }

    @GetMapping("/new")
    public ModelAndView getCreateTaskForm(
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) 
    {
        ModelAndView mav = new ModelAndView();
        
        String userId = getUserId(authUser, oAuth2User);

        // if user have reached 10 current tasks, they cannot add more new task
        // Redirect to homepage
        if (statsSvc.numberOfCurrentTasks(userId) >= 10) {

            logger.info("User: %s has reached maximum number of current tasks. Unable to add more tasks.");

            mav.setViewName("redirect:/task/all");

            return mav;
        }

        Task task = new Task();
        
        logger.info("User: %s creating new task".formatted(userId));

        mav.addObject("task", task);
        mav.setViewName("task-new");
        
        return mav;  
    }
 
    @PostMapping("/new")
    public ModelAndView createTask(
        @Valid Task task,
        BindingResult bindings,
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User
    )
    {  
        ModelAndView mav = new ModelAndView();

        String userId = getUserId(authUser, oAuth2User);

        if (bindings.hasErrors()) {

            mav.setViewName("task-new");

            return mav;
        }

        // Summarise task content into one word and query for an image on Unsplash
        String imageUrl = genSvc.retrieveImageUrl(task.getContent());

        task.setImageUrl(imageUrl);

        taskSvc.createTask(task, userId);

        logger.info("User: %s added a new task".formatted(userId));

        mav.setViewName("redirect:/task/all");

        return mav;
    }

    @PostMapping("/complete")
    public ModelAndView completeTask(
        @RequestBody MultiValueMap<String, String> values,
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) 
    {
        ModelAndView mav = new ModelAndView();

        String userId = getUserId(authUser, oAuth2User);
        String taskId = values.getFirst("taskId");

        taskSvc.completeTask(taskId, userId);

        logger.info("User: %s completed a task: %s".formatted(userId, taskId));

        mav.setViewName("redirect:/task/all");

        return mav;
    }

    @PostMapping("/generate")
    public ModelAndView generateSubtasks(
        @Valid Task task,
        BindingResult bindings
    )
    {
        ModelAndView mav = new ModelAndView();

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
        
    @PostMapping("/regenerate")
    public ModelAndView regenerateSubtasks(
        @Valid Task task,
        BindingResult bindings
    )
    {
        ModelAndView mav = new ModelAndView();

        if (bindings.hasErrors()) {

            mav.setViewName("task-edit");

            return mav;
        }

        String content = task.getContent();

        logger.info("Regenerating subtasks for task: %s".formatted(content));

        List<String> subtasks = genSvc.generateSubtasks(content);

        task.setSubtasks(subtasks);

        mav.setViewName("task-edit");
        
        return mav;
    }

    @GetMapping("/edit/{taskId}")
    public ModelAndView getEditTaskForm(
       @PathVariable("taskId") String taskId
    ) 
    {
        ModelAndView mav = new ModelAndView();

        // Get task info
        Task task = taskSvc.getCurrentTask(taskId);
        
        logger.info("Editing task: %s".formatted(taskId));

        mav.addObject("task", task);
        mav.setViewName("task-edit");
        
        return mav;  
    }

    @PostMapping("/edit")
    public ModelAndView updateTask(
        @Valid Task task,
        BindingResult bindings
    ) 
    {  
        ModelAndView mav = new ModelAndView();

        if (bindings.hasErrors()) {

            mav.setViewName("task-edit");

            return mav;
        }

        taskSvc.updateTask(task);

        logger.info("Task: %s is updated.".formatted(task.getId()));

        mav.setViewName("redirect:/task/all");

        return mav;
    }

    @GetMapping("/tools")
    public ModelAndView getTaksTools() {
        ModelAndView mav = new ModelAndView();

        // Add a DeleteConfirmation object for confirmation before deleting tasks
        mav.addObject("deleteConfirmation", new DeleteConfirmation());
        mav.setViewName("task-tools");

        return mav;
    }

    @GetMapping(path="/export", produces="text/csv")
    @ResponseBody 
    public ResponseEntity<byte[]> exportAllTasks(
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) 
    {
        String userId = getUserId(authUser, oAuth2User);

        List<Task> allTasks = taskSvc.getAllSortedCurrentTasks(userId);
        allTasks.addAll(taskSvc.getAllSortedCompletedTasks(userId));

        // Convert List<Task> to csvBytes
        byte[] csvBytes = Task.generateCsv(allTasks);

        // Get current Date
        ZonedDateTime date = Instant.now().atZone(ZoneId.systemDefault());
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "csv"));
        headers.setContentDispositionFormData("attachment", "tasks_%s_%s.csv".formatted(formattedDate, userId));

        logger.info("User: %s exported tasks csv".formatted(userId));

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    };

    @PostMapping("/clear")
    public ModelAndView deleteAllCompletedTasks(
        @AuthenticationPrincipal AuthUserDetails authUser,
        @AuthenticationPrincipal OAuth2User oAuth2User,
        @Valid DeleteConfirmation deleteConfirmation,
        BindingResult bindings) 
    {
        String userId = getUserId(authUser, oAuth2User);

        ModelAndView mav = new ModelAndView();

        // If the confirmation message is wrong
        if (bindings.hasErrors()) {
            
            mav.setViewName("task-tools");

            return mav;
        }

        taskSvc.clearAllCompletedTasks(userId);

        logger.info("User: %s cleared all completed tasks".formatted(userId));

        mav.setViewName("redirect:/profile");

        return mav;
    }

    public static String getUserId(AuthUserDetails authUser, OAuth2User oAuth2User) {

        if (authUser == null) {
            return oAuth2User.getAttribute("sub");
        }
        return authUser.getId();
    }

    public static String getUsername(AuthUserDetails authUser, OAuth2User oAuth2User) {

        if (authUser == null) {
            return oAuth2User.getAttribute("name");
        }
        return authUser.getUsername();
    }
}
