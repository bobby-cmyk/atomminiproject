package vttp.miniproject.atomnotes.models;

import java.util.HashMap;
import java.util.Map;

public class UserStats {

    private int currentTasks;
    private int completedTasksToday;
    private int completedTasks;
    private float tasksCreatedPerDay;
    private float tasksCompletedPerDay;
    private String userJoinedDate;
    private String todayOverview;
    private String previousWeekOverview;

    public int getCurrentTasks() {return currentTasks;}
    public void setCurrentTasks(int currentTasks) {this.currentTasks = currentTasks;}
    
    public int getCompletedTasksToday() {return completedTasksToday;}
    public void setCompletedTasksToday(int completedTasksToday) {this.completedTasksToday = completedTasksToday;}
    
    public int getCompletedTasks() {return completedTasks;}
    public void setCompletedTasks(int completedTasks) {this.completedTasks = completedTasks;}
    
    public float getTasksCreatedPerDay() {return tasksCreatedPerDay;}
    public void setTasksCreatedPerDay(float tasksCreatedPerDay) {this.tasksCreatedPerDay = tasksCreatedPerDay;}
    
    public float getTasksCompletedPerDay() {return tasksCompletedPerDay;}
    public void setTasksCompletedPerDay(float tasksCompletedPerDay) {this.tasksCompletedPerDay = tasksCompletedPerDay;}

    public String getUserJoinedDate() {return userJoinedDate;}
    public void setUserJoinedDate(String userJoinedDate) {this.userJoinedDate = userJoinedDate;}

    public String getTodayOverview() {return todayOverview;}
    public void setTodayOverview(String todayOverview) {this.todayOverview = todayOverview;}

    public String getPreviousWeekOverview() {return previousWeekOverview;}
    public void setPreviousWeekOverview(String previousWeekOverview) {this.previousWeekOverview = previousWeekOverview;}

    public static UserStats mapToUserStats(Map<String, String> userStatsMap) {

        UserStats stats = new UserStats();

        stats.setCurrentTasks(Integer.valueOf(userStatsMap.get("currentTasks")));
        stats.setCompletedTasksToday(Integer.valueOf(userStatsMap.get("completedTasksToday")));
        stats.setCompletedTasks(Integer.valueOf(userStatsMap.get("completedTasks")));
        stats.setTasksCreatedPerDay(Float.valueOf(userStatsMap.get("tasksCreatedPerDay")));
        stats.setTasksCompletedPerDay(Float.valueOf(userStatsMap.get("tasksCompletedPerDay")));
        stats.setUserJoinedDate(userStatsMap.get("userJoinedDate"));
        stats.setTodayOverview(userStatsMap.get("todayOverview"));
        stats.setPreviousWeekOverview(userStatsMap.get("previousWeekOverview"));

        return stats;
    }

    public Map<String, String> userStatsToMap() {

        Map<String, String> values = new HashMap<>();

        values.put("currentTasks", Integer.toString(currentTasks));
        values.put("completedTasksToday", Integer.toString(completedTasksToday));
        values.put("completedTasks", Integer.toString(completedTasks));
        values.put("tasksCreatedPerDay", Float.toString(tasksCreatedPerDay));
        values.put("tasksCompletedPerDay", Float.toString(tasksCompletedPerDay));
        values.put("userJoinedDate", userJoinedDate);
        values.put("todayOverview", todayOverview);
        values.put("previousWeekOverview", previousWeekOverview);

        return values;
    }
}