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
        String response=ConfigManager.checkIn("rw.clwnpns.frt@gmail.com","maximumstains");
        log(ConfigManager.token);
        t0=System.currentTimeMillis();
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


    @Override
    public final void run() throws InterruptedException {
        long t1= System.currentTimeMillis();
        if (t1-t0>60000){
            t0=t1;
            loadNextTask();
        }
        else if (currentTask == null) {
            loadNextTask();
        } else {
            runTask(currentTask);
        }
    }

    private void loadNextTask() throws InterruptedException {
        Task prevTask=currentTask;
        Task newTask;
        try {
            newTask = ConfigManager.getTaskFromServer();
        }
        catch(IOException e){
            log("Could not retrieve task from server");
            return;
        }

        if (currentTask==null){
            log("Received task: "+newTask.taskName);
        } else if (newTask.taskName==currentTask.taskName){
            log("No new tasks from server.");
            return;
        } else if (newTask.taskName != currentTask.taskName){
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