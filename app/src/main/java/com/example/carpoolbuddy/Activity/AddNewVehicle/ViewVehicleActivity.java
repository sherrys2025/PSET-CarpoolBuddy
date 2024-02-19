package com.example.carpoolbuddy.Activity.AddNewVehicle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carpoolbuddy.Activity.AddNewRide.AddNewRideActivity;
import com.example.carpoolbuddy.Activity.AddNewRide.ViewRideActivity;
import com.example.carpoolbuddy.Activity.Explore.VehicleProfileActivity;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Activity.SettingsActivity;
import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.example.carpoolbuddy.Vehicles.Vehicle;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
/**
 * This is an Activity class that shows all of the user's vehicles. It has a recycler view
 * and a progress bar. User can choose to add or edit their vehicles from the recycler view.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class ViewVehicleActivity extends AppCompatActivity implements MyVehicleButtons{
    private FirebaseFirestore firestore;
    private String uid;
    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
    private ArrayList<Object> vehicles;
    private ArrayList<Vehicle> myVehicles;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    /**
     * create view, find views and start progress bar
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_vehicle);

        uid = getIntent().getStringExtra("uid");
        firestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.myVehiclesRecycler);
        progressBar = findViewById(R.id.progressBarMyVehicles);

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();

        findUser();
    }

    /**
     * Find user and its corresponding child object from Firebase
     */
    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                user = document.toObject(User.class);
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
                convertVehiclesAndImages();
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * Add all of the vehicles that user owns to the ArrayList "myVehicles".
     * This is essential in making the recyclerView
     */
    private void convertVehiclesAndImages(){
        myVehicles = new ArrayList<>();
        vehicles = user.getOwnedVehicles();
        for (Object obj: vehicles){
            Vehicle temp = new Vehicle(obj);
            myVehicles.add(temp);
        }

        if (myVehicles.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        setUpRows();
    }

    /**
     * Set up the recycler view.
     */
    private void setUpRows(){
        MyVehicleRecyclerAdapter myVehicleRecyclerAdapter = new MyVehicleRecyclerAdapter(this, myVehicles, this);
        recyclerView.setAdapter(myVehicleRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //linear layout
    }

    /**
     * When user presses the setting icon, set up intent to go to the Settings Activity.
     * @param v View that called this method
     */
    public void toSettings(View v){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * when explore button is pressed, go to explore page
     * @param v
     */
    public void toExplore(View v){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * when vehicle button is pressed, go to vehicle intent
     * @param v
     */
    public void toVehicle(View v){
        Intent intent = new Intent(this, AddNewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * When user presses the ride/calendar icon, set up intent to go to the ViewRide activity
     * @param v View that called this method
     */
    public void toRide(View v){
        Intent intent = new Intent(this, ViewRideActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * When edit button is pressed, go to its AddNewVehicleActivity page
     * @param position index of the view that was pressed from
     */
    @Override
    public void onEditClick(int position) {
        Intent intent = new Intent(this, AddNewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        intent.putExtra("vid", myVehicles.get(position).getVehicleID());
        startActivity(intent);
    }

    /**
     * When add button pressed, go to new AddNewRideActivity page
     * @param position index of the view that was pressed from
     */
    @Override
    public void onAddClick(int position) {
        Intent intent = new Intent(this, AddNewRideActivity.class);
        intent.putExtra("uid", user.getUid());
        intent.putExtra("vid", myVehicles.get(position).getVehicleID());
        startActivity(intent);
    }

    /**
     * When delete button pressed, check if user wants to delete.
     * If so, remove the vehicle from Firestore.
     * @param position index of the view that was pressed from
     */
    @Override
    public void onDeleteClick(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Vehicle");
        builder.setMessage("Are you sure you want to delete this vehicle?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            firestoreRemoveVehicle(position);
            firestoreUpdateUser(position);
            refreshPage();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Show recycler view and hide progress bar.
     */
    @Override
    public void finishSetup() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Refresh page by starting new intent of this page.
     */
    private void refreshPage() {
        Intent intent = new Intent(this, ViewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * Remove vehicle and update the user on Firestore
     * @param position index of the view/vehicle selected
     */
    private void firestoreUpdateUser(int position) {
        switch (user.getUserType()) {
            case "Student":
                studentUser.removeVehicle(position);
                updateUserFirebaseHelper(studentUser);
                break;
            case "Alum":
                alumUser.removeVehicle(position);
                updateUserFirebaseHelper(alumUser);
                break;
            default:
                staffUser.removeVehicle(position);
                updateUserFirebaseHelper(staffUser);
        }
    }

    /**
     * Update student user on Firebase
     * @param student Student Object
     */
    private void updateUserFirebaseHelper(Student student){
        firestore.collection("users").document(user.getUid()).set(studentUser)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update staff user on Firebase
     * @param staff Staff Object
     */
    private void updateUserFirebaseHelper(Staff staff){
        firestore.collection("users").document(user.getUid()).set(staffUser)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update alum user on Firebase
     * @param alum Alum Object
     */
    private void updateUserFirebaseHelper(Alum alum){
        firestore.collection("users").document(user.getUid()).set(alumUser)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Remove vehicle from Firebase
     * @param position index of the vehicle clicked to be removed
     */
    private void firestoreRemoveVehicle(int position){
        CollectionReference collectionRef = firestore.collection("vehicles");
        DocumentReference documentRef = collectionRef.document(myVehicles.get(position).getVehicleID());
        documentRef.delete()
                .addOnSuccessListener(unused -> Toast.makeText(ViewVehicleActivity.this, "Vehicle Deleted", Toast.LENGTH_LONG))
                .addOnFailureListener(e -> Log.w("Oh no", e.getMessage().toString()));
    }


}