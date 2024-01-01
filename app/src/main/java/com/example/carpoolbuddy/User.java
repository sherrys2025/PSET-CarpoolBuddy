package com.example.carpoolbuddy;

import android.location.Location;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public abstract class User{
    protected String uid;
    protected String name;
    protected String email;
    protected Location location;
    protected ArrayList<String> ownedVehicles;

    public User(FirebaseUser user){
        uid = user.getUid();
        name = user.getDisplayName();
        email = user.getEmail();
        location = new Location("");
        ownedVehicles = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getOwnedVehicles() {
        return ownedVehicles;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public Location getLocation() {
        return location;
    }

    public void addVehicle(String id){
        ownedVehicles.add(id);
    }

//    public void removeVehicle(String id){
//        ownedVehicles.remove(id);
//    }
}
