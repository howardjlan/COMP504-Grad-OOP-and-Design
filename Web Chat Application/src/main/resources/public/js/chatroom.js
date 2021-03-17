'use strict';

var webSocket;
var username;
var isAdmin;
var adminList;
var blockList;
var chatroomName = "General";
var chatroomId = 1;
var chatroomType;

let messageEditTarget;
let requestRoomsList = [];

let isWaitingAccept = false;
let requestRoomId = 0;

var timerID = 0;

function keepAlive() {
    var timeout = 15000;
    if (webSocket.readyState == webSocket.OPEN) {
        webSocket.send('');
    }
    timerID = setTimeout(keepAlive, timeout);
}

function cancelKeepAlive() {
    if (timerID) {
        clearTimeout(timerID);
    }
}

/**
 * Entry point into chat room
 */
window.onload = function() {
    if(window.localStorage){
        username = localStorage.getItem("username");
    }
    if(username !== undefined || username !== ""){
        $("#span-username").empty();
        $("#span-username").append(document.createTextNode(username));
        webSocket = new WebSocket("wss://" + location.hostname + ":" + location.port + "/chatapp?username=" + username,);
        webSocket.onclose =   ()    => closeConnection();
        webSocket.onmessage = (msg) => updateChatRoom(JSON.parse(msg.data));
        getBlockUsers();
        keepAlive();
    } else {
        location.href = "index.html";
    }

    //Load initial chatroom
    loadChatroomOnStart(chatroomId);

    //For emoji
    $('#msg-content').emoji({place: 'after'});

    //Sign out
    $("#link-signOut").click(function(){signOut(event)});

    //Send a message
    $("#btn-msg").click(sendMessage);

    //Report a user
    $("#btn-report-user").click(function () {report($("input[name='all-users']:checked").val())});

    //Create a chatroom
    $('#createRoom').click(createChatroom);

    //Get All rooms
    $("#refresh-allRooms").click(getAllRooms);

    //Get joined rooms
    $("#refresh-allJoinedRooms").click(getJoinedRooms);

    //Get chat room users
    $("#refresh-allUsers").click(getChatRoomUsers);

    //Ban user
    $("#btn-ban-user").click(banUser);

    //Delete user
    $("#btn-delete-user").click(deleteUser);

    //Set admin
    $("#btn-set-admin").click(setAdmin);

    //Connect to chat room
    $("#btn-connect").click(connectToRoom);

    //Leave chat room
    $("#btn-leave").click(leaveChatRoom);

    //Leave all chat rooms
    $("#btn-leave-all").click(leaveAllRooms);

    //Join or Request Join chat room
    $("#btn-join-public").click(function() {
        requestJoin("public")
    });
    $("#btn-request-join").click(function() {
        requestJoin("private")
    });

    //Block user
    $("#btn-block-user").click(blockUser);

    //Unblock user
    $("#btn-unblock-user").click(unblockUser);

};

/**
 * Update chatroom elements based on user
 */
function loadUser() {
    checkAdmin(username);
    hideElement("#btn-invite-form");
    if (isAdmin) {
        // showElement("#btn-block-user");
        showElement("#btn-delete-user");
        showElement("#btn-ban-user");
        showElement("#btn-set-admin");
        hideElement("#btn-report-user");
        showElement(".incoming-message .btn-group");
        if (chatroomType === "Private")
            showElement("#btn-invite-form");
    } else {
        // hideElement("#btn-block-user");
        hideElement("#btn-delete-user");
        hideElement("#btn-ban-user");
        hideElement("#btn-set-admin");
        showElement("#btn-report-user");
        hideElement(".incoming-message .btn-group");
    }
}


/**
 * Load the room list in the left side.
 */
function loadRoomList() {
    getAllRooms();
    getJoinedRooms();
    getChatRoomUsers();
}

/**
 * Set chat room's admin
 */
function setAdmin() {
    let username = $('input[name="all-users"]:checked').val();
    if(!checkElement(username)) return;
    $.post("/chatroom/setAdmin", {username: username, chatRoomId: chatroomId}, function () {
    }, "json");
}

/**
 * Get the admin list of the current room.
 */
function getAdmin(){
    $.post("/chatroom/getAdmin", {chatRoomId: chatroomId}, function (data) {
        adminList = data;
        getChatRoomUsers();
        loadUser();
    }, "json");
}

