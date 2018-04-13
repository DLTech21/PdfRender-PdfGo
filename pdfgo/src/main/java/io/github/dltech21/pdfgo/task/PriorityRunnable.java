package io.github.dltech21.pdfgo.task;

import io.github.dltech21.pdfgo.model.PriorityObject;

/**
 * @author donal
 */
public class PriorityRunnable extends PriorityObject<Runnable> implements Runnable {

    public PriorityRunnable(int priority, Runnable obj) {
        super(priority, obj);
    }

    @Override
    public void run() {
        this.obj.run();
    }
}
