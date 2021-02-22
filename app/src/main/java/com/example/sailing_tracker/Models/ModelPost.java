package com.example.sailing_tracker.Models;

// Declare the variables for the model class
public class ModelPost {
    String pId;
    String pTitle;
    String pDescription;
    String pTime;
    String uid;
    String uEmail;
    String uDp;
    String uName;
    String pSessionID;
    String pSpeed;
    String pLikes;

    public ModelPost(){
        // Constructor
    }

    // Construct the variables
    public ModelPost(String pId, String pTitle, String pDescription, String pTime, String uid,
                     String uEmail, String uDp, String uName, String pSessionID, String pSpeed, String pLikes) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.pSessionID = pSessionID;
        this.pSpeed = pSpeed;
        this.pLikes = pLikes;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpSpeed() {
        return pSpeed;
    }

    public void setpSpeed(String pSpeed) {
        this.pSpeed = pSpeed;
    }

    public String getpSessionID() {
        return pSessionID;
    }

    public void setpSessionID(String pSessionID) {
        this.pSessionID = pSessionID;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpDescription() {
        return pDescription;
    }

    public void setpDescription(String pDescription) {
        this.pDescription = pDescription;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
