package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.character.Pacman;
import edu.rice.comp504.model.item.AItem;


public class InteractCmd implements IPacmanCmd {
    private static InteractCmd singleton;

    private InteractCmd() {

    }

    /**
     * Only makes 1 update command.
     * @return The update command.
     */
    public static InteractCmd makeCmd() {
        if (singleton == null ) {
            singleton = new InteractCmd();
        }
        return singleton;
    }


    /**
     * Update the state of the paint object.
     *
     * @param context The paint object.
     */
    @Override
    public void execute(APaintObj context) {

    }

    /**
     * This function is only used for UpdateStateCmd, route to default execute.
     */
    @Override
    public void execute(APaintObj pac, APaintObj context) {
        if (context != null && pac.getName().equals("pacman")) {
            Pacman pacman = ((Pacman) pac);
            if (pacman.getDeadState() != -1) {
                return;
            }
            if (context.getName().equals("dot") || context.getName().equals("big dot") || context.getName().equals("fruit") || context.getName().equals("zoom")) {
                if (pacman.detectCollisionWithItem((AItem) context)) {
                    pacman.getCollideStrategy().collideState(context);
                }
            } else if (context.getName().equals("ghost")) {
                Ghost g = (Ghost) context;
                if (pacman.detectCollisionWithGhost(g)) {
                    if (!g.isDead() && !g.isFlashing()) {
                        // if ghost is at normal state
                        // pacman is dead
                        pacman.setDeadState();
                        // pacman.reset();
                    } else if (g.isFlashing() && !g.isDead()) {
                        // if ghost is at running away state
                        // ghost is dead
                        g.setDead(true);
                        g.setFlashing(false);
                        g.setFlashingTimer(0);
                        pacman.getCollideStrategy().collideState(context);
                    }
                }
            }
        }
    }
}
