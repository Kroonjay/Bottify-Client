package tasks.tutorial_island;

import org.json.simple.JSONObject;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.randoms.AutoLogin;
import org.osbot.rs07.script.RandomSolver;
import tasks.Task;
import tasks.TaskName;
import utils.Sleep;

public final class TutorialIslandTask extends Task {

    private final TutorialSection rsGuideSection = new RuneScapeGuideSection();
    private final TutorialSection survivalSection = new SurvivalSection();
    private final TutorialSection cookingSection = new CookingSection();
    private final TutorialSection questSection = new QuestSection();
    private final TutorialSection miningSection = new MiningSection();
    private final TutorialSection fightingSection = new FightingSection();
    private final TutorialSection bankSection = new BankSection();
    private final TutorialSection priestSection = new PriestSection();
    private final TutorialSection wizardSection = new WizardSection();

    public boolean success = false;

    public TutorialIslandTask(JSONObject taskJson) {
        super(TaskName.TutorialIsland,taskJson);
    }
    @Override
    public boolean isComplete() {
        log("TutorialIslandTask Execution Completed - Success: " + this.success);
        return this.success;
    }

    @Override
    public void onStart() throws InterruptedException {

        rsGuideSection.exchangeContext(getBot());
        survivalSection.exchangeContext(getBot());
        cookingSection.exchangeContext(getBot());
        questSection.exchangeContext(getBot());
        miningSection.exchangeContext(getBot());
        fightingSection.exchangeContext(getBot());
        bankSection.exchangeContext(getBot());
        priestSection.exchangeContext(getBot());
        wizardSection.exchangeContext(getBot());

        Sleep.sleepUntil(() -> getClient().isLoggedIn() && myPlayer().isVisible() && myPlayer().isOnScreen(), 6000, 500);
    }

    @Override
    public void runTask() throws InterruptedException {
        log("Beginning RunTask Method for TutorialIslandTask - Tutorial Section: " + getTutorialSection());
        switch (getTutorialSection()) {
            case 0:
            case 1:
                log("TutorialIsland Task Starting Section: Runescape Guide");
                rsGuideSection.onLoop();
                break;
            case 2:
            case 3:
                log("TutorialIsland Task Starting Section: Survival");
                survivalSection.onLoop();
                break;
            case 4:
            case 5:
                log("TutorialIsland Task Starting Section: Cooking");
                cookingSection.onLoop();
                break;
            case 6:
            case 7:
                log("TutorialIsland Task Starting Section: Questing");
                questSection.onLoop();
                break;
            case 8:
            case 9:
                log("TutorialIsland Task Starting Section: Mining");
                miningSection.onLoop();

                break;
            case 10:
            case 11:
            case 12:
                log("TutorialIsland Task Starting Section: Combat");
                fightingSection.onLoop();
                break;
            case 14:
            case 15:
                log("TutorialIsland Task Starting Section: Banking");
                bankSection.onLoop();
                break;
            case 16:
            case 17:
                priestSection.onLoop();
                log("TutorialIsland Task Starting Section: Prayer");
                break;
            case 18:
            case 19:
            case 20:
                log("TutorialIsland Task Starting Section: Magic");
                wizardSection.onLoop();
                break;
        }
        log("RunTask Complete for TutorialIslandTask");
        this.success = true;
    }


    private int getTutorialSection() {
        return getConfigs().get(406);
    }
    private boolean isTutorialIslandCompleted() {
        return getConfigs().get(281) == 1000 && myPlayer().isVisible();
    }

    @Override
    public TutorialIslandTask copy() {
        return new TutorialIslandTask(this.taskJson);
    }
}