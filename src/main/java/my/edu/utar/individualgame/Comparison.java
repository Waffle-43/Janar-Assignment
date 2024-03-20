package my.edu.utar.individualgame;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Comparison extends AppCompatActivity {

    private TextView numberTextView;
    private TextView numberTextView2;
    private Button buttonGreater;
    private Button buttonSmaller;

    private int number1;
    private int number2;


    private int trialCount; // Counter for trials
    private int correctCount; // Counter for correct answers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comparison);

        // Initialize views
        numberTextView = findViewById(R.id.numberTextView);
        numberTextView2 = findViewById(R.id.numberTextView2);
        buttonGreater = findViewById(R.id.buttonGreater);
        buttonSmaller = findViewById(R.id.buttonSmaller);

        // Initialize counters
        trialCount = 0;
        correctCount = 0;

        // Start the first trial
        startNewTrial();

        // Set listeners for button clicks
        buttonGreater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compareNumbers("greater");
            }
        });

        buttonSmaller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compareNumbers("smaller");
            }
        });

        // Apply window insets listener for edge-to-edge design
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Start a new trial
    private void startNewTrial() {
        // Increment trial count
        trialCount++;

        // Check if it's the 10th trial
        if (trialCount > 10) {
            // Display the final score
            displayScore();
            return;
        }

        // Generate random numbers for comparison
        generateRandomNumbers();

        // Update the number display on the screen for the new trial
        updateNumberTextView();
    }

    // Generate random numbers for comparison
    private void generateRandomNumbers() {
        number1 = (int) (Math.random() * 100);
        do {
            number2 = (int) (Math.random() * 100);
        } while (number2 == number1); // Regenerate number2 if it's the same as number1
    }

    // Update the number display on the screen
    private void updateNumberTextView() {
        numberTextView.setText(String.format(" %d: Which is greater?", trialCount));
        numberTextView2.setText(String.format("%d vs %d", number1, number2));
    }

    // Compare the numbers based on user choice
    private void compareNumbers(String userChoice) {
        // Check if the user's choice matches the comparison result
        boolean isCorrect = (userChoice.equals("greater") && number1 > number2) ||
                (userChoice.equals("smaller") && number1 < number2);

        // Display toast message
        displayToast(isCorrect ? "Correct! Great job!" : "Oops! Better luck next time.", isCorrect);

        // Increment correct answer count if the answer is correct
        if (isCorrect) {
            correctCount++;
        }
    }
    // Display the final score
    // Display the final score using a custom dialog with "Repeat Game" and "Main Menu" buttons
    private void displayScore() {

        String score = correctCount + "/" + trialCount;

        // Create a dialog instance
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_score);
        dialog.setCancelable(false); // Prevent dialog from being dismissed by tapping outside

        // Initialize views inside the dialog
        TextView textViewScore = dialog.findViewById(R.id.textViewScore2);
        TextView textViewScoreValue = dialog.findViewById(R.id.textViewScore2);
        Button buttonRepeatGame = dialog.findViewById(R.id.buttonRepeatGame2);
        Button buttonMainMenu = dialog.findViewById(R.id.buttonMainMenu2);

        // Set the score value
        textViewScoreValue.setText(String.valueOf(correctCount));

        // Set click listener for the "Repeat Game" button
        buttonRepeatGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog
                startNewGame(); // Start a new game
            }
        });

        // Set click listener for the "Main Menu" button
        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog
                finish(); // Close the activity and return to the main menu
            }
        });

        // Show the dialog
        dialog.show();
    }

    // Start a new game (reset counters and start new trial)
    private void startNewGame() {
        trialCount = 0; // Reset trial count
        correctCount = 0; // Reset correct count
        startNewTrial(); // Start a new trial
    }





    // Display the result to the user with a styled toast message
    private void displayToast(String message, boolean isCorrect) {
        // Inflate custom toast layout
        View layout = getLayoutInflater().inflate(R.layout.custom_toast,
                findViewById(R.id.custom_toast_container));

        // Set text
        TextView text = layout.findViewById(R.id.custom_toast_text);
        text.setText(message);

        // Set background color and icon based on correctness
        layout.setBackgroundResource(isCorrect ? R.drawable.custom_toast_background_correct : R.drawable.custom_toast_background_incorrect);
        ImageView icon = layout.findViewById(R.id.custom_toast_icon);
        icon.setImageResource(isCorrect ? R.drawable.correct : R.drawable.wrong);

        // Create toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);

        // Set a callback to start a new trial after the toast message is finished displaying
        toast.show();
        toast.getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start a new trial after the toast message is fully displayed
                startNewTrial();
            }
        }, 2000); // Adjust the delay (2000 milliseconds) according to the toast message duration
    }

}
