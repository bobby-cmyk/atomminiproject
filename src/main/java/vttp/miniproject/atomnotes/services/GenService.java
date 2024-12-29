package vttp.miniproject.atomnotes.services;

import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.repositories.CacheRepo;

@Service
public class GenService {

    @Autowired
    private CacheRepo cacheRepo;
    
    @Value("${OPENAI.API.KEY}")
    private String OPENAI_API_KEY;

    @Value("${UNSPLASH.API.KEY}")
    private String UNSPLASH_API_KEY;

    private final String GPT4OMINI = "gpt-4o-mini";
    private final String GPT4O = "gpt-4o";

    private final Logger logger = Logger.getLogger(GenService.class.getName());

    public List<String> generateSubtasks(String content) {

        String systemPrompt = "Break down task into 3 smaller subtasks. Keep concise. Output format: 'subtask1|subtask2|subtask3'.";

        try {
            // Convert string generated to subtasks
            return Task.subtasksStringToList(promptGPT(systemPrompt, content, GPT4OMINI));
        }

        catch(Exception e) {
            logger.warning("Error: generateSubtasks(). Message: %s".formatted(e.getMessage()));
            
            // As default placeholder if error
            List<String> subtasks = new ArrayList<>();

            subtasks.add("Subtask 1");
            subtasks.add("Subtask 2");
            subtasks.add("Subtask 3");

            return subtasks;
        }
    }

    public String getTodayOverview(List<Task> currentTasks, List<Task> completedTasksToday) {
          
        // If there are no tasks, return default message
        if (currentTasks.isEmpty() && completedTasksToday.isEmpty()) {
            return "Hmm... its looking empty today.";
        }

        String systemPrompt = "Provide a summary of user's todo list for the day. Motivating tone. Keep it concise and precise, 50 words. Avoid formatting.";

        String userPrompt = todayOverviewPromptBuilder(currentTasks, completedTasksToday);

        try {
            return promptGPT(systemPrompt, userPrompt, GPT4O);
        }

        catch(Exception e) {
            logger.warning("Error: getTodayOverview(). Message: %s".formatted(e.getMessage()));
            return "Sorry we are having some troubles. Check again soon";
        }
    }

    public String getPreviousWeekOverview(List<Task> completedTasksPreviousWeek) {
        
        // If there are no tasks, return default message
        if (completedTasksPreviousWeek.isEmpty()) {
            return "Hmm... there's no tasks completed.";
        }

        String systemPrompt = "Provide a summary of user's completed tasks for the past few days. Motivating tone. Keep it concise and precise, 50 words. Avoid formatting.";

        String userPrompt = previousWeekOverviewPromptBuilder(completedTasksPreviousWeek);

        try {
            return promptGPT(systemPrompt, userPrompt, GPT4O);
        }

        catch(Exception e) {
            logger.warning("Error: getPreviousWeekOverview(). Message: %s".formatted(e.getMessage()));
            return "Sorry we are having some troubles. Check again soon";
        }
    }

    public String retrieveImageUrl(String content) {

        String imageUrl = "";
        
        String base_url = "https://api.unsplash.com/search/photos";

        String url = UriComponentsBuilder
            .fromUriString(base_url)
            .queryParam("query", generateMainTopic(content))
            .queryParam("page", 1)
            .queryParam("per_page", 10)
            .toUriString();

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

            // Initialise a bound for generating random number
            int bound = 10;

            int arrSize = resultsArr.size();

            // bound = arrSize if arrSize is smaller than 10
            if (arrSize < bound) {
                bound = arrSize;
            }

            Random rand = new Random();

            // Get a random index
            int imageIndex = rand.nextInt(bound);

            JsonObject resultObj = resultsArr.getJsonObject(imageIndex);

            JsonObject urlsObj = resultObj.getJsonObject("urls");

            imageUrl = urlsObj.getString("regular");

            // If results is empty
            if (imageUrl.isEmpty()) {
                imageUrl = "https://images.unsplash.com/photo-1672237020985-2e38261617f9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2ODQxMjd8MHwxfHNlYXJjaHwzfHxzdW5zZXQlMjBtb3VudGFpbnxlbnwwfHx8fDE3MzUwMjQxMjB8MA&ixlib=rb-4.0.3&q=80&w=1080";
            }

            return imageUrl;
        }

