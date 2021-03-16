package edu.rice.comp504.model.strategy.chase;

import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.character.Pacman;
import edu.rice.comp504.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * The random walk strategy causes the ghost to travel randomly.
 */
public class RandomWalkStrategy implements IChaseStrategy {
    private static IUpdateStrategy singleton;

    /**
     * Make a patrol strategy.  There is only one (singleton).
     * @return A patrol strategy
     */
    public static IUpdateStrategy makeStrategy() {
        if (singleton == null) {
            singleton = new RandomWalkStrategy();
        }

        return singleton;
    }

    /**
     * Update the character state in the character world.
     * @param context The character to update
     */
    @Override
    public void updateState(ACharacter context) {

    }

    /**
     * Update the paintobj state in the paintobj world.
     * @param pacman The pacman information, if needed
     * @param context The paintobj to update
     */
    @Override
    public void updateState(ACharacter pacman, ACharacter context) {
        if (context == null || !context.getName().equals("ghost")) {
            return;
        }
        if (pacman.getName().equals("pacman")) {
            Pacman pacmanTemp = (Pacman) pacman;
            int newDirection = -1;
            Point currentLoc = context.getLoc();
            Point newLoc = currentLoc;

            int dir = context.getDirection();
            ArrayList<Integer> directions = new ArrayList<>();
            for (int i = 1; i < 5; i++) {
                if (!context.detectCollisionWithWalls(i, pacmanTemp.getHorizontalWall(), pacmanTemp.getVerticalWall())) {
                    directions.add(i);
                }
            }
            if (dir <= 0 || context.detectCollisionWithWalls(dir, pacmanTemp.getHorizontalWall(), pacmanTemp.getVerticalWall())) {
                newDirection = directions.get(new Random().nextInt(directions.size()));
                if (directions.size() > 1) {
                    while (newDirection == dir + 2 || newDirection == (dir + 2) % 4) {
                        newDirection = directions.get(new Random().nextInt(directions.size()));
                    }
                }
            } else if (directions.size() > 2) {
                newDirection = directions.get(new Random().nextInt(directions.size()));
                while (newDirection == dir + 2 || newDirection == (dir + 2) % 4) {
                    newDirection = directions.get(new Random().nextInt(directions.size()));
                }
            } else {
                newDirection = dir;
            }

            // Apply new direction to update the state. Invalid direction of -1 if cannot move (should not be possible).
            context.setDirection(newDirection);
            switch (newDirection) {
                case 1:
                    newLoc = new Point(currentLoc.x - context.getVel().x, currentLoc.y);
                    break;
                case 2:
                    newLoc = new Point(currentLoc.x, currentLoc.y - context.getVel().y);
                    break;
                case 3:
                    newLoc = new Point(currentLoc.x + context.getVel().x, currentLoc.y);
                    break;
                case 4:
                    newLoc = new Point(currentLoc.x, currentLoc.y + context.getVel().y);
                    break;
                default:
                    break;
            }

            context.setLoc(newLoc);
        }
    }

    /**
     * Get the strategy name.
     * @return strategy name
     */
    @Override
    public String getName() {
        return "random walk";
    }
}
