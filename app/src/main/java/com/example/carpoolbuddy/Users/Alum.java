/** Alum
 * This class represents an alum and it inherits User.
 */
package com.example.carpoolbuddy.Users;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
/**
 * This class represents an Alum, inherited from User. It includes the alum's graduated year.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class Alum extends User {
    protected int graduateYear;

    public Alum(){
        super();
        graduateYear = 2023;
    }
    public Alum(FirebaseUser user, int graduateYear) {
        super(user, "Alum");
        this.graduateYear = graduateYear;
    }

    public Alum(FirebaseUser user) {
        super(user, "Alum");
        this.graduateYear = 2023;
    }

    /**
     * Used when user updates from Settings
     * @param name name of user
     * @param graduateYear the year in which the alum graduated
     * @param phoneNumber user's phone number
     */
    public void changeValues(String name, int graduateYear, String phoneNumber){
        changeAllValues(name, phoneNumber);
        this.graduateYear = graduateYear;
    }

    /**
     * getter method
     * @return graduate year
     */
    public int getGraduateYear() {
        return graduateYear;
    }
}
