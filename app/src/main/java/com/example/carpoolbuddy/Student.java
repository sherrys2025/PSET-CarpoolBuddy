/** Student
 * This class represents a student and it inherits User.
 */
package com.example.carpoolbuddy;

import com.google.firebase.auth.FirebaseUser;

public class Student extends User{
    protected int graduatingYear;
    public Student(FirebaseUser user, int graduatingYear) {
        super(user);
        this.graduatingYear = graduatingYear;
    }
}
