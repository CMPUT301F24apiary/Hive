package com.example.hive.Views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hive.R;

public class OrganizerNotificationActivity extends AppCompatActivity {

    private SwitchCompat selected, cancelled, waiting;
    private EditText message;
    private Button sendNotif;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_organizer_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selected = findViewById(R.id.selected_entrants_switch);
        cancelled = findViewById(R.id.cancelled_entrants_switch);
        waiting = findViewById(R.id.waiting_list_switch);
        message = findViewById(R.id.message_edit_text);
        sendNotif = findViewById(R.id.send_notif_button);
        back = findViewById(R.id.back_arrow);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null) {
            finish();
            return;
        }

        // Skeleton - add logic to send notifications here

    }
}