/**
 * Get chatroom information, on startup gets lowest id room.
 */
function loadChatroomOnStart(chatroom) {
    $.post("/chatroom/joinedRooms",{username: username}, function (rooms) {
        //Load general or show no room message if user not in General chat.
        let lowestId = null;
        rooms.forEach(function (room) {
            if (lowestId == null) {
                lowestId = room.id;
            } else if (room.id < lowestId) {
                lowestId = room.id;
            }
        });
        chatroom = lowestId;

        loadChatroom(chatroom);
    }, "json");
}

/**
 * Get chatroom information.
 */
function loadChatroom(chatroom) {
    if (chatroom == null) {
        reloadChatArea();
        chatroomId = -1;
        getJoinedRooms();
        getChatRoomUsers();
    } else {
        $.post("/chatroom/getChatroom", {chatroomId: chatroom}, function (data) {
            chatroomName = data.name;
            chatroomId = data.id;
            adminList = data.admins;
            chatroomType = data.type;
            let chatroomDescription = data.description;
            let userList = data.users;

            $("#chatRoomName").html(chatroomName + " (" + chatroomType + ")");
            $("#chatRoomDescription").html(chatroomDescription);

            startChatArea();

            displayMessages(chatroomId);

            loadUser();
            loadRoomList();

            getChatRoomUsers();


            let unread = $("#joined-room" + chatroomId + " + span .badge-danger");
            unread.addClass("d-none");
            unread.html(0);
        }, "json");
    }
}
/**
 * Create new chatroom.
 */
function createChatroom() {
    var chatroom = $('#form3').val();
    var description = $('#chatroom-description').val();
    var type;
    if (!validate(chatroom)) {
        return;
    }
    if ($('input[type="radio"][id="public"]:checked').val()) {
        type = 'public';
    } else if ($('input[type="radio"][id="private"]:checked').val()){
        type = 'private';
    } else {
        return;
    }

    $.post("/chatroom/createChatroom", {user: username, chatroom: chatroom, description: description, type: type}, function (data) {
        $('#chatroom-description').val('');
        $('#form3').val('');
        $('#public').prop("checked", false);
        $('#private').prop("checked", false);

        startChatArea();

        $("#modalCreateRoomForm").modal('toggle');
        chatroomName = data.name;
        chatroomId = data.id;
        adminList = data.admins;
        loadChatroom(chatroomId, "created");
        loadRoomList();
        getChatRoomUsers();
    }, "json");
}

/**
 * Ban other users.
 */
function banUser() {
    let banUser = $('input[name="all-users"]:checked').val();
    if(!checkElement(banUser)) return;
    $.post("/chatroom/ban", {adminUser: username, banUser: banUser, cid: chatroomId}, function(data) {
        if(data != null) {
            blockList = data;
            getChatRoomUsers();
        }
    }, "json")
}

/**
 * Delete other users.
 */
function deleteUser() {
    let deleteUser = $('input[name="all-users"]:checked').val();
    if(!checkElement(deleteUser)) return;
    $.post("/chatroom/deleteUser", {adminUser: username, deleteUser: deleteUser, cid: chatroomId}, function(data) {
        if(data != null) {
            blockList = data;
            getChatRoomUsers();
        }
    }, "json")
}

/**
 * Get block users.
 */
function getBlockUsers() {
    $.get("/getBlockUsers", {username:username}, function(data) {
        blockList = data;
    })
}

/**
 * Block other users.
 */
function blockUser() {
    let blockUser = $('input[name="all-users"]:checked').val();
    if(!checkElement(blockUser)) return;
    $.post("/chatroom/block", {username: username, blockUser:blockUser}, function(data) {
        if(data != null) {
            blockList = data;
            getChatRoomUsers();
        }
    }, "json")
}

/**
 * Unblock other users.
 */
function unblockUser() {
    let unblockUser = $('input[name="all-users"]:checked').val();
    if(!checkElement(unblockUser)) return;
    $.post("/chatroom/unblock", {username: username, unblockUser:unblockUser}, function(data) {
        if(data != null) {
            blockList = data;
            getChatRoomUsers();
        }
    }, "json")
}

/**
 * Refresh chatroom are when user leaves.
 */
