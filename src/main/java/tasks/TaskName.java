package tasks;

public enum TaskName {

    MASTER_FARMERS("MasterFarmerTask"),
    TRAVEL("Travel"),
    RESOURCE("ResourceTask"),
    FISHING("Fishing"),
    GRAND_EXCHANGE("GrandExchangeTask"),
    TUTORIAL_ISLAND("TutorialIslandTask"),
    BOT_ERROR("BotErrorTask"),
    BREAK("BreakTask"),
    COOKS_ASSISTANT("QuestTask-CooksAssistant"),
    PICK_FLAX("MoneyMaking-FlaxCollecting"),
    RUNE_MYSTERIES("QuestTask-RuneMysteries");

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
