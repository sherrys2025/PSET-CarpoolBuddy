/** Teacher
 * This class represents a teacher and it inherits User.
 */

package com.example.carpoolbuddy;

import com.google.firebase.auth.FirebaseUser;

public class Teacher extends User{
    protected String inSchoolTitle;
    public Teacher(FirebaseUser user, String title) {
        super(user);
        inSchoolTitle = title;
    }


}
