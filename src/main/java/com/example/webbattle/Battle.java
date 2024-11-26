package com.example.webbattle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// A battle in progress.
public class Battle {
    public final static HashMap<String, Battle> ongoingBattles = new HashMap<>();

    private final ArrayList<User> users;
    private int turn = 0;
    private final Random random = new Random();

    // Initializes an id.
    public Battle() {
        users = new ArrayList<>();
    }

    // Creates an instance of battle with the given id or returns the existing battle with that id.
    public static Battle createBattle(String id) {
        if (!ongoingBattles.containsKey(id)) {
            ongoingBattles.put(id, new Battle());
        }
        return ongoingBattles.get(id);
    }

    // Adds a user to this battle.
    public Battle joinBattle(User user) throws IOException {
        user.id = users.size();
        user.name = "Player " + (user.id == 0 ? "Red" : "Blue");
        for (Monster monster : user.monsters) {
            monster.playerId = user.id;
        }

        if (users.size() < 2) {
            // If there are not yet 2 users in the room
            users.add(user);
            user.session.sendMessage(new OutgoingMessage("userid").add("id", user.id));
        } else {
            // If there are already to people in the room show an error.
            user.session.sendMessage(new OutgoingMessage("error").add("message", "Room already full."));
        }

        if (users.size() == 2) {
            // Start the game.
            for (User current : users) {
                // Tell users of all other users.
                messageAll(new OutgoingMessage("player")
                        .add("name", current.name)
                        .add("id", current.id));

                messageAll(new OutgoingMessage("log")
                        .add("message", current.name + " joined the battle."));

                // Alert all users of all the monsters in play.
                for (Monster monster : current.monsters) {
                    sendMonster(monster);
                }
                // Set users active monster.
                setActive(current, current.monsters.get(0).id);
            }

            // Send start game message.
            messageAll(new OutgoingMessage("start"));

            // Pick a random user to start.
            turn = random.nextInt(2);
            changeTurn();
        }
        return this;
    }

    // Returns true if skill is used successfully, which ends user's turn.
    // If not (i.e. monster does not have enough stamina), returns false,
    // which prompts user to take a different action.
    public boolean useSkill(Monster attacker, Monster opponent, Skill skill) throws IOException {
        // Damage is attacker's strength + skill's strength, reduced by opponent's defence but cannot go below 0
        double damage = Math.max((attacker.attack + skill.dmgDealt - opponent.defence)*skill.typeMultiplier(opponent.type), 0);
        // Some skills may be able to restore attacker's health
        double hpRestore = skill.dmgHealed;
        // Hit % is a combination of attacker's accuracy and skill's accuracy, must be <= 100
        double hitRate = Math.min(attacker.accuracy + skill.accuracy, 100);

        if(attacker.stamina >= skill.staminaCost) {
            appendLog(getActivePlayer().name + "'s " + attacker.name + " used " + skill.name);
            attacker.stamina = Math.max(attacker.stamina - skill.staminaCost, 0);

            // Calculate hit based on hitRate
            if (Math.floor(Math.random() * 100) < hitRate) {
                appendLog("Hit!");
                attacker.health = Math.min(attacker.health + hpRestore, attacker.maxHealth);

                // atkBoost and defBoost will have some value >= 0 and <= 75 if the skill buffs user's stats
                attacker.attack = Math.min(Math.max(attacker.attack + skill.atkBoost, 0), 75);
                attacker.defence = Math.min(Math.max(attacker.defence + skill.defBoost, 0), 75);
                opponent.health = Math.max(opponent.health - damage, 0);

                messageAll(new OutgoingMessage("audio").add("audio", "hurt.ogg"));
            } else {
                appendLog("Miss");
                messageAll(new OutgoingMessage("audio").add("audio", "miss.ogg"));
            }

            // attacker's and opponent's health stats are changed to these values if hit connects
            // (as well as attacker's stamina, attack, defence)
            updateStats(attacker);
            updateStats(opponent);
            return true;
        }
        // Notify user if staminaCost is too high to be used
        return false;
    }

    // Called when a user uses a skill.
    // Skill index is the index of the skill in the monsters list of skills.
    public void useSkillAction(User user, int skillIndex) throws IOException {
        if (!assertTurn(user)) return;
        if (useSkill(user.active, getOpponent().active, user.active.skills.get(skillIndex))) {
            // If there was enough stamina check if either users active monster has died.
            activeMonsterDied(user);
            activeMonsterDied(getOpponent());
            // Change the users turn.
            changeTurn();
        } else {
            // If there is not enough stamina, play a sound effect and alert the player.
            user.session.sendMessage(new OutgoingMessage("audio").add("audio", "invalid.wav"));
            BattleSession.appendToLog(user.session.session, "Not enough stamina.");
            sendTurn();
        }
    }

