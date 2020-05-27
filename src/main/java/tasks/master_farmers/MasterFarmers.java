package tasks.master_farmers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.rs07.api.Bank;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Skills;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.listener.GameTickListener;
import tasks.BotSpots;
import tasks.Task;
import tasks.TaskName;
import utils.Location;
import utils.Sleep;
import utils.Location;
import utils.bottify.ConfigManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class MasterFarmers extends Task implements GameTickListener {

    private Position location;
    private String food;
    private int state=States.GOTOFARMER;
    private int lastState=States.GOTOFARMER;
    private int hp;
    private Inventory inv;
    private Bank bnk;
    private JSONObject skills;



    public boolean success=false;

    @Override
    public boolean isComplete() {
        if (success) {
            return true;
        }
        return false;
    }


    public MasterFarmers(String params) {
        super(TaskName.MASTER_FARMERS);
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

        this.location= BotSpots.getByName((String) params.get("location"));
        this.food=(String) params.get("food");
        log("Retrieved params");
        log("Location: "+ params.get("location"));
        log("Food: "+params.get("food"));
    }

    @Override
    public void runTask() throws InterruptedException {

        if (lastState != state){lastState=state;}
        if (hp <= 4){ state=States.HEAL;}

        if (getInventory().isFull() && state != States.HEAL && state != States.BANK) {
            if (getInventory().contains("Empty jug")) { getInventory().dropAll("Empty jug"); }
            if (getInventory().isFull()){state = States.BANK;}
        }

        switch (state){
            case States.GOTOFARMER:
                state = goToFarmer();
                break;
            case States.HEAL:
                heal();
                break;
            case States.PICKPOCKET:
                pickpocket();
                break;
            case States.BANK:
                bank();
                break;
            default:
                break;
        }


    }



    @Override
    public void onGameTick(){

        hp=getSkills().getDynamic(Skill.HITPOINTS);


    }

    private int goToFarmer() throws InterruptedException {
        if (location!=(myPlayer()).getPosition()) {
            getWalking().webWalk(location);
            sleep(rand(780, 1098));
            return States.GOTOFARMER;
        } else {
            sleep(rand(270, 530));
            return States.PICKPOCKET;

        }
    }

    private int pickpocket() throws InterruptedException {
        if (!myPlayer().isAnimating()) {
            NPC npc = getNpcs().closest("Master Farmer");
            if (npc != null && npc.interact("Pickpocket")) {
                Sleep.sleepUntil(() -> myPlayer().isAnimating(), 5000);
                sleep(rand(215,326));
                return States.PICKPOCKET;
            } else{
                return States.PICKPOCKET;
            }
        } else{
            return States.PICKPOCKET;
        }
    }

    private int heal() throws InterruptedException {
        if (getSkills().getDynamic(Skill.HITPOINTS) <= 4){
            return lastState;
        } else if (getInventory().contains(food)) {
            if (Arrays.asList(getInventory().getItem(food).getActions()).contains("Eat")) {
                getInventory().getItem(food).interact("Eat");
            } else if (Arrays.asList(getInventory().getItem(food).getActions()).contains("Drink")) {
                getInventory().getItem(food).interact("Drink");
            }
        }
        sleep(rand(rand(267, 380), rand(430, 510)));
        return States.HEAL;
    }



    private int bank() throws InterruptedException {

        if (myPlayer().isAnimating()){
            sleep(rand(340,388));
            return States.BANK;
        } else if (getBank().isOpen()){
            getBank().depositAll();
            sleep(rand(480,514));
            if (getBank().contains(food)){
                getBank().withdraw(food,5);
                sleep(rand(415,433));
            }
            if (getBank().contains("Dodgy necklace")){
                getBank().withdraw("Dodgy necklace",2);
                sleep(rand(310,422));
            }
            return States.GOTOFARMER;
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
    public MasterFarmers copy () {
        return new MasterFarmers(this.params);
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

    static class States{
        public static final int GOTOFARMER = 0;
        public static final int HEAL = 1;
        public static final int PICKPOCKET = 2;
        public static final int BANK = 3;

    }

}

