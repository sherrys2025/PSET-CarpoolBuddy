package com.example.carpoolbuddy.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carpoolbuddy.Activity.Explore.VehicleProfileActivity;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

/**
 * This is an Activity class that lets users set up or edit their profile information.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class SettingsActivity extends AppCompatActivity {
    private ShapeableImageView profilePic;
    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
    private String userId;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private TextView errorMsg, email;
    private TextInputLayout nameLabel, uniqueFieldLabel, userTypeLabel, uniqueIdLabel, numOfVehiclesLabel, phoneNumberLabel;
    private TextInputEditText name, uniqueField, phoneNumber, uniqueId, userType, numOfVehicles;

    private ShapeableImageView editImage;
    private ImageButton returnButton;
    private Button logOutButton, updateAll;
    private ProgressBar progressBar;
    private View[] listOfViews;
    private Uri imageUri;

    private final int GALLERY_INT_REQ_CODE = 1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // set up views
        findViewsById();
        setInvisible();

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userId = getIntent().getStringExtra("uid");
        findUser();

    }

    /**
     * Set up the views by their Ids.
     */
    private void findViewsById(){
        progressBar = findViewById(R.id.progressBarSettings);
        nameLabel = findViewById(R.id.nameSettingEdit);
        name = findViewById(R.id.nameSettingEditable);
        uniqueField = findViewById(R.id.uniqueFieldSettingsEditable);
        uniqueFieldLabel = findViewById(R.id.uniqueFieldSettings);
        errorMsg = findViewById(R.id.errorMsgSettings);
        returnButton = findViewById(R.id.backButtonSettings);
        updateAll = findViewById(R.id.update);
        email = findViewById(R.id.emailSetting);
        logOutButton = findViewById(R.id.logOutSettings);
        userTypeLabel = findViewById(R.id.userTypeSettings);
        userType = findViewById(R.id.userTypeDisplaySettings);
        profilePic = findViewById(R.id.profilePicSettings);
        uniqueId = findViewById(R.id.userIdSettingsDisplay);
        uniqueIdLabel = findViewById(R.id.userIdSettings);
        editImage = findViewById(R.id.editSettings);
        numOfVehicles = findViewById(R.id.numberOfVehiclesSettingsDisplay);
        numOfVehiclesLabel = findViewById(R.id.numberOfVehiclesSettings);
        phoneNumber = findViewById(R.id.phoneNumberSettingsEditable);
        phoneNumberLabel = findViewById(R.id.phoneNumberSettings);
        listOfViews = new View[]{
                name, nameLabel, uniqueField, uniqueFieldLabel, errorMsg, returnButton, updateAll, email,
                logOutButton, userTypeLabel, profilePic, uniqueId, uniqueIdLabel, editImage, userType, numOfVehicles, numOfVehiclesLabel,
                phoneNumber, phoneNumberLabel};
    }

    /**
     * Retrieve user and its child object from Firebase.
     */
    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);
                    //create the child class based on user's user type
                    switch (user.getUserType()) {
                    case "Student":
                        studentUser = document.toObject(Student.class);
                        break;
                    case "Alum":
                        alumUser = document.toObject(Alum.class);
                        break;
                    default:
                        staffUser = document.toObject(Staff.class);
                      }
                    setFields();
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * Set up the views with the correct user information.
     */
    private void setFields(){
        name.setText(user.getName());
        logOutButton.setVisibility(View.VISIBLE);
        if (user.getPhoneNumber().equals("88888888")) { // if user is not yet set up
            returnButton.setVisibility(View.INVISIBLE);
            updateAll.setText("Create Profile");
        } else {
            returnButton.setVisibility(View.VISIBLE);
            updateAll.setText("Update Profile");
        }
        phoneNumber.setText(user.getPhoneNumber());
        setImage(); //set up profile picture
        switch (user.getUserType()) { //set up special field based on the type of student
            case "Student":
                setFieldsHelper(studentUser);
                break;
            case "Alum":
                setFieldsHelper(alumUser);
                break;
            default:
                setFieldsHelper(staffUser);
        }
        email.setText(user.getEmail());
        uniqueId.setText(user.getUid().substring(0,23) + "...");
        numOfVehicles.setText(Integer.toString(user.getOwnedVehicles().size()));
        setVisible(); // stop progress bar and display fields
    }

    /**
     * Set up the special field for a student user.
     * @param user Student object
     */
    private void setFieldsHelper(Student user){
        userType.setText("Student");
        uniqueFieldLabel.setHint("Graduating Year");
        uniqueField.setText(Integer.toString(studentUser.getGraduatingYear()));
    }

    /**
     * Set up the special field for an alum user.
     * @param user Alum object
     */
    private void setFieldsHelper(Alum user){
        userType.setText("Alum");
        uniqueFieldLabel.setHint("Graduate Year");
        uniqueField.setText(Integer.toString(alumUser.getGraduateYear()));
    }

    /**
     * Set up the special field for a staff user.
     * @param user Staff object
     */
    private void setFieldsHelper(Staff user){
        uniqueFieldLabel.setHint("In School Title");
        uniqueField.setText(staffUser.getInSchoolTitle());
    }

    /**
     * Retrieve image from FirebaseStorage and set up the profile picture.
     */
    private void setImage(){
        StorageReference imageToSet = firebaseStorage.getReference("image/" + user.getUid());
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            File finalLocalFile = localFile;
            imageToSet.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                imageUri = Uri.fromFile(finalLocalFile);
                profilePic.setImageURI(imageUri);
            }).addOnFailureListener(exception -> Log.w("Error retrieving image", exception));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Called when user clicks on the Profile picture to upload a new image
     * @param v View that called this method
     */
    public void uploadPic(View v){
        choosePicture();
    }

    /**
     * Retrieve image that the user has selected
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    /**
     * Upload image to FireBase Storage
     */
    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        StorageReference picRef = storageReference.child("image/" + user.getUid());

        picRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(SettingsActivity.this, "Failed to Upload", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Percentage Uploaded: " + (int) progressPercent + "%");
                });
    }

    /**
     * Start intent to select image from phone album.
     */
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    /**
     * After user presses Create Profile or Update Profile, save all entered information back to Firebase
     * @param v View that called this method
     */
    public void updateAllSettings(View v){
        user.setSetUp(true);
        String newName = name.getText().toString();
        String unique = uniqueField.getText().toString();
        String pN = phoneNumber.getText().toString();

        // update user based on the child user
        switch (user.getUserType()) {
            case "Student":
                if (!updateAllSettingsStudent(newName, unique, pN)) {
                    return;
                }
                break;
            case "Alum":
                if (!updateAllSettingsAlum(newName, unique, pN)) {
                    return;
                }
                break;
            default:
                staffUser.changeValues(newName, unique, pN);
                updateFirebase(staffUser);
        }

        //display saved
        Toast.makeText(SettingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        returnToProfile();
    }

    /**
     * Verifies phone number is valid
     * @param pN the entered phone number
     * @return whether the phone number is valid
     */
    private boolean checkPhone(String pN){
        return pN.length()!=8;
    }

    /**
     * Update all fields for Student User
     * @param newName name of user
     * @param unique unique field of user (graduating year)
     * @param pN phone number of user
     * @return whether update has been successful or not
     */
    private boolean updateAllSettingsStudent(String newName, String unique, String pN){
        try{
            int valueOfUnique = Integer.parseInt(unique);
            int phoneNum = Integer.parseInt(pN);
            if (checkPhone(pN)) {throw new NumberFormatException();}
            studentUser.changeValues(newName, valueOfUnique, pN);
            updateFirebase(studentUser);
            return true;
        } catch (NumberFormatException e){
            errorMessage("Please try again!");
            return false;
        }
    }

    /**
     * Update all fields for Alum User
     * @param newName name of user
     * @param unique unique field of user (graduate year)
     * @param pN phone number of user
     * @return whether update has been successful or not
     */
    private boolean updateAllSettingsAlum(String newName, String unique, String pN){
        try{
            int valueOfUnique = Integer.parseInt(unique);
            int phoneNum = Integer.parseInt(pN);
            if (checkPhone(pN)) {throw new NumberFormatException();}
            alumUser.changeValues(newName, valueOfUnique, pN);
            updateFirebase(alumUser);
            return true;
        } catch (NumberFormatException e){
            errorMessage("Please try again!");
            return false;
        }
    }

    /**
     * Update student user to Firebase
     * @param user Student object
     */
    private void updateFirebase(Student user){
        firestore.collection("users").document(user.getUid()).set(user)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update Alum user to Firebase
     * @param user Alum object
     */
    private void updateFirebase(Alum user){
        firestore.collection("users").document(user.getUid()).set(user)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update staff user to Firebase
     * @param user Staff object
     */
    private void updateFirebase(Staff user){
        firestore.collection("users").document(user.getUid()).set(user)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * When user presses logout, asks for confirmation then log out;
     * Change intent to AuthActivity
     * @param v View that called this method
     */
    public void logOut(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); //build alert
        builder.setTitle("Log out");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", (dialog, which) -> { //if yes,
            FirebaseAuth.getInstance().signOut();
            if (AuthActivity.mGoogleSignInClient!=null) {
                AuthActivity.mGoogleSignInClient.signOut();
            }
            logOutInstance();   // change intents
        });

        builder.setNegativeButton("No", (dialog, which) -> { // if no,
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();                                       // display alert
    }

    /**
     * Display error message.
     * @param message error message
     */
    public void errorMessage(String message){
        errorMsg.setText(message);
    }

    /**
     * Start new intent to AuthActivity.
     */
    private void logOutInstance(){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    /**
     * If user presses the back button, return to VehicleProfile page.
     * @param v View that called this method.
     */
    public void backToMain(View v) {
        returnToProfile();
    }

    /**
     * Return to VehicleProfile page by starting new intent
     */
    private void returnToProfile(){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * Set fields to be invisible while progress bar is loading.
     */
    private void setInvisible(){
        for (View v: listOfViews) {
            v.setVisibility(View.INVISIBLE);
        }
        progressBar.animate();
    }

    /**
     * Set fields to be visible and hide progress bar.
     */
    private void setVisible(){
        for (View v: listOfViews) {
            v.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

}