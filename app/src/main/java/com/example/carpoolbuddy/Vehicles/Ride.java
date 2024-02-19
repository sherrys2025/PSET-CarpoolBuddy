package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Vehicles.Vehicle;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * This class represents a Ride. It includes their date, riders, price, destinations.
 * It inherits from Vehicle.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class Ride extends Vehicle {
    protected Date date;
    protected ArrayList<String> ridersUIDs;
    protected double basePrice;
    protected ArrayList<String> destinations;
    protected boolean open;
    protected String rideID;

    public Ride(){
        super();
        date = new Date();
        ridersUIDs = new ArrayList<>();
        basePrice = 0.0;
        open = true;
        destinations = new ArrayList<>();
        destinations.add("1-Hau-Yuen-Road");
        rideID = UUID.randomUUID().toString();
        isSetUp = false;
    }

    /**
     * Moves information from a vehicle class to a ride class
     * @param vehicle the vehicle that the ride will take
     */
    public void setInformation(Vehicle vehicle){
        owner = vehicle.getOwner();
        typeOfVehicle = vehicle.getTypeOfVehicle();
        title = vehicle.getTitle();
        numberPlate = vehicle.getNumberPlate();
        capacity = vehicle.getCapacity();
        vehicleID = vehicle.getVehicleID();
        imagesOfVehicle = vehicle.getImagesOfVehicle();
        isSetUp = true;
    }


    /**
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

    /**
     * setter method
     * @param open boolean
     */
    public void setOpen(boolean open){
        this.open = open;
    }

    /**
     * This method takes in a user ID and if the vehicle is still open, the user will be
     * added to ridersUID. If not, it will return false to indicate process failed.
     * @param uid user ID to add
     * @return whether the user has been added
     */
    public boolean addRidersUIDs(String uid){
        if (open && !ridersUIDs.contains(uid)) {
            ridersUIDs.add(uid);
            if (ridersUIDs.size() == capacity) {
                setOpen();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method removes a rider from the ridersUIDs. It is called if a rider does not
     * want to ride the vehicle anymore or if the owner removed a rider from their vehicle.
     *
     * @param uid user ID to remove
     */
    public void removeRidersUIDs(String uid){
        destinations.remove(ridersUIDs.indexOf(uid));
        ridersUIDs.remove(uid);
    }

    /**getter methods**/
    public ArrayList<String> getRidersUIDs() {
        return ridersUIDs;
    }

    public Date getDate() {
        return date;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public ArrayList<String> getDestinations() {
        return destinations;
    }

    public String getRideID() {
        return rideID;
    }

    public boolean isOpen() {
        return open;
    }

    /**setter methods**/

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void addDestinations(String destinations) {
        if (!this.destinations.contains(destinations)) {
            this.destinations.add(this.destinations.size()-1, destinations);
        }
    }
}
