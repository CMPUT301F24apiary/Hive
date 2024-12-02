package com.example.hive.Views;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.Controllers.FirebaseController;
import com.example.hive.Models.User;
import com.example.hive.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * The first time a user uses the app, their device id is stored in db
 * and they can choose between permitted roles (entrant, organizer).
 * if a device id is registered as admin in the database, they are
 * given the option of using the app as an admin
 */
public class FirstTimeActivity extends AppCompatActivity {
    FirebaseController firebaseController = new FirebaseController();
    FirebaseFirestore db = firebaseController.getDb();

    private User currentUser = User.getInstance();
    private EditText editName;
    private EditText editEmail;
    private EditText editPhoneNumber;
    private Button buttonToBegin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first_time);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_first_time), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonToBegin = findViewById(R.id.buttonToBegin);
        buttonToBegin.setOnClickListener(view-> validateStart());
    }

    /**
     * Check if email is taken.
     * Make sure the user signs up properly.
     */
    protected void validateStart() {


        editName = findViewById(R.id.editTextUserName);
        editEmail = findViewById(R.id.editTextEmailAddress);
        editPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        String userName = editName.getText().toString();
        String email = editEmail.getText().toString();
        String phoneNumber = editPhoneNumber.getText().toString();



        // validation now
        if (userName.isEmpty()) {
            Toast.makeText(FirstTimeActivity.this, "Enter name", Toast.LENGTH_LONG).show();
        } else if (userName.equals(" ")) {
            Toast.makeText(FirstTimeActivity.this, "Enter name", Toast.LENGTH_LONG).show();
        } else if (email.isEmpty()) {
            Toast.makeText(FirstTimeActivity.this, "Enter email", Toast.LENGTH_LONG).show();
        }

        firebaseController.checkUserByEmail(email).thenAccept(isUserUnique -> {
            if (isUserUnique) {
                Toast.makeText(this, "Email in use", Toast.LENGTH_LONG).show();
            } else {
                String deviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                completeRegistration(deviceId, userName, email, phoneNumber);
            }
        }).exceptionally(e -> {
            Toast.makeText(this, "Error with email check", Toast.LENGTH_LONG).show();
            Log.e("FistTimeActivity", "Error checking mail: ", e);
            return null;
        });
    }

    /**
     * Completes registration of user to db after validation
     * @param deviceId
     * @param userName
     * @param email
     * @param phoneNumber
     */
    private void completeRegistration(String deviceId, String userName, String email, @Nullable String phoneNumber) {
        List<String> roles = new ArrayList<>(); // can add organizer later if organizer wishes to be one; entrant is the default role
        roles.add("entrant");

        firebaseController.addUser(deviceId, userName, email, "entrant", roles, phoneNumber, "");
        Toast.makeText(this, "Entering app", Toast.LENGTH_LONG).show();
        completeFirstTimeActivity();
    }

    /**
     * This leads to the next activity after the 'enter' button is pressed.
     */
    public void completeFirstTimeActivity() {
        Intent roleSelectionIntent = new Intent(this, RoleSelectionActivity.class);
        startActivity(roleSelectionIntent);
    }
}

