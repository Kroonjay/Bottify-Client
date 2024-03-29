package tasks.fishing;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import tasks.BotSpots;
import tasks.Task;
import tasks.TaskName;
import tasks.banking.Bank;
import utils.Sleep;

import java.util.Random;

public class Fishing extends Task {

    private Position fishingLocation;
    private int state;
    public boolean success=false;
    private int taskId;
    @Override
    public boolean isComplete() {
        if (success) {
            return true;
        }
        return false;
    }


    public Fishing(JSONObject taskJson) {
        super(TaskName.FishingLumbridge,taskJson);
    }



    @Override
    public void onStart() {
        state = States.GOTOSPOT;
        JSONParser parser = new JSONParser();
        JSONObject params=this.params;
        this.fishingLocation = BotSpots.getByName((String) params.get("fishingLocation"));
        log("Retrieved params");
        log("fishingLocation: "+ this.fishingLocation);
    }

    @Override
    public void runTask() throws InterruptedException {

        switch (state){
            case States.GOTOSPOT:
                state = goToSpot();
                break;
            case States.FISH:
                state = fish();
                break;
            case States.GOTOBANK:
                state = goToBank();
                break;
            case States.BANK:
                state = bank();
                break;
            default:
                break;
        }
    }

    private int goToSpot() throws InterruptedException {

        if (getInventory().isFull()){
            return States.GOTOBANK;
        } else if (getNpcs().closest(1530)==null) {
            getWalking().webWalk(fishingLocation);
            sleep(rand(780, 1098));
            return States.GOTOSPOT;
        } else {
            sleep(rand(270, 530));
            return States.FISH;

        }
    }

    private int goToBank() throws InterruptedException {
        if (Bank.inAnyBank(myPosition())) {
            if (!getBank().isOpen()) {
                if (getBank().open()) {
                    Sleep.sleepUntil(() -> getBank().isOpen(), 5000);
                }
            }
            else {
                getBank().depositAllExcept("Small fishing net");
            }
        } else {
            log("FishingTask Initializing - Moving to Bank to Start Task");
            getWalking().webWalk(Bank.getAreas());
            Sleep.sleepUntil(() -> Bank.inAnyBank(myPosition()), 5000);
        }
        return States.BANK;
    }


    private int fish() throws InterruptedException {

        if (getInventory().isFull()) {
            return States.GOTOBANK;
        } else if (!myPlayer().isAnimating()) {
            NPC spot = getNpcs().closest("Fishing spot");
            if (spot != null && spot.interact("Net")) {
                Sleep.sleepUntil(() -> myPlayer().isAnimating(), 5000);
                sleep(rand(215, 326));
            }
        }
        return States.FISH;
    }


    private int bank() throws InterruptedException {
        if (myPlayer().isAnimating()){
            sleep(rand(340,388));
            return States.BANK;
        } else if (getBank().isOpen()){
            getBank().depositAllExcept("Small fishing net");
            sleep(rand(480,514));
            return States.GOTOSPOT;
        } else {
            RS2Object bankBooth = getObjects().closest("Bank booth");
            if (bankBooth != null && bankBooth.interact("Bank")) {
                Sleep.sleepUntil(() -> myPlayer().isAnimating(), 5000);
                sleep(rand(215, 326));
            }
            return States.BANK;
        }
    }

    @Override
    public Fishing copy () {
        return new Fishing(this.taskJson);
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


    public static class States{
        public static final int GOTOSPOT = 0;
        public static final int FISH = 1;
        public static final int GOTOBANK = 2;
        public static final int BANK = 3;

    }
}
