package edu.rice.comp504.model.level;

import edu.rice.comp504.model.item.*;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * The game level object that contains level information.
 */
public class GameLevel {
    private int levelCount;
    private List<Wall> walls;
    private List<Fruit> fruits;
    private HashMap<Point, Boolean> fruitLocations;
    private List<Dot> dots;
    private List<BigDot> bigDots;
    private Zoom zoom;

    /**
     * Instatiates ALevel abstract class.
     * @param walls wall list.
     * @param fruits fruit list.
     * @param dots dot list.
     * @param bigDots big dot list.
     */
    public GameLevel(int levelCount, List<Wall> walls, List<Fruit> fruits, List<Dot> dots, List<BigDot> bigDots,
                     HashMap<Point, Boolean> fruitLocations, Zoom zoom) {
        this.levelCount = levelCount;
        this.walls = walls;
        this.fruits = fruits;
        this.dots = dots;
        this.bigDots = bigDots;
        this.fruitLocations = fruitLocations;
        this.zoom = zoom;
    }

    /**
     * Get all the walls.
     * @return All the walls.
     */
    public List<Wall> getWalls() {
        return walls;
    }

    /**
     * Get all the fruits.
     * @return All the fruits.
     */
    public List<Fruit> getFruits() {
        return fruits;
    }

    /**
     * Get the next fruits.
     * @return The next fruits.
     */
    public Fruit getNextFruit(int index) {
        if (!fruits.isEmpty() && index < fruits.size()) {
            return fruits.get(index);
        } else {
            return null;
        }
    }

    /**
     * Remove a fruit.
     * @param fruit The fruit object.
     */
    public void removeFruit(Fruit fruit) {
        this.fruits.remove(fruit);
    }

    /**
     * Get all the dots.
     * @return All the dots.
     */
    public List<Dot> getDots() {
        return dots;
    }

    /**
     * Remove a dots.
     */
    public void removeDot(Dot dot) {
        this.dots.remove(dot);
    }

    /**
     * Get all the big dots.
     * @return All the big dots.
     */
    public List<BigDot> getBigDots() {
        return bigDots;
    }

    /**
     * Remove a big dots.
     */
    public void removeBigDot(BigDot bigDot) {
        this.bigDots.remove(bigDot);
    }

    /**
     * Remove the zoom.
     */
    public void removeZoom() {
        zoom = null;
    }

    /**
     * Get the zoom.
     * @return The zoom object.
     */
    public Zoom getZoom() {
        return zoom;
    }

    /**
     * Generate a zoom object.
     * @return the zoom object.
     */
    public Zoom generateZoom() {
        Point loc = getRandomDotLocation();
        zoom = new Zoom("zoom", loc, null, 30000);
        return zoom;
    }

    /**
     * Get a random available location.
     * @return the random location.
     */
    public Point getRandomDotLocation() {
        int size = dots.size();
        int id = new Random().nextInt(size);
        return dots.get(id).getLoc();
    }
}
