package com.example.carpoolbuddy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import java.util.ArrayList;
import java.util.UUID;

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
    private TextInputLayout titleLabel, specialLabel, capacityLabel;
    private TextInputEditText title, special, capacity, owner, uniqueVehicleId;
    private Spinner typeOfVehicle;
    private Button createVehicle;
    private ImageView imageOfVehicle, deleteImage;
    private ImageButton prev, next;
    private ProgressBar progressBar;
    private Uri imageUri;
    private ArrayList<Uri> imagesUri;
    private int imageIndex;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_vehicle);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        everythingView = findViewById(R.id.everythingVehicle);
        errorMsg = findViewById(R.id.errorMsgVehicle);
        titleLabel = findViewById(R.id.titleVehicle);
        title = findViewById(R.id.titleVehicleEdit);
        specialLabel = findViewById(R.id.specialVehicle);
        special = findViewById(R.id.specialVehicleEdit);
        typeOfVehicle = findViewById(R.id.typeOfVehicleEdit);
        capacityLabel = findViewById(R.id.capacityVehicle);
        capacity = findViewById(R.id.capacityVehicleEdit);
        owner = findViewById(R.id.ownerVehicleEdit);
        uniqueVehicleId = findViewById(R.id.uniqueIdVehicleEdit);
        createVehicle = findViewById(R.id.updateVehicle);
        imageOfVehicle = findViewById(R.id.vehicleImage);
        deleteImage = findViewById(R.id.deleteImageVehicle);
        prev = findViewById(R.id.prevImageVehicle);
        next = findViewById(R.id.nextImageVehicle);
        progressBar = findViewById(R.id.progressBarVehicle);

        imagesUri = new ArrayList<>();
        setInvisible();

        userId = getIntent().getStringExtra("uid");
        vehicleId = getIntent().getStringExtra("vid");
        findUser();

    }

    private void findUser(){
        DocumentReference docRef = firestore.collection("users").document(userId);
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
                    findVehicle();
                    setFields();
                    setVisible();

                } else {
                    Log.d("TAG", "User document does not exist");
                }
            } else {
                Log.d("TAG", "Error retrieving user document: " + task.getException());
            }
        });
    }

    private void findVehicle(){
        if (vehicleId != null) {
            for (Object ve: user.getOwnedVehicles()) {
                Vehicle v = (Vehicle) ve;
                if (v.getVehicleID().equals(vehicleId)) {
                    vehicle = v;
                    switch(vehicle.getTypeOfVehicle()) {
                        case "Car":
                            car = (Car) ve;
                            break;
                        case "Electric Car":
                            electricCar = (ElectricCar) ve;
                            break;
                        default:
                            motorcycle = (Motorcycle) ve;
                    }
                    break;
                }
            }

            type = vehicle.getTypeOfVehicle();
        } else {
            vehicle = new Vehicle(user);
            vehicleId = vehicle.getVehicleID();
            type = "";
        }
    }

    private void findImages(){
        if (vehicle.getImagesOfVehicle() != null && vehicle.getImagesOfVehicle().size() != 0) {
            for (String id: vehicle.getImagesOfVehicle()) {
                setImage(id);
            }
            imageIndex = 0;
            imageOfVehicle.setImageURI(imagesUri.get(imageIndex));
        }
        setPicVisibility();
    }

    private void setPicVisibility(){
        if (imagesUri.size() == 0){
            deleteImage.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imagesUri.size() == 1) {
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imageIndex == 0){
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.INVISIBLE);
        } else if (imageIndex == imagesUri.size() - 1){
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.INVISIBLE);
            prev.setVisibility(View.VISIBLE);
        } else {
            deleteImage.setVisibility(View.VISIBLE);
            next.setVisibility(View.VISIBLE);
            prev.setVisibility(View.VISIBLE);
        }
    }

    private void setFields(){
        title.setText(vehicle.getTitle());
        setSpecial();

        if (vehicle.isSetUp()) {capacity.setText(vehicle.getCapacity());}
        else {capacity.setText("");}
        findImages();
        owner.setText(user.getName());
        uniqueVehicleId.setText(vehicleId);
        setSpinner();
    }

    private void setImage(String imageId){
        StorageReference imageToSet = firebaseStorage.getReference("image/" + imageId);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            File finalLocalFile = localFile;
            imageToSet.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                imageUri = Uri.fromFile(finalLocalFile);
                imagesUri.add(imageUri);
            }).addOnFailureListener(exception -> Log.w("Error retrieving image", exception));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void uploadPic(View v){
        choosePicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData()!=null) {
            imageUri = data.getData();
            imageOfVehicle.setImageURI(imageUri);
            imagesUri.add(imageUri);
            imageIndex = imagesUri.size()-1;
            setPicVisibility();
            uploadPicture();
        }
    }

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

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void nextImage(View v){
        imageIndex++;
        imageIndex %= imagesUri.size();
        imageOfVehicle.setImageURI(imagesUri.get(imageIndex));
        setPicVisibility();
    }

    public void prevImage(View v){
        imageIndex--;
        if (imageIndex < 0) { imageIndex = imagesUri.size()-1; }
        imageOfVehicle.setImageURI(imagesUri.get(imageIndex));
        setPicVisibility();
    }

    public void deleteImage(View v){
        imagesUri.remove(imageIndex);
        String imageToDelete = vehicle.getImagesOfVehicle().get(imageIndex);
        vehicle.getImagesOfVehicle().remove(imageIndex);
        removePicture(imageToDelete);
        if (imageIndex==imagesUri.size()) { imageIndex = 0; }

        if (imagesUri.size() != 0) {
            imageOfVehicle.setImageURI(imagesUri.get(imageIndex));
        } else {
            File localFile = new File("https://github.com/sherrys2025/PSET-CarpoolBuddy/blob/04a069387f8cc660c380cf9295bd833f2f88621c/app/src/main/res/drawable/cardefaultpic.png");
            imageOfVehicle.setImageURI(Uri.fromFile(localFile));
            deleteImage.setVisibility(View.INVISIBLE);
        }
        setPicVisibility();
    }

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

    private void setSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.types_of_vehicles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeOfVehicle.setAdapter(adapter);
        typeOfVehicle.setOnItemSelectedListener(this);
    }

    private void setInvisible(){
        everythingView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.animate();
    }

    private void setVisible(){
        everythingView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void backToVehicles(View v){
        Intent intent = new Intent(this, ViewVehicleActivity.class);
        intent.putExtra("uid", user.getUid());
        startActivity(intent);
    }

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

    public void updateVehicle(View v){
        String newTitle = title.getText().toString();
        String uniqueModel = special.getText().toString();
        int cap = Integer.parseInt(capacity.getText().toString());

        switch(type) {
            case "Car":
                car = new Car(user, type, newTitle, cap, uniqueModel, true);
                updateUserFirebase(car);
                updateVehicleFirebase(car);
                break;
            case "Electric Car":
                electricCar = new ElectricCar(user, type, newTitle, cap, uniqueModel, true);
                updateUserFirebase(electricCar);
                updateVehicleFirebase(electricCar);
                break;
            default:
                motorcycle = new Motorcycle(user, type, newTitle, cap, uniqueModel, true);
                updateUserFirebase(motorcycle);
                updateVehicleFirebase(motorcycle);
        }
    }

    private void updateVehicleFirebase(Car c) {
        firestore.collection("vehicles").document(vehicle.getVehicleID()).set(car)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

    private void updateVehicleFirebase(ElectricCar c) {
        firestore.collection("vehicles").document(vehicle.getVehicleID()).set(electricCar)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        Log.w("Updated Firestore", "yay");
                    } else {
                        Log.w("Oh no, oopsie", task.getException().getMessage().toString());
                    }
                });
    }

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

    private void updateUserFirebase(Object c) {
        switch(user.getUserType()){
            case "Student":
                studentUser.addVehicle(c);
                updateUserFirebaseHelper(studentUser);
                break;
            case "Staff":
                staffUser.addVehicle(c);
                updateUserFirebaseHelper(staffUser);
                break;
            default:
                alumUser.addVehicle(c);
                updateUserFirebaseHelper(alumUser);
        }
    }

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