function reloadChatArea(){
    let notification = $("#notificationArea");
    let chatArea = $("#chatArea");
    showElement(notification);
    hideElement(chatArea);

    $("#btn-userDropdown").html("All users in N/A");
}

/**
 * Reshow the chat area instead of the message.
 */
function startChatArea(){
    let notification = $("#notificationArea");
    let chatArea = $("#chatArea");
    hideElement(notification);
    showElement(chatArea);
}

/**
 * Display the previous messages in a chatroom.
 */
function displayMessages(roomId) {
    $.post("/chatroom/getAllMessages", {chatroomId: roomId}, function (data) {
        $("#chat-area").html("");

        if (checkElement(data)) {
            data.sort(function(a, b) {
                return new Date(a.time).getTime() - new Date(b.time).getTime();
            });
            data.forEach(function(message) {
                //Update chatroom with messages from unblocked users only
                if (blockList.indexOf(message.sender) < 0) {
                    updateChatRoom(message);
                }
            });
        }
    }, "json");
}

/**
 * User leave chatroom
 */
function leaveChatRoom(){
    let roomId = $('input[name="joined-room"]:checked').val();
    if(!checkElement(roomId)) return;
    //Check whether the leave room id is equals with the current room id.
    if(roomId == chatroomId) {
        reloadChatArea();
        chatroomId = -1;
    }
    $.post("/chatroom/leave", {username: username, chatRoomId: roomId}, function () {
        getJoinedRooms();
        getChatRoomUsers();
    }, "json");
}

/**
 * User leaves all chatrooms
 */
function leaveAllRooms() {
    reloadChatArea();
    $.post("/chatroom/leaveAll", {username: username, reasonCode: 0}, function () {
        chatroomId = -1;
        getJoinedRooms();
        getChatRoomUsers();
    }, "json");
}

/**
 * Request to join a room.
 * @param type  The chatroom type, public or private.
 */
function requestJoin(type) {
    let roomId;
    if (type === "public") {
        roomId = $('input[name="all-public-room"]:checked').val();
    } else {
        roomId = $('input[name="all-private-room"]:checked').val();
    }

    if (requestRoomsList.indexOf(parseInt(roomId)) < 0) {
        requestRoomsList.push(parseInt(roomId));
        $.post("/chatroom/requestJoin", {username: username, chatroomId: roomId}, function (data) {
                if (data === "failure") {
                    $("#all-room" + roomId).parent().parent().removeClass("list-group-item-success");
                    $("#all-room" + roomId).parent().parent().addClass("list-group-item-danger");
                }
                if (type === "public") {
                    joinAndConnectRoom(roomId);
                    getChatRoomUsers();
                    getJoinedRooms();
                } else {
                    requestRoomId = roomId;
                    isWaitingAccept = true;
                }
            }
            , "json");
    }
}

/**
 * Respond to join request.
 * @param response  The response type, accept or reject.
 * @param user  The user being accepted.
 */
function respondToJoin(response, user) {
    //Need to parse accept/reject value, which is the text,meassageId.
    let username = user.substr(0, user.indexOf(" requests to join "));
    let messageId = user.substr(user.indexOf(",") + 1, user.length);
    $.post("/chatroom/respondToJoin", {username: username, chatroomId: chatroomId, response: response}, function () {}, "json");
    $.post("/chatroom/deleteMsg", {mid: messageId, cid: chatroomId, user: username}, function () {}, "json");
}


/**
 * Add the notification message to the chat area
 * @param user sender
 * @param msg  message content
 * @param date time of the message
 */
function writeNotificationMessage(data) {
    let msgResult = '<div class="notification-wrap">\n' +
        '<p class="notification border rounded">' + data + '</p>\n' + '</div>';
    $("#chat-area").append(msgResult);
    $("#chat-area").animate({scrollTop: $("#chat-area").prop("scrollHeight")}, 50);
}

/**
 * Add the requestjoin notification message to the chat area
 * @param data message
 * @param messageId the id of the message
 */
