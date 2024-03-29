package quests;
import org.osbot.rs07.api.Configs;
import tasks.Task;
import tasks.banking.DepositAllBanking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.GroundItem;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.listener.MessageListener;
import utils.DialogueCompleter;
import utils.Sleep;

import java.util.stream.Stream;

public class CooksAssistantTask extends Task {


    private final int configID = 29;
    private final int configCompletedVal = 2;
    public boolean taskIsComplete = false;
    private static final Area COOK_ROOM = new Area(3205, 3215, 3212, 3212);
    private static final Area BASEMENT = new Area(3214, 9625, 3216, 9623);
    private static final Area COW = new Area(3253, 3270, 3255, 3275);
    private static final Area CHICKEN = new Area(3235, 3295, 3226, 3300);
    private static final Area WHEAT = new Area(3162, 3295, 3157, 3298);
    private static final Area UPPER = new Area(new Position(3168, 3305, 2), new Position(3165, 3308, 2));
    private static final Area BIN = new Area(3165, 3305, 3168, 3308);

    private static final int INVENTORY_SLOTS_REQUIRED = 7;


    private static final String[] ITEMS_NEEDED = {
            "Pot of flour",
            "Bucket of milk",
            "Egg"
    };
    private final DepositAllBanking depositAllBanking = new DepositAllBanking(ITEMS_NEEDED);
    private final DialogueCompleter cookDialogueCompleter = new DialogueCompleter(
            "Cook",
            COOK_ROOM,
            "What's wrong?",
            "I'm always happy to help a cook in distress.",
            "Actually, I know where to find this stuff."
    );
    private boolean operated = false;
    private boolean put = false;

    protected int getProgress() {
        return getConfigs().get(configID);
    }

    private final MessageListener MILL_MESSAGE_LISTENER = message -> {
        if (message.getType() != Message.MessageType.GAME) {
            return;
        }

        String messageStr = message.getMessage();

        if (messageStr.contains("There is already grain in the hopper.") ||
                messageStr.contains("You put the grain in the hopper.")) {
            put = true;
            return;
        }

        if (messageStr.contains("You operate the hopper. The grain slides down the chute.")) {
            operated = true;
            return;
        }
    };


    public boolean taskIsComplete(final Configs configs) {
        return configs.get(configID) == configCompletedVal;
    };



    @Override
    public boolean isComplete() {
        if (taskIsComplete){
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        depositAllBanking.exchangeContext(getBot());
        cookDialogueCompleter.exchangeContext(getBot());
        getBot().addMessageListener(MILL_MESSAGE_LISTENER);
    }

    @Override
    public void onEnd() {
        getBot().removeMessageListener(MILL_MESSAGE_LISTENER);
    }

    @Override
    public void runTask() throws InterruptedException {
        if (!getInventory().contains(ITEMS_NEEDED) && getInventory().getEmptySlotCount() < INVENTORY_SLOTS_REQUIRED) {
            depositAllBanking.run();
        } else if (getTabs().getOpen() != Tab.INVENTORY) {
            getTabs().open(Tab.INVENTORY);
        } else {
            switch (getProgress()) {
                case 0:
                    cookDialogueCompleter.run();
                    break;
                case 1:
                    if (hasRequiredItems()) {
                        cookDialogueCompleter.run();
                    } else {
                        getItemsNeeded();
                    }
                    break;
                case 2:
                    log("Quest is complete");
                    taskIsComplete = true;
                    break;
                default:
                    log("Unknown progress config value: " + getProgress());
                    setFailed();
                    break;
            }
        }
    }

    private boolean hasRequiredItems() {
        return Stream.of(ITEMS_NEEDED).allMatch(item -> getInventory().contains(item));
    }

    private void getItemsNeeded() throws InterruptedException {
        if (!getInventory().contains("Pot", "Pot of flour", "Bucket of milk")) {
            getGroundItem(COOK_ROOM, "Pot");
        } else if (!getInventory().contains("Bucket", "Bucket of milk")) {
            getGroundItem(BASEMENT, "Bucket");
        } else if (getInventory().contains("Bucket") && !getInventory().contains("Bucket of milk")) {
            getItemFromObject(COW, "Bucket of milk", "Dairy COW", "Milk");
        } else if (!getInventory().contains("Egg")) {
            getGroundItem(CHICKEN, "Egg");
        } else if (!getInventory().contains("Pot of flour")) {

            // Get grain
            if (!put && !getInventory().contains("Grain")) {
                getItemFromObject(WHEAT, "Grain", "Wheat", "Pick");
            }

            // Put grain
            if (!put && !operated && getInventory().contains("Grain")) {
                fillHopper();
            }

            // Operate machine
            if (!operated && put) {
                operateHopper();
            }

            // Get flour
            if (operated && put) {
                getItemFromObject(BIN, "Pot of flour", "Flour BIN", "Empty");
            }
        }
    }

    private void fillHopper() {
        if (!UPPER.contains(myPosition())) {
            getWalking().webWalk(UPPER);
        } else if (!"Grain".equals(getInventory().getSelectedItemName())) {
            getInventory().interact("Use", "Grain");
        } else {
            RS2Object hopper = getObjects().closest("Hopper");

            if (hopper == null) {
                log("Could not find object 'Hopper'");
                setFailed();
                return;
            }

            if (hopper.interact("Use")) {
                Sleep.sleepUntil(() -> put, 15000);
            }
        }
    }

    private void operateHopper() {
        if (!UPPER.contains(myPosition())) {
            getWalking().webWalk(UPPER);
        } else {
            RS2Object controls = getObjects().closest("Hopper controls");

            if (controls == null) {
                log("Could not find object 'Hopper controls'");
                setFailed();
                return;
            }

            if (controls.interact("Operate")) {
                Sleep.sleepUntil(() -> operated, 10000);
            }
        }
    }

    private void getItemFromObject(Area place, String itemName, String objectName, String interaction) throws InterruptedException {
        if (place.contains(myPlayer())) {
            RS2Object object = getObjects().closest(objectName);
            if (object != null && object.interact(interaction)) {
                Sleep.sleepUntil(() -> getInventory().contains(itemName) && !myPlayer().isAnimating(), 15000);
            }
        } else {
            getWalking().webWalk(place);
        }
    }

    private void getGroundItem(Area place, String itemName) throws InterruptedException {
        if (place.contains(myPosition())) {
            GroundItem itemToGet = getGroundItems().closest(itemName);
            if (itemToGet != null && itemToGet.interact("Take")) {
                Sleep.sleepUntil(() -> getInventory().contains(itemName), 8000);
            }
        } else {
            getWalking().webWalk(place);
        }
    }

    @Override
    public Task copy() {
        return new CooksAssistantTask();
    }
}