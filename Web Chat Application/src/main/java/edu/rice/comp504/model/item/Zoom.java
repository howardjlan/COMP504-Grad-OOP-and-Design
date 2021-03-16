package edu.rice.comp504.model.item;

import edu.rice.comp504.model.DispatcherAdapter;

import java.awt.*;

public class Zoom extends AItem {
    private int leftTime;
    /**
     * Constructor.
     *
     * @param name  The name of the AItem
     * @param loc   The location of the AItem on the canvas
     * @param color The AItem color
     */
    public Zoom(String name, Point loc, String color, int timeLeft) {
        super(name, loc, color, 0);
        leftTime = timeLeft;
    }

    /**
     * Get the Zoom time left.
     * @return Zoom time
     */
    public int getTimeLeft() {
        return leftTime;
    }

    /**
     * Decrease the Zoom time left.
     * @return If the time run out.
     */
    public boolean decreaseLeftTime() {
        leftTime -= DispatcherAdapter.updatePeriod;
        if (leftTime <= 0) {
            return true;
        } else {
            return false;
        }
    }
}
