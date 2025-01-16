package com.ridwan.tales_of_dd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.ridwan.tales_of_dd.data.database.AppDatabase;
import com.ridwan.tales_of_dd.data.database.DatabaseInitializer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database with sample data
        AppDatabase db = AppDatabase.getInstance(this);
        DatabaseInitializer.initializeDb(this, db);

        // Button click event
        Button meetGuideButton = findViewById(R.id.meetGuideButton);
        meetGuideButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish(); // Optional: if you don't want users to come back to the welcome screen
        });
    }
}