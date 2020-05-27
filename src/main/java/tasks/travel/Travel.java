package tasks.travel;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.rs07.api.map.Position;
import tasks.BotSpots;
import tasks.Task;
import tasks.TaskName;
import utils.bottify.ConfigManager;

import java.io.IOException;
import java.util.Random;

public class Travel extends Task {

    private Position destination;



    public boolean success=false;

    @Override
    public boolean isComplete() {
        if (success) {
            return true;
        }
        return false;
    }

    public Travel(String params) {

        super(TaskName.TRAVEL);
        this.params=params;
    }

    @Override
    public void onStart() {

        JSONParser parser = new JSONParser();
        JSONObject params=null;
        try {
            params = (JSONObject) parser.parse(this.params);
        } catch (ParseException e){
            log("Failed to parse params");
        }

        this.destination= BotSpots.getByName((String) params.get("destination"));
        log("Retrieved params");
    }

    @Override
    public void runTask() throws InterruptedException {
        if (destination != myPlayer().getPosition()){
            getWalking().webWalk(destination);
        }
        sleep( 1000);
    }

    @Override
    public Travel copy () {
        return new Travel(this.params);
    }

    public static int rand(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Invalid range");
        }

        Random rand = new Random();

        return rand.ints(min, (max + 1))    // IntStream
                .findFirst()            // OptionalInt
                .getAsInt();            // int
    }
}

