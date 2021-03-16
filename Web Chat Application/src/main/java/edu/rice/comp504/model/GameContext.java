package edu.rice.comp504.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.rice.comp504.model.character.Ghost;
import edu.rice.comp504.model.character.Pacman;
import edu.rice.comp504.model.factory.StrategyFactory;
import edu.rice.comp504.model.item.*;
import edu.rice.comp504.model.level.GameLevel;
import edu.rice.comp504.model.strategy.IUpdateStrategy;
import edu.rice.comp504.model.strategy.collision.EatStrategy;
import edu.rice.comp504.model.strategy.collision.ICollideStrategy;
import edu.rice.comp504.model.wall.Wall;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * GameContext is the context of the pacman game. It stores all the game elements and game status.
 */
public class GameContext {
    private GameLevel levelInstance;
    private Pacman pacman;
    private List<Ghost> ghosts;

    // 1 for start, 0 for pause
    private int status;
    private int maxLives;
    private int currentScore;
    private int highestScore;
    private int eatenDots;
    private transient Date sTime;
    private transient Random random;
    private int levelCount;
    private int numberOfGhosts;
    private int numberOfFruits;
    private boolean isZoomAvailable;
    private int ghostScore = 200;
    private int nextFruitIndex = 0; // next fruit that is going to be activated
    private int fruitsActivated = 0; // the number of fruits that have been activated so far
    private final int fruitActiveTime = 10000; // fruit are active for 10000 ms (10s)
    private final int zoomEffectTime = 10000; // how long zoom item affects pacman
    private boolean inEffect = false;
    private int effectLeftTime = 0;
    private int scoreFactor = 1;
    private final int ghostFlashingTime = 10000; // how long ghost keeps flashing
    private final int maxGhosts = 4;
    private transient int[] portals = new int[2];

    /**
     * Constructor.
     * @param gameLevel Either level 1 or level 2
     * @param numberOfGhosts Number of ghosts
     * @param maxLives Number of lives of pacman
     * @param isZoomAvailable If zoom is available
     * @param highestScore Current highest score
     */
    public GameContext(int gameLevel, int numberOfGhosts, int maxLives, boolean isZoomAvailable, int highestScore) {
        levelCount = gameLevel;
        this.numberOfGhosts = numberOfGhosts;
        this.maxLives = maxLives;
        this.isZoomAvailable = isZoomAvailable;
        this.highestScore = highestScore;
        random = new Random();
    }

    /**
     * Initialize a new game with a level and settings.
     */
    public void init() {
        sTime = new Date();
        currentScore = 0;
        eatenDots = 0;
        ghostScore = 200;
        status = 1;
        portals = new int[2];
        fruitsActivated = 0;
        numberOfFruits = 0;
        nextFruitIndex = 0;
        // init pacman, ghosts and all items
        initLevel();
        createGhostsFromJson();
        createPacmanFromJson();
    }

    /**
     * Initialized the level.
     */
    private void initLevel() {
        JsonObject wallObject = null;
        JsonObject dotObject = null;
        JsonObject bigDotObject = null;
        JsonObject fruitObject = null;
        JsonObject fruitLocationsObject = null;
        if (levelCount == 1) {
            wallObject = readJsonFile("public/json/wall new.json");
            dotObject = readJsonFile("public/json/dots new.json");
            bigDotObject = readJsonFile("public/json/big dots.json");
            fruitObject = readJsonFile("public/json/fruits.json");
            fruitLocationsObject = readJsonFile("public/json/fruit locations.json");
        } else {
            wallObject = readJsonFile("public/json/wall 2 new.json");
            dotObject = readJsonFile("public/json/dots 2.json");
            bigDotObject = readJsonFile("public/json/big dots 2.json");
            fruitObject = readJsonFile("public/json/fruits 2.json");
            fruitLocationsObject = readJsonFile("public/json/fruit locations 2.json");
        }
        List<Wall> walls = parseWallObject(wallObject);
        List<Dot> dots = parseDotObject(dotObject);
        List<BigDot> bigDots = parseBigDotObject(bigDotObject);
        List<Fruit> fruits = parseFruitObject(fruitObject);
        HashMap<Point, Boolean> locations = parseFruitLocationsObject(fruitLocationsObject);
        levelInstance = new GameLevel(levelCount, walls, fruits, dots, bigDots, locations, null);
        if (isZoomAvailable) {
            createZoomItem();
        }
    }

