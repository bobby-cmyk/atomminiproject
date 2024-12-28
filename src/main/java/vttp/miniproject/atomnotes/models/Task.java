package vttp.miniproject.atomnotes.models;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.validation.constraints.NotBlank;

public class Task {

    private String id;
    private long createdTime;
    private long lastUpdatedTime;
    private Long completedTime;
    private List<String> subtasks = new ArrayList<>();
    private String imageUrl;
    private boolean priority = false;

    @NotBlank(message = "Task cannot be empty")
    private String content;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}

    public long getCreatedTime() {return createdTime;}
    public void setCreatedTime(long createdTime) {this.createdTime = createdTime;}

    public long getLastUpdatedTime() {return lastUpdatedTime;}
    public void setLastUpdatedTime(long lastUpdatedTime) {this.lastUpdatedTime = lastUpdatedTime;}

    public Long getCompletedTime() {return completedTime;}
    public void setCompletedTime(long completedTime) {this.completedTime = completedTime;}

    public String getImageUrl() {return imageUrl;}
    public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public List<String> getSubtasks() {return subtasks;}
    public void setSubtasks(List<String> subtasks) {this.subtasks = subtasks;}

    public boolean isPriority() {return priority;}
    public void setPriority(boolean priority) {this.priority = priority;};

    public void addSubtask(String subtask) {subtasks.add(subtask);}
    
    public static Task mapToTask(Map<String, String> taskMap) {

        Task task = new Task();

        task.setId(taskMap.get("id"));
        task.setCreatedTime(Long.parseLong(taskMap.get("createdTime")));
        task.setLastUpdatedTime(Long.parseLong(taskMap.get("lastUpdatedTime")));
        task.setContent(taskMap.get("content"));
        task.setImageUrl(taskMap.get("imageUrl"));
        task.setPriority(Boolean.parseBoolean(taskMap.get("priority")));

        String completedTimeString = taskMap.get("completedTime");

        if(completedTimeString != null) {
            task.setCompletedTime(Long.parseLong(completedTimeString));
        }
        
        String subtasksString = taskMap.get("subtasks");
        
        List<String> subtasks = subtasksStringToList(subtasksString);

        task.setSubtasks(subtasks);

        return task;
    }

    public Map<String, String> taskToMap() {

        Map<String, String> values = new HashMap<>();

        values.put("id", this.id);
        values.put("createdTime", Long.toString(this.createdTime));
        values.put("lastUpdatedTime", Long.toString(this.lastUpdatedTime));
        values.put("content", this.content);
        values.put("subtasks", getSubtasksString());
        values.put("imageUrl", this.imageUrl);
        values.put("priority", Boolean.toString(this.priority));

        if (this.completedTime != null) {
            values.put("completedTime", Long.toString(this.completedTime));
        }
        
        return values;
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

        if (subtasksParts.length != 3) {
            subtasks.add("Subtask 1");
            subtasks.add("Subtask 2");
            subtasks.add("Subtask 3");
            return subtasks;
        } 

        for (int i = 0; i < subtasksParts.length; i++) {
            subtasks.add(subtasksParts[i]);
        }

        return subtasks;
    }

    public static byte[] generateCsv(List<Task> allTasks) {
        
        String CSV_HEADER = "id,createdTime,lastUpdatedTime,completedTime,content,subtasks,imageUrl,priority";
    
        StringBuilder csvContent = new StringBuilder();

        csvContent.append(CSV_HEADER).append("\n");

        for (Task task : allTasks) {
            csvContent.append(task.getId()).append(",")
                      .append(task.getCreatedTime()).append(",")
                      .append(task.getLastUpdatedTime()).append(",")
                      .append(task.getCompletedTimeObj()).append(",")
                      .append(task.getContent()).append(",")
                      .append(task.getSubtasksString()).append(",")
                      .append(task.getImageUrl()).append(",")
                      .append(task.isPriority()).append("\n");
        }   

        return csvContent.toString().getBytes(StandardCharsets.UTF_8);
    }

    public JsonObject taskToJsonObj() {

        JsonObjectBuilder taskObjBuilder = Json.createObjectBuilder();
        
        taskObjBuilder
            .add("id", id)
            .add("createdTime", createdTime)
            .add("lastUpdatedTime", lastUpdatedTime)
            .add("completedTime", getCompletedTimeObj())
            .add("content", content)
            .add("subtasks", subTasksToJsonArr())
            .add("imageUrl", imageUrl)
            .add("priority", priority);

        return taskObjBuilder.build();
    }

    private JsonArray subTasksToJsonArr() {

        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();

        for (String subtask : subtasks) {
            arrBuilder.add(subtask);
        }

        return arrBuilder.build();
    }

    private JsonValue getCompletedTimeObj() {
        if (completedTime == null) {
            return Json.createValue("NA"); 
        } else {
            return Json.createValue(completedTime);
        }
    }
    
    @Override
    public String toString() {
        return "Task [id=" + id + ", createdTime=" + createdTime + ", lastUpdatedTime=" + lastUpdatedTime
                + ", completedTime=" + completedTime + ", subtasks=" + subtasks + ", imageUrl=" + imageUrl
                + ", priority=" + priority + ", content=" + content + "]";
    }
}

