package com.example.carpoolbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.carpoolbuddy.Users.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewVehicleActivity extends AppCompatActivity {
    private FirebaseFirestore firestore;
    private String uid;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);

        uid = getIntent().getStringExtra("uid");
        firestore = FirebaseFirestore.getInstance();
        findUser();
    }

    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(uid);
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

    public void toExplore(View v){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    public void toVehicle(View v){
        Intent intent = new Intent(this, AddNewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }
}