let ws;

let monsters = {};
let users = {};
let userId = null;

// Called when the page is loaded.
function pageLoad() {
    if (typeof ws !== "undefined") {
        ws.close();
    }

    // Get the battle room id to connect to.
    let params = new URLSearchParams(window.location.search);
    let room = params.get("room");
    if (room == null) {
        room = "defaultRoom";
    }

    // Connect to the battle room.
    ws = new WebSocket("ws://" + window.location.hostname + ":" + window.location.port + "/WebBattle-1.0/battleSocket/" + room);

    ws.onmessage = handleMessage;

    ws.onopen = (event) => {
        console.log("ws connected");
    }

    ws.onclose = (event) => {
        console.log("ws closed");
    }

    ws.onerror = (event) => {
        console.log(event.data);
    }
}

// Handles an incoming message from the websocket.
function handleMessage(event) {
    console.log(event.data);
    let message = JSON.parse(event.data);

    switch (message["kind"]) {
        case "log":
            appendLog(message["message"]);
            break;
        case "monster":
            addMonster(message);
            break;
        case "player":
            addPlayer(message);
            break;
        case "setActive":
            setActive(message.player, message.monster);
            break;
        case "start":
            start();
            break;
        case "stats":
            setStats(message, true);
            break;
        case "turn":
            setTurn(message.player);
            break;
        case "userid":
            userId = message.id;
            console.log("User id set");
            break;
        case "error":
            console.error(message.message);
            window.alert(message.message);
            break;
        case "audio":
            playSound(message.audio);
            break;
        case "died":
            died(message.player, message.monster);
            break;
        case "redirect":
            window.location.href = message.url;
            break;  // Handle the redirect message
        default:
            console.error("Unrecognized message type");

    }
}

// The server has alerted us a monster has died.
function died(player, monster){
    if(player === userId){
        //hideElements("#controls-" + monster);
        document.getElementById("controls-" + monster).remove();
    }
    //hideElements("#monster-container-" + monster);
    document.getElementById("monster-container-" + monster).remove();
}

function setTurn(player) {
    if (player === userId) {
        setControlVisibility(true);
    }
}

// Cache of loaded sound files, so they don't have to be loaded each time they're played.
let soundCache = {};

// Plays a sound given its file offset from audio/
function playSound(file) {
    file = "audio/" + file;
    let sound;
    if (file in soundCache) {
        sound = soundCache[file];
    } else {
        sound = new Audio(file);
        soundCache[file] = sound;
    }

    sound.play();
}

// Sets the stats of a monster given a incoming stats message.
// if updateHtml is set it will affect the html of the page.
function setStats(message, updateHtml) {
    monster = monsters[message.id];
    monster.health = message.health;
    monster.maxHealth = message.maxHealth;
    monster.stamina = message.stamina;
    monster.maxStamina = message.maxStamina;
    monster.attack = message.attack;
    monster.defence = message.defence;
    monster.accuracy = message.accuracy;
    console.log("Setting stats");
    if (updateHtml) {
        document.getElementById("monster-stats-" + monster.id).outerHTML = getStatsHtml(monster);
    }
}

// Get the monster-card div for a monster.
function getMonsterDiv(monster) {
    return document.getElementById("monster-card-" + monster.id);
}

// Adds a monster card to the page.
function addMonster(monster) {
    monsters[monster.id] = monster;
    users[monster.player].monsters[monster.id] = monster;
    console.log("Adding monster");
    setStats(monster, false);
    // Update html
    let isUsers = monster.player === userId;
    let divId = isUsers ? "player-team" : "enemy-team";
    let text = isUsers ? getPlayerMonsterHtml(monster) : getMonsterHtml(monster);
    document.getElementById(divId).innerHTML += text;
}

// Adds a player to the list of players.
function addPlayer(player) {
    users[player.id] = player;
    player.monsters = {};
    player.active = null;
}

// Called when the server tells the client to set a new active monster.
function setActive(player, monster) {
    let old = users[player].active;
    users[player].active = users[player].monsters[monster];
    console.log("Setting active monster")

    // Update html
    if (!(old === null)) {
        getMonsterDiv(old).className = "monster-card";
        if (player === userId) {
            hideElements("#active-" + old.id);
            showElements("#inactive-" + old.id);
        }
    }

    if (player === userId) {
        hideElements("#inactive-" + monster);
        showElements("#active-" + monster);
    }
    getMonsterDiv(monsters[monster]).className = "active-monster-card";
}

