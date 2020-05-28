package utils.bottify;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tasks.Task;
import tasks.TaskFactory;
import tasks.TaskName;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigManager {

    private static String BotName;
    public static String token;

    private static final String BASE_URL = "http://bottify.io/api/bots/";
    private static final String dataDirectory = Paths.get(System.getProperty("user.home"), "OSBot", "Data").toString();


    public static String checkIn(String BotName) throws IOException {
        ConfigManager.BotName = BotName;
        URL url = new URL(BASE_URL + "check-in?BotName=" + ConfigManager.BotName);
        URLConnection con = url.openConnection();
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        JSONObject response;
        try {
            response = (JSONObject) jsonParser.parse(bufferedReader);
            token = (String) response.get("access_token");
            System.out.println("Token" + token);
        } catch (ParseException e) {
            System.out.println("Error: " + e.toString());
        }
        return token;

    }


    public static Task getTaskFromServer() throws IOException {
        Task task = null;
        JSONObject taskJson = null;
        URL url = new URL(BASE_URL);
        URLConnection con = url.openConnection();
        String authHeaderString = "bearer " + token;
        con.setRequestProperty("Authorization", authHeaderString);
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        try {
            taskJson = (JSONObject) jsonParser.parse(bufferedReader);
            TaskName taskName = TaskName.getByName((String) taskJson.get("task_name"));
            task = TaskFactory.createTask(taskName, taskJson);

        } catch (ParseException e) {
            System.out.println("Failed to parse task");
        }
        return task;
    }


    public static void taskComplete() throws IOException {

        URL url = new URL(BASE_URL + "/done");
        URLConnection con = url.openConnection();
        String authHeaderString = "bearer " + token;
        con.setRequestProperty("Authorization", authHeaderString);

    }


}

















