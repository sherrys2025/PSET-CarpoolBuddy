package com.example.carpoolbuddy.Users;

import android.location.Location;
import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class User{
    protected String uid;
    protected String name;
    protected String email;
    protected Location location = null;
    protected String userType;
    protected String profilePic;
    protected ArrayList<String> ownedVehicles;
    protected boolean setUp;

    public User(){
        uid = "XXXXXXXXX";
        name = "user name";
        email = "email@cis.edu.hk";
        profilePic = "profilepicture.png";
        setUp = false;
        ownedVehicles = new ArrayList<>();
        userType = "Staff";
    }

    public User(FirebaseUser user, String userType){
        uid = user.getUid();
        name = user.getDisplayName();
        email = user.getEmail();
        profilePic = "profilepic.png";
        if (name==null) {
            int indexOfAt = email.indexOf('@');
            name = email.substring(0, indexOfAt);
        }
        setUp = false;
        ownedVehicles = new ArrayList<>();
        this.userType = userType;
    }

    public void changeAllValues(String name, String loca, String photo){
        this.name = name;
        this.location = new Location(loca);
        this.profilePic = photo;
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


    public String getProfilePic() {
        return profilePic;
    }


    public void addVehicle(String id){
        ownedVehicles.add(id);
    }

//    public void removeVehicle(String id){
//        ownedVehicles.remove(id);
//    }


}