    /**
     * Parse wall object from json format.
     */
    public List<Wall> parseWallObject(JsonObject wallObject) {
        //Parse wall object
        List<Wall> walls = new ArrayList<>();
        if (wallObject != null) {
            JsonArray jsonWalls = wallObject.get("wall").getAsJsonArray();
            for (JsonElement element: jsonWalls) {
                JsonArray wallStart = element.getAsJsonObject().get("start").getAsJsonArray();
                JsonArray wallEnd = element.getAsJsonObject().get("end").getAsJsonArray();
                String wallColor = element.getAsJsonObject().get("color").getAsString();
                Point start = new Point(wallStart.get(0).getAsInt(),wallStart.get(1).getAsInt());
                Point end = new Point(wallEnd.get(0).getAsInt(),wallEnd.get(1).getAsInt());
                if (start.x == end.x) {
                    int length = end.y - start.y;
                    Wall wall = new Wall(start,wallColor,length,end);
                    walls.add(wall);
                    if (wall.getColor().equals("red")) {
                        portals[1] += wall.getLoc().x;
                    }
                } else if (start.y == end.y) {
                    int length = end.y - start.y;
                    Wall wall = new Wall(start,wallColor,length,end);
                    walls.add(wall);
                    if (wall.getColor().equals("red")) {
                        portals[0] += wall.getLoc().y;
                    }
                }
            }
        }
        return walls;
    }

    /**
     * Parse dot objects from json format.
     */
    public List<Dot> parseDotObject(JsonObject dotObject) {
        //Parse dot object
        List<Dot> dots = new ArrayList<>();
        if (dotObject != null) {
            JsonArray jsonDots = dotObject.get("dots").getAsJsonArray();
            for (JsonElement element: jsonDots) {
                JsonArray pos = element.getAsJsonObject().get("pos").getAsJsonArray();
                int radius = element.getAsJsonObject().get("radius").getAsInt();
                Point loc = new Point(pos.get(0).getAsInt(),pos.get(1).getAsInt());
                Dot dot = new Dot(loc, "white", 10, radius);
                dots.add(dot);
            }
        }
        return dots;
    }

    /**
     * Parse big dot objects from json format.
     */
    public List<BigDot> parseBigDotObject(JsonObject bigDotObject) {
        //Parse big dot objects
        List<BigDot> bigDots = new ArrayList<>();
        if (bigDotObject != null) {
            JsonArray jsonBigDots = bigDotObject.get("bigDots").getAsJsonArray();
            for (JsonElement element: jsonBigDots) {
                JsonArray pos = element.getAsJsonObject().get("pos").getAsJsonArray();
                int radius = element.getAsJsonObject().get("radius").getAsInt();
                Point loc = new Point(pos.get(0).getAsInt(),pos.get(1).getAsInt());
                BigDot dot = new BigDot(loc, "white", 50, radius);
                bigDots.add(dot);
            }
        }
        return bigDots;
    }

