package vttp.miniproject.atomnotes.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vttp.miniproject.atomnotes.models.Task;
import vttp.miniproject.atomnotes.models.UserStats;
import vttp.miniproject.atomnotes.repositories.CacheRepo;
import vttp.miniproject.atomnotes.repositories.TaskRepo;
import vttp.miniproject.atomnotes.repositories.UserRepo;

@Service
public class StatsService {

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CacheRepo cacheRepo;

    @Autowired
    private GenService genSvc;

    public UserStats getUserStats(String userId) {

        if (cacheRepo.checkStatsCached(userId)) {

            UserStats userStats = cacheRepo.retrieveCacheStats(userId);

            // Check number of completed or current tasks remained same, retrieve stats from cache
            if (userStats.getCompletedTasks() == numberOfCompletedTasks(userId) && userStats.getCurrentTasks() == numberOfCurrentTasks(userId)) {
                return userStats;
            }
        }
        // if changed, then retrieve stats from redis and promptGPT
        UserStats userStats = crunchStats(userId);

        // Once crunched, put it in cache
        cacheRepo.cacheStats(userStats, userId);

        return userStats;
    }

    private UserStats crunchStats(String userId) {
        // Get user's joined time
        long userJoinedEpoch = userRepo.getUserEntity(userId).getCreatedTime();

        // Get list of current tasks and today's completed tasks 
        List<Task> currentTasks = taskRepo.getAllCurrentTasks(userId);
        List<Task> completedTasks = taskRepo.getAllCompletedTasks(userId);
        List<Task> completedTasksToday = completedTasksToday(completedTasks);
        List<Task> completedTasksPreviousWeek = completedTasksPreviousWeek(completedTasks);

        // Get the size of each list
        int numberCurrentTasks = currentTasks.size();
        int numberCompletedTasks = taskRepo.numberOfCompletedTasks(userId);
        int numberCompletedTasksToday = completedTasksToday.size();

        // Get number of days from today since user joined
        int daysActive = userDaysActive(userJoinedEpoch);

        // Get number of tasks created or completed per day since user joined (no. of tasks / daysActive)
        float tasksCreatedPerDay = tasksPerDay((numberCurrentTasks + numberCompletedTasks), daysActive);
        float tasksCompletedPerDay = tasksPerDay(numberCompletedTasks, daysActive);

        // Get summary of today's current tasks and completed tasks 
        String todayOverview = genSvc.getTodayOverview(currentTasks, completedTasksToday);
        
        // Get summary of previous week completed tasks 
        String previousWeekOverview = genSvc.getPreviousWeekOverview(completedTasksPreviousWeek);

        // Format the user joined date
        String formattedUserJoinedDate = formatUserJoinedDate(userJoinedEpoch);

        UserStats userStats = new UserStats();

        userStats.setCurrentTasks(numberCurrentTasks);
        userStats.setCompletedTasksToday(numberCompletedTasksToday);
        userStats.setCompletedTasks(numberCompletedTasks);
        userStats.setTasksCreatedPerDay(tasksCreatedPerDay);
        userStats.setTasksCompletedPerDay(tasksCompletedPerDay);
        userStats.setUserJoinedDate(formattedUserJoinedDate);
        userStats.setTodayOverview(todayOverview);
        userStats.setPreviousWeekOverview(previousWeekOverview);

        return userStats;
    }

    public int numberOfCurrentTasks(String userId) {
        return taskRepo.numberOfCurrentTasks(userId);
    }

    private int numberOfCompletedTasks(String userId) {
        return taskRepo.numberOfCompletedTasks(userId);
    }

    private String formatUserJoinedDate(long userJoinedEpoch) {

        String formattedDate = Instant.ofEpochMilli(userJoinedEpoch)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd MMM yy"));

        return formattedDate;
    }

    private float tasksPerDay(int numberOfTasks, int daysActive) {

        float tasksPerDay = (float) (numberOfTasks) / daysActive;

        tasksPerDay = Math.round(tasksPerDay * 10) / 10.0f;

        return tasksPerDay;
    }

    private int userDaysActive(long userJoinedEpoch) {

        long today = Instant.now().toEpochMilli();
        int daysActive = (int) TimeUnit.MILLISECONDS.toDays(today - userJoinedEpoch);

        if (daysActive < 1) {
            daysActive = 1;
        }

        return daysActive;
    }

    private List<Task> completedTasksToday(List<Task> completedTasks) {

        List<Task> todayCompletedTasks = new ArrayList<>();

        
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate today = now.atZone(zoneId).toLocalDate();
        
        // Get epoch for start and end of day
        long startOfDay = today.atStartOfDay(zoneId).toInstant().toEpochMilli();
        long endOfDay = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli();

        for (Task task : completedTasks) {
            long completedTime = task.getCompletedTime();
            // if task is completed today (startOfDay <= task < endOfDay)
            if (completedTime >= startOfDay && completedTime < endOfDay) {
                todayCompletedTasks.add(task);
            }
        }
        return todayCompletedTasks;
    }

    private List<Task> completedTasksPreviousWeek(List<Task> completedTasks) {

        List<Task> previousWeekCompletedTasks = new ArrayList<>();

        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate today = now.atZone(zoneId).toLocalDate();

        // Get epoch for start of today and start of week
        long startOfWeek = today.minusDays(7).atStartOfDay(zoneId).toInstant().toEpochMilli();
        long startOfDay = today.atStartOfDay(zoneId).toInstant().toEpochMilli();
    
        for (Task task : completedTasks) {
            long completedTime = task.getCompletedTime();
            // if task is completed within past 7 days (excluding today)
            if (completedTime >= startOfWeek && completedTime < startOfDay) {
                previousWeekCompletedTasks.add(task);
            }
        }
        return previousWeekCompletedTasks;
    }

}   
