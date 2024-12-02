package com.example.hive.AdminImage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Controllers.ImageController;
import com.example.hive.R;
import com.example.hive.Views.RoleSelectionActivity;
import com.example.hive.Views.AdminProfileListActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Activity to display list of all images. Implements <code>DeleteImageListener</code>
 *
 * @see DeleteImageListener
 * @author Zach
 */
public class AdminImageListActivity extends AppCompatActivity implements DeleteImageListener {

    /**
     * The <code>ArrayList</code> of image data
     */
    private ArrayList<HashMap<String, String>> images;

    /**
     * The adapter for the recylcer view
     */
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

    /**
     * Handle removal of the image data from the array, once firebase removal is complete. Notify
     * adapter once complete.
     *
     * @param position
     * int: position of the removed image in the array
     */
    private void afterDelete(int position) {
        images.remove(position);
        adapter.notifyItemRemoved(position);
    }

    /**
     * Function that is called by <code>GetAllImagesFromDB</code> in the
     * <code>ImageController</code> once all data is retrieved.
     *
     * @param data
     * ArrayList: Members are HashMap with String key and value. The list of image data retrieved
     * from fierbase.
     */
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
        adapter.notifyDataSetChanged();
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

        findViewById(R.id.role_selection_button).setOnClickListener(v -> {
            Intent i = new Intent(this, RoleSelectionActivity.class);
            startActivity(i);
            finish();
        });

        images = new ArrayList<>();

        adapter = new AdminImageAdapter(images);

        RecyclerView recyclerView = findViewById(R.id.admin_image_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        ImageController controller = new ImageController();

        controller.getAllImagesFromDB(this::updateList);

        // Get references to the three buttons to switch list views
        Button viewProfiles = findViewById(R.id.view_profiles_btn);
        Button viewEvents = findViewById(R.id.view_events_btn);

        // Logic to switch list activities on button presses
        viewProfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminImageListActivity.this,
                        AdminProfileListActivity.class);
                finish();
                startActivity(i);
            }
        });

        viewEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AdminImageListActivity.this,
                        AdminEventListActivity.class);
                finish();
                startActivity(i);
            }
        });

    }
}