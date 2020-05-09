package utils.bottify;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tasks.Task;
import tasks.TaskFactory;
import tasks.TaskName;


import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigManager {
    private static final String BotID = "Kroonjay";
    private static final String BASE_URL = "http://bottify.io:8000/";
    private static final String dataDirectory = Paths.get(System.getProperty("user.home"), "OSBot", "Data").toString();


    public static List<Task> getTasksFromServer() throws IOException {
        String endpoint = "tasks";
        List<Task> tasks = new ArrayList<>();
        JSONArray tasksJSON = new JSONArray();
        URL url = new URL(BASE_URL + endpoint);
        URLConnection con = url.openConnection();
        con.setRequestProperty("BotID", BotID);
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        try{
             tasksJSON = (JSONArray) jsonParser.parse(bufferedReader);
        }
        catch(ParseException e)  {
            System.out.println("Failed to Parse Tasks");
        }
        for (Object taskObj : tasksJSON) {
            JSONObject taskJSON = (JSONObject) taskObj;
            TaskName taskName = TaskName.getByName((String) taskJSON.get("taskName"));
            //System.out.println("TaskName is: " + TaskName.TUTORIAL_ISLAND.toString());
            //TaskName taskName = TaskName.getByName("TUTORIAL_ISLAND");
            Task task = TaskFactory.createTask(taskName);
            tasks.add(task);
        }
        return tasks;
    }


    public Optional<JSONObject> readConfig(final File file) {
        if (!file.exists()) {
            return Optional.empty();
        }
        try (FileReader fileReader = new FileReader(file)) {
            JSONObject jsonObject = (JSONObject) (new JSONParser().parse(fileReader));
            return Optional.of(jsonObject);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}

















