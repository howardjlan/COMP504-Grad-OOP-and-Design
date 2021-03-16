package edu.rice.comp504.model.character;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.DispatcherAdapter;
import edu.rice.comp504.model.GameContext;
import edu.rice.comp504.model.strategy.collision.ICollideStrategy;
import edu.rice.comp504.model.strategy.IUpdateStrategy;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * A ghost that the view can draw on the user's canvas.
 */
public abstract class ACharacter extends APaintObj implements PropertyChangeListener {
    private Point vel;
    private IUpdateStrategy updateStrategy;
    private ICollideStrategy collideStrategy;
    private String color;
    // 1 is left, 2 is up, 3 is right, and 4 is down
    private int direction;
    private final Point originalLoc;
    private int size;
    private transient int gap;

    /**
     * Constructor.
     * @param name The name of the ACharacter
     * @param loc  The location of the ACharacter on the canvas
     * @param vel  The ACharacter velocity
     * @param color The ACharacter color
     * @param updateStrategy  The object updateStrategy
     * @param collideStrategy The object collideStrategy
     * @param direction The character direction
     */
    public ACharacter(String name, Point loc, Point vel, String color, IUpdateStrategy updateStrategy,
                      ICollideStrategy collideStrategy, int direction, int size) {
        super(name, loc);
        this.vel = vel;
        this.updateStrategy = updateStrategy;
        this.collideStrategy = collideStrategy;
        this.color = color;
        this.direction = direction;
        this.originalLoc = new Point(loc.x, loc.y);
        this.size = size;
        this.gap = 21 - size;
    }

    /**
     * Get the ACharacter color.
     * @return ACharacter color
     */
    public String getColor() {
        return this.color;
    }

    /**
     * Set the ACharacter color.
     * @param c The new ACharacter color
     */
    public void setColor(String c) {
        this.color = c;
    }

    /**
     * Get the velocity of the ACharacter.
     * @return The ACharacter velocity
     */
    public Point getVel() {
        return this.vel;
    }

    /**
     * Set the velocity of the ACharacter.
     * @param vel The new ACharacter velocity
     */
    public void setVel(Point vel) {
        this.vel = vel;
    }

    /**
     * Get the ACharacter updateStrategy.
     * @return The ACharacter updateStrategy.
     */
    public IUpdateStrategy getUpdateStrategy() {
        return this.updateStrategy;
    }

    /**
     * Set the updateStrategy of the ACharacter.
     * @param updateStrategy  The new updateStrategy
     */
    public void setUpdateStrategy(IUpdateStrategy updateStrategy) {
        this.updateStrategy = updateStrategy;
    }

    /**
     * Get the ACharacter collideStrategy.
     * @return The ACharacter collideStrategy.
     */
    public ICollideStrategy getCollideStrategy() {
        return collideStrategy;
    }

    /**
     * Set the collideStrategy of the ACharacter.
     * @param collideStrategy  The new collideStrategy
     */
    public void setCollideStrategy(ICollideStrategy collideStrategy) {
        this.collideStrategy = collideStrategy;
    }

    /**
     * Get the ACharacter direction.
     * @return The ACharacter direction.
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Set the direction of the ACharacter.
     * @param direction  The new direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * Get the ACharacter original location.
     * @return The ACharacter original location.
     */
    public Point getOriginalLoc() {
        return originalLoc;
    }


    /**
     * Get the size of the ACharacter.
     * @return The ACharacter size.
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the size of the ACharacter.
     * @param size The ACharacter size.
     */
    public void setSize(int size) {
        size = size;
    }

