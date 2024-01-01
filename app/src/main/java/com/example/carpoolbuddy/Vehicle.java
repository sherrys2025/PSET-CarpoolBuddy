/** Vehicle Class
 *  This class is responsible for representing a vehicle.
 */

package com.example.carpoolbuddy;

import android.location.Location;
import android.media.Image;

import com.google.type.DateTime;

import java.io.*;
import java.util.*;

public class Vehicle implements Serializable {
    protected String owner;
    protected String model;
    protected int capacity;
    protected String vehicleID;
    protected ArrayList<String> ridersUIDs;
    protected boolean open;
    protected double basePrice;
    protected DateTime date;
    protected Route route;


    public Vehicle(User user, String model, int capacity, double basePrice, DateTime date){
        owner = user.getUid();
        this.model = model;
        this.capacity = capacity;
        vehicleID = UUID.randomUUID().toString();
        ridersUIDs = new ArrayList<>();
        ridersUIDs.add(owner);
        open = true;
        this.basePrice = basePrice;
        this.date = date;
        route = new Route(user.getLocation());
    }

    /**editInfo
     * @param model
     * @param capacity
     * @param basePrice
     *
     * This method is called when the owner needs to edit the information of their vehicle.
     * It is called from vehicleProfileActivity.
     */
    public void editInfo(String model, int capacity, double basePrice){
        this.model = model;
        this.capacity = capacity;
        this.basePrice = basePrice;
    }

    /**setOpen
     *
     * This method is called if the owner wants to close the vehicle or if the owner wants
     * to reopen the vehicle. It is also called if the maximum capacity for riders is reached.
     */
    public void setOpen(){
        if (open) {
            open = false;
        } else {
            open = true;
        }
    }

    /**addRidersUIDs
     *
     * @param uid
     * @return
     *
     * This method takes in a user ID and if the vehicle is still open, the user will be
     * added to ridersUID. If not, it will return false to indicate process failed.
     */
    public boolean addRidersUIDs(String uid){
        if (open) {
            ridersUIDs.add(uid);
            if (ridersUIDs.size() == capacity) {
                setOpen();
            }
            return true;
        } else {
            return false;
        }
    }

    /**removeRidersUIDs
     *
     * @param uid
     *
     * This method removes a rider from the ridersUIDs. It is called if a rider does not
     * want to ride the vehicle anymore or if the owner removed a rider from their vehicle.
     */
    public void removeRidersUIDs(String uid){
            ridersUIDs.remove(uid);
    }


    /**
     * getter methods
     */
    public ArrayList<String> getRidersUIDs() {
        return ridersUIDs;
    }

    public DateTime getDate() {
        return date;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getModel() {
        return model;
    }

    public String getOwner() {
        return owner;
    }

    public String getVehicleID() {
        return vehicleID;
    }

}

