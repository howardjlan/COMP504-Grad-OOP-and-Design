package edu.rice.comp504.model.character;

import edu.rice.comp504.model.DispatcherAdapter;
import edu.rice.comp504.model.cmd.IPacmanCmd;
import edu.rice.comp504.model.item.AItem;
import edu.rice.comp504.model.strategy.collision.ICollideStrategy;
import edu.rice.comp504.model.strategy.IUpdateStrategy;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.TreeMap;

/**
 * The pacman in the game.
 */
public class Pacman extends ACharacter {
    private int leftLives;
    private int nextDirection;
    private int deadState;
    private int deadSpeed = 0;
    private transient TreeMap<Point, Wall> horizontalWall;
    private transient TreeMap<Point,Wall> verticalWall;
    private transient int[] portals;

    /**
     * Constructor.
     *
     * @param loc             The location of the ACharacter on the canvas
     * @param vel             The ACharacter velocity
     * @param updateStrategy  The object updateStrategy
     * @param collideStrategy The object collideStrategy
     */
    public Pacman(Point loc, Point vel, IUpdateStrategy updateStrategy,
                  ICollideStrategy collideStrategy, int size, int direction, int leftLives) {
        super("pacman", loc, vel, null, updateStrategy, collideStrategy, direction, size);
        this.leftLives = leftLives;
        this.nextDirection = 0;
        this.deadState = -1;
        this.portals = new int[2];
    }

    /**
     * Detects collision between Pacman and a ghost in the world.
     */
    public boolean detectCollisionWithGhost(Ghost ghost) {

        if (deadState != -1) {
            return false;
        }

        double distance = Math.pow(getLoc().x - ghost.getLoc().x, 2) + Math.pow(getLoc().y - ghost.getLoc().y, 2);
        return  distance <= Math.pow(getSize(), 2) + Math.pow(ghost.getSize(), 2);
    }

    /**
     * Detects collision between Pacman and a item in the world.
     */
    public boolean detectCollisionWithItem(AItem item) {

        if (deadState != -1) {
            return false;
        }

        double distance = Math.pow(getLoc().x - item.getLoc().x, 2) + Math.pow(getLoc().y - item.getLoc().y, 2);
        return  distance <= Math.pow(getSize(), 2);
    }

    /**
     * Set the pacman as dead or update its dead state.
     */
    public void setDeadState() {
        if (deadState == -1) {
            deadState = 0;
            reduceLive();
        } else if (deadState <= 12) {
            deadState++;
        } else {
            deadState = -1;
        }
    }

    /**
     * Get the dead state of pacman.
     * @return the dead state of pacman.
     */
    public int getDeadState() {
        return deadState;
    }

    /**
     * Reduce the pacman live by 1.
     */
    public void reduceLive() {
        this.leftLives--;
    }

    /**
     * Reset the pacman.
     */
    public void reset() {
        this.setLoc(new Point(this.getOriginalLoc().x, this.getOriginalLoc().y));
    }

    /**
     * Get the left lives of pacman.
     * @return the left lives of pacman.
     */
    public int getLeftLives() {
        return leftLives;
    }

    /**
     * Get the next direction of pacman.
     * @return the next direction of pacman.
     */
    public int getNextDirection() {
        return nextDirection;
    }

    /**
     * Set the next direction of pacman.
     * @param nextDirection the next direction of pacman.
     */
    public void setNextDirection(int nextDirection) {
        this.nextDirection = nextDirection;
    }

    /**
     * Get the horizontal walls.
     * @return the horizontal walls.
     */
    public TreeMap<Point, Wall> getHorizontalWall() {
        return horizontalWall;
    }

    /**
     * Set the horizontal walls.
     * @param horizontalWall the horizontal walls.
     */
    public void setHorizontalWall(TreeMap<Point, Wall> horizontalWall) {
        this.horizontalWall = horizontalWall;
    }

    /**
     * Get the vertical walls.
     * @return the vertical walls.
     */
    public TreeMap<Point, Wall> getVerticalWall() {
        return verticalWall;
    }

    /**
     * Set the vertical walls.
     * @param verticalWall the vertical walls.
     */
    public void setVerticalWall(TreeMap<Point, Wall> verticalWall) {
        this.verticalWall = verticalWall;
    }

    /**
     * Get the portals location.
     * @return the portals location.
     */
    public int[] getPortals() {
        return portals;
    }

    /**
     * Set the portals location.
     * @param portals the portals location.
     */
    public void setPortals(int[] portals) {
        this.portals = portals;
    }

    /**
     * Update state of the Pacman when the property change event occurs by executing the command.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        // TODO execute the update command
        IPacmanCmd update = (IPacmanCmd)e.getNewValue();
        update.execute(this, this);

        if (deadState != -1) {
            deadSpeed++;
            if (deadSpeed > 4) {
                deadState++;
                deadSpeed = 0;
            }
            if (deadState > 12) {
                reset();
                deadState = -1;
            }
        }
    }
}
