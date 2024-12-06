package vttp.miniproject.atomnotes.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotEmpty;

public class Task {

    private String id;

    private long addedEpochTime;

    @NotEmpty(message = "Task cannot be empty")
    private String content;

    private List<String> subtasks = new ArrayList<>();

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public long getAddedEpochTime() {
        return addedEpochTime;
    }

    public void setAddedEpochTime(long addedEpochTime) {
        this.addedEpochTime = addedEpochTime;
    }

    public LocalDateTime getAddedDateTime() {
        
        Instant instant = Instant.ofEpochMilli(addedEpochTime);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        return localDateTime;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getSubtasks() {
        return subtasks;
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

    public void setSubtasks(List<String> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(String subtask) {
        subtasks.add(subtask);
    }
    
    public static Task mapToTask(Map<String, String> taskMap) {

        Task task = new Task();

        task.setId(taskMap.get("id"));
        task.setContent(taskMap.get("content"));
        task.setAddedEpochTime(Long.parseLong(taskMap.get("addedEpochTime")));
        
        String subtasksString = taskMap.get("subtasks");
        
        List<String> subtasks = subtasksStringToList(subtasksString);

        task.setSubtasks(subtasks);

        return task;
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

    @Override
    public String toString() {
        return "Task [id=" + id + ", addedEpochTime=" + addedEpochTime + ", content=" + content + ", subtasks="
                + subtasks + "]";
    }
}