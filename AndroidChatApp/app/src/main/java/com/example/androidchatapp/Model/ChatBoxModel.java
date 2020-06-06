package com.example.androidchatapp.Model;

import java.util.List;

public class ChatBoxModel {
    private String roomID;
    private String chatBoxName;
    private List<UserModel> users;
    private String lastMessage;
    private String chatAvatar;
    private String modifiedTime;

    public ChatBoxModel() {}
    public ChatBoxModel(String roomID, String chatBoxName) {
        this.roomID = chatBoxName;
        this.chatBoxName = chatBoxName;
    }
    public String getRoomID() { return roomID; }

    public void setRoomID(String roomID) { this.roomID = roomID; }

    public String getChatBoxName() { return chatBoxName; }

    public void setChatBoxName(String chatBoxName) { this.chatBoxName = chatBoxName; }

    public List<UserModel> getUsers() { return users; }

    public void setUsers(List<UserModel> users) { this.users = users; }

    public String getChatAvatar() {
        return chatAvatar;
    }

    public void setChatAvatar(String avatar) {
        this.chatAvatar = avatar;
    }

    public String getModifiedTime() { return modifiedTime; }

    public void setModifiedTime(String modifiedTime) { this.modifiedTime = modifiedTime; }

    public String getLastMessage() { return lastMessage; }

    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
}
