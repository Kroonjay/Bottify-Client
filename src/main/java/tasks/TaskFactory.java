package tasks;

import org.json.simple.JSONObject;
import quests.CooksAssistantTask;
import tasks.master_farmers.MasterFarmers;
import tasks.tutorial_island.TutorialIslandTask;
import utils.bottify.ConfigManager;

import java.io.IOException;

public class TaskFactory {
    public static Task createTask(final TaskName taskName) {

        switch (taskName) {
            case TUTORIAL_ISLAND:
                return new TutorialIslandTask();
            case COOKS_ASSISTANT:
                return new CooksAssistantTask();
            case MASTER_FARMERS:
                return new MasterFarmers();
            default:
                return null;
        }
    }
}
