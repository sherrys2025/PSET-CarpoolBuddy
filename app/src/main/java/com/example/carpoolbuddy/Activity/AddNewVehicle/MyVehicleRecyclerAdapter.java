package com.example.carpoolbuddy.Activity.AddNewVehicle;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Vehicles.Vehicle;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * MyVehicleRecyclerAdapter is a RecyclerAdapter that sets up the recycler view in ViewVehicleActivity. It displays
 * user's different vehicles. It includes three buttons that users can press to add a ride, edit the vehicle, or
 * delete the vehicle as a whole.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class MyVehicleRecyclerAdapter extends RecyclerView.Adapter<MyVehicleRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Vehicle> vehicles;
    private Uri imageUri;
    private final MyVehicleButtons myVehicleButtons;

    public MyVehicleRecyclerAdapter(Context context, ArrayList<Vehicle> vehicles, MyVehicleButtons myVehicleButtons){
        this.context = context;
        this.vehicles = vehicles;
        this.myVehicleButtons = myVehicleButtons;
    }

    /**
     * Creates the view holder in the Recycler View
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public MyVehicleRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.vehicle_recycler_view, parent, false);
        return new MyVehicleRecyclerAdapter.MyViewHolder(view, myVehicleButtons);
    }

    /**
     * Set up each view holder with the vehicle's information.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyVehicleRecyclerAdapter.MyViewHolder holder, int position) {
        holder.title.setText(vehicles.get(position).getTitle());
        holder.description.setText(vehicles.get(position).getTypeOfVehicle() + ", " + vehicles.get(position).getNumberPlate());
        if (vehicles.get(position).getImagesOfVehicle().isEmpty()) {
            holder.thumbnail.setImageResource(R.drawable.cardefaultpic);
        } else {
            setImage(vehicles.get(position).getImagesOfVehicle().get(0), holder);
        }
    }

    /**
     * Set the thumbnail of ViewHolder by retrieving the image from FirebaseStorage
     * @param imageId image's unique ID from FirebaseStorage
     * @param holder the ViewHolder
     */
    private void setImage(String imageId, @NonNull MyVehicleRecyclerAdapter.MyViewHolder holder){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imageToSet = firebaseStorage.getReference("image/" + imageId);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            File finalLocalFile = localFile;
            imageToSet.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                imageUri = Uri.fromFile(finalLocalFile);
                holder.thumbnail.setImageURI(imageUri);
                myVehicleButtons.finishSetup();
            }).addOnFailureListener(e -> {
                System.out.println(e.getMessage().toString());
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * Count amount of views in the recycler view
     * @return amount of views in the recycler view
     */
    @Override
    public int getItemCount() {
        return vehicles.size();
    }

    /**
     * This is an inner class that represents each of the ViewHolders within the Recycler View.
     *
     * @author sherrys2025
     * @version 1.0
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView thumbnail;
        private TextView title, description;
        private ImageButton edit, addRide, delete;

        public MyViewHolder(@NonNull View itemView, MyVehicleButtons myVehicleButtons) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.myVehicleImageRow);
            title = itemView.findViewById(R.id.titleMyVehicleRow);
            description = itemView.findViewById(R.id.typeOfMyVehicleRow);
            edit = itemView.findViewById(R.id.editMyVehicleRow);
            addRide = itemView.findViewById(R.id.addRideMyVehicleRow);
            delete = itemView.findViewById(R.id.deleteMyVehicleRow);

            /**
             * When the edit button is pressed, call onEditClick()
             */
            edit.setOnClickListener(v -> {
                if (myVehicleButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myVehicleButtons.onEditClick(pos);
                    }
                }
            });

            /**
             * When the add button is pressed, call onAddClick()
             */
            addRide.setOnClickListener(v -> {
                if (myVehicleButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myVehicleButtons.onAddClick(pos);
                    }
                }
            });

            /**
             * When the delete button is pressed, call onDeleteClick()
             */
            delete.setOnClickListener(v -> {
                if (myVehicleButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myVehicleButtons.onDeleteClick(pos);
                    }
                }
            });
        }
    }
}
