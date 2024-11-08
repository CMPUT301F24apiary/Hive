package com.example.hive.AdminImage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hive.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Adapter for displaying Images in <code>RecyclerView</code>.
 *
 * @author Zach
 */
public class AdminImageAdapter extends RecyclerView.Adapter<AdminImageAdapter.AdminImageViewHolder> {

    /**
     * The array of images. Each image stored as a <code>HashMap</code> with download URL and some
     * info on the image.
     */
    private final ArrayList<HashMap<String, String>> imageData;

    /**
     * Constructor for adapter.
     *
     * @param imageData
     * The array of images
     */
    public AdminImageAdapter(ArrayList<HashMap<String, String>> imageData) {
        this.imageData = imageData;
    }

    @NonNull
    @Override
    public AdminImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_image_list_content, parent, false);
        return new AdminImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminImageViewHolder holder, int position) {
        HashMap<String, String> currentItem = imageData.get(position);
        String imageUrl = currentItem.get("url");
        String imageInfo = currentItem.get("info");

        // Use Glide to load the image lazily
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.hivelogo) // Placeholder image
                .into(holder.imageView);

        // Set the image info text
        holder.infoTextView.setText(imageInfo);

        holder.deleteBtn.setOnClickListener(v -> {
            // Show confirmation dialog before deletion
            FragmentManager fragmentManager = ((AppCompatActivity) v.getContext())
                    .getSupportFragmentManager();
            ConfirmImageDelete confirmDeleteDialog = ConfirmImageDelete.newInstance(
                    holder.getAdapterPosition(),
                    imageUrl,
                    currentItem.get("id"),
                    currentItem.get("relatedDocID"));
            confirmDeleteDialog.show(fragmentManager, "ConfirmDeleteDialog");
        });
    }

    @Override
    public int getItemCount() {
        return imageData.size();
    }

    /**
     * Inner class for the <code>ViewHolder</code>
     */
    public static class AdminImageViewHolder extends RecyclerView.ViewHolder {
        /**
         * The <code>ImageView</code> to display the given image
         */
        ImageView imageView;
        /**
         * The <code>TextView</code> to display image information
         */
        TextView infoTextView;
        /**
         * The <code>ImageButton</code> that acts as a button to delete the image
         */
        ImageButton deleteBtn;

        public AdminImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.admin_image_list_item_image_view);
            infoTextView = itemView.findViewById(R.id.admin_image_item_image_detail);
            deleteBtn = itemView.findViewById(R.id.delete_image_button);
        }
    }

}
