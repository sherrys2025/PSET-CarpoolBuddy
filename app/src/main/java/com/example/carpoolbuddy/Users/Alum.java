/** Alum
 * This class represents an alum and it inherits User.
 */
package com.example.carpoolbuddy.Users;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

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

    public void changeValues(String name, int graduateYear, String phoneNumber){
        changeAllValues(name, phoneNumber);
        this.graduateYear = graduateYear;
    }

    public int getGraduateYear() {
        return graduateYear;
    }
}
