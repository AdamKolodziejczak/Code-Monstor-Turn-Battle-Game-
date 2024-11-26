package com.example.webbattle;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.http.WebSocket;
import java.util.*;

@ServerEndpoint("/queue")
public class QueueServer {

    private static final List<Session> sessions = new ArrayList<>();

    @OnOpen
    public void open(Session session) {
        synchronized (sessions) {
            sessions.add(session);
            checkQueue();
        }
    }

    @OnClose
    public void close(Session session) {
        synchronized (sessions) {
            sessions.remove(session);
        }
    }

    /* Checks if two people are in queue, if so then sends them to a battle arena */
    private void checkQueue() {
        if (sessions.size() >= 2) {
            // Pair the first two sessions
            Session session1 = sessions.remove(0);
            Session session2 = sessions.remove(0);
            String roomCode = UUID.randomUUID().toString(); // Generate a unique room code
            // link: https://ioflood.com/blog/java-uuid/#:~:text=There%20are%20various%20methods%20to,each%20time%20it%20is%20ran.
            startGame(session1, session2, roomCode);
        }
    }

    /* Start the battle with the opponents*/
    private void startGame(Session session1, Session session2, String roomCode) {
        // Send messages to each player to navigate them to the game page
        try {
            session1.getBasicRemote().sendText("battleStart:" + roomCode);
            session2.getBasicRemote().sendText("battleStart:" + roomCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}