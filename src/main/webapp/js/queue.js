const wsEndpoint = "ws://" + window.location.hostname + ":" + window.location.port + "/WebBattle-1.0/queue"
let roomws;

function createBattleRoom(){
    roomws = new WebSocket(wsEndpoint);

    roomws.onopen = (event) => {
        console.log("WebSocket connection open.")
    }

    roomws.onmessage = (event) => {
        const message = event.data.split(":");
        if (message[0] === "battleStart") {
            const roomCode = message[1];
            window.location.href = "battle.html?room=" + roomCode; // Pass room code as a query parameter
        }
    };

    roomws.onerror = (event) => {
        console.error("WebSocket error:", event);
    };

    roomws.onclose = (event) => {
        console.log("WebSocket connection closed:",event);
    };
}