function appendLog(text) {
    let log = document.getElementById("log");
    log.scrollTop = log.scrollHeight;
    log.value += text + '\n';
    console.log(text);
}

// Get the html for a monster card.
function getMonsterHtml(monster) {
    return `<div id="monster-card-${monster.id}" class="monster-card">
<div id="monster-container-${monster.id}" class="monster-container">
<img src="${monster.icon}" style="max-width: 200px; max-height: 200px;">
${getStatsHtml(monster)}
</div>
</div>`;
}

// Get the html for a monster cards stats.
function getStatsHtml(monster) {
    return `<div id="monster-stats-${monster.id}">
<h3>${getParadigmIndicator(monster.type)} ${monster.name}</h3>
<p class="inline">HP</p>
<progress value="${monster.health}" max="${monster.maxHealth}" class="inline"></progress>
<p class="inline">${monster.health}/${monster.maxHealth}</p>
<br>
<p class="inline">SP</p>
<progress value="${monster.stamina}" max="${monster.maxStamina}" class="inline"></progress>
<p class="inline">${monster.stamina}/${monster.maxStamina}</p>
<br>
<p>atk:${monster.attack} def:${monster.defence} acc:${monster.accuracy}</p>
</div>`;
}

// Start the game.
function start() {
    document.getElementById("player-lbl").innerHTML = users[userId].name + " (you)";
    for (let user in users) {
        if (users[user].id !== userId) {
            document.getElementById("enemy-lbl").innerHTML = users[user].name;
        }
    }
}

// Gets the html for the buttons which represent the skills of a monster.
function getSkillButtonsHtml(monster) {
    let html = "";
    for (let i = 0; i < monster.skills.length; ++i) {
        let skill = monster.skills[i];
        html += `<button onclick="useSkill(${monster.id}, ${i})" class="skill" title="${skill.description}">${getParadigmIndicator(skill.type)} ${skill.name}</button>`;
        if (i % 1 === 0) {
            html += "<br>";
        }
    }
    return html
}

// Gets the short form version of a paradigm or monster type.
function getParadigmIndicator(paradigm) {
    paradigm = paradigm.toLowerCase();
    switch (paradigm) {
        case "object_oriented":
            return "(OO)";
        case "functional":
            return "(Func)";
        case "procedural":
            return "(Proc)";
        case "unstructured":
            return "(Uns)";
        default:
            return "(???)";
    }
}
/* uses a monsters skill, sends messages to server*/
function useSkill(monster, skillIndex) {
    sendMessage({type: "skill", monster: monster, skill: skillIndex});
    setControlVisibility(false);
}

// Called when a user clicks the rest button.
function rest(monsterId) {
    sendMessage({type: "rest", monster: monsterId});
    setControlVisibility(false);
}

// Gets the html for a monster which belongs to this clients player with all the skill buttons.
function getPlayerMonsterHtml(monster) {
    return `<div class="inline">
${getMonsterHtml(monster)}
<div id="controls-${monster.id}" class="monster-controls" hidden>
<div id="inactive-${monster.id}">
<button onclick="switchClicked(${monster.id})">Select</button>
</div>
<br>
<div id="active-${monster.id}" hidden>
<button onclick="rest(${monster.id})">Rest</button>
${getSkillButtonsHtml(monster)}
</div>
</div>
</div>`;
}

/* hides/disables element when monster dies so they can't be clicked*/
function hideElements(query) {
    document.querySelectorAll(query).forEach((div) => {
        div.setAttribute("hidden", "hidden");
    });
}

// Shows an element based off a query.
function showElements(query) {
    document.querySelectorAll(query).forEach((div) => {
        div.removeAttribute("hidden");
    });
}

// Sets an elements visibility based off a query.
function setVisibility(query, visible) {
    if (visible) {
        showElements(query);
    } else {
        hideElements(query);
    }
}

// Called when a user clicks the select button under a monster card.
function switchClicked(monsterId) {
    sendMessage({type: "setActive", monster: monsterId});
    setControlVisibility(false);
}

// Sets the visibility of all the skill buttons and select buttons.
function setControlVisibility(visible) {
    setVisibility(".monster-controls", visible);
}

// Sends a message to the server.
function sendMessage(message) {
    ws.send(JSON.stringify(message));
}

