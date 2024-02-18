package com.example.carpoolbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VehicleProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        findUser();
    }

    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    user = document.toObject(User.class);
                } else {
                    Log.d("TAG", "User document does not exist");
                }
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    public void toSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    public void toVehicles(View v){
        Intent intent = new Intent(this, ViewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }


}