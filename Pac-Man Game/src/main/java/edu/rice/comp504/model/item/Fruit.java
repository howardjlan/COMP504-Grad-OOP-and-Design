package edu.rice.comp504.model.item;

import edu.rice.comp504.model.DispatcherAdapter;

import java.awt.*;

public class Fruit extends AItem {
    private int timeLeft;
    private int fruitType;
    private boolean isActive;

    /**
     * Constructor.
     *
     * @param loc   The location of the Fruit on the canvas
     */
    public Fruit(Point loc, int score, int size, int fruitType, int timeLeft) {
        super("fruit", loc,"black", score);
        this.timeLeft = timeLeft;
        this.fruitType = fruitType;
        this.isActive = false;
    }

    /**
     * Get the Fruit time left.
     * @return Fruit time
     */
    public int getTimeLeft() {
        return timeLeft;
    }

    /**
     * Set the Fruit time left.
     * @param time Fruit time
     */
    public void setTimeLeft(int time) {
        timeLeft = time;
    }

    /**
     * Get if the Fruit is active.
     * @return Fruit active status
     */
    public boolean getActive() {
        return isActive;
    }

    /**
     * Set if the Fruit is active.
     * @param active Fruit active status
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Get the Fruit type.
     * @return Fruit type
     */
    public int getFruitType() {
        return fruitType;
    }

    /**
     * Decrease the fruit active time.
     * @return If the time run out.
     */
    public boolean decreaseLeftTime() {
        if (isActive) {
            timeLeft -= DispatcherAdapter.updatePeriod;
            if (timeLeft <= 0) {
                isActive = false;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