    /**
     * Detects collision between a ACharacter and a wall in the ACharacter world.  Change direction if ACharacter collides with a wall.
     * @return if it collide with a wall within a step.
     */
    public boolean detectCollisionWithWalls(int direction, TreeMap<Point,Wall> horizontal, TreeMap<Point,Wall> vertical) {
        Set<Map.Entry<Point, Wall>> entrySet;
        switch (direction) {
            // left
            case 1:
                // TODO: update difference getLoc().x - size, getLoc().x
                entrySet = vertical.subMap(new Point(getLoc().x - size - gap, 0), true,
                        new Point(getLoc().x, getLoc().y + size + gap), false).entrySet();
                for (Map.Entry<Point, Wall> entry : entrySet) {
                    Wall temp = entry.getValue();
                    if (temp.getLoc().y < getLoc().y + size + gap && temp.getEndLoc().y > getLoc().y - size - gap) {
//                        return getLoc().x - size - temp.getLoc().x;
//                        return true;
                        if ((!this.getName().equals("pacman") || !temp.getColor().equals("red")) && (!this.getName().equals("ghost") || !temp.getColor().equals("white"))) {
                            return true;
                        } else if (this.getName().equals("pacman") && temp.getColor().equals("red")) {
                            Pacman pacman = (Pacman) this;
                            setLoc(new Point(pacman.getPortals()[direction % 2] - getLoc().x, getLoc().y));
                        }
                    }
                }
                break;
            // up
            case 2:
                entrySet = horizontal.subMap(new Point(0, getLoc().y - gap - size), true,
                        new Point(getLoc().x + size + gap, getLoc().y - size), false).entrySet();
                for (Map.Entry<Point, Wall> entry : entrySet) {
                    Wall temp = entry.getValue();
                    if (temp.getLoc().x < getLoc().x + size + gap && temp.getEndLoc().x > getLoc().x - size - gap) {
                        //                            return -1;
                        if ((!this.getName().equals("pacman") || !temp.getColor().equals("red")) && (!this.getName().equals("ghost") || !temp.getColor().equals("white"))) {
                            return true;
                        } else if (this.getName().equals("pacman") && temp.getColor().equals("red")) {
                            Pacman pacman = (Pacman) this;
                            setLoc(new Point(getLoc().x, pacman.getPortals()[direction % 2] - getLoc().y));
                        }
                    }
                }
                break;
            // right
            case 3:
                entrySet = vertical.subMap(new Point(getLoc().x + size, 0), false,
                        new Point(getLoc().x + size + gap, getLoc().y + size + gap), true).entrySet();
                for (Map.Entry<Point, Wall> entry : entrySet) {
                    Wall temp = entry.getValue();
                    if (temp.getLoc().y < getLoc().y + size + gap && temp.getEndLoc().y > getLoc().y - size - gap) {
//                        return temp.getLoc().x - getLoc().x - size;
//                        return true;
                        if ((!this.getName().equals("pacman") || !temp.getColor().equals("red")) && (!this.getName().equals("ghost") || !temp.getColor().equals("white"))) {
                            return true;
                        } else if (this.getName().equals("pacman") && temp.getColor().equals("red")) {
                            Pacman pacman = (Pacman) this;
                            setLoc(new Point(pacman.getPortals()[direction % 2] - getLoc().x, getLoc().y));
                        }
                    }
                }
                break;
            // down
            case 4:
                entrySet = horizontal.subMap(new Point(0, getLoc().y + size), false,
                        new Point(getLoc().x + size + gap, getLoc().y + size + gap), true).entrySet();
                for (Map.Entry<Point, Wall> entry : entrySet) {
                    Wall temp = entry.getValue();
                    if (temp.getLoc().x < getLoc().x + size + gap && temp.getEndLoc().x > getLoc().x - size - gap) {
                        //                            return -1;
                        if ((!this.getName().equals("pacman") || !temp.getColor().equals("red")) && (!this.getName().equals("ghost") || !temp.getColor().equals("white"))) {
                            return true;
                        } else if (this.getName().equals("pacman") && temp.getColor().equals("red")) {
                            Pacman pacman = (Pacman) this;
                            setLoc(new Point(getLoc().x, pacman.getPortals()[direction % 2] - getLoc().y));
                        }
                    }
                }
                break;
            default:
                break;
        }
//        return -1;
        return false;
    }

}

