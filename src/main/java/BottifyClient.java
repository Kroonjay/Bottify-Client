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

import java.io.*;
import java.nio.Buffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ScriptManifest(author = "Kroonjay", name = "BottifyClient", info = "Bottify Client", version = 4.20, logo = "http://i.imgur.com/58Zz0fb.png")
public class BottifyClient extends Script {

    static final String VERSION = "v3.1.10";
    private static final String BASE_URL = "http://64.227.95.98/botapi";
    private final String CONFIG_PATH = getDirectoryData() + File.separator + "BottifyClientConfig.txt";
    private final File BottifyConfigFile = new File(CONFIG_PATH);
    private String CheckInToken;
    private String RunescapeUsername;
    private String RunescapePassword;
    private SkillTracker skillTracker;
    private TaskExecutor taskExecutor;
    private boolean osrsClientIsConfigured;
    private LoginEvent loginEvent;
    @Override




    public void onStart() throws InterruptedException {
        log("BottifyClient Startup Initiated - Server Host: " + BASE_URL);
        try {
            BufferedReader configFileReader = new BufferedReader(new FileReader(BottifyConfigFile));
            String line;
            while ((line = configFileReader.readLine()) != null) {
                if (line.contains("CheckInToken")) {
                    this.CheckInToken = line.split(" - ")[1];
                }
                if (line.contains("RunescapeUsername")) {
                    this.RunescapeUsername = line.split(" - ")[1];
                }
                if (line.contains("RunescapePassword")) {
                    this.RunescapePassword = line.split(" - ")[1];
                }
            }
            configFileReader.close();
        } catch (FileNotFoundException e) {
            log("BottifyClient Failed to Start - Config File Not Found - Msg: " + e.getMessage());
            stop(true);
            return;
        } catch (IOException e) {
            log("BottifyClient Failed to Start - Failed to Parse Config File - Msg: " + e.getMessage());
            stop(true);
            return;
        }
        try {
            String token = ConfigManager.checkIn(this.CheckInToken, RunescapeUsername, RunescapePassword);
            log("BottifyClient Successfully Retrieved Oauth Token: ");
        } catch (IOException e) {
            log("BottifyClient Failed to Start - Failed to Retrieve Oauth Token - Msg: " + e.getMessage());
            e.printStackTrace();
            stop(true);
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