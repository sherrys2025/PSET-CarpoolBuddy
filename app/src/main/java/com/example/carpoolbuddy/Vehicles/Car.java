/** Car
 * This class represents a car and it inherits Vehicle.
 */
package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.UUID;

public class Car extends Vehicle {
    private String carModel;

    public Car(User user, String model, int capacity, double basePrice, DateTime date, String carModel){
        super(user, model, capacity, basePrice, date);
        owner = user.getUid();
        this.model = model;
        this.capacity = capacity;
        vehicleID = UUID.randomUUID().toString();
        ridersUIDs = new ArrayList<>();
        ridersUIDs.add(owner);
        open = true;
        this.basePrice = basePrice;
        this.date = date;
        //route = new Route(user.getLocation());
        this.carModel = carModel;
    }

}
