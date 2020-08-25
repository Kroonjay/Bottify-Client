package tasks;


import org.json.simple.JSONObject;
import quests.CooksAssistantTask;
import tasks.fishing.Fishing;
import tasks.master_farmers.MasterFarmers;
import tasks.travel.Travel;
import tasks.tutorial_island.TutorialIslandTask;

public class TaskFactory {
    public static Task createTask(final TaskName taskName, final JSONObject taskJson) {
        switch (taskName) {
            case TutorialIsland:
                return new TutorialIslandTask(taskJson);
            case TRAVEL:
                return new Travel(taskJson);
            case COOKS_ASSISTANT:
                return new CooksAssistantTask();
            case MasterFarmers:
                return new MasterFarmers(taskJson);
            case FishingLumbridge:
                return new Fishing(taskJson);
            default:
                return null;
        }
    }
}
