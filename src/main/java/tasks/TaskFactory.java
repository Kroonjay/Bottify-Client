package tasks;

import quests.CooksAssistantTask;
import tasks.tutorial_island.TutorialIslandTask;

public class TaskFactory {
    public static Task createTask(final TaskName taskName) {
        switch (taskName) {
            case TUTORIAL_ISLAND:
                return new TutorialIslandTask();
            case COOKS_ASSISTANT:
                return new CooksAssistantTask();
            default:
                return null;
        }
    }
}
