/** Teacher
 * This class represents a teacher and it inherits User.
 */

package com.example.carpoolbuddy.Users;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

/**
 * This class represents a Staff, inherited from User. It includes the staff's in school title.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class Staff extends User {
    protected String inSchoolTitle;

    public Staff(){
        super();
        this.inSchoolTitle = "N/A";
    }
    public Staff(FirebaseUser user) {
        super(user, "Staff");
        inSchoolTitle = "N/A";
    }
    public Staff(FirebaseUser user, String title) {
        super(user, "Staff");
        inSchoolTitle = title;
        userType = "Staff";
    }

    /**
     * Setter method
     * @param inSchoolTitle in school title
     */
    public void setInSchoolTitle(String inSchoolTitle) {
        this.inSchoolTitle = inSchoolTitle;
    }

    /**
     * Change values of user. Used when user updates from Settings
     * @param name name of user
     * @param inSchoolTitle staff's in school title
     * @param phoneNumber user's phone number
     */
    public void changeValues(String name, String inSchoolTitle, String phoneNumber){
        changeAllValues(name, phoneNumber);
        setInSchoolTitle(inSchoolTitle);
    }

    /**
     * getter method
     * @return in school title
     */
    public String getInSchoolTitle() {
        return inSchoolTitle;
    }
}
