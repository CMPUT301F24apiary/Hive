package com.example.hive;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.AdminEvent.AdminEventListActivity;
import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.Views.FirstTimeActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    // Zach - DEV BUTTON
    // private Button eventsButton;  // this is a null button object and prevents the first time sign in from launching (crashes app)

    // sign in / log in variables
    private ActivityResultLauncher<Intent> roleSelectionlauncher;
    User currentUser = User.getInstance();
    FirebaseController firebaseController = new FirebaseController();
    FirebaseFirestore db = firebaseController.getDb();


    // sign in with device id if user is already in db otherwise, user must enter info to enter app
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        roleSelectionlauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                   if (result.getResultCode() == RESULT_OK) {
                        Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                        startActivity(roleSelectionIntent);
                        finish();

                   } else {
                        Toast.makeText(this, "registerForActivityResult not set up properly", Toast.LENGTH_LONG).show();
                    }
                }
        );
        String deviceId = retrieveDeviceId();
        firebaseController.checkUserByDeviceId(deviceId).thenAccept(isUserExisting -> {
            if (isUserExisting) {
                Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                startActivity(roleSelectionIntent);
                finish();
            } else {
                Intent firstTimeIntent = new Intent(this, FirstTimeActivity.class);
                Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
                startActivity(firstTimeIntent);
                finish(); // users cannot go back to this activity, even after pressing back button

            }
        }).exceptionally(e->{
            Toast.makeText(this, "Error checking if user exists", Toast.LENGTH_LONG).show();
            return null;
        });



 //       eventsButton = findViewById(R.id.view_events_button);

  //      eventsButton.setOnClickListener(new View.OnClickListener() {
   //         @Override
   //         public void onClick(View v) {
    //            Intent i = new Intent(MainActivity.this, AdminEventListActivity.class);
     //           startActivity(i);
     //       }

//            @Override
//            public void onClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent i = new Intent(MainActivity.this, ShowActivity.class);
//                i.putExtra("city", dataList.get(position));
//                startActivity(i);
//            }
       // });
    }

    public String retrieveDeviceId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void enableEdgeToEdgeMode() {
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}