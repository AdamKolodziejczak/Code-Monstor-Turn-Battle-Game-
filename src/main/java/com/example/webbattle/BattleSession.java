package com.example.webbattle;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@ServerEndpoint("/battleSocket/{bid}")
public class BattleSession {
    // A kind of incoming message.
    enum Incoming {
        SKILL,
        SWITCH_ACTIVE,
        REST,
    }

    private static final Map<String, Incoming> incomingTypes = new HashMap<>() {{
        put("skill", Incoming.SKILL);
        put("setActive", Incoming.SWITCH_ACTIVE);
        put("rest", Incoming.REST);
    }};

    // The battle which this session is playing in.
    private Battle battle;
    // This session.
    public Session session;
    // This sessions user.
    User user;


    @OnOpen
    public void handleOpen(@PathParam("bid") String bId, Session session) throws IOException {
        synchronized (Battle.ongoingBattles) {
            // Connect to the battle given this battle id.
            this.session = session;
            sendMessage(session, new OutgoingMessage("log").add("message", "Connected to battle server."));
            user = new User(this, "", Monster.createMonsterList());
            battle = Battle.createBattle(bId);
            synchronized (battle) {
                battle = Battle.ongoingBattles.get(bId).joinBattle(user);
            }
        }
    }

    @OnClose
    public void handleClose(Session session) throws IOException {
        battle.endGame(user);
    }

    @OnMessage
    public void handleMessage(String comm, Session session) throws IOException {
        synchronized (battle) {
            // Parse the incoming message.
            JSONObject message = new JSONObject(comm);
            Incoming type = incomingTypes.get(message.getString("type"));

            switch (type) {
                case SKILL:
                    handleSkill(message.getInt("skill"));
                    break;
                case SWITCH_ACTIVE:
                    battle.setActiveAction(user, message.getInt("monster"));
                    break;
                case REST:
                    battle.skipTurnAction(user);
                    break;
                default:
                    sendMessage(new OutgoingMessage("error").add("message", "Unrecognized message type."));
                    break;
            }
        }
    }

    private void handleSkill(int skill) throws IOException {
        battle.useSkillAction(user, skill);
    }

    public static void appendToLog(Session session, String message) throws IOException {
        sendMessage(session, new OutgoingMessage("log").add("message", message));
    }

    public void sendMessage(OutgoingMessage message) throws IOException {
        BattleSession.sendMessage(this.session, message);
    }

    public static void sendMessage(Session session, OutgoingMessage message) throws IOException {
        session.getBasicRemote().sendText(message.toString());
    }
}

