package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.character.Pacman;



/**
 * The UpdateStateCmd will possibly update either the paintobj location or attribute (color).
 */
public class UpdateStateCmd implements IPacmanCmd {
    private static UpdateStateCmd singleton;

    private UpdateStateCmd() {
    }

    /**
     * Only makes 1 update command.
     * @return The update command.
     */
    public static UpdateStateCmd makeCmd() {
        if (singleton == null ) {
            singleton = new UpdateStateCmd();
        }
        return singleton;
    }

    /**
     * Update the state of the paint object.
     * @param context The paint object.
     */
    @Override
    public void execute(APaintObj context) {

    }

    /**
     * This function is only used for UpdateStateCmd.
     */
    @Override
    public void execute(APaintObj pacman, APaintObj context) {
        if (context != null) {
            if (pacman.getName().equals("pacman")) {
                Pacman pacmanTemp = (Pacman) pacman;
                if (context.getName().equals("pacman")) {
                    ACharacter character = ((ACharacter) context);
                    character.getUpdateStrategy().updateState(pacmanTemp, character);
                } else if (context.getName().equals("ghost")) {
                    ACharacter character = ((ACharacter) context);
                    ACharacter pac = ((ACharacter) pacman);
                    Pacman pacmanObj = (Pacman) pac;
                    if (pacmanObj.getDeadState() == -1) {
                        character.getUpdateStrategy().updateState(pacmanTemp, character);
                    } else if (pacmanObj.getDeadState() == 12) {
                        Ghost ghost = (Ghost) context;
                        ghost.setStartLoc();
                    }


                }
            }
        }
    }
}
