package edu.rice.comp504.model.user;

import java.util.ArrayList;

/**
 * Chat room users.
 */
public class User {
    private String username;
    private String name;
    private String pwd;
    private int age;
    private String gender;
    private String school;
    private String department;
    private String major;
    private int status = 0; // Use 0-3 to represent the status of user.
                            // 0 - offline, 1 - online, 2 - warn, 3 ban.
    private ArrayList<String> interests = new ArrayList<>();
    private ArrayList<Integer> myChatrooms = new ArrayList<>();
    private ArrayList<String> usersBlocked = new ArrayList<>();

    /**
     * Constructor.
     * @param username user's username
     * @param name user's name
     * @param pwd user's password
     * @param age user's age
     * @param gender user's gender
     * @param school user's school
     * @param department user's department
     * @param major user's major
     * @param interests user's interests
     */
    public User(String username, String name, String pwd, int age, String gender,
                String school, String department, String major, ArrayList interests) {
        this.username = username;
        this.name = name;
        this.pwd = pwd;
        this.age = age;
        this.gender = gender;
        this.school = school;
        this.department = department;
        this.major = major;
        this.interests = interests;
    }

    /**
     * Get user's username.
     * @return user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get user's name.
     * @return user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get user's password.
     * @return user's password
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * Get user's age.
     * @return user's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Get user's gender.
     * @return user's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Get user's school.
     * @return user's school
     */
    public String getSchool() {
        return school;
    }

    /**
     * Get user's department.
     * @return user's department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Get user's major.
     * @return user's major
     */
    public String getMajor() {
        return major;
    }


    /**
     * Get user's status.
     * @return status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the user's status.
     * @param status status number
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get user's interest list.
     * @return user's interest list
     */
    public ArrayList<String> getInterests() {
        return interests;
    }


    /**
     * Get user's chat room list.
     * @return user's chat room list
     */
    public ArrayList<Integer> getMyChatrooms() {
        return myChatrooms;
    }

    /**
     * Add user's chat room.
     * @param chatRoomId chat room id that user joined
     */
    public void addMyChatrooms(int chatRoomId) {
        this.myChatrooms.add(chatRoomId);
    }

    /**
     * Remove user's chat room.
     * @param chatRoomId id of the chatroom.
     */
    public void removeMyChatroom(int chatRoomId) {
        int index = this.myChatrooms.indexOf(chatRoomId);
        this.myChatrooms.remove(index);
    }

    /**
     * Get user's blocked users list.
     * @return user's blocked users list
     */
    public ArrayList<String> getUsersBlocked() {
        return usersBlocked;
    }

    /**
     * Add blocked user.
     * @param usersBlocked the user to be blocked.
     */
    public void setUsersBlocked(String usersBlocked) {
        this.usersBlocked.add(usersBlocked);
    }

    /**
     * Remove a blocked user.
     * @param userUnblocked the user to be unblocked.
     */
    public void removeUsersBlocked(String userUnblocked) {
        this.usersBlocked.remove(userUnblocked);
    }

}
