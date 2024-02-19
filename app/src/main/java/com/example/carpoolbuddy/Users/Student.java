/** Student
 * This class represents a student and it inherits User.
 */
package com.example.carpoolbuddy.Users;

import android.net.Uri;

import com.example.carpoolbuddy.Users.User;
import com.google.firebase.auth.FirebaseUser;
/**
 * This class represents a Student, inherited from User. It includes the student's graduating year.
 *
 * @author sherrys2025
 * @version 1.0
 */
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

    /**
     * Used when user updates from Settings
     * @param name name of user
     * @param graduatingYear graduating Year of user
     * @param phoneNumber phone number of user
     */
    public void changeValues(String name, int graduatingYear, String phoneNumber){
        changeAllValues(name, phoneNumber);
        this.graduatingYear = graduatingYear;
    }

    /**
     * getter method
     * @return graduating year
     */
    public int getGraduatingYear() {
        return graduatingYear;
    }
}
