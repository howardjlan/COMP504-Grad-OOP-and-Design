package edu.rice.comp504.model.item;

import java.awt.*;

public class BigDot extends AItem {
    private int radius;


    /**
     * Constructor.
     *
     * @param loc   The location of the Dot on the canvas
     * @param color The Dot color
     */
    public BigDot(Point loc, String color, int score, int radius) {
        super("big dot", loc, color, score);
        this.radius = radius;
    }

    /**
     * Get the AItem radius.
     * @return AItem radius
     */
    public int getRadius() {
        return radius;
    }
}