    /**
     * Parse fruit objects from json format.
     */
    public List<Fruit> parseFruitObject(JsonObject fruitObject) {
        //Parse fruit object
        List<Fruit> fruits = new ArrayList<>();
        if (fruitObject != null) {
            JsonArray jsonFruits = fruitObject.get("fruit").getAsJsonArray();
            for (JsonElement element: jsonFruits) {
                JsonArray pos = element.getAsJsonObject().get("loc").getAsJsonArray();
                Point loc = new Point(pos.get(0).getAsInt(),pos.get(1).getAsInt());
                int score = element.getAsJsonObject().get("score").getAsInt();
                int fruitType = element.getAsJsonObject().get("fruitType").getAsInt();
                int timeLeft = element.getAsJsonObject().get("timeLeft").getAsInt();
                int size = element.getAsJsonObject().get("size").getAsInt();
                Fruit fruit = new Fruit(loc, score, size, fruitType, timeLeft);
                fruits.add(fruit);
                numberOfFruits++;
            }
        }
        return fruits;
    }

    /**
     * Parse fruit locations objects from json format.
     */
    public HashMap<Point, Boolean> parseFruitLocationsObject(JsonObject fruitLocationsObject) {
        //Parse fruit locations object
        HashMap<Point, Boolean> locations = new HashMap<>();
        if (fruitLocationsObject != null) {
            JsonArray jsonLocation = fruitLocationsObject.get("fruitLocations").getAsJsonArray();
            for (JsonElement element : jsonLocation) {
                JsonArray pos = element.getAsJsonObject().get("loc").getAsJsonArray();
                Point loc = new Point(pos.get(0).getAsInt(),pos.get(1).getAsInt());
                locations.put(loc, false);
            }

        }
        return locations;
    }

    /**
     * Parse ghost objects from json format.
     */
    public void createGhostsFromJson() {
        JsonObject ghostsObject = null;
        if (levelCount == 1) {
            ghostsObject = readJsonFile("public/json/ghosts.json");
        } else {
            ghostsObject = readJsonFile("public/json/ghosts2.json");
        }
        if (ghostsObject != null) {
            ghosts = new ArrayList<>();
            List<Integer> randoms = new ArrayList<>();
            while (randoms.size() < numberOfGhosts) { // generate numberOfGhosts random numbers between [0, maxGhosts)
                int num = random.nextInt(maxGhosts);
                if (!randoms.contains(num)) {
                    randoms.add(num);
                }
            }
            int id = 0;
            JsonArray ghostsJson = ghostsObject.get("ghosts").getAsJsonArray();
            for (JsonElement ele : ghostsJson) {
                JsonArray pos = ele.getAsJsonObject().get("loc").getAsJsonArray();
                JsonArray vel = ele.getAsJsonObject().get("vel").getAsJsonArray();
                String color = ele.getAsJsonObject().get("color").getAsString();
                String updateStrategyName = ele.getAsJsonObject().get("updateStrategy").getAsString();
                String collideStrategyName = ele.getAsJsonObject().get("collideStrategy").getAsString();
                int size = ele.getAsJsonObject().get("size").getAsInt();
                boolean isFlashing = ele.getAsJsonObject().get("isFlashing").getAsBoolean();
                boolean isDead = ele.getAsJsonObject().get("isDead").getAsBoolean();
                int flashingTimer = ele.getAsJsonObject().get("flashingTimer").getAsInt();
                int direction = ele.getAsJsonObject().get("direction").getAsInt();

                Ghost ghost = new Ghost(new Point(pos.get(0).getAsInt(), pos.get(1).getAsInt()), new Point(vel.get(0).getAsInt(), vel.get(1).getAsInt()),
                        color, getUpdateStrategy(updateStrategyName), getCollideStrategy(collideStrategyName),
                        size, isFlashing, isDead, flashingTimer, new Point(0, 0));
                if (randoms.contains(id)) {
                    ghosts.add(ghost);
                }
                id++;
            }
        }
    }

