package edu.rice.comp504.controller;

import edu.rice.comp504.service.MediatorService;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.List;

/**
 * Create a web socket for the server.
 */
@WebSocket
public class WebSocketController {
    private MediatorService ms = new MediatorService();

    /**
     * Open user's session.
     * @param user The user whose session is opened.
     */
    @OnWebSocketConnect
    public void onConnect(Session user) {
        List<String> userName = user.getUpgradeRequest().getParameterMap().get("username");
        if (userName.size() == 1) {
            ms.online(user, userName.get(0));
        }
    }

    /**
     * Close the user's session.
     * @param user The use whose session is closed.
     */
    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        List<String> userName = user.getUpgradeRequest().getParameterMap().get("username");
        if (userName.size() == 1) {
            ms.offline(userName.get(0));
        }
    }

    /**
     * Send a message.
     * @param user  The session user sending the message.
     * @param message The message to be sent.
     */
    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    }
}