function writeRequestJoinMessage(data, messageId) {
    let msgResult =  "<div class=\"notification-wrap\">" +
        "<p class=\"notification border rounded\">" +
        "<span>" + data + "</span>" +
        "<button class=\"d-inline btn btn-link btn-sm request request-btn\" id='" + "btn-agree-" + messageId + "' value='" + data + "," + messageId + "'>Agree</button>" +
        "<button class=\"d-inline btn btn-link btn-sm request request-btn\" id='" + "btn-reject-" + messageId + "' value='" + data + "," + messageId + "'>Reject</button>" +
        "</p>";
    $("#chat-area").append(msgResult);
    $("#chat-area").animate({scrollTop: $("#chat-area").prop("scrollHeight")}, 50);

    //Accept join request or reject it
    $("#btn-agree-" + messageId).click(function() {
        respondToJoin("accept", $(this).val());
        let root = this.parentNode.parentNode;
        root.parentNode.removeChild(root);
    });
    $("#btn-reject-" + messageId).click(function() {
        respondToJoin("reject", $(this).val());
        let root = this.parentNode.parentNode;
        root.parentNode.removeChild(root);
    });
}


/**
 * Send a message to the server.
 * @param msg  The message to send to the server.
 */
function sendMessage() {
    let msg = $("#msg-content").val();

    if (msg !== '') {
        $("#msg-content").val('');
        let directToName = $( "#receiver option:selected" ).val();
        $.post("/chatroom/sendMessage", { username: username, roomId: chatroomId, message: msg, directTo: directToName }, function (data) {
        }, "json");
    }
}

/**
 * Update the chat room with a message.
 * @param response  The information of message to update the chat room with.
 */
function updateChatRoom(data) {
    // convert the data to JSON and use .append(text) on a html element to append the message to the chat area
    if ((typeof data) === 'string' ) {
        messageOperations(data);
        return;
    }
    if (data.chatroomId == chatroomId) {
        let name = data.sender;
        let notificationType = data.notificationType;
        if (name === username) {
            name = "Me";
        }
        if (data.messageType === "text") {
            let msg = createMessageHTML(data.messageId, name, data.data, data.time);
            $("#chat-area").append(msg);
            $("#chat-area").animate({scrollTop: $("#chat-area").prop("scrollHeight")}, 100);
            if (name === "Me" && detectHate(data.data)) {
                warnUser();
            }
            if (!isAdmin && name !== "Me") {
                $(".incoming-message .btn-group").remove();
            }
        } else if (data.messageType === "direct" && (name === "Me" || data.directToUser === username)) {
            let msg = createMessageHTML(data.messageId, name, data.data, data.time);
            $("#chat-area").append(msg);
            $("#chat-area").animate({scrollTop: $("#chat-area").prop("scrollHeight")}, 100);
            if (name === "Me" && detectHate(data.data)) {
                warnUser();
            }
            if (!isAdmin && name !== "Me") {
                $(".incoming-message .btn-group").remove();
            }
            if (name === "Me") {
                $("#msg-" + data.messageId + " b").html("Me to " + data.directToUser);
            } else if (data.directToUser === username) {
                $("#msg-" + data.messageId + " b").html(data.sender + " to Me");
                $("#msg-" + data.messageId + " .other-message").addClass("other-direct-message");
            }
        } else if (data.messageType === "notification") {
            if (data.notificationType === "requestJoin" && checkAdmin(username)) {
                writeRequestJoinMessage(data.data, data.messageId);
            } else {
                writeNotificationMessage(data.data);
            }
            if (notificationType === "ban" || notificationType === "delete") {
                getAdmin();
                if (data.sender === username) {
                    reloadChatArea();
                    $("#notificationArea h2").html("You were removed from the chatroom.</br>" +
                        "No chat room is connected. Please select a chat room to connect. ")
                    chatroomId = -1;
                }
            } else if (notificationType === "admin" || notificationType === "leave") {
                getAdmin();
            }
            loadRoomList();
        }
        if (data.data.includes("is set as admin")) {
            getAdmin();
        }
        if (name === 'Me') {
            $("#msg-" + data.messageId + " span").addClass("check");
        }
    } else {
        if (data.notificationType === "join" && isWaitingAccept && data.chatroomId === Number(requestRoomId)) {
            isWaitingAccept = false;
            requestRoomId = 0;
            // joinAndConnectRoom(data.chatroomId);
            getChatRoomUsers();
            getJoinedRooms();
        } else if (data.notificationType === "join") {
            getJoinedRooms();
        }
        let unread = $("#joined-room" + data.chatroomId + " + span .badge-danger");
        unread.removeClass("d-none");
        unread.html(function(n, c){
            return parseInt(c) + 1;
        });
    }
}