    /**
     * Parse pacman objects from json format.
     */
    public void createPacmanFromJson() {
        JsonObject pacmanObject = null;
        if (levelCount == 1) {
            pacmanObject = readJsonFile("public/json/pacman.json");
        } else {
            pacmanObject = readJsonFile("public/json/pacman2.json");
        }
        if (pacmanObject != null) {
            JsonArray pacmanJson = pacmanObject.get("pacman").getAsJsonArray();
            for (JsonElement ele : pacmanJson) {
                JsonArray pos = ele.getAsJsonObject().get("loc").getAsJsonArray();
                JsonArray vel = ele.getAsJsonObject().get("vel").getAsJsonArray();
                String updateStrategyName = ele.getAsJsonObject().get("updateStrategy").getAsString();
                String collideStrategyName = ele.getAsJsonObject().get("collideStrategy").getAsString();
                int size = ele.getAsJsonObject().get("size").getAsInt();
                int direction = ele.getAsJsonObject().get("direction").getAsInt();
                pacman = new Pacman(new Point(pos.get(0).getAsInt(), pos.get(1).getAsInt()), new Point(vel.get(0).getAsInt(), vel.get(1).getAsInt()),
                        getUpdateStrategy(updateStrategyName), getCollideStrategy(collideStrategyName),
                        size, direction, maxLives);
                pacman.setPortals(portals);
            }
        }
    }

    /**
     * Create zoom item.
     * @return a new zoom item
     */
    public Zoom createZoomItem() {
        return levelInstance.generateZoom();
    }

    /**
     * Get the update strategy.
     * @param name strategy name
     * @return update strategy.
     */
    private IUpdateStrategy getUpdateStrategy(String name) {
        StrategyFactory factory = StrategyFactory.makeStrategyFactory();
        return factory.makeStrategy(name);
    }

    /**
     * Get the collide strategy.
     * @param name strategy name
     * @return collide strategy.
     */
    private ICollideStrategy getCollideStrategy(String name) {
        if ("EatStrategy".equals(name)) {
            return EatStrategy.makeStrategy();
        }
        return null;
    }

