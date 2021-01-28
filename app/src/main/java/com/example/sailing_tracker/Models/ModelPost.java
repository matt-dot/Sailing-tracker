package com.example.sailing_tracker.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ModelPost {
    String pId, pTitle, pDescription, pTime, uid, uEmail, uDp, uName;
    ArrayList<LatLng> latLongArrayList = new ArrayList<>(); // Create an ArrayList object




    public ModelPost(){
        // Constructor
    }

    public ModelPost(String pId, String pTitle, String pDescription, String pTime, String uid, String uEmail, String uDp, String uName, ArrayList<LatLng> latLongArrayList){
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescription = pDescription;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.latLongArrayList = latLongArrayList;



    }



    public ArrayList<LatLng> getLatLongArrayList() {
        return latLongArrayList;
    }

    public void setLatLongArrayList(ArrayList<LatLng> latLongArrayList) {
        this.latLongArrayList = latLongArrayList;
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
