package vttp.miniproject.atomnotes.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.miniproject.atomnotes.models.Task;

@Service
public class GenService {
    
    @Value("${OPENAI.API.KEY}")
    private String OPENAI_API_KEY;

    @Value("${UNSPLASH.API.KEY}")
    private String UNSPLASH_API_KEY;

    private final Logger logger = Logger.getLogger(GenService.class.getName());

    public String retrieveImageUrl(String content) {

        String imageUrl = "";
        
        String base_url = "https://api.unsplash.com/search/photos";

        // 2. Instead lets build url
        // Build the URL with the query parameters
        String url = UriComponentsBuilder
            .fromUriString(base_url)
            .queryParam("query", generateMainTopic(content))
            .queryParam("page", 1)
            .queryParam("per_page", 10)
            //.queryParam("color", "black_and_white")
            .toUriString();

        // 3. Request Entity
        RequestEntity<Void> req = RequestEntity
            .get(url)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Client-ID " + UNSPLASH_API_KEY)
            .build();
        
        RestTemplate template = new RestTemplate();

        try {
            ResponseEntity<String> resp = template.exchange(req, String.class);

            String payload = resp.getBody();

            JsonReader reader = Json.createReader(new StringReader(payload));

            JsonObject payloadObj = reader.readObject();

            JsonArray resultsArr = payloadObj.getJsonArray("results");

            int bound = 10;

            int arrSize = resultsArr.size();

            if (arrSize < bound) {
                bound = arrSize;
            }

            Random rand = new Random();

            int imageIndex = rand.nextInt(bound);

            JsonObject resultObj = resultsArr.getJsonObject(imageIndex);

            JsonObject urlsObj = resultObj.getJsonObject("urls");

            imageUrl = urlsObj.getString("regular");

            if (imageUrl.isEmpty()) {
                imageUrl = "https://images.unsplash.com/photo-1672237020985-2e38261617f9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2ODQxMjd8MHwxfHNlYXJjaHwzfHxzdW5zZXQlMjBtb3VudGFpbnxlbnwwfHx8fDE3MzUwMjQxMjB8MA&ixlib=rb-4.0.3&q=80&w=1080";
            }

            return imageUrl;
        }

        catch (Exception e) {
            logger.info("Error: %s".formatted(e.getMessage()));

            imageUrl = "https://images.unsplash.com/photo-1672237020985-2e38261617f9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2ODQxMjd8MHwxfHNlYXJjaHwzfHxzdW5zZXQlMjBtb3VudGFpbnxlbnwwfHx8fDE3MzUwMjQxMjB8MA&ixlib=rb-4.0.3&q=80&w=1080";
            
            return imageUrl;
        }
    }

    private String generateMainTopic(String content) {

        String mainTopic = "";

        // 1. URL 
        String url = "https://api.openai.com/v1/chat/completions";
        
        // 2. Json Payload
        String model = "gpt-4o-mini";
        String systemPrompt = "Extract the main object or event in one word or a phrase";
        String userPrompt = content;

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

            mainTopic = messageObj.getString("content");

            return mainTopic;
        }

        catch (Exception e) {

            mainTopic = "sunset mountain";

            logger.info("Error occured: %s.".formatted(e.getMessage()));
            return mainTopic;
        }
    }


    public List<String> generateSubtasks(String content) {
        
        List<String> subtasks = new ArrayList<>();

        // 1. URL 
        String url = "https://api.openai.com/v1/chat/completions";
        
        // 2. Json Payload
        String model = "gpt-4o-mini";
        String systemPrompt = "Break down task into 3 smaller subtasks. Keep concise. Output format: 'subtask1|subtask2|subtask3'.";
        String userPrompt = content;

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

            subtasks = Task.subtasksStringToList(subtasksString);

            return subtasks;
        }

        catch (Exception e) {
            subtasks.add("Subtask 1");
            subtasks.add("Subtask 2");
            subtasks.add("Subtask 3");

            logger.info("Error occured: %s.".formatted(e.getMessage()));
            return subtasks;
        }
    }
}


    /*
     * 
    
    public String getSchedule(List<Task> tasks) {

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

        for (Task task : tasks) {
            JsonObject taskObj = task.getTaskJsonObject();
            arrBuilder.add(taskObj);
        }

        JsonObject tasksObj = Json.createObjectBuilder()
            .add("tasks", arrBuilder)
            .build();


        // Initialise schedule variable
        String schedule = "";

        // 1. URL   
        String url = "https://api.openai.com/v1/chat/completions";
        
        // 2. Json Payload
        String model = "gpt-4o-2024-08-06";
        String systemPrompt = "Analyse the tasks' content and added datetime to determine their datetime. Output in the given schedule structure sorted by datetime";
        String userPrompt = tasksObj.toString();

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
            .add("response_format", Json.createObjectBuilder()
                .add("type", "json_schema")
                .add("json_schema", Json.createObjectBuilder()
                    .add("name", "schedule")
                    .add("schema", Json.createObjectBuilder()
                        .add("type", "object")
                        .add("properties", Json.createObjectBuilder()
                            .add("tasks", Json.createObjectBuilder()
                                .add("type", "array")
                                .add("items", Json.createObjectBuilder()
                                    .add("type", "object")
                                    .add("properties", Json.createObjectBuilder()
                                        .add("datetime", Json.createObjectBuilder()
                                            .add("type", "string"))
                                        .add("task_content", Json.createObjectBuilder()
                                            .add("type", "string")))
                                    .add("required", Json.createArrayBuilder()
                                        .add("datetime")
                                        .add("task_content"))
                                    .add("additionalProperties", false))))
                        .add("required", Json.createArrayBuilder()
                            .add("tasks"))
                        .add("additionalProperties", false))
                    .add("strict", true))) // Strict parameter added
            .build();
        

        // 3. Request Entity
        RequestEntity<String> req = RequestEntity
            .post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + OPENAI_API_KEY)
            .body(reqBody.toString(), String.class);

        logger.info("Request Body: \n%s".formatted(req.toString()));
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

            String content = messageObj.getString("content");

            schedule = content;

            return schedule;
        }

        catch (Exception e) {
            //TODO if exception try again

            logger.info("Error occured: %s.".formatted(e.getMessage()));
            return schedule;
        }


        
    } */