/**
 * Preforms operation on a message with input data.
 * @param data the message data
 */
function messageOperations(data) {
    if (data.indexOf('&') >= 0) {
        let pos = data.indexOf('&');
        let mid = data.substring(0, pos);
        let text = data.substring(pos + 1);
        let htmlText =  $('#msg-' + mid).find('p').html();
        let start = htmlText.indexOf("</b>: ");
        let username = htmlText.substring(0, start);
        let html = username + "</b>: " + text;
        $('#msg-' + mid).find('p').html(html);
    } else {
        $('#msg-' + data).remove();
    }
}

/**
 * Detect whether the message contains hate word.
 * @param data
 */
function detectHate(data) {
    let reg = new RegExp('.*\\bhate\\b.*',"i");
    return reg.test(data);
}

/**
 * Invite user to private room, if you are admin.
 */
function invite() {
    var usernames = $("#form4").val();
    if (!validate(usernames)) {
        return;
    }
    $.post("/chatroom/invite", {usernames: usernames, cid: chatroomId, inviter: username}, function (data) {
        if (data === "success") {
            hideElement("#invite-error");
            $("#form4").val('');
            $("#modalInviteForm").modal('toggle');
            getChatRoomUsers();
        }
        else {
            showElement("#invite-error");
            $("#form4").val('');
        }
    }, "json");
}

/**
 * Warn a user for hate.
 */
function warnUser() {
    $.post("/chatroom/warning", {sender: username,  chatroom: chatroomId}, function (data) {
    }, "json");
}

/**
 * Report the user to admins.
 * @param user user being reported
 */
function report(user) {
    if (user) {
        $.post("/reportUser", {sender: username, receiver: user, chatroom: chatroomId}, function (data) {}, "json");
    }
}

/**
 * Show element.
 * @param selector Element selector
 */
function showElement(selector) {
    $(selector).removeClass("d-none");
}

/**
 * Hide element.
 * @param selector Element selector
 */
function hideElement(selector) {
    $(selector).addClass("d-none");
}

/**
 * Sign out.
 * @param event
 */
function signOut(event) {
    cancelKeepAlive();
    localStorage.removeItem("username");
    location.href = "index.html";
}

let templateMyMsg = '<div class="message-wrap row" id="msg-ID-VARIANT">\n' +
    '           <div class="col"></div>\n' +
    '            <div>\n' +
    '              <p class="my-message messages border rounded"><b>USERNAME-VARIANT</b>: MSG-VARIANT</p>\n' +
    '              <div class="btn-group dropup">\n' +
    '                <a  type="button" data-toggle="dropdown">\n' +
    '                  <i class="fas fa-ellipsis-v"></i>\n' +
    '                </a>\n' +
    '                <div class="dropdown-menu message-menu-font">\n' +
    '                  <button class="dropdown-item" data-toggle="modal" data-target="#modalEditMessage" onclick="saveEditTarget(this)">Edit</button>\n' +
    '                  <div class="dropdown-divider"></div>\n' +
    '                  <button class="dropdown-item" onclick="recallMessage(this)">Recall</button>\n' +
    '                </div>\n' +
    '              </div>\n' +
    '              <span class="time-date">TIME-VARIANT</span>\n' +
    '            </div>\n' +
    '          </div>';

let templateOtherMsg = '<div class="message-wrap row" id="msg-ID-VARIANT">\n' +
    '            <div class="incoming-message">\n' +
    '              <p class="other-message messages border rounded"><b>USERNAME-VARIANT</b>: MSG-VARIANT</p>\n' +
    '              <div class="btn-group dropdown">\n' +
    '                <a  type="button" data-toggle="dropdown">\n' +
    '                  <i class="fas fa-ellipsis-v"></i>\n' +
    '                </a>\n' +
    '                <div class="dropdown-menu message-menu-font">\n' +
    '                  <button class="dropdown-item" id="dlt-btn-ID-VARIANT" onclick="deleteMsg(this)">Delete</button>\n' +
    '                </div>\n' +
    '              </div>\n' +
    '              <span class="time-date">TIME-VARIANT</span>\n' +
    '            </div>\n' +
    '          </div>';

