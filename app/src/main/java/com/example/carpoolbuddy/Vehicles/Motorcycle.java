/** Motorcycle
 * This class represents a motorcycle and it inherits Vehicle.
 */

package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents a Motorcycle. It includes their motorcycle model.
 * It inherits from Vehicle.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class Motorcycle extends Vehicle {

    private String motorcycleModel;
    public Motorcycle(){
        super();
        motorcycleModel = " ";
    }

    public Motorcycle(User user){
        super(user);
        motorcycleModel = " ";
    }

    public Motorcycle(Object obj){
        super(obj);
        HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;
        motorcycleModel = hashMap.get("motorcycleModel").toString();
    }

    public Motorcycle(User user, String vid, String model, String title, int capacity, String motorcycleModel, String carPlate, ArrayList<String> images, boolean isSetUp, ArrayList<Ride> rides) {
        super(user, vid, model, title, capacity, carPlate, images, isSetUp, rides);
        this.motorcycleModel = motorcycleModel;
    }

    /**
     * getter method
     * @return motorcycle model
     */
    public String getMotorcycleModel() {
        return motorcycleModel;
    }
}
