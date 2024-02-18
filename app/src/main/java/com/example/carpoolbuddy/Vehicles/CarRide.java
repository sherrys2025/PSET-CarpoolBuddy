package com.example.carpoolbuddy.Vehicles;

import com.google.type.DateTime;

import java.util.ArrayList;

public class CarRide extends Car{
    protected DateTime date;
    protected ArrayList<String> ridersUIDs;
    protected double basePrice;
    protected boolean open;

    public CarRide(){
        super();
        date = DateTime.getDefaultInstance();
        ridersUIDs = new ArrayList<>();
        basePrice = 0.0;
        open = true;
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

}
