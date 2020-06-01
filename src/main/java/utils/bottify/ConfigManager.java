package utils.bottify;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.rs07.utility.ConditionalSleep;
import tasks.Task;
import tasks.TaskFactory;
import tasks.TaskName;
import utils.event.LoginEvent;


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
    private static String token;
    private static String rsUsername;
    private static String rsPassword;
    private static final String BASE_URL = "http://bottify.io/api/bots/";
    private static final String dataDirectory = Paths.get(System.getProperty("user.home"), "OSBot", "Data").toString();
    private LoginEvent loginEvent;



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
            rsUsername = (String) response.get("rs_username");
            rsPassword = (String) response.get("rs_password");
            token = (String) response.get("bearer_token");
        } catch (ParseException e) {
        }
        return token;

    }


    public static Task getTaskFromServer() throws IOException {
        Task task = null;
        JSONObject taskJson;

        URL url = new URL(BASE_URL);
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        String authHeaderString = "bearer " + token;
        con.setRequestProperty("Authorization", authHeaderString);
        con.connect();
        int responseCode=con.getResponseCode();
        if (responseCode==403){
            System.out.println("Auth expired. Checking in again.");
            con.disconnect();
            checkIn(BotName);
            return getTaskFromServer();
        }

        if (responseCode==420){
            con.disconnect();

            new ConditionalSleep(10000) {
                @Override
                public boolean condition() {
                    return false;
                }
            }.sleep();
        }


        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        con.disconnect();
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


    public static String taskComplete() throws IOException {

        URL url = new URL(BASE_URL + "done");
        URLConnection con = url.openConnection();
        String authHeaderString = "bearer " + token;
        con.setRequestProperty("Authorization", authHeaderString);
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        JSONObject response;
        String result;
        try {
            response = (JSONObject) jsonParser.parse(bufferedReader);
            result = (String) response.get("success");
        } catch (ParseException e) {
            result = ("error");
        }
        return result;
    }

    public static LoginEvent getLoginEvent() {
        return new LoginEvent(rsUsername, rsPassword);
    }

}

















