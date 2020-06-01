import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.script.ScriptManifest;
import org.osbot.rs07.utility.ConditionalSleep;
import tasks.Task;
import tasks.task_executor.TaskExecutor;
import tasks.tutorial_island.TutorialIslandTask;
import utils.bottify.ConfigManager;
import utils.SkillTracker;
import utils.event.EnableFixedModeEvent;
import utils.event.LoginEvent;
import utils.event.ToggleRoofsHiddenEvent;
import utils.event.ToggleShiftDropEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ScriptManifest(author = "Kroonjay", name = "BottifyClient", info = "Bottify Client", version = 4.20, logo = "http://i.imgur.com/58Zz0fb.png")
public class BottifyClient extends Script {

    static final String VERSION = "v3.1.10";
    private static final String BASE_URL = "http://bottify.io/api/bots";
    private SkillTracker skillTracker;
    private TaskExecutor taskExecutor;
    private boolean osrsClientIsConfigured;
    private LoginEvent loginEvent;
    @Override




    public void onStart() throws InterruptedException {


        String BotName = getParameters();
        log("Attempting to Login to Bottify Server with BotID: " + BotName);
        try {
            String token = ConfigManager.checkIn(BotName);
            log("Retrieved Token: " + token);
        } catch (IOException e) {
            log("Couldn't Get Token");
            e.printStackTrace();
        }
        loginEvent = ConfigManager.getLoginEvent();
        getBot().addLoginListener(loginEvent);
        execute(loginEvent);
        try {
            taskExecutor = new TaskExecutor();
        } catch (IOException e) {
            log("Could not initialize task executor");
        }
        taskExecutor.exchangeContext(getBot());
        taskExecutor.addTaskChangeListener((oldTask, newTask) -> {
            skillTracker.stopAll();
        });
        taskExecutor.onStart();
        skillTracker = new SkillTracker(getSkills());

    }

    @Override
    public int onLoop() throws InterruptedException {
        if (loginEvent != null ) {
            if (loginEvent.isQueued() || loginEvent.isWorking()) {
                new ConditionalSleep(2000) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return !loginEvent.isQueued() && loginEvent.isWorking();
                    }
                };
            }
            if (loginEvent.hasFailed()) {
                LoginEvent.LoginEventResult result = loginEvent.getLoginEventResult();
                log(result.toString());
                stop(true);
            }
            getBot().removeLoginListener(loginEvent);
            loginEvent = null;
        } else if (!osrsClientIsConfigured && osrsClientIsConfigurable()) {
            osrsClientIsConfigured = configureOSRSClient();
        } else if (taskExecutor.isComplete()) {
            stop(true);
        } else  {
            taskExecutor.run();
        }
        return random(200, 300);
    }

    private boolean osrsClientIsConfigurable() {
        return !Tab.SETTINGS.isDisabled(getBot()) &&
                !getDialogues().isPendingContinuation() &&
                !myPlayer().isAnimating() &&
                taskExecutor.getCurrentTask() != null &&
                !(taskExecutor.getCurrentTask() instanceof TutorialIslandTask) &&
                getNpcs().closest("Lumbridge Guide") == null;
    }

    private boolean configureOSRSClient() {
        if (!EnableFixedModeEvent.isFixedModeEnabled(getBot().getMethods())) {
            execute(new EnableFixedModeEvent());
        } else if (!getSettings().areRoofsEnabled()) {
            execute(new ToggleRoofsHiddenEvent());
        } else if (!getSettings().isShiftDropActive()) {
            execute(new ToggleShiftDropEvent());
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void pause() {
        if (skillTracker != null) {
            skillTracker.pauseAll();
        }
    }

    @Override
    public void resume() {

        if (skillTracker != null) {
            skillTracker.resumeAll();
        }
    }
}