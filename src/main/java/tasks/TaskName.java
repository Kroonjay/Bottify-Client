package tasks;

public enum TaskName {

    MasterFarmers("MasterFarmers"),
    TRAVEL("Travel"),
    RESOURCE("ResourceTask"),
    FishingLumbridge("FishingLumbridge"),
    GRAND_EXCHANGE("GrandExchangeTask"),
    TutorialIsland("TutorialIslandTask"),
    BOT_ERROR("BotErrorTask"),
    COOKS_ASSISTANT("QuestTask-CooksAssistant"),
    PICK_FLAX("MoneyMaking-FlaxCollecting"),
    RUNE_MYSTERIES("QuestTask-RuneMysteries"),
    WAIT("WaitTask");
    String taskName;
    public static TaskName getByName(String taskName) {
        return TaskName.valueOf(taskName);
    }

    TaskName(final String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return taskName;
    }
}
