package edu.rice.comp504.controller;

import com.google.gson.Gson;
import edu.rice.comp504.model.DispatcherAdapter;

import java.util.concurrent.ConcurrentHashMap;

import static spark.Spark.*;

/**
 * The shape world controllers communicates to the model make and update paint object requests from the view and
 * communicates to the view new paint objects and updates to the existing paint objects.
 */
public class PacmanController {

    /**
     * Main entry point into the program.
     * @param args Program arguments normally passed to main on the command line
     */
    public static void main(String[] args) {
        staticFiles.location("/public");
        port(getHerokuAssignedPort());
        Gson gson = new Gson();
        // Map of DispatchAdapter for multiple users and pages
        ConcurrentHashMap<String, DispatcherAdapter> map = new ConcurrentHashMap<>();

        post("/update", (request, response) -> {
            String uuid = request.queryParams("uuid");
            if (uuid == null) {
                return "Init first";
            }
            DispatcherAdapter dis = null;
            if (map.containsKey(uuid)) {
                dis = map.get(uuid);
            } else {
                return null;
            }
            return gson.toJson(dis.updateCanvas(request.queryParams("keyCode")));
        });

        get("/init", (request, response) -> {
            String uuid = request.queryParams("uuid");
            if (uuid == null) {
                return "Init first";
            }
            DispatcherAdapter dis = null;
            if (map.containsKey(uuid)) {
                dis = map.get(uuid);
            } else {
                dis = new DispatcherAdapter();
                map.put(uuid, dis);
            }
            return gson.toJson(dis.init());
        });

        get("/clear", (request, response) -> {
            String uuid = request.queryParams("uuid");
            if (uuid == null) {
                return "Init first";
            }
            DispatcherAdapter dis = null;
            if (map.containsKey(uuid)) {
                dis = map.get(uuid);
            } else {
                return null;
            }
            dis.removeAll();
            return "removed all balls and inner walls in paintobj world";
        });


        post("/setGameParameters", (request, response) -> {
            String uuid = request.queryParams("uuid");
            if (uuid == null) {
                return "Init first";
            }
            DispatcherAdapter dis = null;
            if (map.containsKey(uuid)) {
                dis = map.get(uuid);
            } else {
                return null;
            }
            int numGhosts = Integer.parseInt(request.queryParams("numOfGhosts"));
            int numLives = Integer.parseInt(request.queryParams("numberOfLives"));
            int gameLevel = Integer.parseInt(request.queryParams("gameLevel"));
            boolean isZoomSet;
            if (request.queryParams("isZoomAvailable").equals("true")) {
                isZoomSet = true;
            } else {
                isZoomSet = false;
            }
            dis.setGameParameters(gameLevel, numGhosts, numLives, isZoomSet);
            return gson.toJson(null);
        });

        post("/setHighestScore", (request, response) -> {
            String uuid = request.queryParams("uuid");
            if (uuid == null) {
                return "Init first";
            }
            DispatcherAdapter dis = null;
            if (map.containsKey(uuid)) {
                dis = map.get(uuid);
            } else {
                return null;
            }
            String score = request.queryParams("score");
            dis.getContext().setHighestScore(Integer.parseInt(score));
            return gson.toJson(null);
        });


    }

    /**
     * Get the Heroku assigned port number.
     * @return  Heroku assigned port number
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

}
