package com.example.hive.AdminImage;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hive.Controllers.ImageController;
import com.example.hive.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminImageListActivity extends AppCompatActivity implements DeleteImageListener {

    private ArrayList<HashMap<String, String>> images;

    private AdminImageAdapter adapter;

    public void onDelete(int position, String url, String id, String relatedDocID) {
        ImageController imgController = new ImageController();
        imgController.deleteImageAndUpdateRelatedDoc(url, id, relatedDocID, (success) -> {
            if (success) {
                afterDelete(position);
            } else {
                Log.d("ImageDeletion", "Deletion Failed");
            }
        });
    }

    private void afterDelete(int position) {
        images.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void updateList(ArrayList<HashMap<String, String>> data) {
        // Get reference to TextView that displays loading text and hide it
        TextView loading = findViewById(R.id.image_list_loading_text);
        loading.setVisibility(View.GONE);
        // Un-hide the container that displays the list, search, and sort
        findViewById(R.id.admin_image_list_linear_layout).setVisibility(View.VISIBLE);
        // Update the array in this activity and call the adapter's update method
        images.clear();
        images.addAll(data);
        // Notify the adapter that the dataset has changed, so it can update the ListView
        adapter.notifyItemRangeChanged(0, images.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_image_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        images = new ArrayList<>();

        adapter = new AdminImageAdapter(images, this);

        RecyclerView recyclerView = findViewById(R.id.admin_image_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        ImageController controller = new ImageController();

        controller.getAllImagesFromDB(this::updateList);

    }
}