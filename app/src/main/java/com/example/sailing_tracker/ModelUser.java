package com.example.sailing_tracker;

import java.util.List;

public class ModelUser {
    String name, email, search, phone, image, boatClass, uid;

    public ModelUser(List<ModelUser> userList) {
    }

    public ModelUser(){}

    public ModelUser(String name, String email, String search, String phone, String image, String boatClass, String uid) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.phone = phone;
        this.image = image;
        this.boatClass = boatClass;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBoatClass() {
        return boatClass;
    }

    public void setBoatClass(String boatClass) {
        this.boatClass = boatClass;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
