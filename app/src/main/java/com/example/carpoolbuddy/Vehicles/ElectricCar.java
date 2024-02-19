/** Electric Car
 * This class represents an electric car and it inherits Vehicle.
 */
package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class represents a ElectricCar. It includes their electric car model.
 * It inherits from Vehicle.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class ElectricCar extends Vehicle {
    private String eCarModel;

    public ElectricCar(){
        super();
        eCarModel = " ";
    }

    public ElectricCar(User user){
        super(user);
        eCarModel = " ";
    }

    public ElectricCar(Object obj){
        super(obj);
        HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;

        eCarModel = hashMap.get("ecarModel").toString();
    }

    public ElectricCar(User user, String vid, String model, String title, int capacity, String carModel, String carPlate, ArrayList<String> images, boolean isSetUp, ArrayList<Ride> rides){
        super(user, vid, model, title, capacity, carPlate, images, isSetUp, rides);
        this.eCarModel = carModel;
    }

    /**
     * getter method
     * @return eCarModel
     */
    public String getECarModel() {
        return eCarModel;
    }
}
