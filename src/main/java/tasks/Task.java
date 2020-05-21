package tasks;

import utils.Copyable;
import utils.Executable;

public abstract class Task extends Executable implements Copyable<Task> {
    public TaskName taskName;

    public Task(final TaskName taskName) {
        this.taskName = taskName;
    }

    protected Task() {
    }


    public abstract boolean isComplete();

    public TaskName getTaskName() {
        return taskName;
    }

    @Override
    public void run() throws InterruptedException {
        runTask();
    }

    public abstract void runTask() throws InterruptedException;

    public boolean postUpdate(){return false;}

}
