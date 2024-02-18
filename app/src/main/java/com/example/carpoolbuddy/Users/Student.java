/** Student
 * This class represents a student and it inherits User.
 */
package com.example.carpoolbuddy.Users;

import android.net.Uri;

import com.example.carpoolbuddy.Users.User;
import com.google.firebase.auth.FirebaseUser;

public class Student extends User {
    protected int graduatingYear;

    public Student(){
        super();
        this.graduatingYear = 2024;
    }
    public Student(FirebaseUser user) {
        super(user, "Student");
        this.graduatingYear = 2024;
    }
    public Student(FirebaseUser user, int graduatingYear) {
        super(user, "Student");
        this.graduatingYear = graduatingYear;
    }

    public void changeValues(String name, int graduatingYear, String phoneNumber){
        changeAllValues(name, phoneNumber);
        this.graduatingYear = graduatingYear;
    }

    public int getGraduatingYear() {
        return graduatingYear;
    }
}
