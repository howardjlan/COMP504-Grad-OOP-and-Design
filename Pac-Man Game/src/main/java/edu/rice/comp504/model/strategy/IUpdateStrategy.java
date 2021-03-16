package edu.rice.comp504.model.strategy;

import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.util.TreeMap;

/**
 * The IUpdateStrategy interface is used to determine the behavior of a character in the canvas over time.
 */
public interface IUpdateStrategy {

    /**
     * Update the state the character.
     * @param context The character to apply the strategy to.
     */
    public void updateState(ACharacter context);


    /**
     * Update the state the character.
     * @param pacman The pacman character to compare to.
     * @param context The character to apply the strategy to.
     */
    public void updateState(ACharacter pacman, ACharacter context);

    /**
     * Get the name of the strategy.
     * @return The strategy name.
     */
    public String getName();
}
