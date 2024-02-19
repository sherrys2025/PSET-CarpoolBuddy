package com.example.carpoolbuddy.Activity.AddNewVehicle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
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
import com.example.carpoolbuddy.Vehicles.Vehicle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * AddNewVehicleActivity is an Activity class that shows up when users have either chosen to edit or add a
 * new vehicle. It is only displayed to the owner of vehicle. Owner can change capacity, title, model, and
 * other details. Owner can upload as many images of their vehicle as desired.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class AddNewVehicleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private User user;
    private Student studentUser;
    private Staff staffUser;
    private Alum alumUser;
    private Car car;
    private ElectricCar electricCar;
    private Motorcycle motorcycle;
    private Vehicle vehicle;
    private String userId, vehicleId, type;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private ScrollView everythingView;
    private TextView errorMsg;
    private TextInputLayout specialLabel;
    private TextInputEditText title, special, capacity, owner, uniqueVehicleId, numberPlate;
    private Spinner typeOfVehicle;
    private Button createVehicle;
    private ImageView imageOfVehicle, deleteImage;
    private ImageButton prev, next;
    private ProgressBar progressBar;
    private Uri imageUri;
    private int imageIndex;

    /**
     * create activity, set up fields while progress bar is laoding
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vehicle);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        findViews();
        setInvisible();

        userId = getIntent().getStringExtra("uid");
        vehicleId = getIntent().getStringExtra("vid");
        findUser();
    }

    /**
     * find the Views by their IDs
     */
    private void findViews(){
        everythingView = findViewById(R.id.everythingVehicle);
        errorMsg = findViewById(R.id.errorMsgVehicle);
        title = findViewById(R.id.titleVehicleEdit);
        specialLabel = findViewById(R.id.specialVehicle);
        special = findViewById(R.id.specialVehicleEdit);
        typeOfVehicle = findViewById(R.id.typeOfVehicleEdit);
        capacity = findViewById(R.id.capacityVehicleEdit);
        owner = findViewById(R.id.ownerVehicleEdit);
        uniqueVehicleId = findViewById(R.id.uniqueIdVehicleEdit);
        createVehicle = findViewById(R.id.updateVehicle);
        imageOfVehicle = findViewById(R.id.vehicleImage);
        deleteImage = findViewById(R.id.deleteImageVehicle);
        prev = findViewById(R.id.prevImageVehicle);
        next = findViewById(R.id.nextImageVehicle);
        progressBar = findViewById(R.id.progressBarVehicle);
        numberPlate = findViewById(R.id.plateVehicleEdit);
    }

    /**
     * find user and its corresponding child object from firebase
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
                setFields();
                setVisible();
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    /**
     * find Vehicle and its child object from user's owned vehicle
     */
    private void findVehicle(){
        if (vehicleId != null) {
            for (Object ve: user.getOwnedVehicles()) {      //each Object user's owned vehicles
                Vehicle v = new Vehicle(ve);
                if (v.getVehicleID().equals(vehicleId)) {   //if vehicle is the one we're looking for
                    vehicle = v;
                    switch(vehicle.getTypeOfVehicle()) {    //set child object
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
            type = vehicle.getTypeOfVehicle();              //indicate type of vehicle
        } else {
            vehicle = new Vehicle(user);                    //if not existing, instantiate new Vehicle
            vehicleId = vehicle.getVehicleID();
            type = "";
        }
    }

    /**
     * find image to set up view
     */
    private void findImages(){
        if (vehicle.getImagesOfVehicle() != null && vehicle.getImagesOfVehicle().size() != 0) {
//            for (String id: vehicle.getImagesOfVehicle()) {
//                setImage(id);
//            }
            imageIndex = 0;
            setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        }
        setPicVisibility();
    }

    /**
     * set delete button, next, prev buttons based on which image the user is on
     */
    private void setPicVisibility(){
        if (vehicle.getImagesOfVehicle().size() == 0){
            deleteImage.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (vehicle.getImagesOfVehicle().size() == 1) {
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imageIndex == 0){
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imageIndex == vehicle.getImagesOfVehicle().size() - 1){
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.VISIBLE);
        } else {
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
        }
    }

    /**
     * set details fields with correct information
     */
    private void setFields(){
        title.setText(vehicle.getTitle());
        setSpecial();
        if (vehicle.isSetUp()) {
            capacity.setText(""+vehicle.getCapacity());
            createVehicle.setText("Update Vehicle");
            numberPlate.setText(vehicle.getNumberPlate());
        } else {
            capacity.setText("");
            createVehicle.setText("Create Vehicle");
            numberPlate.setText("");
        }
        findImages();
        owner.setText(user.getName());
        uniqueVehicleId.setText(vehicleId);
        deleteImage.setImageResource(R.drawable.baseline_delete_forever_24);
        setSpinner();
    }

    /**
     * set image of vehicle based on imageId
     * @param imageId
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
     * when user presses the image to upload an image, call choosePicture()
     * @param v
     */
    public void uploadPic(View v){
        choosePicture();
    }

    /**
     * retrieve image selected by user, set as vehicle image
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null) {
            imageUri = data.getData();
            imageOfVehicle.setImageURI(imageUri);
            imageIndex = vehicle.getImagesOfVehicle().size()-1;
            setPicVisibility();
            uploadPicture();
        }
    }

    /**
     * upload selected image to firebase storage
     */
    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Uploading Image...");
        pd.show();

        String imageId = UUID.randomUUID().toString();
        vehicle.addImagesOfVehicle(imageId);
        StorageReference picRef = storageReference.child("image/" + imageId);

        picRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Image Uploaded.", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(AddNewVehicleActivity.this, "Failed to Upload", Toast.LENGTH_LONG).show();
                })
                .addOnProgressListener(snapshot -> {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Percentage Uploaded: " + (int) progressPercent + "%");
                });
    }

    /**
     * start new intent to choose picture
     */
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    /**
     * when user presses next image, show the next image
     * @param v
     */
    public void nextImage(View v){
        imageIndex++;
        imageIndex %= vehicle.getImagesOfVehicle().size();
        setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        setPicVisibility();
    }

    /**
     * when user presses prev image, show the previous image
     * @param v
     */
    public void prevImage(View v){
        imageIndex--;
        if (imageIndex < 0) { imageIndex = vehicle.getImagesOfVehicle().size()-1; }
        setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        setPicVisibility();
    }

    /**
     * when user presses to delete image, ask for confirmation then delete
     * @param v
     */
    public void deleteImage(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Image");
        builder.setMessage("Are you sure you want to delete this image?");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            deleteImageHelper();
        });

        builder.setNegativeButton("No", (dialog, which) -> {
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * remove image from vehicle and set the image to another
     */
    private void deleteImageHelper(){
        String imageToDelete = vehicle.getImagesOfVehicle().get(imageIndex);
        vehicle.getImagesOfVehicle().remove(imageIndex);
        removePicture(imageToDelete);
        if (imageIndex==vehicle.getImagesOfVehicle().size()) { imageIndex = 0; }
        if (vehicle.getImagesOfVehicle().size() != 0) {
            setImage(vehicle.getImagesOfVehicle().get(imageIndex));
        } else {
            imageOfVehicle.setImageResource(R.drawable.cardefaultpic);
            deleteImage.setVisibility(View.INVISIBLE);
        }
        setPicVisibility();
    }

    /**
     * delete image from firestore storage
     * @param imageId
     */
    private void removePicture(String imageId) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Deleting Image...");
        pd.show();

        StorageReference picRef = storageReference.child("image/" + imageId);

        picRef.delete()
                .addOnSuccessListener(taskSnapshot -> {
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Image Removed.", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(AddNewVehicleActivity.this, "Failed to Remove", Toast.LENGTH_LONG).show();
                });
    }


    /**
     * set special field of details
     */
    private void setSpecial(){
        if (car != null) {
            specialLabel.setHint("Car Model");
            special.setText(car.getCarModel());
        } else if (electricCar != null) {
            specialLabel.setHint("Electric Car Model");
            special.setText(electricCar.getECarModel());
        } else if (motorcycle != null) {
            specialLabel.setHint("Motorcycle Model");
            special.setText(motorcycle.getMotorcycleModel());
        } else {
            specialLabel.setHint(type + " Model");
            special.setText("");
        }
    }

    /**
     * set spinner as either car, electric car, or motorcycle
     */
    private void setSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types_of_vehicles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfVehicle.setAdapter(adapter);
        switch (vehicle.getTypeOfVehicle()) {
            case "Motorcycle":
                typeOfVehicle.setSelection(2);
                break;
            case "Electric Car":
                typeOfVehicle.setSelection(1);
                break;
            default:
                typeOfVehicle.setSelection(0);
        }
        typeOfVehicle.setOnItemSelectedListener(this);
    }

    /**
     * set details as invisible while progress bar show
     */
    private void setInvisible(){
        everythingView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();
    }

    /**
     * set details as visible and hide progress bar
     */
    private void setVisible(){
        everythingView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * when user presses back button, return to vehicles page
     * @param v
     */
    public void backToVehicles(View v){
        toVehicles();
    }

    /**
     * start new intent to vehicles page
     */
    private void toVehicles() {
        Intent intent = new Intent(this, ViewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

    /**
     * based on item selected on the Spinner, set type of vehicle
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        type = parent.getItemAtPosition(position).toString();
        setSpecial();

        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        ((TextView) parent.getChildAt(0)).setTextSize(16);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * update vehicle on firebase; update user with replaced vehicle on firebase
     * @param v
     */
    public void updateVehicle(View v){
        String newTitle = title.getText().toString();
        String uniqueModel = special.getText().toString();
        int cap = Integer.parseInt(capacity.getText().toString());
        String numPlate = numberPlate.getText().toString();

        switch(type) {
            case "Car":
                car = new Car(user, vehicleId, type, newTitle, cap, uniqueModel, numPlate, vehicle.getImagesOfVehicle(), true, vehicle.getRides());
                updateUserFirebase(car);
                updateVehicleFirebase(car);
                break;
            case "Electric Car":
                electricCar = new ElectricCar(user, vehicleId, type, newTitle, cap, uniqueModel, numPlate, vehicle.getImagesOfVehicle(), true, vehicle.getRides());
                updateUserFirebase(electricCar);
                updateVehicleFirebase(electricCar);
                break;
            default:
                motorcycle = new Motorcycle(user, vehicleId, type, newTitle, cap, uniqueModel, numPlate, vehicle.getImagesOfVehicle(), true, vehicle.getRides());
                updateUserFirebase(motorcycle);
                updateVehicleFirebase(motorcycle);
        }

        Toast.makeText(AddNewVehicleActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        toVehicles();
    }

    /**
     * update Car on firebase
     * @param c
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
     * update ElectricCar on firebase
     * @param c
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
     * update Motorcycle on firebase
     * @param c
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
     * update user in Firebase
     * @param c
     */
    private void updateUserFirebase(Object c) {
        int index = 0;
        Vehicle v;
        switch(user.getUserType()){
            case "Student":
                v = (Vehicle) c;
                index = studentUser.containsVehicle(v);
                if (index >= 0){
                    studentUser.replaceVehicle(index, c);
                } else {
                    studentUser.addVehicle(c);
                }
                updateUserFirebaseHelper(studentUser);
                break;
            case "Staff":
                v = (Vehicle) c;
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
     * update Student on firebase
     * @param student
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
     * update Staff on firebase
     * @param staff
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
     * update Alum on firebase
     * @param alum
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