package com.example.carpoolbuddy.Activity.AddNewRide;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.carpoolbuddy.Activity.Explore.BookNewRideActivity;
import com.example.carpoolbuddy.Activity.Explore.VehicleProfileActivity;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Activity.SettingsActivity;
import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.example.carpoolbuddy.Vehicles.Ride;
import com.example.carpoolbuddy.Activity.AddNewVehicle.ViewVehicleActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This is an Activity class that shows all of the user's booked rides. It has a recycler view
 * and a progress bar. User can choose to edit, navigate, or delete their rides from the recycler view.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class ViewRideActivity extends AppCompatActivity implements MyRideButtons {
    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
    private String uid;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ArrayList<Ride> myRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ride);

        uid = getIntent().getStringExtra("uid");
        firestore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBarMyRides);
        recyclerView = findViewById(R.id.myRidesRecycler);

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.animate();
        findUser();
    }

    /**
     * Find user from Firebase
     */
    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(uid);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
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
                    convertRidesAndImages();
                    setUpRows();
                } else {
                    Log.d("TAG", "User document does not exist");
                }
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * Set up the recycler view.
     */
    private void setUpRows() {
        if (myRides.size() == 0) {
            return;
        }
        MyRideRecyclerAdapter myRideRecyclerAdapter = new MyRideRecyclerAdapter(this, myRides, this);
        recyclerView.setAdapter(myRideRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Find all of the rides that user owns/booked to the ArrayList "myRides".
     * This is essential in making the recyclerView
     */
    private void convertRidesAndImages() {
        myRides = user.getAllBookedRides();
        if (myRides == null) {
            myRides = new ArrayList<>();
        }
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
     * When user presses the dashboard icon, set up intent to go to the VehicleProfile activity.
     * @param v View that called this method
     */
    public void toExplore(View v){
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * When user presses the vehicle icon, set up intent to go to the ViewVehicle activity.
     * @param v View that called this method
     */
    public void toVehicle(View v){
        Intent intent = new Intent(this, ViewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * When user presses the add icon, set up intent to go to the AddNewRide activity.
     * @param v View that called this method
     */
    public void toRide(View v){
        Intent intent = new Intent(this, AddNewRideActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
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
     * When the edit button is pressed, if the user is the owner, go to its AddNewRideActivity page.
     * If the user is not the owner, go to BookNewRideActivity page.
     * @param pos index of the view that was pressed from
     */
    @Override
    public void onEditClick(int pos) {
        if (myRides.get(pos).getOwner().equals(user.getUid())) {
            Intent intent = new Intent(this, AddNewRideActivity.class);
            intent.putExtra("uid", user.getUid());
            intent.putExtra("vid", myRides.get(pos).getVehicleID());
            intent.putExtra("rid", myRides.get(pos).getRideID());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, BookNewRideActivity.class);
            intent.putExtra("uid", user.getUid());
            intent.putExtra("vid", myRides.get(pos).getVehicleID());
            intent.putExtra("rid", myRides.get(pos).getRideID());
            startActivity(intent);
        }
    }

    /**
     * When the delete button is pressed, check if user wants to delete.
     * If so, remove the ride from Firestore.
     * @param pos index of the view that was pressed from
     */
    @Override
    public void onDeleteClick(int pos) {
        if (user.getUid().equals(myRides.get(pos).getOwner())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cancel Ride");
            builder.setMessage("Are you sure you want to cancel this ride?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                firestoreRemoveRide(pos);
                firestoreUpdateUser(pos);
                refreshPage();
            });

            builder.setNegativeButton("No", (dialog, which) -> {
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            Toast toast = Toast.makeText(this, "You can't delete a ride of someone else.", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    /**
     * When the navigate button is pressed, move to new intent that is the google maps direction page
     * @param pos index of the view that was pressed from
     */
    @Override
    public void onNavigateClick(int pos) {
        String directions = "";
        for (String location: myRides.get(pos).getDestinations()) {
            String temp = location.toLowerCase();
            directions += "/" + temp + " hong kong";
        }

        Uri uri = Uri.parse("https://www.google.com/maps/dir" + directions);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Refresh page by starting new intent of this page.
     */
    private void refreshPage() {
        Intent intent = new Intent(this, ViewRideActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * Remove ride and update the user and owner on Firestore
     * @param position index of the view/ride selected
     */
    private void firestoreUpdateUser(int position) {
        switch (user.getUserType()) {
            case "Student":
                studentUser.removeRide(myRides.get(position), myRides.get(position).getDate().toString());
                updateUserFirebaseHelper(studentUser);
                break;
            case "Alum":
                alumUser.removeRide(myRides.get(position), myRides.get(position).getDate().toString());
                updateUserFirebaseHelper(alumUser);
                break;
            default:
                staffUser.removeRide(myRides.get(position), myRides.get(position).getDate().toString());
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
     * Remove ride from Firebase
     * @param position index of the ride clicked to be removed
     */
    private void firestoreRemoveRide(int position){
        if (user.getUid().equals(myRides.get(position).getOwner())) {
            CollectionReference collectionRef = firestore.collection("rides");
            DocumentReference documentRef = collectionRef.document(myRides.get(position).getRideID());
            documentRef.delete()
                    .addOnSuccessListener(unused -> Toast.makeText(ViewRideActivity.this, "Ride Deleted", Toast.LENGTH_LONG))
                    .addOnFailureListener(e -> Log.w("Oh no", e.getMessage().toString()));
        } else {
            myRides.get(position).removeRidersUIDs(user.getUid());
            CollectionReference collectionRef = firestore.collection("rides");
            DocumentReference documentRef = collectionRef.document(myRides.get(position).getRideID());
            documentRef.set(myRides.get(position))
                    .addOnSuccessListener(unused -> Toast.makeText(ViewRideActivity.this, "Ride Updated", Toast.LENGTH_LONG))
                    .addOnFailureListener(e -> Log.w("Oh no", e.getMessage().toString()));
        }

    }
}