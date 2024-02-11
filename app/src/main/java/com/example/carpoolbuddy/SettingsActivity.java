package com.example.carpoolbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {
    private ShapeableImageView profilePic;
    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
    private String userId, image;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private EditText name, locationOfUser, uniqueField, location;
    private TextView uniqueFieldLabel, errorMsg, email, userTypeLabel, uniqueIdLabel, uniqueId,
            userType, numOfVehiclesLabel, numOfVehicles, locationLabel;

    private ShapeableImageView editImage;
    private ImageButton returnButton;
    private ImageView editIcon;
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

        progressBar = findViewById(R.id.progressBarSettings);

        name = findViewById(R.id.nameSettingEdit);
        locationOfUser = findViewById(R.id.locationSettings2);
        uniqueField = findViewById(R.id.uniqueSettings2);
        uniqueFieldLabel = findViewById(R.id.uniqueSettings);
        errorMsg = findViewById(R.id.errorMsgSettings);
        returnButton = findViewById(R.id.backButtonSettings);
        updateAll = findViewById(R.id.update);
        email = findViewById(R.id.emailSetting);
        logOutButton = findViewById(R.id.logOutSettings);
        userTypeLabel = findViewById(R.id.userTypeSettings2);
        profilePic = findViewById(R.id.profilePicSettings);
        uniqueId = findViewById(R.id.uidSettings);
        uniqueIdLabel = findViewById(R.id.uidSettingsLabel);
        editImage = findViewById(R.id.editSettings);
        userType = findViewById(R.id.userTypeSettings);
        numOfVehicles = findViewById(R.id.numOfVehiclesSettings);
        numOfVehiclesLabel = findViewById(R.id.numOfVehiclesSettings2);
        editIcon = findViewById(R.id.imageView3);
        locationLabel = findViewById(R.id.locationSettings);
        location = findViewById(R.id.locationSettings2);
        listOfViews = new View[]{name, locationOfUser, uniqueField, uniqueFieldLabel, errorMsg, returnButton, updateAll, email,
                logOutButton, userTypeLabel, profilePic, uniqueId, uniqueIdLabel, editImage, userType, numOfVehicles, numOfVehiclesLabel
        , editIcon, locationLabel, location};

        setInvisible();

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userId = getIntent().getStringExtra("uid");
        findUser();

    }

    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    user = document.toObject(User.class);
                        switch (user.getUserType()) {
                        case "Student":
                            studentUser = document.toObject(Student.class);
                            user = studentUser;
                            break;
                        case "Alum":
                            alumUser = document.toObject(Alum.class);
                            user = alumUser;
                            break;
                        default:
                            staffUser = document.toObject(Staff.class);
                            user = staffUser;
                          }

                        name.setText(user.getName());
                        setFields();
                } else {
                    Log.d("TAG", "User document does not exist");
                }
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    private void setFields(){
        logOutButton.setVisibility(View.VISIBLE);
        if (!user.isSetUp()) {
            returnButton.setVisibility(View.INVISIBLE);
            updateAll.setText("Create Profile");
        } else {
            returnButton.setVisibility(View.VISIBLE);
            updateAll.setText("Update Profile");
        }
        switch (user.getUserType()) {
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
        uniqueId.setText(user.getUid());
        numOfVehicles.setText(Integer.toString(user.getOwnedVehicles().size()));
        setVisible();
    }

    private void setFieldsHelper(Student user){
        userTypeLabel.setText("Student");
        uniqueFieldLabel.setText("Graduating Year:");
        uniqueField.setText(Integer.toString(studentUser.getGraduatingYear()));
    }

    private void setFieldsHelper(Alum user){
        userTypeLabel.setText("Alum");
        uniqueFieldLabel.setText("Graduate Year:");
        uniqueField.setText(Integer.toString(alumUser.getGraduateYear()));
    }

    private void setFieldsHelper(Staff user){
        uniqueFieldLabel.setText("In School Title:");
        uniqueField.setText(staffUser.getInSchoolTitle());
    }


    public void uploadPic(View v){
        choosePicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null) {
            imageUri = data.getData();
            profilePic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {

    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    public void updateAllSettings(View v){
        user.setSetUp(true);
        String newName = name.getText().toString();
        String location = locationOfUser.getText().toString();
        String unique = uniqueField.getText().toString();

        switch (user.getUserType()) {
            case "Student":
                if (!updateAllSettingsStudent(newName, location, unique)) {
                    return;
                }
                break;
            case "Alum":
                if (!updateAllSettingsAlum(newName, location, unique)) {
                    return;
                }
                break;
            default:
                staffUser.changeValues(newName, location, unique, image);
                updateFirebase(staffUser);
        }

        Toast.makeText(SettingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        returnToProfile();
    }

    private boolean updateAllSettingsStudent(String newName, String location, String unique){
        try{
            int valueOfUnique = Integer.parseInt(unique);
            studentUser.changeValues(newName, location, valueOfUnique, image);
            updateFirebase(studentUser);
            return true;
        } catch (NumberFormatException e){
            errorMessage("Please re-enter your Graduating Year");
            return false;
        }
    }

    private boolean updateAllSettingsAlum(String newName, String location, String unique){
        try{
            int valueOfUnique = Integer.parseInt(unique);
            alumUser.changeValues(newName, location, valueOfUnique, image);
            updateFirebase(alumUser);
            return true;
        } catch (NumberFormatException e){
            errorMessage("Please re-enter your Graduate Year");
            return false;
        }
    }

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

    public void logOut(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Log out");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            if (AuthActivity.mGoogleSignInClient!=null) {
                AuthActivity.mGoogleSignInClient.signOut();
            }

            logOutInstance();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void errorMessage(String message){
        errorMsg.setText(message);
    }

    private void logOutInstance(){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }

    public void backToMain(View v) {
        returnToProfile();
    }

    private void returnToProfile(){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    private void setInvisible(){
        for (View v: listOfViews) {
            v.setVisibility(View.INVISIBLE);
        }
        progressBar.animate();
    }

    private void setVisible(){
        for (View v: listOfViews) {
            v.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

}