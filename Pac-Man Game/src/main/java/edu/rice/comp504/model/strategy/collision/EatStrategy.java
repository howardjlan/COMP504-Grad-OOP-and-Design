package edu.rice.comp504.model.strategy.collision;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.item.BigDot;
import edu.rice.comp504.model.item.Dot;
import edu.rice.comp504.model.item.Fruit;
import edu.rice.comp504.model.item.Zoom;


public class EatStrategy implements ICollideStrategy {
    private static ICollideStrategy singleton;

    /**
     * Make a eat strategy.  There is only one (singleton).
     * @return A eat strategy
     */
    public static ICollideStrategy makeStrategy() {
        if (singleton == null) {
            singleton = new EatStrategy();
        }
        return singleton;
    }

    /**
     * Get the strategy name.
     * @return strategy name
     */
    @Override
    public String getName() {
        return "eat";
    }

    /**
     * Collide the character in the character world.
     * @param other The character to update
     */
    @Override
    public void collideState(APaintObj other) {
        if (other.getName().equals("dot")) {
            Dot dot = (Dot) other;
            dot.setEaten(true);
        } else if (other.getName().equals("big dot")) {
            BigDot dot = (BigDot) other;
            dot.setEaten(true);
        } else if (other.getName().equals("fruit")) {
            Fruit fruit = (Fruit) other;
            fruit.setEaten(true);
        } else if (other.getName().equals("zoom")) {
            Zoom zoom = (Zoom) other;
            zoom.setEaten(true);
        }
    }
}
