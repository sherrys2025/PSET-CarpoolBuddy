/** Motorcycle
 * This class represents a motorcycle and it inherits Vehicle.
 */

package com.example.carpoolbuddy;

import com.google.type.DateTime;

public class Motorcycle extends Vehicle{

    public Motorcycle(User user, String model, int capacity, double basePrice, DateTime date) {
        super(user, model, capacity, basePrice, date);
    }
}
