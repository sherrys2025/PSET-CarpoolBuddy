package com.example.carpoolbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VehicleProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);
        mAuth = FirebaseAuth.getInstance();
    }

    public void toSettings(View v){
        FirebaseUser user = mAuth.getCurrentUser();

        Intent intent = new Intent(this, SettingsActivity.class);
        System.out.println(user.getUid());
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }


}