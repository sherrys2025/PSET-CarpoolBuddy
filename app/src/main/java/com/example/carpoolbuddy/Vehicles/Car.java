/** Car
 * This class represents a car and it inherits Vehicle.
 */
package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class represents a Car. It includes their car model.
 * It inherits from Vehicle.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class Car extends Vehicle {
    private String carModel;

    public Car(){
        super();
        this.carModel = " ";
    }

    public Car(Object obj){
        super(obj);
        HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;
        carModel = hashMap.get("carModel").toString();
    }

    public Car(User user){
        super(user);
        this.carModel = " ";
    }

    public Car(User user, String vid, String model, String title, int capacity, String carModel, String carPlate, ArrayList<String> images, boolean isSetUp, ArrayList<Ride> rides){
        super(user, vid, model, title, capacity, carPlate, images, isSetUp, rides);
        this.carModel = carModel;
    }

    /**
     * getter method
     * @return car model
     */
    public String getCarModel() {
        return carModel;
    }
}