/**
 * Create the message HTML code.
 * @param msgId  messageId
 * @param username posting user
 * @param text  message text
 * @param time  message time
 * @returns {string} of html code
 */
function createMessageHTML(msgId, username, text, time) {
    let message;
    if (username === 'Me') {
        message = templateMyMsg;
    } else {
        message = templateOtherMsg;
    }
    message = message.replaceAll('ID-VARIANT', msgId);
    if (isAdmin && username !== 'Me')
        showElement("#dlt-btn-" + msgId);
    else
        hideElement("#dlt-btn-" + msgId);
    if (!isAdmin && username !== 'Me')
        hideElement(".incoming-message .btn-group");
    message = message.replace('USERNAME-VARIANT', username);
    message = message.replace('MSG-VARIANT', text);
    message = message.replace('TIME-VARIANT', time);
    return message;
}

/**
 * Helper funciton to delete element e.
 * @param e the html element to be deleted
 */
function deleteMsg(e) {
    let root = e.parentNode.parentNode.parentNode.parentNode;
    let mid = root.getAttribute('id').replace('msg-', '');
    $.post("/chatroom/deleteMsg", {mid: mid, cid: chatroomId, user: username}, function () {
        root.parentNode.removeChild(root);
    }, "json");
}

/**
 * Store edit target.
 * @param e edit targer
 */
function saveEditTarget(e) {
    messageEditTarget = e;
}

/**
 * Edit message.
 * @param e the input message
 */
function editMessage(e) {
    let div = messageEditTarget.parentNode.parentNode.parentNode;
    let root = div.parentNode;
    let p = div.firstElementChild;
    let text = p.innerHTML;
    let end = text.indexOf('</b>: ');
    let editText = $('#edit-box-input').val();
    let msg = text.substring(0, end) + '</b>: ' + editText;
    let mid = root.getAttribute('id').replace('msg-', '');
    $.post("/chatroom/editMsg", {mid: mid, cid: chatroomId, user: username, message: editText}, function () {
        p.innerHTML = msg;
        $('#edit-box-input').val('');
        $('#close-edit-box').click();
    }, "json");
}

/**
 * Recall a user's message
 * @param e the message
 */
function recallMessage(e) {
    let root = e.parentNode.parentNode.parentNode.parentNode;
    let mid = root.getAttribute('id').replace('msg-', '');
    $.post("/chatroom/recallMsg", {mid: mid, cid: chatroomId, user: username}, function () {
    }, "json");
}


let templateRoom = '<div class="list-group-item ROOM-STYLE">\n' +
    '            <label>\n' +
    '              <input type="radio" name="ROOM-GROUP-NAME" value="ROOM-ID" id = "all-room-ROOM-ID">\n' +
    '              <span class="list-group-item-text">\n' +
    '                <i class="fa fa-fw"></i>\n' +
    '                ROOM-NAME<a href="#">\n' +
    '                <span data-toggle="tooltip" data-original-title="Share">\n' +
    '                </span></a></span></label></div>';

let templateJoinedRoom = '<div class="list-group-item ROOM-STYLE">\n' +
    '            <label>\n' +
    '              <input type="radio" name="ROOM-GROUP-NAME" value="ROOM-ID" id = "all-room-ROOM-ID">\n' +
    '              <span class="list-group-item-text">\n' +
    '                <i class="fa fa-fw"></i>\n' +
    '                ROOM-NAME<span class="badge badge-danger d-none">0</span></span></label></div>';

/**
 * Get all created rooms.
 */
function getAllRooms() {
    $.get('/chatroom/allChatrooms', function (data) {
        let allPublicGroup = $("#allPublicGroup");
        allPublicGroup.empty();
        let allPrivateGroup = $("#allPrivateGroup");
        allPrivateGroup.empty();
        for(let i = 0; i<data.length ;i++) {
            let room = data[i];
            let type = room.type;
            let roomTemp = templateRoom;
            roomTemp = roomTemp.replace('ROOM-NAME', room.name);
            roomTemp = roomTemp.replace('ROOM-ID',room.id);
            roomTemp = roomTemp.replace('all-room-ROOM-ID','all-room' + room.id);
            if(type === "Public") {
                if(room.name === "General") {
                    roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-success");
                } else {
                    roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-primary");
                }
                roomTemp = roomTemp.replace('ROOM-GROUP-NAME',"all-public-room");
                allPublicGroup.append(roomTemp);
            } else if (type === "Private") {
                roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-secondary");
                roomTemp = roomTemp.replace('ROOM-GROUP-NAME',"all-private-room");
                allPrivateGroup.append(roomTemp);
            }
        }
    },"json");
}

