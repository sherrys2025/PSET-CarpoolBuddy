package com.example.carpoolbuddy.Activity.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.carpoolbuddy.Activity.AuthActivity;
import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Activity.SettingsActivity;
import com.example.carpoolbuddy.Users.User;
import com.example.carpoolbuddy.Vehicles.Ride;
import com.example.carpoolbuddy.Activity.AddNewRide.ViewRideActivity;
import com.example.carpoolbuddy.Activity.AddNewVehicle.ViewVehicleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * This is an Activity class that shows all of the rides open for booking. It has a recycler view
 * and a progress bar. User can choose to book the rides they want from the recycler view.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class VehicleProfileActivity extends AppCompatActivity implements MyExploreButtons {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private User user;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ArrayList<Ride> rides;

    /**
     * Create Explore Page, set up views
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        rides = new ArrayList<>();
        recyclerView = findViewById(R.id.myExploreRecycler);
        progressBar = findViewById(R.id.progressBarMyExplore);

        progressBar.animate();
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        findUser();
    }

    /**
     * Find user from Firebase
     */
    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    user = document.toObject(User.class);
                    findAllRides();
                } else {
                    Log.d("TAG", "User document does not exist");
                }
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * Find all of the rides that are not owned by the owner from firebase and add to the ArrayList "rides".
     * This is essential in making the recyclerView
     */
    private void findAllRides() {
        firestore.collection("rides")
                .whereEqualTo("open", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Retrieved ride", document.getId() + " => " + document.getData());
                            if (!document.toObject(Ride.class).getOwner().equals(user.getUid())){
                                rides.add(document.toObject(Ride.class));
                            }
                        }
                        setUpRows();
                    } else {
                        Log.d("Error retrieving ride", "Error getting documents: ", task.getException());
                    }
                });
    }

    /**
     * Set up the recycler view.
     */
    private void setUpRows() {
        if (rides.size() == 0) { //if no rides
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }
        MyExploreRecyclerAdapter myExploreRecyclerAdapter = new MyExploreRecyclerAdapter(this, rides, this);
        recyclerView.setAdapter(myExploreRecyclerAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); //set up as grid format
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
     * When user presses the vehicle icon, set up intent to go to the ViewVehicle activity.
     * @param v View that called this method
     */
    public void toVehicles(View v){
        Intent intent = new Intent(this, ViewVehicleActivity.class);
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
     * If the recycler view has finished setting up, set recycler view to be visible.
     */
    @Override
    public void finishSetup() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * if user presses edit icon, move to book new ride activity
     * @param pos index of the view that was pressed from
     */
    @Override
    public void onEditClick(int pos) {
        Intent intent = new Intent(this, BookNewRideActivity.class);
        intent.putExtra("uid", user.getUid());
        intent.putExtra("vid", rides.get(pos).getVehicleID());
        intent.putExtra("rid", rides.get(pos).getRideID());
        startActivity(intent);
    }
}