package com.example.carpoolbuddy.Activity.Explore;

import android.content.Context;
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
 * MyExploreRecyclerAdapter is a RecyclerAdapter that sets up the recycler view in VehicleProfileActivity. It displays
 * the different vehicles open for booking. It includes a button that users can press to book the ride.
 *
 * @author sherrys2025
 * @version 1.0
 */
public class MyExploreRecyclerAdapter extends RecyclerView.Adapter<MyExploreRecyclerAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Ride> rides;
    private Uri imageUri;
    private final MyExploreButtons myExploreButtons;

    public MyExploreRecyclerAdapter(Context context, ArrayList<Ride> rides, MyExploreButtons myExploreButtons) {
        this.context = context;
        this.rides = rides;
        this.myExploreButtons = myExploreButtons;
    }

    /**
     * Creates the view holder in the Recycler View
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return MyViewHolder, the small components in the Recycler View
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_recycler_view, parent, false);
        return new MyExploreRecyclerAdapter.MyViewHolder(view, myExploreButtons);
    }

    /**
     * Set up each view holder with the ride's information.
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(rides.get(position).getTitle());

        String dateToSet = rides.get(position).getDate().toString();
        int remove = dateToSet.indexOf("00:00:00 GMT+08:00"); //format date
        dateToSet = dateToSet.substring(0, remove) + dateToSet.substring(remove+19);
        holder.date.setText(dateToSet);

        //display type of vehicle and price
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
    private void setImage(String imageId, @NonNull MyExploreRecyclerAdapter.MyViewHolder holder){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference imageToSet = firebaseStorage.getReference("image/" + imageId);
        File localFile = null;
        try {
            localFile = File.createTempFile("images", "jpg");
            File finalLocalFile = localFile;
            imageToSet.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                imageUri = Uri.fromFile(finalLocalFile);
                holder.thumbnail.setImageURI(imageUri);
                myExploreButtons.finishSetup();
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
     * @author sherrys2025
     * @version 1.0
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView thumbnail;
        private TextView title, description, date;
        private ImageButton edit, delete;
        private CardView cardView;
        public MyViewHolder(@NonNull View itemView, MyExploreButtons myExploreButtons) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.myExploreImageRow);
            title = itemView.findViewById(R.id.titleMyExploreRow);
            description = itemView.findViewById(R.id.typeOfMyExploreRow);
            date = itemView.findViewById(R.id.dateMyExplore);
            edit = itemView.findViewById(R.id.editMyExploreRow);
            cardView = itemView.findViewById(R.id.cardViewExplore);

            /**
             * When the edit button is pressed on the recycler view, call onEditClick()
             */
            edit.setOnClickListener(v -> {
                if (myExploreButtons != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        myExploreButtons.onEditClick(pos);
                    }
                }
            });
        }

    }
}
