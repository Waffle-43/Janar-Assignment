package my.edu.utar.individualgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Composition extends AppCompatActivity {

    private TextView targetNumberTextView;
    private LinearLayout smallerNumbersLayout;
    private Button resetButton;
    private Button submitButton;
    private TextView roundTextView; // Add TextView to display current round
    private int roundCounter = 1; // Track the current round number
    private int score = 0; // Track the player's score

    private int targetNumber;
    private List<Integer> smallerNumbers;
    private List<Integer> selectedNumbers;

    private static final int NUM_ROUNDS = 10; // Total number of rounds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_composition);

        targetNumberTextView = findViewById(R.id.targetNumberTextView);
        smallerNumbersLayout = findViewById(R.id.smallerNumbersLayout);
        resetButton = findViewById(R.id.resetButton);
        submitButton = findViewById(R.id.submitButton);
        roundTextView = findViewById(R.id.roundTextView); // Initialize roundTextView

        // Initialize the game
        initializeGame();

        // Set click listener for reset button
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeGame();
            }
        });

        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSelectedNumbers();
            }
        });
    }

    private void initializeGame() {
        // Generate a random target number between 10 and 99
        targetNumber = generateRandomNumber(10, 99);
        targetNumberTextView.setText("Number: " + targetNumber);

        // Generate smaller numbers for the current round
        smallerNumbers = generateSmallerNumbers(targetNumber);

        // Shuffle the list of smaller numbers to randomize their position
        Collections.shuffle(smallerNumbers);

        // Clear the layout before adding new buttons
        smallerNumbersLayout.removeAllViews();

        // Create buttons for smaller numbers and add them to the layout
        selectedNumbers = new ArrayList<>();
        for (int num : smallerNumbers) {
            Button button = createButton(num);
            smallerNumbersLayout.addView(button);
        }

        // Enable submit button
        submitButton.setEnabled(true);
    }

    private void updateRoundTextView() {
        roundTextView.setText("Round: " + roundCounter + "/" + NUM_ROUNDS); // Display current round
    }

    private void startNextRound() {
        roundCounter++; // Increment round counter
        updateRoundTextView(); // Update the round TextView
        initializeGame(); // Reinitialize the game for the next round
    }



    private void handleNumberSelection(Button selectedButton) {
        // Get the selected number
        int selectedNumber = Integer.parseInt(selectedButton.getText().toString());

        // Toggle the selection state of the button
        selectedButton.setSelected(!selectedButton.isSelected());

        // Update the button background based on the selection state
        if (selectedButton.isSelected()) {
            selectedButton.setBackgroundResource(R.drawable.blueborder); // Set the selected button background
            // Add the selected number to the list
            selectedNumbers.add(selectedNumber);
        } else {
            selectedButton.setBackgroundResource(R.drawable.button_background23); // Set the default button background
            // Remove the selected number from the list
            selectedNumbers.remove(Integer.valueOf(selectedNumber));
        }

        // Update the Submit button state based on the number of selected buttons
        submitButton.setEnabled(selectedNumbers.size() == 2); // Enable submit button only if exactly 2 buttons are selected
    }

    private void checkSelectedNumbers() {
        // Check if exactly two numbers are selected
        if (selectedNumbers.size() == 2) {
            int sum = selectedNumbers.get(0) + selectedNumbers.get(1);
            if (sum == targetNumber) {
                showCustomDialog("Congratulations!", "It is the right answers.", true);
                score++; // Increment score for correct answer
            } else {
                showCustomDialog("Try Again!", "Not the right answers.", false);
            }

            // Check if all rounds are completed
            if (roundCounter < NUM_ROUNDS) {
                startNextRound(); // Start the next round
            } else {
                // Game over, display final score and options
                showFinalScoreDialog();
            }
        } else {
            Toast.makeText(this, "Please select two numbers first.", Toast.LENGTH_SHORT).show();
        }
        // Disable submit button after each click
        submitButton.setEnabled(false);
    }

    private void showFinalScoreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialogscorecomposition, null);

        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        LinearLayout rootLayout = dialogView.findViewById(R.id.dialog_root_layout);

        titleTextView.setText("Game Over!");
        messageTextView.setText("Final score: " + score);

        // Customize background color of the dialog
        rootLayout.setBackgroundColor(getResources().getColor(R.color.color23));

        builder.setView(dialogView)
                .setPositiveButton("Main Menu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateToModesActivity();
                    }
                })
                .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetGame();
                    }
                })
                .setCancelable(false);

        // Apply custom button styles
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setBackgroundResource(R.drawable.compositionscorebut);
                positiveButton.setTextColor(getResources().getColor(R.color.custom_button_text_color));

                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setBackgroundResource(R.drawable.compositionscorebut);
                negativeButton.setTextColor(getResources().getColor(R.color.custom_button_text_color));
            }
        });

        dialog.show();
    }



    private void resetGame() {
        roundCounter = 1;
        score = 0;
        initializeGame(); // Reset the game
    }

    private void navigateToModesActivity() {
        // Start the ModesActivity
        Intent intent = new Intent(this, Modes.class);
        startActivity(intent);
        // Finish the current activity
        finish();
    }


    private void showCustomDialog(String title, String message, boolean isCorrect) {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.dialogcomposition, null);

        // Find views within the custom layout
        TextView titleTextView = dialogView.findViewById(R.id.titleTextView);
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        LinearLayout rootLayout = dialogView.findViewById(R.id.dialog_root_layout);

        // Set title and message
        titleTextView.setText(title);
        messageTextView.setText(message);

        // Set background color based on correctness
        int backgroundColor = isCorrect ? Color.GREEN : Color.RED;
        rootLayout.setBackgroundColor(backgroundColor);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with the game or perform any additional action
                        if (isCorrect) {
                            startNextRound();
                        } else {
                            // Handle incorrect combination
                        }
                    }
                })
                .setCancelable(false) // Prevent dismissing by tapping outside
                .show();
    }





    private int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    private List<Integer> generateSmallerNumbers(int targetNumber) {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();

        // Generate the first smaller number
        int smallerNumber1 = random.nextInt(targetNumber);
        numbers.add(smallerNumber1);

        // Generate the second smaller number
        int smallerNumber2 = targetNumber - smallerNumber1;
        numbers.add(smallerNumber2);

        // Generate two more random numbers
        for (int i = 0; i < 2; i++) {
            int randomNumber;
            do {
                randomNumber = random.nextInt(targetNumber) + 1; // Generate numbers between 1 and targetNumber
            } while (numbers.contains(randomNumber)); // Ensure the number is unique
            numbers.add(randomNumber);
        }

        return numbers;
    }

    private Button createButton(int number) {
        Button button = new Button(this);
        button.setText(String.valueOf(number));
        button.setBackgroundResource(R.drawable.button_background23); // Set the initial button background
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                Button selectedButton = (Button) v;
                Log.d("Composition", "Button clicked: " + selectedButton.getText());
                handleNumberSelection(selectedButton);
            }
        });
        return button;
    }
}