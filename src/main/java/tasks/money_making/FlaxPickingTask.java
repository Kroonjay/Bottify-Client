package tasks.money_making;

import org.json.simple.JSONObject;
import tasks.Task;
import tasks.TaskName;
import tasks.banking.Banking;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.utility.ConditionalSleep;
import utils.Executable;

public class FlaxPickingTask extends Task {

    private final Area flaxArea = new Area(
            new int[][]{
                    {2739, 3437},
                    {2737, 3439},
                    {2737, 3445},
                    {2736, 3446},
                    {2736, 3450},
                    {2738, 3452},
                    {2739, 3452},
                    {2741, 3454},
                    {2743, 3454},
                    {2744, 3453},
                    {2745, 3453},
                    {2746, 3452},
                    {2752, 3452},
                    {2752, 3437},
                    {2747, 3437},
                    {2746, 3436},
                    {2742, 3436},
                    {2741, 3437}
            }
    );

    private Entity flax;
    private Executable flaxBankNode;

    public FlaxPickingTask(JSONObject taskJson) {
        super(TaskName.PICK_FLAX,taskJson);
    }

    @Override
    public void onStart() {
        flaxBankNode = new FlaxBank();
        flaxBankNode.exchangeContext(getBot());
    }

    @Override
    public void runTask() throws InterruptedException {
        if (!getInventory().isFull() && getEquipment().isEmpty()) {
            if (getBank() != null && getBank().isOpen()) {
                getBank().close();
            } else if (!flaxArea.contains(myPosition())) {
                getWalking().webWalk(flaxArea);
            } else {
                if (flax == null || !flax.exists()) flax = getObjects().closest(true, "Flax");
                if (flax != null) {
                    long flaxInvAmount = getInventory().getAmount("Flax");
                    flax.interact("Pick");
                    new ConditionalSleep(5000) {
                        @Override
                        public boolean condition() throws InterruptedException {
                            return getInventory().getAmount("Flax") > flaxInvAmount || !flax.exists();
                        }
                    }.sleep();
                }
            }
        } else {
            flaxBankNode.run();
        }
    }

    @Override
    public Task copy() {
        return new FlaxPickingTask(this.taskJson);
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    private class FlaxBank extends Banking {

        @Override
        public boolean bank() {
            if (!getInventory().isEmpty()) {
                getBank().depositAll();
            } else if (!getEquipment().isEmpty()) {
                getBank().depositWornItems();
            }

            return true;
        }
    }
}
