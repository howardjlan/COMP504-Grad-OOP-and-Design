package edu.rice.comp504.model.cmd;

import edu.rice.comp504.model.APaintObj;
import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.util.TreeMap;

/**
 * The IPacmanCmd is an interface used to pass commands to objects in the Pacman.  The
 * character must execute the command.
 */
public interface IPacmanCmd {

    /**
     * Execute the command.
     * @param context The receiver paintobj on which the command is executed.
     */
    public void execute(APaintObj context);

    /**
     * Execute the command.
     * @param context The receiver paintobj on which the command is executed.
     */
    public void execute(APaintObj pacman, APaintObj context);
}
