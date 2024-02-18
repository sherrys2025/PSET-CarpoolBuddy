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

    public Car(){
        super();
        this.carModel = " ";
    }

    public Car(User user){
        super(user);
        this.carModel = " ";
    }

    public Car(User user, String model, String title, int capacity, String carModel, boolean isSetUp){
        super(user, model, title, capacity, isSetUp);
        this.carModel = carModel;
    }

    public String getCarModel() {
        return carModel;
    }
}
