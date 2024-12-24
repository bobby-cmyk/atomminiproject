package vttp.miniproject.atomnotes.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotEmpty;

public class Task {

    private String id;

    private long createdTime;

    private long lastUpdatedTime;

    private String imageUrl;

    @NotEmpty(message = "Task cannot be empty")
    private String content;

    private List<String> subtasks = new ArrayList<>();

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public long getCreatedTime() {return createdTime;}
    public void setCreatedTime(long createdTime) {this.createdTime = createdTime;}

    public long getLastUpdatedTime() {return lastUpdatedTime;}
    public void setLastUpdatedTime(long lastUpdatedTime) {this.lastUpdatedTime = lastUpdatedTime;}

    public String getImageUrl() {return imageUrl;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public List<String> getSubtasks() {return subtasks;}
    public void setSubtasks(List<String> subtasks) {this.subtasks = subtasks;}

    public void addSubtask(String subtask) {subtasks.add(subtask);}
    
    public static Task mapToTask(Map<String, String> taskMap) {

        Task task = new Task();

        task.setId(taskMap.get("id"));
        task.setContent(taskMap.get("content"));
        task.setCreatedTime(Long.parseLong(taskMap.get("createdTime")));
        task.setLastUpdatedTime(Long.parseLong(taskMap.get("lastUpdatedTime")));
        task.setImageUrl(taskMap.get("imageUrl"));
        
        String subtasksString = taskMap.get("subtasks");
        
        List<String> subtasks = subtasksStringToList(subtasksString);

        task.setSubtasks(subtasks);

        return task;
    }

    public String getSubtasksString() {

        String subtasksString = "";

        if (subtasks.isEmpty()) {
            return subtasksString;
        }

        for (String subtask : subtasks) {
            subtasksString += subtask + "|";
        }

        subtasksString = subtasksString.substring(0, subtasksString.length() - 1);

        return subtasksString;
    }

    public static List<String> subtasksStringToList(String subtasksString) {
        
        List<String> subtasks = new ArrayList<>();

        // if no subtasks submitted
        if (subtasksString.isEmpty()) {
            return subtasks;
        }
        
        String[] subtasksParts = subtasksString.split("\\|");

        for (int i = 0; i < subtasksParts.length; i++) {
            subtasks.add(subtasksParts[i]);
        }

        return subtasks;
    }
}



    

    /* 

    public JsonObject getTaskJsonObject() {
        
        JsonObject object = Json.createObjectBuilder()
            .add("createdTime", getCreatedTime())
            .add("content", getContent())
            .build();

        return object;
    }
        
    public String getAddedDateTime() {
        
        Instant instant = Instant.ofEpochMilli(addedEpochTime);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        // Define the formatter to match "Mon, 2:30pm, 26 Nov 2024"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, h:mma, d MMM yyyy", java.util.Locale.ENGLISH);
        
        return localDateTime.format(formatter);
    }
    */