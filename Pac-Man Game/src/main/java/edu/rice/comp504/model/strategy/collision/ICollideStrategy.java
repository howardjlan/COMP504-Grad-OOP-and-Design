package edu.rice.comp504.model.strategy.collision;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.DispatcherAdapter;
import edu.rice.comp504.model.GameContext;
import edu.rice.comp504.model.character.ACharacter;

import java.beans.PropertyChangeSupport;

/**
 * An interface for ACharacter strategies that determine the ACharacter collision behavior with others.
 */
public interface ICollideStrategy {
    /**
     * The name of the strategy.
     * @return strategy name
     */
    String getName();

    /**
     * Update the state of the ACharacter.
     * @param other The collide object.
     */
    void collideState(APaintObj other);
}
