/** Alum
 * This class represents an alum and it inherits User.
 */
package com.example.carpoolbuddy;

import com.google.firebase.auth.FirebaseUser;

public class Alum extends User {
    protected int graduateYear;
    public Alum(FirebaseUser user, int graduateYear) {
        super(user);
        this.graduateYear = graduateYear;
    }
}