/**
 * Get the user's joined rooms
 */
function getJoinedRooms() {
    $.post("/chatroom/joinedRooms",{username:username},function(data) {
        let joinedRoomsGroup = $("#joinedRooms");
        let map = new Map()
        let reads = []
        let all = $(".badge-danger");
        for (let i = 0; i < all.length; i++) {
            reads.push(all[i].innerHTML);
        }
        joinedRoomsGroup.empty();
        if (data == null) return;
        for(let i = 0; i < data.length ;i++) {
            let room = data[i];
            let type = room.type;
            let roomTemp = templateJoinedRoom;
            roomTemp = roomTemp.replace('ROOM-NAME', room.name);
            roomTemp = roomTemp.replace('ROOM-ID',room.id);
            roomTemp = roomTemp.replace('all-room-ROOM-ID','joined-room' + room.id);
            if(type === "Public") {
                if(room.name === "General") {
                    roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-success");
                } else {
                    roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-primary");
                }
            } else if (type === "Private") {
                roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-secondary");
            } else {
                return null;
            }
            roomTemp = roomTemp.replace('ROOM-GROUP-NAME',"joined-room");
            joinedRoomsGroup.append(roomTemp);

            //Update the requestJoinList
            let requestIndex = requestRoomsList.indexOf(room.id);
            if (requestIndex >= 0) {
                requestRoomsList.splice(requestIndex, 1);
            }

            for (let i = 0; i < all.length; i++) {
                reads.push(all[i].innerHTML);
                if (reads[i] !== '0' && $(".badge-danger")[i] !== null && $(".badge-danger")[i] !== undefined) {
                    $(".badge-danger")[i].classList.remove("d-none");
                    $(".badge-danger")[i].innerHTML = reads[i];
                }

            }
        }
    },"json")
}

/**
 * Join & connect to a room.
 * @param roomId the room to join & connect to
 */
function joinAndConnectRoom (roomId) {
    $.post("/chatroom/joinedRooms", {username:username}, function(data) {
        let joinedRoomsGroup = $("#joinedRooms");
        joinedRoomsGroup.empty();
        if (data == null) return;
        for(let i = 0; i < data.length ;i++) {
            let room = data[i];
            let type = room.type;
            let roomTemp = templateJoinedRoom;
            roomTemp = roomTemp.replace('ROOM-NAME', room.name);
            roomTemp = roomTemp.replace('ROOM-ID',room.id);
            roomTemp = roomTemp.replace('all-room-ROOM-ID','joined-room' + room.id);
            if(type === "Public") {
                if(room.name === "General") {
                    roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-success");
                } else {
                    roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-primary");
                }
            } else if (type === "Private") {
                roomTemp = roomTemp.replace('ROOM-STYLE',"list-group-item-secondary");
            } else {
                return null;
            }
            roomTemp = roomTemp.replace('ROOM-GROUP-NAME',"joined-room");
            joinedRoomsGroup.append(roomTemp);
        }
        $('#joined-room' + roomId).prop("checked", true);
        connectToRoom();
    },"json");
}

let templateUser = '<div class="list-group-item USER-STYLE">\n' +
    '            <label>\n' +
    '              <input type="radio" name="USER-GROUP-NAME" value="USER-NAME" id = "user-USER-NAME">\n' +
    '              <span class="list-group-item-text">\n' +
    '                <i class="fa fa-fw"></i>USER-NAME-TYPE</span></label></div>\n';

/**
 * Get chat room's users.
 * @param admins
 */
