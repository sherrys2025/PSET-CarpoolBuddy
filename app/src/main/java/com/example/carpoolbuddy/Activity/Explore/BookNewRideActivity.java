package com.example.carpoolbuddy.Activity.Explore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Users.Alum;
import com.example.carpoolbuddy.Users.Staff;
import com.example.carpoolbuddy.Users.Student;
import com.example.carpoolbuddy.Users.User;
import com.example.carpoolbuddy.Vehicles.Car;
import com.example.carpoolbuddy.Vehicles.ElectricCar;
import com.example.carpoolbuddy.Vehicles.Motorcycle;
import com.example.carpoolbuddy.Vehicles.Ride;
import com.example.carpoolbuddy.Vehicles.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * BookNewRideActivity is an Activity class that shows up when users have chosen to book or edit their booking
 * of a ride. It is only displayed to non-owners. Non-owners can only change their pick-up address. Booked rides
 * will show up in their ViewRide Activity.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class BookNewRideActivity extends AppCompatActivity {
    private User user, oUser;
    private Student studentUser, sUser;
    private Staff staffUser, stUser;
    private Alum alumUser, aUser;
    private Car car;
    private ElectricCar electricCar;
    private Motorcycle motorcycle;
    private Vehicle vehicle;
    private Ride ride;
    private String userId, vehicleId, rideId, vehicleTitle;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri imageUri;
    private int imageIndex;
    private TextView title, errorMsg;
    private TextInputEditText special, capacity, owner, contactNumber, uniqueVehicleId, numberPlate, date, price, address;
    private ImageView imageOfVehicle;
    private LinearLayout details;
    private Button update;
    private ImageButton next, prev, navigate;
    private ProgressBar progressBar;
    private ScrollView scrollView;

    /**
     * Set up activity by finding the Views and finding user from Firebase.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_new_ride);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userId = getIntent().getStringExtra("uid");
        vehicleId = getIntent().getStringExtra("vid");
        rideId = getIntent().getStringExtra("rid");

        findViews();

        progressBar.animate();
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.INVISIBLE);
        findUser();
    }

    /**
     * Find the id of each Views and set up the Views.
     */
    private void findViews(){
        progressBar = findViewById(R.id.progressBarBookRide);
        scrollView = findViewById(R.id.everythingBookRide);
        title = findViewById(R.id.titleBookRideLabel);
        special = findViewById(R.id.specialVehicleEditBookRide);
        capacity = findViewById(R.id.capacityVehicleEditBookRide);
        owner = findViewById(R.id.ownerVehicleEditBookRide);
        uniqueVehicleId = findViewById(R.id.uniqueIdVehicleEditBookRide);
        numberPlate = findViewById(R.id.plateVehicleEditBookRide);
        date = findViewById(R.id.dateVehicleEditBookRide);
        price = findViewById(R.id.priceVehicleEditBookRide);
        address = findViewById(R.id.departureVehicleEditBookRide);
        details = findViewById(R.id.detailsOfBookRide);
        update = findViewById(R.id.updateVehicleBookRide);
        contactNumber = findViewById(R.id.phoneNumberVehicleEditBookRide);
        imageOfVehicle = findViewById(R.id.bookRideImage);
        next = findViewById(R.id.nextImageVehicleBookRide);
        prev = findViewById(R.id.prevImageVehicleBookRide);
        errorMsg = findViewById(R.id.errorMsgBookRide);
    }

    /**
     * Instantiate user and its corresponding child user object from Firebase.
     */
    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(userId);
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
                findVehicle();
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * Instantiate vehicle and its corresponding child vehicle object from Firebase.
     */
    private void findVehicle(){
        DocumentReference docRef = firestore.collection("vehicles").document(vehicleId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                vehicle = document.toObject(Vehicle.class);
                switch (vehicle.getTypeOfVehicle()) {
                    case "Car":
                        car = document.toObject(Car.class);
                        break;
                    case "electricCar":
                        electricCar = document.toObject(ElectricCar.class);
                        break;
                    default:
                        motorcycle = document.toObject(Motorcycle.class);
                }
                findRide();
            } else {
                Log.d("TAG", "Error retrieving vehicle document: " + task.getException());
            }
        });
    }

    /**
     * Instantiate ride from Firebase.
     */
    private void findRide() {
        DocumentReference docRef = firestore.collection("rides").document(rideId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                ride = document.toObject(Ride.class);
                getOwnerUser();
            } else {
                Log.d("TAG", "Error retrieving vehicle document: " + task.getException());
            }
        });
    }

    /**
     * Set the details (on Views) of ride to the correct information from Firebase.
     */
    private void setFields() {
        setSpecial();
        setDate();

        capacity.setText("" + ride.getRidersUIDs().size() + "/" + vehicle.getCapacity());
        owner.setText(oUser.getName());
        contactNumber.setText(oUser.getPhoneNumber());
        uniqueVehicleId.setText(vehicleId);
        numberPlate.setText(vehicle.getNumberPlate());

        if (ride.getRidersUIDs().contains(userId)) {
            update.setText("Cancel Carpool");
            update.setBackgroundColor(Color.RED);
            address.setText(ride.getDestinations().get(ride.getRidersUIDs().indexOf(userId)));
        } else {
            update.setText("Join Carpool");
            update.setBackgroundColor(ContextCompat.getColor(this, R.color.lightgreen));
            address.setText("");
        }

        imageIndex = 0;
        setImage(vehicle.getImagesOfVehicle().get(0));
        setPicVisibility();

        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * Set the special View based on information from Firebase.
     */
    private void setSpecial(){
        switch(vehicle.getTypeOfVehicle()) {
            case "Car":
                special.setText(car.getCarModel());
                break;
            case "Electric Car":
                special.setText(electricCar.getECarModel());
                break;
            default:
                special.setText(motorcycle.getMotorcycleModel());
        }
    }

    /**
     * Format and set the date View based on Firebase.
     */
    private void setDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(ride.getDate());
        date.setText(formattedDate);
        price.setText("" + ride.getBasePrice());
    }

    /**
     * Retrieve from Firebase Storage and set image of vehicle based on the imageId given
     * @param imageId unique ID of image
     */
    private void setImage(String imageId){
        StorageReference imageToSet = firebaseStorage.getReference("image/" + imageId);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            File finalLocalFile = localFile;
            imageToSet.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                imageUri = Uri.fromFile(finalLocalFile);
                imageOfVehicle.setImageURI(imageUri);
            }).addOnFailureListener(exception -> Log.w("Error retrieving image", exception));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the visibility of the next, prev buttons based on which image it is on.
     */
    private void setPicVisibility(){
        if (vehicle.getImagesOfVehicle().size() == 0){
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (vehicle.getImagesOfVehicle().size() == 1) {
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imageIndex == 0){
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imageIndex == vehicle.getImagesOfVehicle().size() - 1){
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.VISIBLE);
        } else {
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
        }
    }

    /**
     * When the next image button is pressed, show the next image
     * @param v View that called this method
     */
    public void nextImage(View v){
        imageIndex++;
        imageIndex %= vehicle.getImagesOfVehicle().size();
        setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        setPicVisibility();
    }

    /**
     * When the prev image button is pressed, show the previous image
     * @param v View that called this method
     */
    public void prevImage(View v){
        imageIndex--;
        if (imageIndex < 0) { imageIndex = vehicle.getImagesOfVehicle().size()-1; }
        setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        setPicVisibility();
    }

    /**
     * When back button is pressed, return to VehicleProfileActivity
     * @param v View that called this method
     */
    public void backToExplore(View v){
        toExplore();
    }

    /**
     * Start new intent to go to VehicleProfileActivity
     */
    private void toExplore() {
        Intent intent = new Intent(this, VehicleProfileActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * When user presses the update ride button, if the user is already in the ride,
     * cancel its carpool; if the user is not in the ride, join the carpool
     * @param v View that called this method
     */
    public void updateRide(View v) {
        if (ride.getRidersUIDs().contains(userId)) { // if already in this carpool
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Cancel carpool");
            builder.setMessage("Are you sure you want to leave this carpool?");

            builder.setPositiveButton("Yes", (dialog, which) -> cancelCarpool()); // if Yes, call cancelCarpool()
            builder.setNegativeButton("No", (dialog, which) -> {}); // if No, do nothing
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else { // if not joined yet, join carpool
            joinCarpool();
            toExplore();
        }

    }

    /**
     * Join carpool by retrieving the entered address and then adding the user to the ride;
     * update ride, vehicle, user, and owner on Firebase
     */
    private void joinCarpool() {
        String address1 = address.getText().toString();         // retrieve address
        if (address1.equals("")) {
            errorMsg.setText("Please enter a pick up address");
            return;
        }
        ride.addRidersUIDs(userId);
        ride.addDestinations(address1);
        updateRideFirebase();                                   // update ride on firebase
        switch(vehicle.getTypeOfVehicle()) {
            case "Car":
                car.updateRide(ride);           // update car
                updateVehicleFirebase(car);     // update vehicle on firebase
                updateUserFirebase(car);        // update both user and the owner on firebase
                break;
            case "Electric Car":
                electricCar.updateRide(ride);
                updateUserFirebase(electricCar);
                updateVehicleFirebase(electricCar);
                break;
            default:
                motorcycle.updateRide(ride);
                updateUserFirebase(motorcycle);
                updateVehicleFirebase(motorcycle);
        }
    }

    /**
     * Update ride on Firebase.
     */
    private void updateRideFirebase() {
        firestore.collection("rides").document(rideId).set(ride)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update car on Firebase
     * @param c Car object
     */
    private void updateVehicleFirebase(Car c) {
        firestore.collection("vehicles").document(vehicleId).set(car)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update electricCar on Firebase
     * @param c ElectricCar object
     */
    private void updateVehicleFirebase(ElectricCar c) {
        firestore.collection("vehicles").document(vehicleId).set(electricCar)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update motorcycle on Firebase
     * @param c Motorcycle object
     */
    private void updateVehicleFirebase(Motorcycle c) {
        firestore.collection("vehicles").document(vehicleId).set(motorcycle)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update user on Firebase. If the user is not the owner, update the owner too.
     * @param c child object of User, either Student, Staff, or Alum
     */
    private void updateUserFirebase(Object c) {
        int index = 0;
        Vehicle v;
        switch(user.getUserType()){
            case "Student":
                v = (Vehicle) c;
                updateUserFirebaseStudent(v, c);
                break;
            case "Staff":
                v = (Vehicle) c;
                updateUserFirebaseStaff(v, c);
                break;
            default:
                v = (Vehicle) c;
                updateUserFirebaseAlum(v, c);
        }
    }

    /**
     * Add ride in studentUser; update owner and user on Firebase
     * @param v Vehicle to check if User is Owner
     * @param c Object, a child object of Vehicle, either Car, ElectricCar, or Motorcycle
     */
    private void updateUserFirebaseStudent(Vehicle v, Object c){
        studentUser.addRide(ride, date.getText().toString());
        if (ride.getOwner().equals(userId)) {
            int index = studentUser.containsVehicle(v);
            studentUser.replaceVehicle(index, c);
        } else {
            updateOwner(ride.getOwner(), v);
        }
        updateUserFirebaseHelper(studentUser);
    }

    /**
     * Add ride in staffUser; update owner and user on Firebase
     * @param v Vehicle to check if User is Owner
     * @param c Object, a child object of Vehicle, either Car, ElectricCar, or Motorcycle
     */
    private void updateUserFirebaseStaff(Vehicle v, Object c){
        staffUser.addRide(ride, date.getText().toString());
        if (ride.getOwner().equals(userId)) {
            int index = staffUser.containsVehicle(v);
            staffUser.replaceVehicle(index, c);
        } else {
            updateOwner(ride.getOwner(), v);
        }
        updateUserFirebaseHelper(staffUser);
    }

    /**
     * Add ride in alumUser; update owner and user on Firebase
     * @param v Vehicle to check if User is Owner
     * @param c Object, a child object of Vehicle, either Car, ElectricCar, or Motorcycle
     */
    private void updateUserFirebaseAlum(Vehicle v, Object c){
        alumUser.addRide(ride, date.getText().toString());
        if (ride.getOwner().equals(userId)) {
            int index = alumUser.containsVehicle(v);
            alumUser.replaceVehicle(index, c);
        } else {
            updateOwner(ride.getOwner(), v);
        }
        updateUserFirebaseHelper(alumUser);
    }

    /**
     * Update owner in Firebase
     * @param owner owner's uid
     * @param v vehicle to be replaced in owner
     */
    private void updateOwner(String owner, Vehicle v) {
        switch (oUser.getUserType()) {
            case "Student":
                sUser.replaceVehicle(sUser.containsVehicle(v), v);
                updateUserFirebaseHelper(sUser);
                break;
            case "Alum":
                aUser.replaceVehicle(aUser.containsVehicle(v), v);
                updateUserFirebaseHelper(aUser);
                break;
            default:
                stUser.replaceVehicle(stUser.containsVehicle(v), v);
                updateUserFirebaseHelper(stUser);
        }
    }

    /**
     * Retrieve the owner user from Firebase.
     */
    private void getOwnerUser(){
        DocumentReference docRef = firestore.collection("users").document(ride.getOwner());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                oUser = document.toObject(User.class);
                switch (oUser.getUserType()) {
                    case "Student":
                        sUser = document.toObject(Student.class);
                        break;
                    case "Alum":
                        aUser = document.toObject(Alum.class);
                        break;
                    default:
                        stUser = document.toObject(Staff.class);
                }
                setFields();
            }
        });
    }

    /**
     * Update Student user to Firebase
     * @param student User to be updated
     */
    private void updateUserFirebaseHelper(Student student){
        firestore.collection("users").document(student.getUid()).set(student)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update Staff user to Firebase
     * @param staff User to be updated
     */
    private void updateUserFirebaseHelper(Staff staff){
        firestore.collection("users").document(staff.getUid()).set(staff)
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
     * @param alum User to be updated
     */
    private void updateUserFirebaseHelper(Alum alum){
        firestore.collection("users").document(alum.getUid()).set(alum)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Remove ride from User; update vehicle, user, owner on Firebase
     */
    private void cancelCarpool() {
        ride.removeRidersUIDs(userId);
        cancelUserCarpool();
        updateRideFirebase();
        switch(vehicle.getTypeOfVehicle()) {
            case "Car":
                car.updateRide(ride);
                updateVehicleFirebase(car);
                updateUserFirebase(car);
                updateOwner(ride.getOwner(), car);
                break;
            case "Electric Car":
                electricCar.updateRide(ride);
                updateUserFirebase(electricCar);
                updateVehicleFirebase(electricCar);
                updateOwner(ride.getOwner(), electricCar);
                break;
            default:
                motorcycle.updateRide(ride);
                updateUserFirebase(motorcycle);
                updateVehicleFirebase(motorcycle);
                updateOwner(ride.getOwner(), motorcycle);
        }
        toExplore();    // back to explore page
    }

    /**
     * Remove ride from User
     */
    private void cancelUserCarpool() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(ride.getDate());
        switch(user.getUserType()){
            case "Student":
                studentUser.removeRide(ride, formattedDate);
                updateUserFirebaseHelper(studentUser);
                break;
            case "Staff":
                staffUser.removeRide(ride, formattedDate);
                updateUserFirebaseHelper(staffUser);
                break;
            default:
                alumUser.removeRide(ride, formattedDate);
                updateUserFirebaseHelper(alumUser);
        }
    }
}