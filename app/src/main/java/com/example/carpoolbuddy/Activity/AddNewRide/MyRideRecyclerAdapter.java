package com.example.carpoolbuddy.Activity.AddNewRide;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carpoolbuddy.R;
import com.example.carpoolbuddy.Vehicles.Ride;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * MyRideRecyclerAdapter is a RecyclerAdapter that sets up the recycler view in ViewRideActivity. It displays
 * user's different booked rides. It includes three buttons that users can press to view the map navigation,
 * edit the booking, or delete the ride as a whole (if they are the owner).
 *
 * @author sherrys2025
 * @version 1.0
 */
public class MyRideRecyclerAdapter extends RecyclerView.Adapter<MyRideRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Ride> rides;
    private Uri imageUri;
    private final MyRideButtons myRideButtons;
    public MyRideRecyclerAdapter(Context context, ArrayList<Ride> rides, MyRideButtons myRideButtons){
        this.context = context;
        this.rides = rides;
        this.myRideButtons = myRideButtons;
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
    public MyRideRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ride_recycler_view, parent, false);
        return new MyRideRecyclerAdapter.MyViewHolder(view, myRideButtons);
    }

    /**
     * Set up each view holder with the ride's information.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyRideRecyclerAdapter.MyViewHolder holder, int position) {
        holder.title.setText(rides.get(position).getTitle());

        String dateToSet = rides.get(position).getDate().toString();
        System.out.println(dateToSet);
        int remove = dateToSet.indexOf("00:00:00 GMT+08:00");
        dateToSet = dateToSet.substring(0, remove) + dateToSet.substring(remove+19);
        System.out.println(dateToSet);
        holder.date.setText(dateToSet);
        if (!rides.get(position).isOpen()) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#BABABA"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.description.setText(rides.get(position).getTypeOfVehicle() + ", $" + rides.get(position).getBasePrice());
        if (rides.get(position).getImagesOfVehicle().isEmpty()) {
            holder.thumbnail.setImageResource(R.drawable.cardefaultpic);
        } else {
            setImage(rides.get(position).getImagesOfVehicle().get(0), holder);
        }
    }

    /**
     * Set the thumbnail of ViewHolder by retrieving the image from FirebaseStorage
     * @param imageId image's unique ID from FirebaseStorage
     * @param holder the ViewHolder
     */
    private void setImage(String imageId, @NonNull MyRideRecyclerAdapter.MyViewHolder holder){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imageToSet = firebaseStorage.getReference("image/" + imageId);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            File finalLocalFile = localFile;
            imageToSet.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                imageUri = Uri.fromFile(finalLocalFile);
                holder.thumbnail.setImageURI(imageUri);
                myRideButtons.finishSetup();
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
        return rides.size();
    }

    /**
     * This is an inner class that represents each of the ViewHolders within the Recycler View.
     *
     * @author sherrys2025
     * @version 1.0
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView thumbnail;
        private TextView title, description, date;
        private ImageButton edit, delete, navigate;
        private CardView cardView;

        public MyViewHolder(@NonNull View itemView, MyRideButtons myRideButtons) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.myRideImageRow);
            title = itemView.findViewById(R.id.titleMyRideRow);
            description = itemView.findViewById(R.id.typeOfMyRideRow);
            date = itemView.findViewById(R.id.dateMyRide);
            edit = itemView.findViewById(R.id.editMyRideRow);
            delete = itemView.findViewById(R.id.deleteMyRideRow);
            cardView = itemView.findViewById(R.id.cardViewRide);
            navigate = itemView.findViewById(R.id.navigateMyRideRow);

            /**
             * When the edit button is pressed, call onEditClick()
             */
            edit.setOnClickListener(v -> {
                if (myRideButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myRideButtons.onEditClick(pos);
                    }
                }
            });

            /**
             * When the delete button is pressed, call onDeleteClick()
             */
            delete.setOnClickListener(v -> {
                if (myRideButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myRideButtons.onDeleteClick(pos);
                    }
                }
            });

            /**
             * When the navigate button is pressed, call onNavigateClick()
             */
            navigate.setOnClickListener(v -> {
                if (myRideButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myRideButtons.onNavigateClick(pos);
                    }
                }
            });
        }
    }
}
