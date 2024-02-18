/** Vehicle Class
 *  This class is responsible for representing a vehicle.
 */

package com.example.carpoolbuddy.Vehicles;

import android.net.Uri;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import java.io.*;
import java.util.*;

public class Vehicle implements Serializable {
    protected String owner;
    protected String typeOfVehicle;
    protected String title;
    protected int capacity;
    protected String vehicleID;

    protected ArrayList<String> imagesOfVehicle;
    protected boolean isSetUp;

    public Vehicle(){
        owner = "XXXXXXX";
        this.typeOfVehicle = "Car";
        this.title = "Title of Vehicle";
        this.capacity = 2;
        vehicleID = UUID.randomUUID().toString();
        imagesOfVehicle = new ArrayList<>();
        isSetUp = false;
    }

    public Vehicle(User user){
        owner = user.getUid();
        this.typeOfVehicle = "";
        this.title = "";
        this.capacity = 0;
        vehicleID = UUID.randomUUID().toString();
        imagesOfVehicle = new ArrayList<>();
        isSetUp = false;
    }

    public Vehicle(User user, String type, String title, int capacity, boolean isSetUp){
        owner = user.getUid();
        this.typeOfVehicle = type;
        this.title = title;
        this.capacity = capacity;
        vehicleID = UUID.randomUUID().toString();
        imagesOfVehicle = new ArrayList<>();
        this.isSetUp = isSetUp;
    }

    /**editInfo
     * @param model
     * @param capacity
     *
     * This method is called when the owner needs to edit the information of their vehicle.
     * It is called from vehicleProfileActivity.
     */
    public void editInfo(String model, int capacity){
        this.typeOfVehicle = model;
        this.capacity = capacity;
    }

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

    public void addImagesOfVehicle(String imageOfVehicle) {
        this.imagesOfVehicle.add(imageOfVehicle);
    }

    public void removeImageOfVehicle(String imageOfVehicle) {
        this.imagesOfVehicle.remove(imageOfVehicle);
    }

    public void setSetUp() {
        isSetUp = true;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSetUp() {
        return isSetUp;
    }
}