    // Called when the user presses the select button.
    public void setActiveAction(User user, int monsterId) throws IOException {
        if (!assertTurn(user)) return;
        setActive(user, monsterId);
        // Play the sound effect and indicate that the turn has stayed the same.
        messageAll(new OutgoingMessage("audio").add("audio", "switch.wav"));
        sendTurn();
    }

    // Sets the active monster.
    public void setActive(User user, int monsterId) throws IOException {
        user.swapMonster(monsterId);
        messageAll(new OutgoingMessage("setActive")
                .add("player", user.id)
                .add("monster", monsterId));
    }

    // Changes the active user and alerts the players.
    private void changeTurn() throws IOException {
        turn = turn == 0 ? 1 : 0;
        appendLog(getActivePlayer().name + "s turn start!");
        sendTurn();
    }

    // Gets the player whose turn it is
    private User getActivePlayer() {
        return users.get(turn);
    }

    // Returns the current players opponent.
    private User getOpponent() {
        return users.get((turn + 1) % 2);
    }

    // Gets the opponent of a specific player.
    private User getOpponent(User user) {
        return users.get((user.id + 1) % 2);
    }

    // Alerts the players of whoevers turn it is.
    private void sendTurn() throws IOException {
        messageAll(new OutgoingMessage("turn").add("player", turn));
    }

    // Checks if it is the given users turn.
    private boolean isTurn(User user) {
        return user.id == turn;
    }

    // Sends information on a monster to all players.
    private void sendMonster(Monster monster) throws IOException {
        OutgoingMessage message = new OutgoingMessage("monster")
                .add("player", monster.playerId)
                .add("name", monster.name)
                .add("icon", "img/" + monster.imageFile) // Use the icon file name from the monster
                .add("skills", monster.skills)
                .add("type", monster.type.name());
        addStatsToMessage(message, monster);
        messageAll(message);
    }

    // Updates a monsters stats on the frontend of its stats.
    private void updateStats(Monster monster) throws IOException {
        messageAll(addStatsToMessage(new OutgoingMessage("stats"), monster));
    }

    // Adds a monsters stats to a given message.
    private OutgoingMessage addStatsToMessage(OutgoingMessage message, Monster monster) {
        message.add("id", monster.id)
                .add("health", monster.health)
                .add("maxHealth", monster.maxHealth)
                .add("stamina", monster.stamina)
                .add("maxStamina", monster.maxStamina)
                .add("attack", monster.attack)
                .add("defence", monster.defence)
                .add("accuracy", monster.accuracy);
        return message;
    }

    // User can choose not to act, which restores the stamina of their active monster
    public boolean skipTurn(Monster monster) throws IOException {
        monster.stamina = Math.min(monster.stamina + 50, monster.maxStamina);
        updateStats(monster);
        return true;
    }

    // Called when a player presses the rest button.
    public void skipTurnAction(User user) throws IOException {
        if (!assertTurn(user)) return;
        skipTurn(user.active);
        messageAll(new OutgoingMessage("audio").add("audio", "restore.ogg"));
        changeTurn();
    }

    // If it is not the given users turn, give them an error and return false.
    private boolean assertTurn(User user) throws IOException {
        if (!isTurn(user)) {
            user.session.sendMessage(new OutgoingMessage("error")
                    .add("message", "Tried to perform action off turn."));
            return false;
        }
        return true;
    }

    // Send a message to all users.
    private void messageAll(OutgoingMessage message) throws IOException {
        for (User user : users) {
            user.session.sendMessage(message);
        }
    }

    // Appends a message to all players logs.
    private void appendLog(String message) throws IOException {
        messageAll(new OutgoingMessage("log").add("message", message));
    }

    // Check if the given users active monster is dead and if it is, check if the whole team is dead and potentially end the game.
    public void activeMonsterDied(User user) throws IOException {
        if (user.active.health <= 0) {
            messageAll(new OutgoingMessage("died")
                    .add("player", user.id)
                    .add("monster", user.active.id));
            appendLog(user.name + "s " + user.active.name + " crashed!");
            messageAll(new OutgoingMessage("audio").add("audio", "death.ogg"));
            boolean gameOver = true;

            for (Monster monster : user.monsters) {
                if (monster.health > 0) {
                    gameOver = false;
                    setActive(user, monster.id);
                    break;
                }
            }

            if (gameOver) {
                endGame(user);
            }

        }
    }

    // End the game with the given user as the loser.
    public void endGame(User loser) throws IOException {
        // If the whole team is dead
        messageAll(new OutgoingMessage("log")
                .add("message", loser.name + "'s team has all crashed. " + getOpponent(loser) + " Wins!"));
        messageAll(new OutgoingMessage("log")
                .add("message", "Game Over"));

        // Redirect logic
        getOpponent(loser).session.sendMessage(new OutgoingMessage("redirect").add("url", "winner.html"));
        loser.session.sendMessage(new OutgoingMessage("redirect").add("url", "loser.html"));
    }

}