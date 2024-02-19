/** Vehicle Class
 *  This class is responsible for representing a vehicle.
 */

package com.example.carpoolbuddy.Vehicles;

import android.net.Uri;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import org.checkerframework.checker.units.qual.K;

import java.io.*;
import java.util.*;

/**
 * This class represents a Vehicle. It includes their unique ID, owner, title, number plate,
 * vehicle type, and many more. Its child classes include Ride, Motorcycle, Car, and Electric Car.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class Vehicle implements Serializable {
    protected String owner;
    protected String typeOfVehicle;
    protected String title;
    protected String numberPlate;
    protected int capacity;
    protected String vehicleID;

    protected ArrayList<String> imagesOfVehicle;
    protected ArrayList<Ride> rides;
    protected boolean isSetUp;

    public Vehicle(){
        owner = "XXXXXXX";
        this.typeOfVehicle = "Car";
        this.title = "Title of Vehicle";
        this.capacity = 2;
        numberPlate = "XXXXXX";
        vehicleID = UUID.randomUUID().toString();
        imagesOfVehicle = new ArrayList<>();
        isSetUp = false;
        rides = new ArrayList<>();
    }

    public Vehicle(User user){
        owner = user.getUid();
        this.typeOfVehicle = "";
        this.title = "";
        this.capacity = 0;
        numberPlate = "";
        vehicleID = UUID.randomUUID().toString();
        imagesOfVehicle = new ArrayList<>();
        isSetUp = false;
        rides = new ArrayList<>();
    }

    public Vehicle(User user, String vid, String type, String title, int capacity, String numberPlate, ArrayList<String> images, boolean isSetUp, ArrayList<Ride> rides){
        owner = user.getUid();
        this.typeOfVehicle = type;
        this.title = title;
        this.capacity = capacity;
        this.numberPlate = numberPlate;
        vehicleID = vid;
        imagesOfVehicle = images;
        this.isSetUp = isSetUp;
        this.rides = rides;
        if (this.rides == null) {
            this.rides = new ArrayList<>();
        }
    }

    public Vehicle(Object obj){
        HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;
        owner = hashMap.get("owner").toString();
        typeOfVehicle = hashMap.get("typeOfVehicle").toString();
        title = hashMap.get("title").toString();
        long capacityLong = (long) hashMap.get("capacity");
        capacity = (int) capacityLong;
        numberPlate = hashMap.get("numberPlate").toString();
        vehicleID = hashMap.get("vehicleID").toString();
        imagesOfVehicle = (ArrayList<String>) hashMap.get("imagesOfVehicle");
        this.isSetUp = Boolean.valueOf(hashMap.get("setUp").toString());
        this.rides = (ArrayList<Ride>) hashMap.get("rides");
        if (rides == null) {
            rides = new ArrayList<>();
        }
    }

    /**
     * This method is called when the owner needs to edit the information of their vehicle.
     * It is called from vehicleProfileActivity.
     *
     * @param model vehicle's model
     * @param capacity vehicle's capacity
     */
    public void editInfo(String model, int capacity){
        this.typeOfVehicle = model;
        this.capacity = capacity;
    }

    /**getter methods**/

    public int getCapacity() {
        return capacity;
    }

    public String getTypeOfVehicle() {
        return typeOfVehicle;
    }

    public String getOwner() {
        return owner;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public ArrayList<String> getImagesOfVehicle() {
        return imagesOfVehicle;
    }
    public String getTitle() {
        return title;
    }

    public boolean isSetUp() {
        return isSetUp;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public ArrayList<Ride> getRides() {
        return rides;
    }


    /**
     * Adds an image of vehicle to the list of images
     * @param imageOfVehicle to-be-added image's unique ID
     */
    public void addImagesOfVehicle(String imageOfVehicle) {
        this.imagesOfVehicle.add(imageOfVehicle);
    }

    /**
     * Removes an image of vehicle from the list
     * @param imageOfVehicle to-be-removed image's unique ID
     */
    public void removeImageOfVehicle(String imageOfVehicle) {
        this.imagesOfVehicle.remove(imageOfVehicle);
    }

    /**
     * Returns whether an image exists within the vehicle
     * @param imageOfVehicle image's unique ID
     * @return whether an image exists within the vehicle
     */
    public boolean containsImageOfVehicle(String imageOfVehicle){
        return this.imagesOfVehicle.contains(imageOfVehicle);
    }

    /**setter method**/
    public void setSetUp() {
        isSetUp = true;
    }

    /**
     * Adds a ride to the vehicle
     * @param ride the ride to be added
     */
    public void addRide(Ride ride) {
        this.rides.add(ride);
    }

    /**
     * Remove a ride from the vehicle
     * @param ride the ride to be removed
     */
    public void removeRide(Ride ride) {
        this.rides.remove(ride);
    }

    /**
     * Returns whether a ride is already in a vehicle, uses HashMap
     * @param ride ride to be checked
     * @return whether a ride is already in a vehicle
     */
    public int containsRide(Ride ride) {
        for (int i = 0; i < rides.size(); i++) {
            Object temp = rides.get(i);
            if (((HashMap<String, Object>) temp).get("rideID").equals(ride.getRideID())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether a ride is already in a vehicle
     * @param ride Ride to check
     * @param random a number to differentiate method signature
     * @return whether a ride is already in a vehicle
     */
    public int containsRide(Ride ride, int random) {
        for (int i = 0; i < rides.size(); i++) {
            Object temp = rides.get(i);
            if (ride.getRideID().equals(ride.getRideID())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether this vehicle occupies a ride
     * @param ride the ride to be checked
     * @return whether this vehicle occupies a ride
     */
    public boolean occupiedRide(Ride ride){
        for (int i = 0; i < rides.size(); i++) {
            Object temp = rides.get(i);
            HashMap<String, Object> temp2 = (HashMap<String, Object>) temp;

            if (temp2.get("date").equals(ride.getDate())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Update a ride within the list
     * @param ride ride to be updated
     */
    public void updateRide(Ride ride){
        int index = containsRide(ride, 1);
        if (index >= 0) {
            rides.set(index, ride);
            return;
        }
        rides.add(ride);
    }

}

