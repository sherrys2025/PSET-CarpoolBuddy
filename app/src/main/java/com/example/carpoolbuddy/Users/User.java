package com.example.carpoolbuddy.Users;

import android.location.Location;
import android.net.Uri;

import com.example.carpoolbuddy.Vehicles.Ride;
import com.example.carpoolbuddy.Vehicles.Vehicle;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * This class represents a User. It includes their unique ID, name, email, contact methods, user type,
 * owned vehicles, and many more. Its child classes include Student, Staff, and Alum.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class User{
    protected String uid;
    protected String name;
    protected String email;
    protected String phoneNumber;
    protected String userType;
    protected ArrayList<Object> ownedVehicles;
    protected HashMap<String, ArrayList<Ride>> bookedRides;
    protected boolean setUp;

    public User(){
        uid = "XXXXXXXXX";
        name = "user name";
        email = "email@cis.edu.hk";
        phoneNumber = "88888888";
        setUp = false;
        ownedVehicles = new ArrayList<>();
        bookedRides = new HashMap<>();
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
        bookedRides = new HashMap<>();
        this.userType = userType;
    }

    /**
     * @param name user's name
     * @param phoneNumber user's phone number
     */
    public void changeAllValues(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /**getter methods**/

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

    public String getUserType() {
        return userType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public HashMap<String, ArrayList<Ride>> getBookedRides() {
        return bookedRides;
    }

    public ArrayList<Ride> getAllBookedRides() {
        ArrayList<Ride> allRides = new ArrayList<>();
        for (ArrayList<Ride> dateRides: bookedRides.values()){
            for (Ride r: dateRides){
                allRides.add(r);
            }
        }
        return allRides;
    }

    /**setter methods**/

    public void setSetUp(boolean setUp) {
        this.setUp = setUp;
    }


    public void addVehicle(Object vehicle){
        ownedVehicles.add(vehicle);
    }

    public void removeVehicle(Vehicle vehicle){
        ownedVehicles.remove(vehicle);
    }
    public void removeVehicle(int index){
        ownedVehicles.remove(index);
    }

    /**
     * Returns the index in which vehicle is contained within ownedVehicles; returns -1 if it does not exist
     *
     * @param vehicle a Vehicle
     * @return the index in which vehicle is contained within ownedVehicles, returns -1 if it does not exist
     */
    public int containsVehicle(Vehicle vehicle) {
        for (int i = 0; i < ownedVehicles.size(); i++) {
            HashMap<String, Object> temp = (HashMap<String, Object>) ownedVehicles.get(i);
            if (temp.get("vehicleID").equals(vehicle.getVehicleID())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Replace the ride from the date with the new ride. This method is used to update
     * any changes to a ride.
     *
     * @param date date of ride
     * @param ride ride to replace
     */
    public void replaceRide(String date, Ride ride) {
        ArrayList<Ride> temp = bookedRides.get(date);
        for (int i = 0; i < temp.size(); i++){
            if (temp.get(i).getRideID().equals(ride.getRideID())) {
                temp.set(i, ride);
                return;
            }
        }
    }

    /**
     * Adds a ride to user's list of rides. Checks if the ride already exists first. If the ride
     * already exists, replace the ride.
     *
     * @param ride Ride to add or replace
     * @param date Date of the ride
     */
    public void addRide(Ride ride, String date){
        ArrayList<Ride> temp = bookedRides.get(date);
        if (temp == null) {
            temp = new ArrayList<>();
        }

        if (containsRide(date, ride)) {
            replaceRide(date, ride);
        } else {
            temp.add(ride);
            bookedRides.put(date, temp);
        }
    }

    /**
     * Remove ride from the user's list of rides.
     * @param ride Ride to remove
     * @param date Date of the ride
     */
    public void removeRide(Object ride, String date){
        bookedRides.get(date).remove(ride);
    }

    /**
     * Remove all rides from a vehicle. This is used to clean out rides when a vehicle is deleted.
     * @param vehicle Vehicle that owns the rides.
     */
    public void removeRide(Vehicle vehicle) {
        HashMap<String, ArrayList<Ride>> newHash = new HashMap<>();
        for(String date: bookedRides.keySet()) {
            ArrayList<Ride> temp = bookedRides.get(date);
            ArrayList<Ride> toRemove = new ArrayList<>();
            for (Ride r: temp) {
                if (r.getVehicleID().equals(vehicle.getVehicleID())) {
                    toRemove.add(r);
                }
            }
            for (Ride remove: toRemove) {
                temp.remove(remove);
            }
            newHash.put(date, temp);
        }
        bookedRides = newHash;
    }

    /**
     * Returns whether a ride exists on a specific date from the user's list
     *
     * @param date date to check
     * @param ride ride to check
     * @return whether this ride exists on this date
     */
    public boolean containsRide(String date, Ride ride) {
        ArrayList<Ride> temp = bookedRides.get(date);
        if (temp == null) {
            return false;
        }
        for (Ride r: temp) {
            if (r.getRideID().equals(ride.getRideID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Replace vehicle from a certain index
     * @param index index to replace
     * @param vehicle vehicle to replace
     */
    public void replaceVehicle(int index, Object vehicle) {
        ownedVehicles.set(index, vehicle);
    }




}
