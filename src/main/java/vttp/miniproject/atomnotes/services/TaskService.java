package vttp.miniproject.atomnotes.services;

import java.io.StringReader;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.repositories.ConnectorRepo;
import vttp.miniproject.atomnotes.repositories.TaskRepo;

@Service
public class TaskService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private ConnectorRepo connectorRepo;

    @Autowired
    private HelperService helperSvc;

    @Value("${OPENAI.API.KEY}")
    private String OPENAI_API_KEY;

    private final Logger logger = Logger.getLogger(TaskService.class.getName());

    public List<String> generateSubtasks(String content) {
        
        List<String> subtasks = new ArrayList<>();

        // 1. URL 
        String url = "https://api.openai.com/v1/chat/completions";
        
        // 2. Json Payload
        String model = "gpt-4o-mini";
        String systemPrompt = "Break down task into 3 smaller subtasks. Keep concise. Output format: 'subtask1|subtask2|subtask3'";
        String userPrompt = "{Task: " + content + ", Current DateTime: " + helperSvc.getCurrentDateTime() + "}";

        logger.info("User prompt: %s".formatted(userPrompt));

        JsonObject reqBody = Json.createObjectBuilder()
            .add("model", model)
            .add("messages", Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("role", "system")
                    .add("content", systemPrompt))
                .add(Json.createObjectBuilder()
                    .add("role", "user")
                    .add("content", userPrompt)))
            .build();
        logger.info("Request Body: \n%s".formatted(reqBody.toString()));

        // 3. Request Entity
        RequestEntity<String> req = RequestEntity
            .post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + OPENAI_API_KEY)
            .body(reqBody.toString(), String.class);

        // ... Use RestTemplate to exchange for response

        RestTemplate template = new RestTemplate();

        try {
            ResponseEntity<String> resp = template.exchange(req, String.class);

            String payload = resp.getBody();

            JsonReader reader = Json.createReader(new StringReader(payload));

            JsonObject result = reader.readObject();

            JsonArray choicesArr = result.getJsonArray("choices");

            JsonObject firstChoiceObj = choicesArr.getJsonObject(0);

            JsonObject messageObj = firstChoiceObj.getJsonObject("message");

            String subtasksString = messageObj.getString("content");

            logger.info("Generated subtasks: %s".formatted(subtasksString));

            subtasks = Task.subtasksStringToList(subtasksString);

            return subtasks;
        }

        catch (Exception e) {
            //TODO if exception try again

            logger.info("Error occured: %s.".formatted(e.getMessage()));
            return subtasks;
        }
    }

    public void addTask(String username, Task task) {
        String taskId = UUID.randomUUID().toString();
        long addedEpochTime = Instant.now().toEpochMilli();

        task.setId(taskId);
        task.setAddedEpochTime(addedEpochTime);

        taskRepo.addTask(task);
        connectorRepo.addTaskId(username, taskId);
    }

    public Task getTask(String taskId) {

        Task task = taskRepo.getTask(taskId);

        return task;
    }

    public List<Task> getAllSortedTasks(String user) {

        Set<String> taskIds = connectorRepo.getUserTaskIds(user);

        List<Task> tasks = new ArrayList<>(taskRepo.getTasks(taskIds));

        // Sort reverse, descending from latest to oldest
        List<Task> sortedTasks = tasks.stream()
            .sorted((t1, t2) -> Long.compare(t2.getAddedEpochTime(), t1.getAddedEpochTime()))
            .collect(Collectors.toList());
            
        return sortedTasks;
    }

    public void deleteTask(String username, String taskId) {
        taskRepo.deleteTask(taskId);
        connectorRepo.deleteTaskId(username, taskId);
    }

    public void updateTask(Task task) {

        // Update epoch time
        long addedEpochTime = Instant.now().toEpochMilli();
        task.setAddedEpochTime(addedEpochTime);
        
        taskRepo.updateTask(task);
    }
}
