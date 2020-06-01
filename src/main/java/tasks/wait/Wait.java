package tasks.wait;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.utility.ConditionalSleep;
import tasks.BotSpots;
import tasks.Task;
import tasks.TaskName;
import utils.event.LoginEvent;

import java.util.concurrent.TimeUnit;

public class Wait extends Task {
    private Position location;
    private int duration;
    private boolean complete = false;
    private long endTime;
    private TaskName taskName;
    private int lastState = States.GETTOSAFETY;
    private int state = States.GETTOSAFETY;


    public Wait(JSONObject taskJson) {
        super(TaskName.WAIT, taskJson);
    }


    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public void onStart() {
        JSONParser parser = new JSONParser();
        JSONObject params = null;
        try {
            params = (JSONObject) parser.parse(this.params);
        } catch (ParseException e) {
            log("Failed to parse params");
        }
        try {
            this.location = BotSpots.getByName((String) params.get("location"));
            long startTime = System.currentTimeMillis();
            this.duration = (int) params.get("duration");
            this.endTime = startTime + duration;
            log("Started Wait Task");
        }
        catch (NullPointerException e) {
            log("Wait Task Failed" + e.getStackTrace());
        }

    }

    @Override
    public void runTask() throws InterruptedException {
        if (lastState != state) {
            lastState = state;
        }

        if (!getClient().isLoggedIn()) {
            state = States.SLEEP;
        }

        switch (state){
            case States.GETTOSAFETY:
                state = goToSafeLocation();
                break;
            case States.LOGOUT:
                botLogout();
                break;
            case States.SLEEP:
                botSleep();
                break;
            default:
                break;
        }

    }

    private int goToSafeLocation() {
        if (location != (myPlayer()).getPosition()) {
            log("Walking to Safe Location");
            getWalking().webWalk(location);
            ConditionalSleep sleepUntilArrival = new ConditionalSleep(2000) {
                @Override
                public boolean condition() {
                    return location == (myPlayer()).getPosition();
                }
            };
        }
        return States.LOGOUT;
    }

    private int botLogout() {

        if (getClient().isLoggedIn()) {
            getLogoutTab().logOut();
        }
        return States.SLEEP;
    }

    private int botSleep() {
        long currentTime = System.currentTimeMillis();
        if (currentTime >= endTime) {
            complete = true;
        }
        return States.SLEEP;
    }


    @Override
    public Task copy() {
        return null;
    }

    static class States {
        public static final int LOGOUT = 0;
        public static final int SLEEP = 1;
        public static final int GETTOSAFETY = 2;
    }
}

