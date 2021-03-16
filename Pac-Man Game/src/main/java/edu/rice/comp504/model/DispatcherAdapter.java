package edu.rice.comp504.model;

import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.character.Pacman;
import edu.rice.comp504.model.cmd.IPacmanCmd;
import edu.rice.comp504.model.cmd.InteractCmd;
import edu.rice.comp504.model.cmd.SwitchStrategyCmd;
import edu.rice.comp504.model.cmd.UpdateStateCmd;
import edu.rice.comp504.model.factory.CmdFactory;
import edu.rice.comp504.model.factory.StrategyFactory;
import edu.rice.comp504.model.item.*;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * The dispatch adapter provides the interface between the controller and model.
 */
public class DispatcherAdapter {

    private GameContext context;
    private PropertyChangeSupport pcs;
    private TreeMap<Point,Wall> horizontalWall;
    private TreeMap<Point,Wall> verticalWall;
    public static final int updatePeriod = 60;
    private int timeElapsed = 0;
    private int endTime;

    /**
     * Constructor.
     */
    public DispatcherAdapter() {
        context = new GameContext(1, 4, 3, true, 0);
        pcs = new PropertyChangeSupport(this);
        horizontalWall = new TreeMap<>(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o1.y - o2.y == 0 ? o1.x - o2.x : o1.y - o2.y;
            }
        });
        verticalWall = new TreeMap<>(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return o1.x - o2.x == 0 ? o1.y - o2.y : o1.x - o2.x;
            }
        });
    }

    /**
     * Initialize the game.
     * @return PacmanContext record information about the game
     */
    public GameContext init() {
        horizontalWall.clear();
        verticalWall.clear();
        context.init();
        for (Wall wall : context.getLevelInstance().getWalls()) {
            Point start = wall.getLoc();
            Point end = wall.getEndLoc();
            if (start.x == end.x) {
                this.verticalWall.put(wall.getLoc(), wall);
            } else if (start.y == end.y) {
                this.horizontalWall.put(wall.getLoc(), wall);
            }
        }
        this.context.getPacman().setHorizontalWall(horizontalWall);
        this.context.getPacman().setVerticalWall(verticalWall);
        pcs.addPropertyChangeListener("characters", context.getPacman());
        for (Ghost ghost : context.getGhosts()) {
            pcs.addPropertyChangeListener("characters", ghost);
        }
        for (Dot dot : context.getLevelInstance().getDots()) {
            pcs.addPropertyChangeListener("items", dot);
        }
        for (BigDot dot : context.getLevelInstance().getBigDots()) {
            pcs.addPropertyChangeListener("items", dot);
        }
        for (Fruit fruit : context.getLevelInstance().getFruits()) {
            pcs.addPropertyChangeListener("items", fruit);
        }
        if (context.isZoomAvailable()) {
            pcs.addPropertyChangeListener("items", context.getLevelInstance().getZoom());
        }
        endTime = (int) (((float) 1000 / updatePeriod) * 100);
        timeElapsed = 0;
        return context;
    }

    /**
     * Pacman eat a dot.
     * @param dot The eaten dot.
     */
    public void eatDot(Dot dot) {
        pcs.removePropertyChangeListener(dot);
        context.removeDot(dot, true);
    }

    /**
     * Pacman eat a big dot.
     * @param dot The eaten big dot.
     */
    public void eatBigDot(BigDot dot) {
        pcs.removePropertyChangeListener(dot);
        context.removeBigDot(dot, true);
    }

    /**
     * Pacman eat a fruit.
     * @param fruit The eaten fruit.
     */
    public void eatFruit(Fruit fruit) {
        pcs.removePropertyChangeListener(fruit);
        context.removeFruit(fruit, true);
    }

    /**
     * Pacman eat a zoom.
     * @param zoom The eaten zoom.
     */
    public void eatZoom(Zoom zoom) {
        pcs.removePropertyChangeListener(zoom);
        context.removeZoom(true);
    }

    /**
     * Pacman eat a ghost.
     * @param ghost The eaten ghost.
     */
    public void eatGhost(Ghost ghost) {
        context.setCurrentScore(context.getCurrentScore() + context.getGhostScore());
        context.setHighestScore(Math.max(context.getCurrentScore(), context.getHighestScore()));
        context.setGhostScore(context.getGhostScore() * 2);
    }

    /**
     * Remove the items that are not in effect time.
     * @param item The out-of-time item.
     */
    public void removeTimeOutItem(AItem item) {
        pcs.removePropertyChangeListener("items", item);
        if (item.getName().equals("fruit")) {
            context.removeFruit((Fruit) item, false);
        } else if (item.getName().equals("zoom")) {
            context.removeZoom(false);
        }
    }

    /**
     * Call the update method on all the character observers to update their position in the pacman world.
     */
    public GameContext updateCanvas(String keyCode) {
        if (context.getStatus() == 1) {
            int dir = Integer.parseInt(keyCode);
            Pacman pacman = context.getPacman();
            pacman.setNextDirection(dir);
            if (context.isZoomInEffect() && context.decreaseEffectTime()) {
                context.resetScoreFactor();
            }
            IPacmanCmd updateStateCmd = CmdFactory.makeCmdFactory().makeCmd("update state");
            pcs.firePropertyChange("characters", pacman, updateStateCmd);
            // TODO: is this useful?
//            pcs.firePropertyChange("items", null, updateStateCmd);
            IPacmanCmd interactCmd = CmdFactory.makeCmdFactory().makeCmd("interact");
            pcs.firePropertyChange("characters", pacman, interactCmd);
            pcs.firePropertyChange("items", pacman, interactCmd);
            charactersAction();
            for (Iterator<Dot> it = context.getLevelInstance().getDots().iterator(); it.hasNext();) {
                Dot dot = it.next();
                if (dot.isEaten()) {
                    it.remove();
                    eatDot(dot);
                }
            }
            for (Iterator<BigDot> it = context.getLevelInstance().getBigDots().iterator(); it.hasNext();) {
                BigDot dot = it.next();
                if (dot.isEaten()) {
                    it.remove();
                    eatBigDot(dot);
                }
            }
            for (Iterator<Fruit> it = context.getLevelInstance().getFruits().iterator(); it.hasNext();) {
                Fruit fruit = it.next();
                if (fruit.decreaseLeftTime() && fruit.getActive()) {
                    it.remove();
                    removeTimeOutItem(fruit);
                } else {
                    if (fruit.isEaten()) {
                        it.remove();
                        eatFruit(fruit);
                    }
                }
            }
            if (context.getLevelInstance().getZoom() != null && context.getLevelInstance().getZoom().isEaten()) {
                eatZoom(context.getLevelInstance().getZoom());
            }
            if (context.getLevelInstance().getZoom() != null && context.getLevelInstance().getZoom().decreaseLeftTime()) {
                removeTimeOutItem(context.getLevelInstance().getZoom());
            }
            if (endTime / context.getNumberOfFruits() == timeElapsed / (context.getFruitsActivated() + 1)) {
                context.activateNextFruit();
            }
            timeElapsed++;
        }
        return context;
    }

    /**
     * Extra action for characters.
     */
    private void charactersAction() {
        for (Ghost ghost : context.getGhosts()) {
            IPacmanCmd switchCmd = null;
            if (ghost.isFlashing() && ghost.getFlashingTimer() <= 0) {
                ghost.setFlashing(false);
                switchCmd = resetGhostStrategy(ghost, switchCmd);
                context.setGhostScore(200);
            } else if ((ghost.getLoc().equals(ghost.getOriginalLoc()) && ghost.isDead())) {
                switchCmd = resetGhostStrategy(ghost, switchCmd);
                ghost.setDead(false);
            } else if (ghost.isDead()) {
                if (!ghost.getUpdateStrategy().getName().equals("return to base")) {
                    switchCmd = new SwitchStrategyCmd("ReturnToBase");
                    eatGhost(ghost);
                }
            } else if (ghost.isFlashing()) {
                switchCmd = new SwitchStrategyCmd("RunAway");
            }
            if (switchCmd != null) {
                switchCmd.execute(ghost);
            }
        }
    }

    /**
     * Reset the ghost strategy based on ghost's color.
     * @param ghost The ghost object.
     * @param switchCmd Switch Command.
     * @return The Switch Command.
     */
    private IPacmanCmd resetGhostStrategy(Ghost ghost, IPacmanCmd switchCmd) {
        switch (ghost.getColor()) {
            case "red":
                switchCmd = new SwitchStrategyCmd("AggressiveChase");
                break;
            case "blue":
                switchCmd = new SwitchStrategyCmd("PatrolChase");
                break;
            case "orange":
                switchCmd = new SwitchStrategyCmd("RandomWalk");
                break;
            case "pink":
                switchCmd = new SwitchStrategyCmd("AmbushChase");
                break;
            default:
                break;
        }
        return switchCmd;
    }

    /**
     * Remove all PropertyChangeListener.
     */
    public void removeAll() {
        for (PropertyChangeListener pcl : pcs.getPropertyChangeListeners()) {
            pcs.removePropertyChangeListener(pcl);
        }
    }

    /**
     * Set game settings.
     * @param gameLevel The game level.
     * @param numGhosts Number of ghosts.
     * @param numLives Number of lives.
     * @param isZoomSet If zoom is set.
     */
    public void setGameParameters(int gameLevel, int numGhosts, int numLives, boolean isZoomSet) {
        context.setGameParameters(gameLevel, numGhosts, numLives, isZoomSet);
    }

    /**
     * Get game context.
     * @return The game context.
     */
    public GameContext getContext() {
        return context;
    }
}
