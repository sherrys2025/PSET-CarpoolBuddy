package com.example.carpoolbuddy.Users;

import android.location.Location;
import android.net.Uri;

import com.example.carpoolbuddy.Vehicles.Vehicle;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User{
    protected String uid;
    protected String name;
    protected String email;
    protected String phoneNumber;
    protected String userType;
    protected ArrayList<Object> ownedVehicles;
    protected boolean setUp;

    public User(){
        uid = "XXXXXXXXX";
        name = "user name";
        email = "email@cis.edu.hk";
        phoneNumber = "88888888";
        setUp = false;
        ownedVehicles = new ArrayList<>();
        userType = "Staff";
    }

    public User(FirebaseUser user, String userType){
        uid = user.getUid();
        name = user.getDisplayName();
        email = user.getEmail();
        if (name==null) {
            int indexOfAt = email.indexOf('@');
            name = email.substring(0, indexOfAt);
        }
        setUp = false;
        phoneNumber = "88888888";
        ownedVehicles = new ArrayList<>();
        this.userType = userType;
    }

    public void changeAllValues(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<Object> getOwnedVehicles() {
        return ownedVehicles;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public boolean isSetUp() {
        return setUp;
    }

    public void setSetUp(boolean setUp) {
        this.setUp = setUp;
    }

    public String getUserType() {
        return userType;
    }
    //    public Location getLocation() {
//        return location;
//    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void addVehicle(Object vehicle){
        ownedVehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle){
        ownedVehicles.remove(vehicle);
    }


}