    /**
     * Read json files from url.
     */
    private JsonObject readJsonFile(String url) {
        JsonParser parser = new JsonParser();
        try {
//            File file = new File(getClass().getClassLoader().getResource(url).getFile());
//            Object obj = parser.parse(new FileReader(file));
//            JsonObject jsonObject = (JsonObject)obj;

//            InputStream input = getClass().getResourceAsStream("/resources/" + url);
            InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(url);

//            BufferedReader streamReader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
//            StringBuilder responseStrBuilder = new StringBuilder();
//
//            String inputStr;
//            while ((inputStr = streamReader.readLine()) != null) {
//                responseStrBuilder.append(inputStr);
//            }
//            JsonObject jsonObject = (JsonObject)parser.parse(responseStrBuilder.toString());

            JsonObject jsonObject = (JsonObject)parser.parse(new InputStreamReader(input, "UTF-8"));

            input.close();
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Remove a dot.
     * @param dot dot needs to be removed
     * @param isEaten if the dot is eaten
     */
    public void removeDot(Dot dot, boolean isEaten) {
        levelInstance.removeDot(dot);
        if (isEaten) {
            eatenDots += 1;
            currentScore += (dot.getScore() * scoreFactor);
            highestScore = Math.max(currentScore, highestScore);
        }
    }

    /**
     * Remove a big dot.
     * @param dot big dot needs to be removed
     * @param isEaten if the big dot is eaten
     */
    public void removeBigDot(BigDot dot, boolean isEaten) {
        levelInstance.removeBigDot(dot);
        if (isEaten) {
            eatenDots += 1;
            currentScore += (dot.getScore() * scoreFactor);
            highestScore = Math.max(currentScore, highestScore);
            for (Ghost ghost : ghosts) {
                if (!ghost.isDead()) {
                    ghost.setFlashing(true);
                    ghost.setFlashingTimer(ghostFlashingTime);
                }
            }
        }
    }

    /**
     * Remove the zoom.
     * @param isEaten if the zoom is eaten
     */
    public void removeZoom(boolean isEaten) {
        levelInstance.removeZoom();
        if (isEaten) {
            scoreFactor = 2;
            inEffect = true;
            effectLeftTime = zoomEffectTime;
        }
    }

    /**
     * Remove a fruit.
     * @param fruit fruit needs to be removed
     * @param isEaten if the fruit is eaten
     */
    public void removeFruit(Fruit fruit, boolean isEaten) {
        levelInstance.removeFruit(fruit);
        if (isEaten) {
            currentScore += (fruit.getScore() * scoreFactor);
            highestScore = Math.max(currentScore, highestScore);
        }
        if (nextFruitIndex > 0) {
            nextFruitIndex--;
        }
    }

    /**
     * Active the next fruit to be display.
     */
    public void activateNextFruit() {
        Fruit fruit = levelInstance.getNextFruit(nextFruitIndex);
        if (fruit != null) {
            fruit.setTimeLeft(fruitActiveTime);
            fruit.setActive(true);
            nextFruitIndex++;
            fruitsActivated++;
        }
    }

    /**
     * Get the pacman.
     * @return the pacman.
     */
    public Pacman getPacman() {
        return pacman;
    }

    /**
     * Get the ghosts.
     * @return the ghosts.
     */
    public List<Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * Get the level object.
     * @return the level object.
     */
    public GameLevel getLevelInstance() {
        return levelInstance;
    }

    /**
     * Get the lives.
     * @return the lives.
     */
    public int getLives() {
        return pacman.getLeftLives();
    }

    /**
     * Get the current score.
     * @return the current score.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Set the current score.
     * @param currentScore the current score.
     */
    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    /**
     * Get the highest score.
     * @return the highest score.
     */
    public int getHighestScore() {
        return highestScore;
    }

    /**
     * Set the highest score.
     * @param highestScore the highest score.
     */
    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    /**
     * Get the game status.
     * @return the game status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the game status.
     * @param status the game status.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get the number of fruits.
     * @return the number of fruits.
     */
    public int getNumberOfFruits() {
        return numberOfFruits;
    }

    /**
     * Get the active fruits.
     * @return the active fruits.
     */
    public int getFruitsActivated() {
        return fruitsActivated;
    }

    /**
     * If the game is over.
     * @return if the game is over.
     */
    public boolean isGameOver() {
        return pacman.getLeftLives() == 0;
    }

    /**
     * If the zoom is available.
     * @return if the zoom is available.
     */
    public boolean isZoomAvailable() {
        return isZoomAvailable;
    }

    /**
     * Set the game settings.
     * @param gameLevel Game level.
     * @param numGhosts Number of ghosts.
     * @param numLives  Number of lives.
     * @param isZoomSet If the zoom is set.
     */
    public void setGameParameters(int gameLevel, int numGhosts, int numLives, boolean isZoomSet) {
        levelCount = gameLevel;
        numberOfGhosts = numGhosts;
        maxLives = numLives;
        isZoomAvailable = isZoomSet;
        init();
    }

    /**
     * Get the ghost score.
     * @return Ghost score.
     */
    public int getGhostScore() {
        return ghostScore;
    }

    /**
     * Set the ghost score.
     * @param ghostScore Ghost score.
     */
    public void setGhostScore(int ghostScore) {
        this.ghostScore = ghostScore;
    }

    /**
     * Decrease the effect time.
     * @return If the effect time runs out.
     */
    public boolean decreaseEffectTime() {
        effectLeftTime -= DispatcherAdapter.updatePeriod;
        if (effectLeftTime <= 0) {
            effectLeftTime = 0;
            return true;
        } else {
            return false;
        }
    }

    /**
     * If the zoom is in effect.
     * @return If the zoom is in effect.
     */
    public boolean isZoomInEffect() {
        return inEffect;
    }

    /**
     * Reset the score factor for zoom.
     */
    public void resetScoreFactor() {
        inEffect = false;
        scoreFactor = 1;
    }
}
