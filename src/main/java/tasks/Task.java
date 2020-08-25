package tasks;

import org.json.simple.JSONObject;
import utils.Copyable;
import utils.Executable;

public abstract class Task extends Executable implements Copyable<Task> {
    public TaskName taskName;
    public JSONObject params;
    public long taskId;
    public JSONObject taskJson;

    public Task(final TaskName taskName, final JSONObject taskJson) {
        this.taskName = taskName;
        this.taskId=(long) taskJson.get("taskId");
        this.params= (JSONObject) taskJson.get("taskParams");
    }

    protected Task() {
    }


    public abstract boolean isComplete();

    public TaskName getTaskName() {
        return this.taskName;
    }

    public long getTaskId(){ return this.taskId; }

    @Override
    public void run() throws InterruptedException {
        runTask();
    }


    public abstract void runTask() throws InterruptedException;

    public boolean postUpdate(){return false;}

}
