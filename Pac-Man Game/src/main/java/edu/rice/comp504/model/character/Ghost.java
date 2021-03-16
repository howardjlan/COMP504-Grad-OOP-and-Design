package edu.rice.comp504.model.character;


import edu.rice.comp504.model.cmd.IPacmanCmd;
import edu.rice.comp504.model.strategy.collision.ICollideStrategy;
import edu.rice.comp504.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * A ghost in the game.
 */
public class Ghost extends ACharacter {
    private boolean isFlashing;
    private boolean isDead;
    private int flashingTimer;
    private int score;
    private Point overlapItem;
    private int prevDir;
    private Point startLoc;
    private boolean wasFlashing;

    /**
     * Constructor.
     *
     * @param loc             The location of the ACharacter on the canvas
     * @param vel             The ACharacter velocity
     * @param color           The ACharacter color
     * @param updateStrategy  The object updateStrategy
     * @param collideStrategy The object collideStrategy
     */
    public Ghost(Point loc, Point vel, String color, IUpdateStrategy updateStrategy, ICollideStrategy collideStrategy,
                 int size, boolean isFlashing, boolean isDead, int flashingTimer, Point overlapItem) {
        super("ghost", loc, vel, color, updateStrategy, collideStrategy, 1, size);
        this.isFlashing = isFlashing;
        this.isDead = isDead;
        this.flashingTimer = flashingTimer;
        this.overlapItem = overlapItem;
        this.score = 200;
        this.prevDir = getDirection();
        this.startLoc = loc;
    }

    /**
     * Set the Ghost to its spawn location.
     */
    public void setStartLoc() {
        setLoc(this.startLoc);
    }

    /**
     * Get if Ghost is flashing.
     * @return If ghost is flashing.
     */
    public boolean isFlashing() {
        return isFlashing;
    }

    /**
     * Set if Ghost is flashing.
     * @param flashing If ghost is flashing.
     */
    public void setFlashing(boolean flashing) {
        isFlashing = flashing;
        if (flashing) {
            wasFlashing = true;
        }
    }

    /**
     * Get if Ghost is dead.
     * @return if ghost is dead.
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Set if Ghost is dead.
     * @param dead if ghost is dead.
     */
    public void setDead(boolean dead) {
        isDead = dead;
    }

    /**
     * Get the flashing time left of the Ghost.
     * @return The Ghost flashing time.
     */
    public int getFlashingTimer() {
        return flashingTimer;
    }

    /**
     * Set the flashing time left of the Ghost.
     * @param flashingTimer The Ghost flashing time.
     */
    public void setFlashingTimer(int flashingTimer) {
        this.flashingTimer = flashingTimer;
    }

    /**
     * Set the Ghost previous direction.
     * @param dir  The direction coordinate.
     */
    public void setPrevDir(int dir) {
        this.prevDir = dir;
    }

    /**
     * Update state of the Ghost when the property change event occurs by executing the command.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        // TODO execute the update command
        IPacmanCmd update = (IPacmanCmd) e.getNewValue();
        ACharacter pac = (ACharacter) e.getOldValue();
        update.execute(pac, this);
    }
}
