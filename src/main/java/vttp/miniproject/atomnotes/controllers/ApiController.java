package vttp.miniproject.atomnotes.controllers;

import java.util.List;

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

    @GetMapping(path="/alltasks", produces="application/json") 
    public ResponseEntity<String> getAllTasks(@AuthenticationPrincipal AuthUserDetails authUser)
    {   
        String userId = authUser.getId();

        List<Task> allTasks = taskSvc.getAllSortedCurrentTasks(userId);
        allTasks.addAll(taskSvc.getAllSortedCompletedTasks(userId));

        JsonArrayBuilder tasksArrBuilder = Json.createArrayBuilder();
        
        for (Task task : allTasks) {
            tasksArrBuilder.add(task.taskToJsonObj());
        }
        return ResponseEntity.ok(tasksArrBuilder.build().toString());
    }
}