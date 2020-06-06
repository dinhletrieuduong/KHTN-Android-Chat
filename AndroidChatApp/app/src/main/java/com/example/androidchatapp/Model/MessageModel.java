package com.example.androidchatapp.Model;
public class MessageModel {
    private String _id;
    private String userID;
    private String userName;
    private String message;
    private boolean isSent;
    private boolean isSeen;
    private boolean isImage;
    private String sentTime;

    public MessageModel() {}

    public MessageModel(String userID, String fromUser, String message, boolean isSent, boolean isImage) {
        this.userID = userID;
        this.userName = fromUser;
        this.message = message;
        this.isImage = isImage;
        this.isSent = isSent;
    }

    public String get_ID() { return _id; }

    public void set_ID(String id) { this._id = id; }

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public boolean isSent() { return isSent; }

    public void setSent(boolean isSent) { this.isSent = isSent; }

    public boolean isImage() { return isImage; }

    public void setImage(boolean isImage) { this.isImage = isImage; }

    public String getSentTime() { return sentTime; }

    public void setSentTime(String time) { this.sentTime = time; }

    public boolean isSeen() { return isSeen; }

    public void setSeen(boolean seen) { isSeen = seen; }
}
