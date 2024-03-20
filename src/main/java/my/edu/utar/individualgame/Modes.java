package my.edu.utar.individualgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class Modes extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modes);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        Button playButton2 = findViewById(R.id.button2);

        // Set up OnClickListener for the "Play" button
        playButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the Comparison activity
                Intent intent = new Intent(Modes.this, Comparison.class);
                startActivity(intent);
            }
        });

        Button playButton3 = findViewById(R.id.button3);

        // Set up OnClickListener for the "Play" button
        playButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the Ordering activity
                Intent intent = new Intent(Modes.this, Ordering.class);
                startActivity(intent);
            }
        });

        Button playButton4 = findViewById(R.id.button4);

        // Set up OnClickListener for the "Play" button
        playButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an Intent to start the Composition activity
                Intent intent = new Intent(Modes.this, Composition.class);
                startActivity(intent);
            }
        });
    }
}
