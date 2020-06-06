package com.example.androidchatapp.Model;

public class SettingModel {
    private String Name;
    private String Phone;
    private String Image;
    private String icon_right;
    public SettingModel(String name, String phone, String image, String icon_right){
        this.Name = name;
        this.Phone = phone;
        this.Image = image;
        this.icon_right = icon_right;
    }

    public String getIcon_right() {
        return icon_right;
    }

    public void setIcon_right(String icon_right) {
        this.icon_right = icon_right;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


}
