package edu.rice.comp504.model.factory;


import edu.rice.comp504.model.cmd.IPacmanCmd;
import edu.rice.comp504.model.cmd.InteractCmd;
import edu.rice.comp504.model.cmd.UpdateStateCmd;

/**
 * Command factory helps to create a command.
 */
public class CmdFactory {
    private static CmdFactory singleton;


    /**
     * Only makes 1 command factory.
     * @return The command factory.
     */
    public static CmdFactory makeCmdFactory() {
        if (singleton == null ) {
            singleton = new CmdFactory();
        }
        return singleton;
    }

    private CmdFactory() {
    }

    /**
     * Make a command based on input type.
     * @param type The type of the command.
     * @return The corresponding command.
     */
    public IPacmanCmd makeCmd(String type) {
        IPacmanCmd command;
        switch (type) {
            case "update state":
                command = UpdateStateCmd.makeCmd();
                break;
            case "interact":
                command = InteractCmd.makeCmd();
                break;
            default:
                command = null;
                break;
        }
        return command;
    }
}

