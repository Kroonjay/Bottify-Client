package tasks.task_executor;

import tasks.Task;
import utils.Executable;
import utils.SkillTracker;
import utils.bottify.ConfigManager;

import java.io.IOException;
import java.util.*;

public final class TaskExecutor extends Executable {

    private final List<TaskChangeListener> taskChangeListeners = new ArrayList<>();
    private Task currentTask;
    private long t0;

    public TaskExecutor() throws IOException {
    }

    public Task getCurrentTask() {
        return currentTask;
    }



    public final void addTaskChangeListener(final TaskChangeListener taskChangeListener) {
        this.taskChangeListeners.add(taskChangeListener);
    }

    public final void addTaskChangeListeners(final Collection<TaskChangeListener> taskChangeListeners) {
        this.taskChangeListeners.addAll(taskChangeListeners);
    }

    public boolean isComplete() {
        return false;
    }

    public long lastCheckedAt = 0;

    @Override
    public final void run() throws InterruptedException {
        long now = System.currentTimeMillis();
        if (now-lastCheckedAt>30000){
            loadNextTask();
            lastCheckedAt=now;
        }
        else if (currentTask == null) {
            loadNextTask();
            lastCheckedAt=now;
        } else {
            runTask(currentTask);
        }
    }

    private void loadNextTask() throws InterruptedException {
        log("Checking for new task");
        if (currentTask!=null) {
            log("Current task: "+currentTask.taskName.toString());
            log("Current task complete? " + currentTask.isComplete());
            if (currentTask.isComplete()) {
                try {
                    log("Notification success: " + ConfigManager.taskComplete());
                    sleep(2000);
                    currentTask=null;
                } catch (IOException e) {
                    log("Failed to notify server of task completion.");
                }
                sleep(2000);
            }
        }
        Task prevTask=currentTask;
        Task newTask;
        try {
            newTask = ConfigManager.getTaskFromServer();
        }
        catch(IOException e){
            log("Could not retrieve task from server");
            sleep(2000);
            return;
        }

        if (currentTask==null){
            log("Received task: "+newTask.taskName);
        } else if (newTask.taskId==currentTask.taskId){
            log("No new tasks from server.");
            return;
        } else {
            log("Switching to new task: "+newTask.taskName);
        }
        currentTask=newTask;
        currentTask.exchangeContext(getBot());
        currentTask.onStart();

        for (final TaskChangeListener taskChangeListener : taskChangeListeners) {
            taskChangeListener.taskChanged(prevTask, currentTask);
        }
    }

    private void runTask(final Task task) throws InterruptedException {
        try {
            task.run();
        } catch (NullPointerException nullPointer) {
            log("Found null pointer exception. Task failed, exiting.");

            StackTraceElement[] stack = nullPointer.getStackTrace();
            for (StackTraceElement element : stack) {
                log(element.toString());
            }

            currentTask = null;

        }
    }
}