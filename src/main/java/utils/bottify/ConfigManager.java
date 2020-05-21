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

    private static String username;
    private static String password;
    public static String token;

    private static final String BASE_URL = "http://bottify.io/api/";
    private static final String dataDirectory = Paths.get(System.getProperty("user.home"), "OSBot", "Data").toString();


    public static String checkIn(String username, String password) throws IOException {
        ConfigManager.username = username;
        ConfigManager.password = password;

        URL url = new URL(BASE_URL+"bots/check-in?rs_username="+ConfigManager.username+"&rs_password="+ConfigManager.password);
        URLConnection con = url.openConnection();
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        JSONObject response;
        try{
            response = (JSONObject) jsonParser.parse(bufferedReader);
            token = (String) response.get("access_token");
            return token;

        }
        catch(ParseException e)  {
            return e.toString();
        }


    }



    public static Task getTaskFromServer() throws IOException {
        String endpoint = "tasks";
        Task task=null;
        JSONObject taskJSON=null;
        URL url = new URL(BASE_URL + endpoint);
        URLConnection con = url.openConnection();
        con.setRequestProperty("BotID", ConfigManager.username);
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        try{
            taskJSON = (JSONObject) jsonParser.parse(bufferedReader);
            TaskName taskName = TaskName.getByName((String) taskJSON.get("taskName"));
            task = TaskFactory.createTask(taskName);
        }
        catch(ParseException e)  {
            System.out.println("Failed to parse task");
        }

        return task;
    }

    public static JSONObject getTaskParameters(String endpoint) throws IOException {


        JSONObject taskParams=null;
        URL url = new URL(BASE_URL + "tasks/" +endpoint);
        URLConnection con = url.openConnection();
        con.setRequestProperty("BotID", ConfigManager.username);
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        try{
            taskParams = (JSONObject) jsonParser.parse(bufferedReader);
        }
        catch(ParseException e)  {
            System.out.println("Failed to parse task parameters");
        }

        return taskParams;
    }

    public static JSONObject postUpdate(String endpoint) throws IOException {


        JSONObject taskParams=null;
        URL url = new URL(BASE_URL + "tasks/" +endpoint);
        URLConnection con = url.openConnection();
        con.setRequestProperty("BotID", ConfigManager.username);
        InputStreamReader inputStreamReader = new InputStreamReader(con.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        JSONParser jsonParser = new JSONParser();
        try{
            taskParams = (JSONObject) jsonParser.parse(bufferedReader);
        }
        catch(ParseException e)  {
            System.out.println("Failed to parse task parameters");
        }

        return taskParams;
    }


}

















