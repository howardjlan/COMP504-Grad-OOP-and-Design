package edu.rice.comp504.model.wall;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.cmd.IPacmanCmd;
import edu.rice.comp504.model.item.AItem;
import edu.rice.comp504.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * A wall at a location within the canvas.
 */
public class Wall extends APaintObj {
    private Point endLoc;
    private int length;
    private String color;

    /**
     * Constructor for Wall.
     * @param loc The wall starting location.
     * @param color The wall color.
     * @param length The vertical length of the wall.
     * @param endLoc The wall end location.
     */
    public Wall(Point loc, String color, int length, Point endLoc) {
        super("wall", loc);
        this.length = length;
        this.endLoc = endLoc;
        this.color = color;
    }

    /**
     * Get the color of the wall.
     * @return The color of wall.
     */
    public String getColor() {
        return color;
    }

    /**
     * Set the color of the wall.
     * @param color The color of wall.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Get the end location of wall.
     * @return The wall end location.
     */
    public Point getEndLoc() {
        return endLoc;
    }
}
