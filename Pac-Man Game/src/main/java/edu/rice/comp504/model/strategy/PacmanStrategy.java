package edu.rice.comp504.model.strategy;

import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.character.Pacman;

import java.awt.*;

/**
 * The pacman strategy causes an pacman to move.
 */
public class PacmanStrategy implements IUpdateStrategy {
    private static IUpdateStrategy singleton;

    /**
     * Constructor.
     */
    private PacmanStrategy() {

    }

    /**
     * Make a straight strategy.  There is only one (singleton).
     * @return A straight strategy
     */
    public static IUpdateStrategy makeStrategy() {
        if (singleton == null) {
            singleton = new PacmanStrategy();
        }

        return singleton;
    }

    /**
     * Get the strategy name.
     * @return strategy name
     */
    @Override
    public String getName() {
        return "pacman";
    }

    /**
     * Update the character state in the character world.
     * @param context The character to update
     */
    @Override
    public void updateState(ACharacter context) {
        if (context.getName().equals("pacman")) {
            Pacman pacman = (Pacman) context;
            if (pacman.getDeadState() != -1) {
                return;
            }
            Point currVel = pacman.getVel();
            Point newLoc = pacman.getLoc();
            int direction = pacman.getDirection();
            int nextDir = pacman.getNextDirection();
            boolean isCollide = true ;
            if (direction != nextDir && nextDir <= 4 && nextDir >= 1) {
                isCollide = pacman.detectCollisionWithWalls(nextDir, pacman.getHorizontalWall(), pacman.getVerticalWall());
                //Could change the direction
                if (!isCollide) {
                    pacman.setDirection(nextDir);
                    direction = nextDir;
                } else {
                    isCollide = pacman.detectCollisionWithWalls(direction, pacman.getHorizontalWall(), pacman.getVerticalWall());
                }
            } else {
                isCollide = pacman.detectCollisionWithWalls(direction, pacman.getHorizontalWall(), pacman.getVerticalWall());
            }
            //No collision
            Point currLoc = pacman.getLoc();
            if (!isCollide) {
                switch (direction) {
                    case 1:
                        newLoc = new Point(currLoc.x - currVel.x, currLoc.y);
                        break;
                    case 2:
                        newLoc = new Point(currLoc.x,currLoc.y - currVel.y);
                        break;
                    case 3:
                        newLoc = new Point(currLoc.x + currVel.x, currLoc.y);
                        break;
                    case 4:
                        newLoc = new Point(currLoc.x, currLoc.y + currVel.y);
                        break;
                    default:
                        return;
                }
                pacman.setLoc(newLoc);
            }
        }
    }

    /**
     * Update the paintobj state in the paintobj world.
     * @param pacman The pacman information, if needed
     * @param context The paintobj to update
     */
    @Override
    public void updateState(ACharacter pacman, ACharacter context) {
        updateState(context);
    }
}