        catch (Exception e) {
            logger.warning("Error: getPreviousWeekOverview(). Message: %s".formatted(e.getMessage()));

            // Default photo if error occurs
            imageUrl = "https://images.unsplash.com/photo-1672237020985-2e38261617f9?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3w2ODQxMjd8MHwxfHNlYXJjaHwzfHxzdW5zZXQlMjBtb3VudGFpbnxlbnwwfHx8fDE3MzUwMjQxMjB8MA&ixlib=rb-4.0.3&q=80&w=1080";
            
            return imageUrl;
        }
    }

    private String generateMainTopic(String content) {
        
        String systemPrompt = "Extract the main object or event in one word or a phrase";

        try {
            return promptGPT(systemPrompt, content, GPT4OMINI);
        }

        catch(Exception e) {
            logger.warning("Error: generateMainTopic(). Message: %s".formatted(e.getMessage()));
            // As default placeholder if error
            return "sunset mountain";
        }
    }

    public String getQuote() {

        String quote = "";

        // If quote is cached, return from cache
        if (cacheRepo.checkQuoteCached()) {

            quote = cacheRepo.retrieveCachedQuote();

            return quote;
        }

        else {
            String url = "https://api.adviceslip.com/advice";

            RequestEntity<Void> req = RequestEntity
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .build();

            RestTemplate template = new RestTemplate();

            try {
                ResponseEntity<String> resp = template.exchange(req, String.class);
    
                String payload = resp.getBody();
    
                JsonReader reader = Json.createReader(new StringReader(payload));
    
                JsonObject result = reader.readObject();

                JsonObject slip = result.getJsonObject("slip");
    
                quote = slip.getString("advice");
    
                cacheRepo.cacheQuote(quote);
    
                return quote;
            }
    
            catch (Exception e) {
                
                logger.warning("Error: getQuote(). Message: %s.".formatted(e.getMessage()));
    
                quote = "If you want to be happily married, marry a happy person.";
    
                return quote;
            }
        }
    }

    private String promptGPT(String systemPrompt, String userPrompt, String model) throws Exception{

        String gptReponse = "";
        
        String url = "https://api.openai.com/v1/chat/completions";

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

        RequestEntity<String> req = RequestEntity
            .post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + OPENAI_API_KEY)
            .body(reqBody.toString(), String.class);

        RestTemplate template = new RestTemplate();

        ResponseEntity<String> resp = template.exchange(req, String.class);

        String payload = resp.getBody();

        JsonReader reader = Json.createReader(new StringReader(payload));

        JsonObject result = reader.readObject();

        JsonArray choicesArr = result.getJsonArray("choices");

        JsonObject firstChoiceObj = choicesArr.getJsonObject(0);

        JsonObject messageObj = firstChoiceObj.getJsonObject("message");

        gptReponse = messageObj.getString("content");

        return gptReponse;
    }

    private String todayOverviewPromptBuilder(List<Task> currentTasks, List<Task> completedTasks) {

        // {"Completed": ["task1", "task2"...], "Current": ["task3", "task4"...]}

        JsonArrayBuilder currentTasksArrayBuilder = Json.createArrayBuilder();

        for (Task currentTask : currentTasks) {
            currentTasksArrayBuilder.add(currentTask.getContent());
        }

        JsonArrayBuilder completedTasksArrayBuilder = Json.createArrayBuilder();

        for (Task completedTask : completedTasks) {
            completedTasksArrayBuilder.add(completedTask.getContent());
        }

        JsonObject promptJsonObj = Json.createObjectBuilder()
            .add("Completed", completedTasksArrayBuilder)
            .add("Current", currentTasksArrayBuilder) 
            .build();

        return promptJsonObj.toString();
    }

    private String previousWeekOverviewPromptBuilder(List<Task> completedTasks) {

        // [{"task": "task1", "date": "Wed, 24 Dec 2024"},{"task": "task2", "date": "Tue, 23 Dec 2024"}]

        JsonObjectBuilder completedTaskObjectBuilder = Json.createObjectBuilder();

        JsonArrayBuilder completedTasksArrayBuilder = Json.createArrayBuilder();

        for (Task completedTask : completedTasks) {
            completedTaskObjectBuilder
                .add("task", completedTask.getContent())
                .add("date", formatCompletedDate(completedTask.getCompletedTime()));

            completedTasksArrayBuilder.add(completedTaskObjectBuilder);
        }

        JsonArray promptJsonArr = completedTasksArrayBuilder.build();

        return promptJsonArr.toString();
    }

    private String formatCompletedDate(long completedDate) {
        // epochmilli -> "Wed, 24 Dec 2024"
        String formattedDate = Instant.ofEpochMilli(completedDate)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy"));

        return formattedDate;
    }
}
   