function getChatRoomUsers() {
    $.get("/chatroom/allUsers",{chatroomId:chatroomId},function(data) {
        $("#btn-userDropdown").html("All users in " + chatroomName);
        $("#receiver").find('option').remove();
        $("#receiver").append('<option value="all">All users</option>');
        let userGroup = $("#userGroup");
        userGroup.empty();
        if(data == null) return;
        for(let i = 0; i < data.length ;i++) {
            let user = data[i];
            if (user !== username) {
                $("#receiver").append('<option value="' + user + '">' + user + '</option>');
            }
            let userTemp = templateUser;
            if(checkAdmin(user)) {
                userTemp = userTemp.replace('USER-NAME-TYPE', user + "(Admin)");
            } else {
                userTemp = userTemp.replace('USER-NAME-TYPE', user);
            }
            if(checkBlock(user)) {
                userTemp = userTemp.replace('USER-STYLE',"list-group-item-secondary");
            } else {
                userTemp = userTemp.replace('USER-STYLE',"list-group-item-info");
            }
            userTemp = userTemp.replace('USER-NAME',user);
            userTemp = userTemp.replace('user-USER-NAME','user' + user);
            userTemp = userTemp.replace('USER-GROUP-NAME',"all-users");
            userGroup.append(userTemp);
            $('input:radio[name = "all-users"]').change(function() {
                let user = $('input[name="all-users"]:checked').val();
                switchBlockButton(checkBlock(user));
                if(user === username) {
                    $("#btn-block-user").prop("disabled",true);
                    $("#btn-delete-user").prop("disabled",true);
                    $("#btn-ban-user").prop("disabled",true);
                    $("#btn-set-admin").prop("disabled",true);
                    $("#btn-report-user").prop("disabled",true);
                } else {
                    $("#btn-block-user").prop("disabled",false);
                    $("#btn-delete-user").prop("disabled",false);
                    $("#btn-ban-user").prop("disabled",false);
                    $("#btn-set-admin").prop("disabled",false);
                    $("#btn-report-user").prop("disabled",false);
                }
            })
        }
    },"json")
}

/**
 * Check whether user is admin
 * @param user
 * @param admins
 * @returns {boolean}
 */
function checkAdmin(user) {
    for(let i = 0; i < adminList.length; i++) {
        if(adminList[i] === user) {
            isAdmin = true;
            return isAdmin;
        }
    }
    isAdmin = false;
    return isAdmin;
}

/**
 * Check whether the user is blocked by the current user.
 * @param user
 * @returns {boolean}
 */
function checkBlock(user) {
    for(let i = 0; i< blockList.length;i++) {
        if(blockList[i] === user) {
            return true;
        }
    }
    return false;
}

/**
 * Toggles block button
 * @param isBlocked current button state
 */
function switchBlockButton(isBlocked) {
    if(isBlocked) {
        showElement($("#btn-unblock-user"));
        hideElement($("#btn-block-user"));
    } else {
        showElement($("#btn-block-user"));
        hideElement($("#btn-unblock-user"));
    }
}

/**
 * Close the connection.
 */
function closeConnection() {
    // $.post("/chatroom/offline", {username: username,reasonCode:2}, function () {
    // }, "json");
    signOut(event);
}

/**
 * Connect to a chatroom.
 */
function connectToRoom() {
    let roomId = $("#joinedRooms").find('input[name="joined-room"]:checked').val();
    if(!checkElement(roomId)) return;

    startChatArea();

    $.post("/chatroom/connect", {username: username,  chatroomId: roomId}, function (data) {
        chatroomId = roomId;
        chatroomType = data.type;
        chatroomName = data.name;
        adminList = data.admins;
        $("#chatRoomName").html(data.name + " (" + data.type + ")");
        $("#chatRoomDescription").html(data.description);

        loadUser();

        displayMessages(roomId);
        getChatRoomUsers();

        //Clear unread notification
        let unread = $("#joined-room" + chatroomId + " + span .badge-danger");
        unread.addClass("d-none");
        unread.html(0);

        $('#collapseTwo').removeClass('show');
        $('#collapseOne').addClass('show');
        $("#joinedRooms").find('input[name="joined-room"]:checked').prop("checked", false);
    }, "json");
}

/**
 * Check whether the element has value.
 * @param element
 * @returns {boolean}
 */
function checkElement(element) {
    return !(element === undefined || element === "");

}

/**
 * Validate field context
 * @param element
 * @returns {boolean}
 */
const validate=(element)=>{
    return !(element === "" || element == null);

};
