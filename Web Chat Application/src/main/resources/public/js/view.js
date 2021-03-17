'use strict';

const webSocket = new WebSocket("wss://" + location.hostname + ":" + location.port + "/chatapp");
var userName;
/**
 * Entry point into chat room
 */
window.onload = function() {

    webSocket.onmessage = (msg) => updateChatRoom(msg);
    $("#btn-msg").click(()      => sendMessage($("#message").val()));

};

/**
 * Send a message to the server.
 * @param msg  The message to send to the server.
 */
function sendMessage(msg) {
    if (msg !== "") {
        webSocket.send(msg);
        $("#message").val("");
        $("#chatArea").append("<p>I say: " + msg + "</p>");
    }
}

/**
 * Update the chat room with a message.
 * @param message  The message to update the chat room with.
 */
function updateChatRoom(message) {
    // convert the data to JSON and use .append(text) on a html element to append the message to the chat area
    let data = JSON.parse(message.data);
    console.log(data.userMessage);
    $("#chatArea").append(data.userMessage);
}
