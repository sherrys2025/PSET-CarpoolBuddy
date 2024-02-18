/** Electric Car
 * This class represents an electric car and it inherits Vehicle.
 */
package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

import java.util.ArrayList;
import java.util.UUID;

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

    public ElectricCar(User user, String model, String title, int capacity, String carModel, boolean isSetUp){
        super(user, model, title, capacity, isSetUp);
        this.eCarModel = carModel;
    }

    public String getECarModel() {
        return eCarModel;
    }
}
