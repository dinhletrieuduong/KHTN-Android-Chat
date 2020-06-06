package com.example.androidchatapp.Model;

import android.graphics.Bitmap;

public class UserModel {

    private String userID;
    private String userName;
    private String password;
    private String phone;
    private String age;
    private String gender;
    private String avatar;
//    private Bitmap bitmap;

    private String genderSelection;
    private String locationDistance;
    private String ageSelection;
    private String lastLocation;

    private String roomID;

    public UserModel() {}
    public UserModel(String userID, String userName) {
        this.userID = userID;
        this.userName = userName;
    }
    public UserModel(String userID, String userName, String avatar) {
        this.userID = userID;
        this.userName = userName;
        this.avatar = avatar;
    }


    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public String getUserName() { return userName; }

    public void setUserName(String userName) { this.userName = userName; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getAge() { return age; }

    public void setAge(String age) { this.age = age; }

    public String getGender() { return gender; }

    public void setGender(String gender) { this.gender = gender; }

    public String getAvatar() { return avatar; }

    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getLastLocation() { return lastLocation; }

    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }
//
//    public Bitmap getBitmap() { return bitmap; }
//
//    public void setBitmap(Bitmap bm) { this.bitmap = bm; }

    public String getRoomID() { return roomID; }

    public void setRoomID(String roomID) { this.roomID = roomID; }

    public String getGenderSelection() { return genderSelection; }

    public void setGenderSelection(String genderSelection) { this.genderSelection = genderSelection; }

    public String getLocationDistance() { return locationDistance; }

    public void setLocationDistance(String locationDistance) { this.locationDistance = locationDistance; }

    public String getAgeSelection() { return ageSelection; }

    public void setAgeSelection(String ageSelection) { this.ageSelection = ageSelection; }
}
