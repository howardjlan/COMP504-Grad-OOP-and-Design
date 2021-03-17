package edu.rice.comp504.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.message.AMessage;
import edu.rice.comp504.model.user.User;
import edu.rice.comp504.service.IMediatorService;
import edu.rice.comp504.service.MediatorService;
import org.eclipse.jetty.websocket.api.Session;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.p;
import static j2html.TagCreator.sub;
import static spark.Spark.*;

/**
 * The chat app controller communicates with all the clients on the web socket.
 */
public class ChatAppController {
    static Map<Session, User> userMap = new ConcurrentHashMap<>();
    static Map<Session, String> userNameMap = new ConcurrentHashMap<>();

    /**
     * Chat App entry point.
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFiles.location("/public");
        IMediatorService ms = new MediatorService();
        Gson gson = new Gson();
        // Timeout 60 minutes
        webSocketIdleTimeoutMillis(3600000);
        webSocket("/chatapp", WebSocketController.class);
        init();
        post("/register", (request, response) -> {
            User user = ms.register(request.queryParams("username"), request.queryParams("pwd"),
                    request.queryParams("firstName"), request.queryParams("lastName"),
                    request.queryParams("age"), request.queryParams("gender"),
                    request.queryParams("school"), request.queryParams("department"),
                    request.queryParams("major"), request.queryParams("interests"));
            return gson.toJson(user);
        });

        post("/signin", (request, response) -> {
            User user = ms.login(request.queryParams("username"),request.queryParams("pwd"));
            return gson.toJson(user);
        });

        post("/profile", (request, response) -> {
            User user = ms.getProfile(request.queryParams("username"));
            return gson.toJson(user);
        });

        post("/chatroom/getChatroom", ((request, response) -> {
            int chatroomId = Integer.parseInt(request.queryParams("chatroomId"));
            AChatroom chatroom = ms.getChatroom(chatroomId);
            return gson.toJson(chatroom);
        }));

        post("/chatroom/createChatroom", ((request, response) -> {
            AChatroom chatroom = ms.createChatroom(request.queryParams("user"), request.queryParams("chatroom"), request.queryParams("type"), request.queryParams("description"));
            return gson.toJson(chatroom);
        }));

        get("/chatroom/allUsers", ((request, response) -> {
            List<String> userList = ms.getAllUsers(Integer.parseInt(request.queryParams("chatroomId")));
            return gson.toJson(userList);
        }));

        post("/chatroom/joinedRooms", ((request, response) -> {
            List<AChatroom> roomList =  ms.getJoinedRooms(request.queryParams("username"));
            return gson.toJson(roomList);
        }));

        get("/chatroom/allChatrooms", ((request, response) -> {
            List<AChatroom> roomList =  ms.getAllRooms();
            return gson.toJson(roomList);
        }));

        post("/chatroom/requestJoin", ((request, response) -> {
            if (ms.requestJoin(request.queryParams("username"), request.queryParams("chatroomId"))) {
                return gson.toJson("success");
            }
            return gson.toJson("failure");
        }));

        post("/chatroom/respondToJoin", ((request, response) -> {
            if (ms.respondToJoin(request.queryParams("username"), request.queryParams("chatroomId"), request.queryParams("response"))) {
                return gson.toJson("success");
            }
            return gson.toJson("failure");
        }));

        post("/chatroom/setAdmin", ((request, response) -> {
            ms.setAdmin(request.queryParams("username"),Integer.parseInt(request.queryParams("chatRoomId")));
            return gson.toJson("adminList");
        }));

        post("/chatroom/getAdmin", ((request, response) -> {
            List<String> adminList = ms.getAdmin(Integer.parseInt(request.queryParams("chatRoomId")));
            return gson.toJson(adminList);
        }));

        post("/chatroom/connect", ((request, response) -> {
            String username = request.queryParams("username");
            int roomId = Integer.parseInt(request.queryParams("chatroomId"));
            if (ms.getAllUsers(roomId).contains(username)) {
                AChatroom room = ms.getChatroom(roomId);
                return gson.toJson(room);
            }
            return null;
        }));

        post("/chatroom/getAllMessages", ((request, response) -> {
            int roomId = Integer.parseInt(request.queryParams("chatroomId"));
            List<AMessage> msgs = ms.getMessages(roomId);
            return gson.toJson(msgs);
        }));

        post("/chatroom/sendMessage", ((request, response) -> {
            String username = request.queryParams("username");
            int roomId = Integer.parseInt(request.queryParams("roomId"));
            String message = request.queryParams("message");
            String directTo = request.queryParams("directTo");
            ms.sendMessage("text", message, username, roomId, directTo);
            return "Send message";
        }));

        post("/chatroom/warning", ((request, response) -> {
            ms.warn(request.queryParams("sender"), Integer.parseInt(request.queryParams("chatroom")));
            return gson.toJson("Reports user within this chatroom");
        }));

        post("/chatroom/recallMsg", (request, response) -> {
            int mid = Integer.parseInt(request.queryParams("mid"));
            int cid = Integer.parseInt(request.queryParams("cid"));
            String user = request.queryParams("user");
            ms.recallMessage(mid, cid, user);
            return gson.toJson(null);
        });

        post("/chatroom/editMsg", (request, response) -> {
            String mid = request.queryParams("mid");
            String cid = request.queryParams("cid");
            String username = request.queryParams("user");
            String text = request.queryParams("message");
            ms.editMessage(Integer.parseInt(mid), Integer.parseInt(cid), username, text);
            return gson.toJson(null);
        });

        post("/chatroom/deleteMsg", (request, response) -> {
            int mid = Integer.parseInt(request.queryParams("mid"));
            int cid = Integer.parseInt(request.queryParams("cid"));
            String user = request.queryParams("user");
            ms.deleteMessage(mid, cid, user);
            return gson.toJson(null);
        });

        post("/chatroom/invite", ((request, response) -> {
            String usernames = request.queryParams("usernames");
            int cid = Integer.parseInt(request.queryParams("cid"));
            String inviter = request.queryParams("inviter");
            if (ms.invite(usernames, cid, inviter)) {
                return gson.toJson("success");
            }
            return gson.toJson("failure");
        }));

        post("/chatroom/ban", ((request, response) -> {
            String banUser = request.queryParams("banUser");
            int cid = Integer.parseInt(request.queryParams("cid"));
            String adminUser = request.queryParams("adminUser");
            if (ms.ban(banUser, cid, adminUser)) {
                return gson.toJson("success");
            }
            return gson.toJson("failure");
        }));

        post("/chatroom/deleteUser", ((request, response) -> {
            String deleteUser = request.queryParams("deleteUser");
            int cid = Integer.parseInt(request.queryParams("cid"));
            String adminUser = request.queryParams("adminUser");
            if (ms.deleteUser(deleteUser, cid, adminUser)) {
                return gson.toJson("success");
            }
            return gson.toJson("failure");
        }));

        post("/chatroom/block", ((request, response) -> {
            List<String> blockList = ms.block(request.queryParams("username"),request.queryParams("blockUser"));
            return gson.toJson(blockList);
        }));

        post("/chatroom/unblock", ((request, response) -> {
            List<String> blockList = ms.unblock(request.queryParams("username"),request.queryParams("unblockUser"));
            return gson.toJson(blockList);
        }));

        post("/chatroom/leave", ((request, response) -> {
            ms.leave(request.queryParams("username"),Integer.parseInt(request.queryParams("chatRoomId")),0);
            return gson.toJson("User leaves this chatroom");
        }));

        post("/chatroom/leaveAll", ((request, response) -> {
            ms.leaveAll(request.queryParams("username"),Integer.parseInt(request.queryParams("reasonCode")));
            return gson.toJson("User leaves this chatroom");
        }));


        post("/reportUser", ((request, response) -> {
            ms.reportUser(request.queryParams("sender"), request.queryParams("receiver"), Integer.parseInt(request.queryParams("chatroom")));
            return gson.toJson(null);
        }));

        get("/getBlockUsers", ((request, response) -> {
            List<String> blockUsers = ms.getBlockList(request.queryParams("username"));
            return gson.toJson(blockUsers);
        }));
    }

    /**
     * Broadcast message to all users.
     * @param sender  The message sender.
     * @param message The message.
     */
    static void broadcastMessage(Session sender, String message) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd");
        String time = date.format(formatter);

        userNameMap.keySet().forEach(session -> {
            JsonObject jo = new JsonObject();
            // use .addProperty(key, value) add a JSON object property that has a key "userMessage"
            //  and a j2html paragraph value
            jo.addProperty("userMessage", message);
            jo.addProperty("messageType", "text");
            jo.addProperty("time", time);
            try {
                if (!session.equals(sender)) {
                    jo.addProperty("username", userNameMap.get(sender));

                } else {
                    jo.addProperty("username", "Me");
                }
                session.getRemote().sendString(String.valueOf(jo));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Get the heroku assigned port number.
     * @return The heroku assigned port number
     */
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; // return default port if heroku-port isn't set.
    }
}
