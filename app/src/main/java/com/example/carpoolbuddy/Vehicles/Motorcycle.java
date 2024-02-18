/** Motorcycle
 * This class represents a motorcycle and it inherits Vehicle.
 */

package com.example.carpoolbuddy.Vehicles;

import com.example.carpoolbuddy.Users.User;
import com.google.type.DateTime;

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

    public Motorcycle(User user, String model, String title, int capacity, String motorcycleModel, boolean isSetUp) {
        super(user, model, title, capacity, isSetUp);
        this.motorcycleModel = motorcycleModel;
    }

    public String getMotorcycleModel() {
        return motorcycleModel;
    }
}
