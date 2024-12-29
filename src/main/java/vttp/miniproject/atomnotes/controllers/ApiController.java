package vttp.miniproject.atomnotes.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import vttp.miniproject.atomnotes.models.AuthUserDetails;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.services.TaskService;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private TaskService taskSvc;

    private final Logger logger = Logger.getLogger(ApiController.class.getName());

    @GetMapping(path="/tasks/all", produces="application/json") 
    public ResponseEntity<String> getAllTasks(@AuthenticationPrincipal AuthUserDetails authUser)
    {   
        String userId = authUser.getId();

        List<Task> allTasks = taskSvc.getAllSortedCurrentTasks(userId);
        allTasks.addAll(taskSvc.getAllSortedCompletedTasks(userId));

        JsonArrayBuilder tasksArrBuilder = Json.createArrayBuilder();
        
        for (Task task : allTasks) {
            tasksArrBuilder.add(task.taskToJsonObj());
        }

        logger.info("User: %s retrieved all tasks information through API".formatted(userId));

        return ResponseEntity.ok(tasksArrBuilder.build().toString());
    }

    @GetMapping(path="/tasks/current", produces="application/json") 
    public ResponseEntity<String> getAllCurrentTasks(@AuthenticationPrincipal AuthUserDetails authUser)
    {   
        String userId = authUser.getId();

        List<Task> currentTasks = taskSvc.getAllSortedCurrentTasks(userId);

        JsonArrayBuilder tasksArrBuilder = Json.createArrayBuilder();
        
        for (Task task : currentTasks) {
            tasksArrBuilder.add(task.taskToJsonObj());
        }

        logger.info("User: %s retrieved all current tasks information through API".formatted(userId));

        return ResponseEntity.ok(tasksArrBuilder.build().toString());
    }

    @GetMapping(path="/tasks/completed", produces="application/json") 
    public ResponseEntity<String> getAllCompletedTasks(@AuthenticationPrincipal AuthUserDetails authUser)
    {   
        String userId = authUser.getId();

        List<Task> completedTasks = taskSvc.getAllSortedCompletedTasks(userId);

        JsonArrayBuilder tasksArrBuilder = Json.createArrayBuilder();
        
        for (Task task : completedTasks) {
            tasksArrBuilder.add(task.taskToJsonObj());
        }

        logger.info("User: %s retrieved all completed tasks information through API".formatted(userId));

        return ResponseEntity.ok(tasksArrBuilder.build().toString());
    }
}