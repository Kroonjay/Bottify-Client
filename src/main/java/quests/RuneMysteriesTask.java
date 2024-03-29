package quests;

import org.osbot.rs07.api.Configs;
import tasks.Task;
import tasks.banking.Bank;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Tab;
import utils.Sleep;

public class RuneMysteriesTask extends Task {
    public final int configID = 63;
    public final int configCompletedVal = 100;

    public boolean taskComplete = false;

    private final DialogueCompleter dukeHoracioDialogueCompleter = new DialogueCompleter(
            "Duke Horacio",
            new Area(new Position(3208, 3218, 1), new Position(3213, 3225, 1)),
            "Duke Horacio", "Have you any quests for me?", "Sure, no problem."
    );

    private final DialogueCompleter sedridorDialogueCompleter = new DialogueCompleter(
            "Sedridor",
            new Area(3105, 9570, 3103, 9572),
            "I'm looking for the head wizard.", "Ok, here you are.", "Yes, certainly."
    );

    private final DialogueCompleter auburyDialogueCompleter = new DialogueCompleter(
            "Aubury",
            new Area(3251, 3400, 3254, 3402),
            "I have been sent here with a package for you."
    );


    public boolean taskIsComplete(final Configs configs) {
        return configs.get(configID) == configCompletedVal;
    };

    protected int getProgress() {
        return getConfigs().get(configID);
    }


    @Override
    public boolean isComplete() {
        return taskComplete;
    }

    @Override
    public void onStart() {
        dukeHoracioDialogueCompleter.exchangeContext(getBot());
        sedridorDialogueCompleter.exchangeContext(getBot());
        auburyDialogueCompleter.exchangeContext(getBot());
    }

    @Override
    public void runTask() throws InterruptedException {
        if (!getInventory().contains("Air talisman", "Research package") && getInventory().isFull()) {
            if (Bank.inAnyBank(myPosition())) {
                if (!getBank().isOpen()) {
                    if (getBank().open()) {
                        Sleep.sleepUntil(() -> getBank().isOpen(), 5000);
                    }
                } else getBank().depositAllExcept("Air talisman", "Research package");
            } else {
                getWalking().webWalk(Bank.getAreas());
            }
        } else {

            if (getTabs().getOpen() != Tab.INVENTORY) {
                getTabs().open(Tab.INVENTORY);
            }

            switch (getProgress()) {
                case 0:
                    dukeHoracioDialogueCompleter.run();
                    break;
                case 1:
                    if (!getInventory().contains("Air talisman")) {
                        dukeHoracioDialogueCompleter.run();
                    } else {
                        sedridorDialogueCompleter.run();
                    }
                    break;
                case 2:
                    sedridorDialogueCompleter.run();
                    break;
                case 3:
                    if (!getInventory().contains("Research package")) {
                        sedridorDialogueCompleter.run();
                    } else {
                       auburyDialogueCompleter.run();
                    }
                    break;
                case 4:
                    auburyDialogueCompleter.run();
                    break;
                case 5:
                    sedridorDialogueCompleter.run();
                    break;

            }
        }
    }

    @Override
    public Task copy() {
        return new RuneMysteriesTask();
    }
}