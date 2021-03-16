package edu.rice.comp504.model.factory;

import edu.rice.comp504.model.strategy.IUpdateStrategy;
import edu.rice.comp504.model.strategy.PacmanStrategy;
import edu.rice.comp504.model.strategy.chase.AggressiveChaseStrategy;
import edu.rice.comp504.model.strategy.chase.AmbushChaseStrategy;
import edu.rice.comp504.model.strategy.chase.PatrolChaseStrategy;
import edu.rice.comp504.model.strategy.chase.RandomWalkStrategy;
import edu.rice.comp504.model.strategy.retreat.ReturnToBaseStrategy;
import edu.rice.comp504.model.strategy.runaway.RunAwayStrategy;

/**
 * Strategy factory helps to create a strategy.
 */
public class StrategyFactory {

    public static StrategyFactory singleton;

    /**
     * Only makes 1 eat strategy factory.
     * @return The eat strategy factory.
     */
    public static StrategyFactory makeStrategyFactory() {
        if (singleton == null ) {
            singleton = new StrategyFactory();
        }
        return singleton;
    }

    /**
     * Make a strategy based on input type.
     * @param type The type of the strategy.
     * @return The corresponding strategy.
     */
    public IUpdateStrategy makeStrategy(String type) {
        IUpdateStrategy strategy = null;
        switch (type) {
            case "RunAway":
                strategy = RunAwayStrategy.makeStrategy();
                break;
            case "ReturnToBase":
                strategy = ReturnToBaseStrategy.makeStrategy();
                break;
            case "AggressiveChase":
                strategy = AggressiveChaseStrategy.makeStrategy();
                break;
            case "AmbushChase":
                strategy = AmbushChaseStrategy.makeStrategy();
                break;
            case "PatrolChase":
                strategy = PatrolChaseStrategy.makeStrategy();
                break;
            case "RandomWalk":
                strategy = RandomWalkStrategy.makeStrategy();
                break;
            case "PacmanStrategy":
                strategy = PacmanStrategy.makeStrategy();
                break;
            default:
                break;
        }
        return strategy;
    }


}
