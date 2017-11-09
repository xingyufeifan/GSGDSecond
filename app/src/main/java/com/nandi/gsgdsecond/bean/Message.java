package com.nandi.gsgdsecond.bean;

import java.io.Serializable;

/**
 * Created by ChenPeng on 2017/10/12.
 */

public class Message implements Serializable{
    private int userId;
    private int roomId;
    private String inviteMan;
    private String message;

    public Message() {
    }

    public Message(int userId, int roomId, String inviteMan, String message) {
        this.userId = userId;
        this.roomId = roomId;
        this.inviteMan = inviteMan;
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getInviteMan() {
        return inviteMan;
    }

    public void setInviteMan(String inviteMan) {
        this.inviteMan = inviteMan;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
