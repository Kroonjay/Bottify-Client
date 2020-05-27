package tasks;

import org.json.simple.JSONObject;
import quests.CooksAssistantTask;
import tasks.fishing.Fish;
import tasks.master_farmers.MasterFarmers;
import tasks.tutorial_island.TutorialIslandTask;
import tasks.fishing.FishingTask;
import utils.bottify.ConfigManager;

import java.io.IOException;

public class TaskFactory {
    public static Task createTask(final TaskName taskName, final String taskParams) {

        switch (taskName) {
            case TUTORIAL_ISLAND:
                return new TutorialIslandTask();
            case COOKS_ASSISTANT:
                return new CooksAssistantTask();
            case MASTER_FARMERS:
                return new MasterFarmers();
            case FISHING:
                return new FishingTask(taskParams);
            default:
                return null;
        }
    }
}
