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

    private static String CheckInToken;
    private static String BearerToken;
    private static String RunescapeUsername;
    private static String RunescapePassword;
    private static final String BASE_URL = "http://64.227.95.98/botapi";
    private static final String dataDirectory = Paths.get(System.getProperty("user.home"), "OSBot", "Data").toString();
    private LoginEvent loginEvent;



    public static String checkIn(String CheckInToken, String RunescapeUsername, String RunescapePassword) throws IOException {
        ConfigManager.RunescapeUsername = RunescapeUsername;
        ConfigManager.RunescapePassword = RunescapePassword;
        ConfigManager.CheckInToken = CheckInToken;
        URL url = new URL(BASE_URL + "/check-in?check_in_token=" + ConfigManager.CheckInToken);
        URLConnection con = url.openConnection();
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        JSONObject response;
        try {
            response = (JSONObject) jsonParser.parse(bufferedReader);
            BearerToken = (String) response.get("access_token");
        } catch (ParseException e) {
        }
        return BearerToken;

    }


    public static Task getTaskFromServer() throws IOException {
        Task task = null;
        JSONObject taskJson;

        URL url = new URL(BASE_URL + "/task");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        String authHeaderString = "bearer " + BearerToken;
        con.setRequestProperty("Authorization", authHeaderString);
        con.connect();
        int responseCode=con.getResponseCode();
        if (responseCode==403){
            System.out.println("Auth expired. Checking in again.");
            con.disconnect();
            checkIn(CheckInToken, RunescapeUsername, RunescapePassword);
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
        JSONParser jsonParser = new JSONParser();
        try {
            taskJson = (JSONObject) jsonParser.parse(bufferedReader);
            TaskName taskName = TaskName.getByName((String) taskJson.get("taskName"));
            task = TaskFactory.createTask(taskName, taskJson);

        } catch (ParseException e) {
            System.out.println("Failed to parse task");
        }
        return task;
    }


    public static String taskComplete() throws IOException {

        URL url = new URL(BASE_URL + "done");
        URLConnection con = url.openConnection();
        String authHeaderString = "bearer " + BearerToken;
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
            result = ("Failed to Parse TaskComplete Response");
        }
        return result;
    }

    public static LoginEvent getLoginEvent() {
        return new LoginEvent(RunescapeUsername, RunescapePassword);
    }

}

















