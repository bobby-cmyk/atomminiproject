package vttp.miniproject.atomnotes.models;

public class User {

    private String id;
    private String username;
    private String password;
    private String email;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}


    /*
    
     public String getTaskJsonString() {
        JsonArrayBuilder tasksBuilder = Json.createArrayBuilder();

        for (Task task : tasks) {
            tasksBuilder.add(task.toJsonObj());
        }

        return tasksBuilder.build().toString();
    }

    public String userToJson() {

        JsonArrayBuilder tasksBuilder = Json.createArrayBuilder();

        for (Task task : tasks) {
            tasksBuilder.add(task.taskToJsonObj());
        }

        JsonObject userObj = Json.createObjectBuilder()
            .add("id", this.id)
            .add("username", this.username)
            .add("password", this.password)
            .add("email", this.email)
            .add("tasks", tasksBuilder)
            .build();

        return userObj.toString();
    }

    public static User JsonToUser(String JsonString) {

        User user = new User();

        try {
            JsonReader reader = Json.createReader(new StringReader(JsonString));

            JsonObject userObj = reader.readObject();

            user.setId(userObj.getString("id"));
            user.setEmail(userObj.getString("email"));
            user.setPassword(userObj.getString("password"));
            user.setUsername(userObj.getString("username"));

            JsonArray tasksArray = userObj.getJsonArray("tasks");
            
            for (int i = 0; i < tasksArray.size(); i++) {
                JsonObject taskObj = tasksArray.getJsonObject(i);
                Task task = Task.jsonObjToTask(taskObj);
                user.addTask(task);
            }

            return user;
        }
        
        catch (Exception e) {
            logger.info("Error parsing JsonString to User. Error message: %s".formatted(e.getMessage()));
        }
        return user;
    }

    */