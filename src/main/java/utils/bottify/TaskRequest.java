package utils.bottify;

import tasks.Task;
import utils.interfaces.JSONSerializable;

/**
 *
 * A TaskRequest used to create Tasks of 'type'
 */
public abstract class TaskRequest implements JSONSerializable {

    public abstract Task toTask();

}
