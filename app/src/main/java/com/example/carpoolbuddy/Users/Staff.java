/** Teacher
 * This class represents a teacher and it inherits User.
 */

package com.example.carpoolbuddy.Users;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

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

    public void setInSchoolTitle(String inSchoolTitle) {
        this.inSchoolTitle = inSchoolTitle;
    }

    public void changeValues(String name, String location, String inSchoolTitle, String pic){
        changeAllValues(name, location, pic);
        setInSchoolTitle(inSchoolTitle);
    }

    public String getInSchoolTitle() {
        return inSchoolTitle;
    }
}
