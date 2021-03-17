package edu.rice.comp504.service;

import edu.rice.comp504.model.chatroom.AChatroom;
import edu.rice.comp504.model.message.AMessage;
import junit.framework.TestCase;
import edu.rice.comp504.model.user.*;

import java.util.ArrayList;
import java.util.List;

public class MediatorServiceTest extends TestCase{
    IMediatorService ms = new MediatorService();
    ChatroomService cs = ChatroomService.makeService();

    MessageService messageService = MessageService.makeService();
    ChatroomService chatroomService = ChatroomService.makeService();
    /**
     * Test register in MediatorService.
     */
    public void testRegister() {
        User testUser =  ms.register("adminTest", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        User testUser1 =  ms.register("adminTest", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        assertEquals("register successfully test","adminTest",testUser.getUsername());
        assertEquals("register with occupied name test",null,testUser1);
    }

    /**
     * Test login in MediatorService.
     */
    public void testLogin() {
        User userAdmin = ms.login("admin","admin");
        assertEquals("login with valid password test",userAdmin.getUsername(),"admin");
        userAdmin = ms.login("admin","ad");
        assertEquals("login with invalid password test",null,userAdmin);
    }


    /**
     * Test if a user's message can be deleted.
     */
    public void testDeleteMsg() {
        User userTest = ms.login("test","test");
        ms.requestJoin(userTest.getUsername(), Integer.toString(1)); // join "General" chatroom
        ms.sendMessage("text", "test message", userTest.getUsername(), 1, "all");
        List<AMessage> allMessages = cs.getMessages(1); // All messages in "General"
        AMessage msg = null;
        for (AMessage message : allMessages) {
            if (message.getMessageType().equals("text") && message.getData().equals("test message")) {
                msg = message; // find the message we add
            }
        }
        int size = allMessages.size();
        ms.deleteMessage(msg.getId(), 1, userTest.getUsername());
        assertEquals("messages after deleting", --size, cs.getMessages(1).size());
    }

    /**
     * Test if a user's message can be edited.
     */
    public void testEditMsg() {
        User userTest = ms.login("test","test");
        ms.requestJoin(userTest.getUsername(), Integer.toString(1));
        ms.sendMessage("text", "test message", userTest.getUsername(), 1, "all");
        List<AMessage> allMessages = cs.getMessages(1);
        AMessage msg = null;
        for (AMessage message : allMessages) {
            if (message.getMessageType().equals("text") && message.getData().equals("test message")) {
                msg = message;
            }
        }
        ms.editMessage(msg.getId(), 1, userTest.getUsername(), "new message text"); // edit msg's text to "new message text"
        assertEquals("messages after editing", "new message text", msg.getData());
    }

    /**
     * Test if a user's message can be recalled.
     */
    public void testRecallMsg() {
        User userTest = ms.login("test","test");
        ms.requestJoin(userTest.getUsername(), Integer.toString(1));
        int size = cs.getMessages(1).size();
        ms.sendMessage("text", "test message", userTest.getUsername(), 1, "all");
        List<AMessage> allMessages = cs.getMessages(1);
        AMessage msg = allMessages.get(allMessages.size()-1);
        assertEquals("messages before recalling", size + 1, allMessages.size()); // One is join notification, another is test message
        ms.recallMessage(msg.getId(), 1, userTest.getUsername());
        assertEquals("messages after recalling", size, cs.getMessages(1).size());
    }

    /**
     * Test block & unblock user and get block list in MediatorService.
     */
    public void testBlockUser() {
        User userAdmin = ms.login("admin","admin");
        User userTest = ms.login("test","test");
        ms.block(userAdmin.getUsername(),userTest.getUsername());
        List<String> blockList = userAdmin.getUsersBlocked();
        List<String> getBlockList = ms.getBlockList(userAdmin.getUsername());
        assertEquals("Get block list test",blockList,getBlockList);
        Boolean isBlocked = blockList.contains(userTest.getUsername());
        assertTrue(isBlocked);
        ms.unblock(userAdmin.getUsername(),userTest.getUsername());
        blockList = userAdmin.getUsersBlocked();
        isBlocked = blockList.contains(userTest.getUsername());
        assertFalse(isBlocked);
    }

    /**
     * Test warn user in MediatorService.
     */
    public void testWarnUser() {
        User warnUser =  ms.register("warnTest", "warn", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        ms.login("warnTest","warn");
        ms.warn(warnUser.getUsername(),1);
        int userStatus = warnUser.getStatus();
        assertEquals("warn user test",2,userStatus);
        ms.warn(warnUser.getUsername(),1);
        userStatus = warnUser.getStatus();
        assertEquals("User is warned twice",3,userStatus);
    }

    /**
     * Test set admin in MediatorService.
     */
    public void testSetAdmin() {
        User userTest = ms.login("test","test");
        ms.setAdmin(userTest.getUsername(),1);
        List<String> adminList = ms.getAdmin(1);
        boolean isAdmin = adminList.contains(userTest.getUsername());
        assertEquals("Set admin test",true,isAdmin);
    }

    /**
     * Test leave chat rooms.
     */
    public void testLeave() {
        User testLeaveUser =  ms.register("testLeave", "leave", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        ms.login("testLeave","leave");
        ms.leave(testLeaveUser.getUsername(),1,0);
        List<Integer> joinedRooms = testLeaveUser.getMyChatrooms();
        boolean isLeft = joinedRooms.contains(1);
        assertFalse(isLeft);
        List<String> userList = ms.getChatroom(1).getUsers();
        isLeft = userList.contains(testLeaveUser.getUsername());
        assertFalse(isLeft);
    }

    /**
     * Test leave all chat rooms.
     */
    public void testLeaveAll() {
        User testLeaveUser =  ms.register("testLeaveAll", "leave", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        ms.login("testLeaveAll","leave");
        ms.leaveAll(testLeaveUser.getUsername(),0);
        List<Integer> joinedRooms = testLeaveUser.getMyChatrooms();
        int number = joinedRooms.size();
        assertEquals("Leave all chat rooms test",0,number);
        List<String> userList = ms.getChatroom(1).getUsers();
        boolean isLeft = userList.contains(testLeaveUser.getUsername());
        assertFalse(isLeft);
    }

    /**
     * Test get user's profile.
     */
    public void testGetProfile() {
        User userAdmin = ms.login("admin","admin");
        User user = ms.getProfile(userAdmin.getUsername());
        assertEquals("Get user's profile test",user,userAdmin);
    }

    /**
     * Test creating a chatroom.
     */
    public void testCreateChatroom() {
        User userAdmin = ms.login("admin","admin");
        ms.leaveAll(userAdmin.getUsername(), 0);
        AChatroom room = ms.createChatroom(userAdmin.getUsername(), "test", "public", "desc");
        List<AChatroom> roomList = ms.getJoinedRooms(userAdmin.getUsername());
        assertEquals("Creating a room makes the right chatroom",room, roomList.get(0));
    }

    /**
     * Test get user's joined rooms.
     */
    public void testGetJoinedRooms() {
        User testJoinedRoom =  ms.register("joinRoom", "join", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        ms.login("joinRoom","join");
        List<AChatroom> roomList = ms.getJoinedRooms(testJoinedRoom.getUsername());
        AChatroom room = ms.getChatroom(1);
        assertEquals("Get user's joined rooms test",1,roomList.size());
        assertEquals("Get user's joined rooms test",room,roomList.get(0));
    }

    /**
     * Test requesting to join a room.
     */
    public void testRequestJoin() {
        User userAdmin = ms.login("admin","admin");
        ms.leaveAll(userAdmin.getUsername(),0);
        ms.requestJoin(userAdmin.getUsername(),"1");
        List<AChatroom> roomList = ms.getJoinedRooms(userAdmin.getUsername());
        AChatroom room = ms.getChatroom(1);
        assertEquals("Request join adds a room test",1, roomList.size());
        assertEquals("Request join adds correct room test",room,roomList.get(0));
    }

    /**
     * Test responding to a join request.
     */
    public void testRespondJoin() {
        User userAdmin = ms.login("admin","admin");
        ms.leaveAll(userAdmin.getUsername(),0);
        ms.respondToJoin(userAdmin.getUsername(),"1", "accept");
        List<AChatroom> roomList = ms.getJoinedRooms(userAdmin.getUsername());
        AChatroom room = ms.getChatroom(1);
        assertEquals("Respond join adds a room test",1,roomList.size());
        assertEquals("Respond join adds correct room test",room,roomList.get(0));
    }

    /**
     * Test getting a chatroom.
     */
    public void testGetChatroom() {
        User userAdmin = ms.login("admin","admin");
        ms.requestJoin(userAdmin.getUsername(),"1");
        List<AChatroom> roomList = ms.getJoinedRooms(userAdmin.getUsername());
        AChatroom room = ms.getChatroom(1);
        assertEquals("Get correct chatroom test", room, roomList.get(0));
    }


    /**
     * Test getting all chatrooms.
     */
    public void testGetAllChatroom() {
        IMediatorService ms = new MediatorService();
        User userAdmin = ms.login("admin", "admin");
        List<AChatroom> roomList = ms.getAllRooms();
        int expectSize = roomList.size() + 1;
        AChatroom room = ms.createChatroom(userAdmin.getUsername(), "test", "public", "desc");
        roomList = ms.getAllRooms();
        assertEquals("Get all chatroom for 2 rooms test", expectSize, roomList.size());
    }

    /**
     * Tests invite in MediatorService.
     */
    public void testInvite() {
        User userAdmin = ms.login("admin", "admin");
        User testUser1 =  ms.register("user1", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        User testUser2 =  ms.register("user2", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        AChatroom chatroom = ms.createChatroom("admin", "test", "private","for test");
        assertEquals("Public Chatroom admin invite test", false, ms.invite("test", 1, "admin"));
        assertEquals("Private Chatroom admin invite test", true, ms.invite("test", chatroom.getId(), "admin"));
        assertEquals("Private Chatroom admin invite a exist user test", false, ms.invite("test", chatroom.getId(), "admin"));
        assertEquals("Private Chatroom admin invite multiple users test", true, ms.invite("user1,user2", chatroom.getId(), "admin"));
    }

    /**
     * Tests ban in MediatorService.
     */
    public void testBan() {
        User testUser1 =  ms.register("user1", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        User testUser2 =  ms.register("user2", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        AChatroom chatroom = ms.createChatroom("user1", "test2", "private","for test");
        ms.invite("user2", chatroom.getId(), "admin");
        assertEquals("Admin ban user in chatroom", true, ms.ban("user1", chatroom.getId(), "user1"));
        assertEquals("Admin ban user who is not in the chatroom", false, ms.ban("abc", 2, "user1"));
    }

    /**
     * Tests ban in MediatorService.
     */
    public void testDeleteUser() {
        User testUser1 =  ms.register("temp1", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        User testUser2 =  ms.register("temp2", "admin", "admin", "admin", "0",
                "Male", "Rice University", "CS", "CS", "CS");
        AChatroom chatroom = ms.createChatroom("temp1", "test room", "private","for test");
        ms.invite("temp2", chatroom.getId(), "temp1");
        int users = ms.getChatroom(chatroom.getId()).getUsers().size();
        assertEquals("Admin delete user in chatroom", true, ms.deleteUser("temp2", chatroom.getId(), "temp1"));
        assertEquals("Admin delete user in chatroom", false, ms.deleteUser("abc", chatroom.getId(), "temp1"));
        assertEquals("Admin delete user in chatroom", users-1, ms.getChatroom(chatroom.getId()).getUsers().size());

    }

    /**
     * Tests testReportUser in MediatorService.
     */
    public void testReportUser() {
        int msg = ms.getChatroom(1).getMessages().size();
        ms.reportUser("test", "admin", 1);
        assertEquals("Report User test", msg+1, ms.getChatroom(1).getMessages().size());
    }

    /**
     * Tests send message in MediatorService.
     */
    public void testSendMessage() {
        // test text message
        AChatroom room = ms.createChatroom("admin", "t", "public", "");
        ms.sendMessage("text", "text data", "admin", room.getId(), "All users");
        List msgs = new ArrayList(room.getMessages());
        AMessage msg = (AMessage)msgs.get(msgs.size()-1);
        assertEquals("Send text message", "text", msg.getMessageType());
        assertEquals("Send text message", "text data", msg.getData());

        // test direct message
        ms.requestJoin("test", room.getId()+"");
        ms.sendMessage("text", "direct data", "admin", room.getId(), "test");
        msgs = new ArrayList(room.getMessages());
        msg = (AMessage)msgs.get(msgs.size()-1);
        assertEquals("Send direct message", "direct", msg.getMessageType());
        assertEquals("Send direct message", "direct data", msg.getData());

        // test notification message
        ms.sendMessage("notification", "notification data", "admin", room.getId(), "test");
        msgs = new ArrayList(room.getMessages());
        msg = (AMessage)msgs.get(msgs.size()-1);
        assertEquals("Send notification message", "notification", msg.getMessageType());
        assertEquals("Send notification message", "notification data", msg.getData());
    }

    /**
     * Tests offline in MediatorService.
     */
    public void testOffline() {
        int msg = ms.getChatroom(1).getMessages().size();
        ((MediatorService)ms).offline("admin");
        assertEquals("Offline test", msg+1, ms.getChatroom(1).getMessages().size());
    }

    /**
     * Tests for interfaces in MediatorService.
     */
    public void testInterface() {
        //AChatroom
        assertEquals("Test for AChatroom", "General", ChatroomService.makeService().get(1).getName());
        assertEquals("Test for AChatroom", "General", ChatroomService.makeService().get("General").getName());

        //
    }
}
