package edu.rice.comp504.model.strategy.chase;

import edu.rice.comp504.model.character.ACharacter;
import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.character.Pacman;
import edu.rice.comp504.model.strategy.IUpdateStrategy;

import java.awt.*;
import java.util.ArrayList;

/**
 * The ambush chase strategy causes the ghost to choose path to ambush pacman.
 */
public class AmbushChaseStrategy implements IChaseStrategy {
    private static IUpdateStrategy singleton;

    /**
     * Make a ambush strategy.  There is only one (singleton).
     * @return A ambush strategy
     */
    public static IUpdateStrategy makeStrategy() {
        if (singleton == null) {
            singleton = new AmbushChaseStrategy();
        }

        return singleton;
    }

    /**
     * Converts distance to direction. 1 - Left, 2 - Up, 3 - Right, 4 - Down.
     * @param distance Input distance.
     * @param isX If in X or Y direction.
     * @return Appropriate direction.
     */
    public Point distToDirection(int distance, boolean isX) {
        if (isX) {
            if (distance > 0) {
                return new Point(3, 1);
            } else {
                return new Point(1, 3);
            }
        } else {
            if (distance > 0) {
                return new Point(4, 2);
            } else {
                return new Point(2, 4);
            }
        }
    }

    /**
     * Update the character state in the character world.
     * @param context The character to update
     */
    @Override
    public void updateState(ACharacter context) {}

    /**
     * Update the paintobj state in the paintobj world.
     * @param pacman The pacman information, if needed
     * @param context The paintobj to update
     */
    @Override
    public void updateState(ACharacter pacman, ACharacter context) {
        if (context != null) {
            if (pacman.getName().equals("pacman")) {
                Pacman pacmanTemp = (Pacman) pacman;
                if (context.getName().equals("ghost")) {
                    Point currentLoc = context.getLoc();
                    Point newLoc = currentLoc;
                    Point pacmanLoc = pacman.getLoc();
                    int distPerStep = (int) Math.floor(2 * pacman.getSize() / pacman.getVel().x);
                    int stepsToPredict = 7;

                    switch (pacman.getDirection()) {
                        case 1:
                            pacmanLoc = new Point(pacmanLoc.x - stepsToPredict * distPerStep * pacman.getVel().x, pacmanLoc.y);
                            break;
                        case 2:
                            pacmanLoc = new Point(pacmanLoc.x, pacmanLoc.y - stepsToPredict * distPerStep * pacman.getVel().y);
                            break;
                        case 3:
                            pacmanLoc = new Point(pacmanLoc.x + stepsToPredict * distPerStep * pacman.getVel().x, pacmanLoc.y);
                            break;
                        case 4:
                            pacmanLoc = new Point(pacmanLoc.x, pacmanLoc.y + stepsToPredict * distPerStep * pacman.getVel().y);
                            break;
                        default:
                            break;
                    }

                    Point distance = new Point(pacmanLoc.x - currentLoc.x, pacmanLoc.y - currentLoc.y);

                    ArrayList<Integer> directions = new ArrayList<>();
                    int currentDir = context.getDirection();
                    int newDirection = 0;
                    int backupDirection = 0;
                    Point xDir = distToDirection(distance.x, true);
                    Point yDir = distToDirection(distance.y, false);

                    // Checks whether to approach Pacman from x or y axis first. Then the other directions are prioritized according to which are more likely to lead to Pacman.
                    if (Math.abs(distance.x) >= Math.abs(distance.y)) {
                        directions.add(xDir.x);
                        directions.add(yDir.x);
                        directions.add(yDir.y);
                        directions.add(xDir.y);
                    } else {
                        directions.add(yDir.x);
                        directions.add(xDir.x);
                        directions.add(xDir.y);
                        directions.add(yDir.y);
                    }

                    boolean foundFirstDir = false;
                    for (Integer dir : directions) {
                        if (!context.detectCollisionWithWalls(dir, pacmanTemp.getHorizontalWall(), pacmanTemp.getVerticalWall())) {
                            if (foundFirstDir) {
                                backupDirection = dir;
                                break;
                            } else {
                                newDirection = dir;
                                foundFirstDir = true;
                            }
                        }
                    }

                    /**
                     * Apply new direction to update the state. Invalid direction of 0 if cannot move (should not be possible).
                     * If the new direction is opposite of the last direction, a backup direction exists, the choose
                     * his backupdirection instead of going backwards. This avoids Ghosts looping indefinitely or getting
                     * stuck in dead ends, since they cannot double back unless they are forced to or need to chase pacman
                     * in a different direction.
                     **/
                    if ((newDirection + 1) % 4 + 1 == currentDir && backupDirection != 0) {
                        newDirection = backupDirection;
                    }
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

                    // Update ghost state.
                    context.setLoc(newLoc);
                    ((Ghost) context).setPrevDir(currentDir);
                }
            }
        }
    }


    /**
     * Get the strategy name.
     * @return strategy name
     */
    @Override
    public String getName() {
        return "ambush";
    }
}
