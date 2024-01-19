package com.example.carpoolbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URI;

public class SettingsActivity extends AppCompatActivity {
    private ShapeableImageView profilePic;
    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
    private String userId;
    private FirebaseFirestore firestore;
    private EditText name;
    private EditText locationOfUser;

    private EditText uniqueField;
    private TextView uniqueFieldLabel;

    private TextView errorMsg;

    private String debuggingMessage;
    private Uri image;

    private ImageButton returnButton;
    private Button updateAll;

    private final int GALLERY_INT_REQ_CODE = 1000;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        firestore = FirebaseFirestore.getInstance();
        profilePic = findViewById(R.id.profilePicSettings);


        userId = getIntent().getStringExtra("uid");
        System.out.println("Uid: " + userId);
        debuggingMessage = "it never ran";
        findUser();

        System.out.println(debuggingMessage);

        if (studentUser == null && alumUser == null && staffUser == null) {
            System.out.println("oh no, to object didn't work");
        } else {
            System.out.println("one of them is set up");
        }

        if (user == null){
            System.out.println("user is null");
        } else {
            System.out.println("user is not null");
            setProfilePic(user.getProfilePic());
        }

        name = findViewById(R.id.nameSettingEdit);
        locationOfUser = findViewById(R.id.locationSettings2);
        uniqueField = findViewById(R.id.uniqueSettings2);
        uniqueFieldLabel = findViewById(R.id.uniqueSettings);
        errorMsg = findViewById(R.id.errorMsg);
        returnButton = findViewById(R.id.backButtonSettings);
        updateAll = findViewById(R.id.update);
        if (!user.isSetUp()) {
            returnButton.setVisibility(View.GONE);
            updateAll.setText("Create Profile");
        } else {
            returnButton.setVisibility(View.VISIBLE);
            updateAll.setText("Update Profile");
        }

        switch (user.getUserType()) {
            case "Student":
                uniqueFieldLabel.setText("Graduating Year:");
                break;
            case "Alum":
                uniqueFieldLabel.setText("Graduate Year:");
                break;
            default:
                uniqueFieldLabel.setText("In School Title:");
        }



    }

    private void findUser(){
        firestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot ds = task.getResult();
                User user1 = ds.toObject(User.class);

                System.out.println("the user: " + user1.toString());
                switch (user1.getUserType()) {
                    case "Student":
                        studentUser = ds.toObject(Student.class);
                        user = studentUser;
                        break;
                    case "Alum":
                        alumUser = ds.toObject(Alum.class);
                        user = alumUser;
                        break;
                    default:
                        staffUser = ds.toObject(Staff.class);
                        user = staffUser;
                }
            } else {
                if (task.isCanceled()) {
                    System.out.println("cancelled");
                } else {
                    System.out.println("task exception:" + task.getException().getMessage().toString());
                    debuggingMessage = task.getException().getMessage().toString();
                }
            }
        });

    }

    private void setProfilePic(Uri uri){
        image = uri;
        profilePic.setImageURI(uri);
    }

    public void uploadPic(View v){
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivity(iGallery, GALLERY_INT_REQ_CODE);
    }

    private void startActivity(Intent iGallery, int galleryIntReqCode) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_INT_REQ_CODE){
                setProfilePic(data.getData());
            }
        }
    }

    public void updateAllSettings(View v){
        user.setSetUp(true);
        String newName = name.getText().toString();
        String location = locationOfUser.getText().toString();
        String unique = uniqueField.getText().toString();

        switch (user.getUserType()) {
            case "Student":
                try{
                    int valueOfUnique = Integer.parseInt(unique);
                    studentUser.changeValues(newName, location, valueOfUnique, image);
                    updateFirebase(studentUser);
                } catch (NumberFormatException e){
                    errorMessage("Please re-enter your Graduating Year");
                    return;
                }
                break;
            case "Alum":
                try{
                    int valueOfUnique = Integer.parseInt(unique);
                    alumUser.changeValues(newName, location, valueOfUnique, image);
                    updateFirebase(alumUser);
                } catch (NumberFormatException e){
                    errorMessage("Please re-enter your Graduate Year");
                    return;
                }
                break;
            default:
                staffUser.changeValues(newName, location, unique, image);
                updateFirebase(staffUser);
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
        FirebaseAuth.getInstance().signOut();
        AuthActivity.mGoogleSignInClient.signOut();
        logOutInstance();
    }

    public void errorMessage(String message){
        errorMsg.setText(message);
    }

    private void logOutInstance(){
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }
}