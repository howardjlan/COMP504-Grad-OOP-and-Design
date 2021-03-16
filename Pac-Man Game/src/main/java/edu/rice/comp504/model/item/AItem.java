package edu.rice.comp504.model.item;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.cmd.IPacmanCmd;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AItem extends APaintObj implements PropertyChangeListener {
    private String color;
    private int score;
    private transient boolean isEaten;

    /**
     * Constructor.
     * @param name The name of the AItem
     * @param loc  The location of the AItem on the canvas
     * @param color The AItem color
     */
    public AItem(String name, Point loc, String color, int score) {
        super(name, loc);
        this.color = color;
        this.score = score;
        this.isEaten = false;
    }

    /**
     * Get the AItem color.
     * @return AItem color
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Set the AItem color.
     * @param c The new AItem color
     */
    public void setColor(String c) {
        this.color = c;
    }

    /**
     * Get the AItem score.
     * @return AItem score
     */
    public int getScore() {
        return score;
    }

    /**
     * Get if the AItem color is eaten.
     * @return If AItem is eaten
     */
    public boolean isEaten() {
        return isEaten;
    }

    /**
     * Set if the AItem color is eaten.
     * @param eaten If AItem is eaten
     */
    public void setEaten(boolean eaten) {
        isEaten = eaten;
    }

    /**
     * Item respond to property change event.
     * @param evt changed event.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        IPacmanCmd cmd = (IPacmanCmd) evt.getNewValue();
        APaintObj pacman = (APaintObj) evt.getOldValue();
        cmd.execute(pacman, this);
    }
}
