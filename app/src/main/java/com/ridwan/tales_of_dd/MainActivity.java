package com.ridwan.tales_of_dd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button click event
        Button meetGuideButton = findViewById(R.id.meetGuideButton);
        meetGuideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the next screen
                Intent intent = new Intent(MainActivity.this, GuideActivity.class);
                startActivity(intent);
            }
        });
    }
}