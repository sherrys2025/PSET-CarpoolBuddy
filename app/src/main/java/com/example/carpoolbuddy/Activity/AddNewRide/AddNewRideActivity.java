package com.example.carpoolbuddy.Activity.AddNewRide;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * AddNewRideActivity is an Activity class that shows up when users have either chosen to edit or add a new ride.
 * It is only displayed to the owner of vehicle. Owner can change price, departure address, and date of ride. Each
 * ride must be created from a specific vehicle of their own.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class AddNewRideActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
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
    private Spinner myVehicleSelected;
    private TextView title, errorMsg;
    private TextInputEditText special, capacity, owner, uniqueVehicleId, numberPlate, date, price, address;
    private ImageView imageOfVehicle;
    private LinearLayout details;
    private Button update;
    private ImageButton next, prev;
    private Switch isOpen;
    private ArrayList<CharSequence> myVehicleTitles;

    /**
     * Create View for adding new Ride
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_ride);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        userId = getIntent().getStringExtra("uid");
        vehicleId = getIntent().getStringExtra("vid");
        rideId = getIntent().getStringExtra("rid");

        findFields();

        findUser();
    }

    /**
     * Find each View by their id.
     */
    private void findFields(){
        myVehicleSelected = findViewById(R.id.selectVehicleEditRide);
        title = findViewById(R.id.titleRideLabel);
        special = findViewById(R.id.specialVehicleEditRide);
        capacity = findViewById(R.id.capacityVehicleEditRide);
        owner = findViewById(R.id.ownerVehicleEditRide);
        uniqueVehicleId = findViewById(R.id.uniqueIdVehicleEditRide);
        numberPlate = findViewById(R.id.plateVehicleEditRide);
        date = findViewById(R.id.dateVehicleEditRide);
        price = findViewById(R.id.priceVehicleEditRide);
        address = findViewById(R.id.departureVehicleEditRide);
        details = findViewById(R.id.detailsOfRide);
        update = findViewById(R.id.updateVehicleRide);
        imageOfVehicle = findViewById(R.id.rideImage);
        next = findViewById(R.id.nextImageVehicleRide);
        prev = findViewById(R.id.prevImageVehicleRide);
        errorMsg = findViewById(R.id.errorMsgRide);
        isOpen = findViewById(R.id.openRide);
    }

    /**
     * Find User and its corresponding child object from Firebase.
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
                setSpinner();
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * Find vehicle and its corresponding child object from Firebase.
     */
    private void findVehicle(){
        if (vehicleId != null) {
            for (Object ve: user.getOwnedVehicles()) {
                Vehicle v = new Vehicle(ve);
                if (v.getVehicleID().equals(vehicleId)) {
                    vehicle = v;
                    switch(vehicle.getTypeOfVehicle()) {
                        case "Car":
                            car = new Car(ve);
                            break;
                        case "Electric Car":
                            electricCar = new ElectricCar(ve);
                            break;
                        default:
                            motorcycle = new Motorcycle(ve);
                    }
                    break;
                }
            }

        } else {
            title.setText("New Ride");
        }
        findRide();
    }

    /**
     * Set up Spinner with the choices of the titles of user's vehicles.
     */
    private void setSpinner(){
        setVehicleTitles();

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, myVehicleTitles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myVehicleSelected.setAdapter(adapter);
        myVehicleSelected.setSelection(myVehicleTitles.size()-1);

        if (vehicleId != null) { //if vehicle and ride still have not been created
            findVehicle();
            findRide();
            myVehicleSelected.setSelection(myVehicleTitles.indexOf(vehicle.getTitle()));
        }

        if (rideId != null) {
            findRide();
        }

        myVehicleSelected.setOnItemSelectedListener(this);
    }

    /**
     * Find Ride from user's booked rides, otherwise create new Ride.
     */
    private void findRide() {
        if (rideId != null) {
            for (Ride ride1: user.getAllBookedRides()) {
                if (ride1.getRideID().equals(rideId)) {
                    ride = ride1;
                    ride.setSetUp();
                    break;
                }
            }
        } else {
            ride = new Ride();
            rideId = ride.getRideID();
        }
    }

    /**
     * set Spinner titles with myVehicleTitles
     */
    private void setVehicleTitles() {
        myVehicleTitles = new ArrayList<>();
        for (Object obj: user.getOwnedVehicles()) {
            HashMap<String, Object> hashMap = (HashMap<String, Object>) obj;
            myVehicleTitles.add(hashMap.get("title").toString());
        }
        myVehicleTitles.add("Select a vehicle");
    }

    /**
     * when Spinner is selected
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position).toString().equals("Select a vehicle")){ //if nothing has been selected yet
            setDetailsInvisible();
        } else { //remove Select a vehicle as an option
            if (myVehicleTitles.contains("Select a vehicle")) {
                myVehicleTitles.remove(myVehicleTitles.size()-1);
            }
            vehicleTitle = parent.getItemAtPosition(position).toString();
            // set up vehicle
            Object temp = user.getOwnedVehicles().get(position);
            vehicleId = ((HashMap<String, Object>) temp).get("vehicleID").toString();
            findVehicle();
            setDetailsVisible();
        }
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) parent.getChildAt(0)).setTextSize(16);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        setDetailsInvisible();
    }

    /**
     * Set up Views that represent the details of Ride and set it to be visible.
     */
    private void setDetailsVisible() {
        setUpDetails();
        details.setVisibility(View.VISIBLE);
    }

    /**
     * Set up the text displayed in the views that represent the details of Ride.
     */
    private void setUpDetails() {
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

        capacity.setText("" + vehicle.getCapacity());
        owner.setText(user.getName());
        uniqueVehicleId.setText(vehicleId);
        numberPlate.setText(vehicle.getNumberPlate());

        if (ride.isSetUp()) {
            update.setText("Update Ride");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = dateFormat.format(ride.getDate());
            date.setText(formattedDate);
            price.setText("" + ride.getBasePrice());
            address.setText(ride.getDestinations().get(ride.getRidersUIDs().indexOf(userId)));
            isOpen.setChecked(ride.isOpen());
        } else {
            update.setText("Create Ride");
            date.setHint("yyyy-MM-dd");
            date.setText("yyyy-MM-dd");
            date.setHintTextColor(getResources().getColor(R.color.white));
            price.setText("5.00");
            address.setText("");
            title.setText(vehicleTitle);
            isOpen.setChecked(true);
        }

        imageIndex = 0;
        setImage(vehicle.getImagesOfVehicle().get(0));
        setPicVisibility();
    }

    /**
     * Set the Views that represent the ride details as invisible.
     */
    private void setDetailsInvisible() {
        details.setVisibility(View.INVISIBLE);
    }

    /**
     * If user presses the back button, return to see the page with all of the user's booked rides.
     * @param v View that called the method
     */
    public void backToRides(View v){
        toRides();
    }

    /**
     * Start new intent to go to ViewRideActivity.
     */
    private void toRides() {
        Intent intent = new Intent(this, ViewRideActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * When next image button is pressed, set imageOfVehicle as the next image.
     * @param v View that called the method
     */
    public void nextImage(View v){
        imageIndex++;
        imageIndex %= vehicle.getImagesOfVehicle().size();
        setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        setPicVisibility();
    }

    /**
     * When prev image button is pressed, set imageOfVehicle as the previous image.
     * @param v View that called the method
     */
    public void prevImage(View v){
        imageIndex--;
        if (imageIndex < 0) { imageIndex = vehicle.getImagesOfVehicle().size()-1; }
        setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        setPicVisibility();
    }

    /**
     * Set imageOfVehicle as the image named imageId from Firebase Storage.
     * @param imageId Unique ID of image file
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
     * Set visibility of next and prev image buttons based on which image user is on.
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
     * When user presses update ride button, retrieve entered information and update Ride on Firebase
     * @param v Button that called this method
     */
    public void updateRide(View v){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date1 = null;
        try {
            String date2 = date.getText().toString();
            date1 = dateFormat.parse(date2);
            if (!date1.after(new Date())){
                throw new Exception();
            }
        } catch (ParseException e) {
            errorMsg.setText("Enter date in the yyyy-MM-dd format.");
            return;
        } catch (Exception e) {
            errorMsg.setText("Enter a date after today.");
            return;
        }
        double price1;
        try {
            price1 = Double.valueOf(price.getText().toString());
        } catch (Exception e) {
            errorMsg.setText("Give a valid price.");
            return;
        }
        boolean open = isOpen.isChecked();

        String address1 = address.getText().toString();
        ride.setInformation(vehicle);
        ride.addRidersUIDs(userId);
        ride.addDestinations(address1);
        ride.setDate(date1);
        ride.setBasePrice(price1);
        ride.setOpen(open);
        ride.setSetUp();

        if (vehicle.occupiedRide(ride)) {
            errorMsg.setText("This " + vehicle.getTypeOfVehicle().toLowerCase() + " is occupied on " + date.getText().toString());
            return;
        }

        updateRideFirebase();
        switch(vehicle.getTypeOfVehicle()) {
            case "Car":
                car.updateRide(ride);
                updateVehicleFirebase(car);
                updateUserFirebase(car);
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

        Toast.makeText(AddNewRideActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        toRides();
    }

    /**
     * Update Ride on Firebase
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
     * Update Car on Firebase
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
     * Update ElectricCar on Firebase
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
     * Update Motorcycle on Firebase
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
     * Update User on Firebase
     * @param c User object
     */
    private void updateUserFirebase(Object c) {
        int index = 0;
        Vehicle v;
        switch(user.getUserType()){
            case "Student":
                v = (Vehicle) c;
                studentUser.addRide(ride, date.getText().toString());
                index = studentUser.containsVehicle(v);
                studentUser.replaceVehicle(index, c);
                updateUserFirebaseHelper(studentUser);
                break;
            case "Staff":
                v = (Vehicle) c;
                staffUser.addRide(ride, date.getText().toString());
                index = staffUser.containsVehicle(v);
                if (index >= 0){
                    staffUser.replaceVehicle(index, c);
                } else {
                    staffUser.addVehicle(c);
                }
                updateUserFirebaseHelper(staffUser);
                break;
            default:
                v = (Vehicle) c;
                alumUser.addRide(ride, date.getText().toString());
                index = alumUser.containsVehicle(v);
                if (index >= 0){
                    alumUser.replaceVehicle(index, c);
                } else {
                    alumUser.addVehicle(c);
                }
                updateUserFirebaseHelper(alumUser);
        }
    }

    /**
     * Update Student on Firebase
     * @param student Student object
     */
    private void updateUserFirebaseHelper(Student student){
        firestore.collection("users").document(userId).set(studentUser)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update Staff on Firebase
     * @param staff Staff object
     */
    private void updateUserFirebaseHelper(Staff staff){
        firestore.collection("users").document(userId).set(staffUser)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    /**
     * Update Alum on Firebase
     * @param alum Alum object
     */
    private void updateUserFirebaseHelper(Alum alum){
        firestore.collection("users").document(userId).set(alumUser)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }


}