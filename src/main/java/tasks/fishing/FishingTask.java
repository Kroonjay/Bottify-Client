package tasks.fishing;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.rs07.api.map.Area;
import tasks.Task;
import tasks.TaskName;

public class FishingTask extends Task {

    private Fish fish;
    private FishingLocation location;
    public String params;

    public FishingTask(String params) {
        super(TaskName.FISHING);
        this.params = params;
    }

    @Override
    public void onStart() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject paramObj = (JSONObject) parser.parse(params);
            String fishingLocation = paramObj.get("location").toString();
            location = FishingLocation.valueOf(fishingLocation);

        } catch (ParseException e) {
            log("Failed to Parse Params");
            e.printStackTrace();
        }
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public void runTask() throws InterruptedException {

    }

    @Override
    public Task copy() {
        return null;
    }
}
