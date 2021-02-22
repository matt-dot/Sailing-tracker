package com.example.sailing_tracker.Models;

import java.util.List;

public class ModelUser {
    // Variables to be retrieved from database
    String name, email, image, boatClass, uid;

    public ModelUser(List<ModelUser> userList) {
        // Required empty public constructor
    }

    public ModelUser(){}

    // Init
    public ModelUser(String name, String email,  String image, String boatClass, String uid) {
        this.name = name;
        this.email = email;
        this.image = image;
        this.boatClass = boatClass;
        this.uid = uid;

    }

    // Series of generated getters and setters
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
