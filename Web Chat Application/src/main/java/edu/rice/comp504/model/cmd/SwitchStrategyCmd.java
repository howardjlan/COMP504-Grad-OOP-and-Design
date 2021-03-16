package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.factory.StrategyFactory;
import edu.rice.comp504.model.strategy.IUpdateStrategy;

public class SwitchStrategyCmd implements IPacmanCmd {
    private IUpdateStrategy strategyTo;

    /**
     * Constructor of the switch command.
     */
    public SwitchStrategyCmd(String strategyTo) {
        this.strategyTo = StrategyFactory.makeStrategyFactory().makeStrategy(strategyTo);
    }


    /**
     * Execute the command and set the ball to the desired strategy.
     * @param context The receiver paint object on which the command is executed.
     */
    @Override
    public void execute(APaintObj context) {
        if (context != null && context.getName().equals("ghost")) {
            Ghost ghost = ((Ghost) context);
            ((Ghost) context).setUpdateStrategy(strategyTo);
        }
    }

    /**
     * This function is only used for UpdateStateCmd, route to default execute.
     */
    @Override
    public void execute(APaintObj context, APaintObj pacman) {}
}
