package my.edu.utar.individualgame;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Random;

public class Ordering extends AppCompatActivity {

    LinearLayout numbersContainer;
    ArrayList<Integer> numbersList;
    ArrayList<Integer> usedNumbers;
    ArrayList<TextView> boxViews;

    ToggleButton orderToggle;

    boolean isDescending = false; // Flag to track the ordering preference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        orderToggle = findViewById(R.id.orderToggle); // Initialize the toggle button

        // Set listener for toggle button
        orderToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the toggle button state change
                isDescending = isChecked;
            }
        });

        // Initialize numbersContainer and other necessary variables
        numbersContainer = findViewById(R.id.numbersContainer);
        usedNumbers = new ArrayList<>();
        boxViews = new ArrayList<>();

        // Generate random numbers and display them
        generateRandomNumbers();
        displayNumbers();

        // Set touch listener for generated numbers
        for (int i = 0; i < numbersContainer.getChildCount(); i++) {
            View childView = numbersContainer.getChildAt(i);
            childView.setOnTouchListener(new MyTouchListener());
        }

        // Set drop listener for boxes
        for (int i = 0; i < 5; i++) {
            TextView boxView = findViewById(getResources().getIdentifier("box" + (i + 1), "id", getPackageName()));
            boxViews.add(boxView);
            boxView.setOnDragListener(new MyDragListener());
        }

        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartActivity();
            }
        });

        // Set click listener for check button
        Button checkButton = findViewById(R.id.checkButton);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDescending) {
                    // Perform actions for descending order
                    checkDescendingOrder();
                } else {
                    // Perform actions for ascending order
                    checkAscendingOrder();
                }
            }
        });
    }

    private void generateRandomNumbers() {
        numbersList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int randomNumber;
            do {
                randomNumber = random.nextInt(900) + 100; // Generates random 3-digit numbers
            } while (usedNumbers.contains(randomNumber)); // Ensure the number is not used
            numbersList.add(randomNumber);
            usedNumbers.add(randomNumber);
        }
    }

    private void displayNumbers() {
        numbersContainer.removeAllViews(); // Clear the previous numbers
        for (int number : numbersList) {
            TextView textView = new TextView(this);
            textView.setText(String.valueOf(number));
            textView.setTextSize(20);
            textView.setBackgroundResource(R.drawable.box_background); // You need to define this drawable
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(8, 0, 8, 0);
            textView.setLayoutParams(layoutParams);
            textView.setOnTouchListener(new MyTouchListener());
            numbersContainer.addView(textView);
        }
    }

    class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Start drag operation
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDragAndDrop(null, shadowBuilder, view, 0);

                // Hide all boxes
                for (TextView box : boxViews) {
                    box.setVisibility(View.VISIBLE);
                }

                // Disable touch listener for this number
                disablePickedNumber((TextView) view);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Do nothing for now
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    return true;
                case DragEvent.ACTION_DROP:
                    // Handle the drop event
                    View draggedView = (View) event.getLocalState();
                    ViewGroup owner = (ViewGroup) draggedView.getParent();
                    if (owner.getId() == R.id.numbersContainer) {
                        // Check if the drop spot is a box
                        if (v instanceof TextView) {
                            TextView dropSpot = (TextView) v;
                            dropSpot.setText(((TextView) draggedView).getText());
                            draggedView.setVisibility(View.VISIBLE);
                            disablePickedNumber(dropSpot); // Disable touch listener for this box
                        }
                    }
                    // Restore visibility of boxes
                    for (TextView box : boxViews) {
                        box.setVisibility(View.VISIBLE);
                    }
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    return true;
            }
            return false;
        }
    }

    private void disablePickedNumber(TextView textView) {
        textView.setOnTouchListener(null);
    }

    private void checkAscendingOrder() {
        ArrayList<Integer> sortedList = new ArrayList<>();
        // Loop through each box and check if it's empty
        for (int i = 1; i <= 5; i++) {
            TextView textView = findViewById(getResources().getIdentifier("box" + i, "id", getPackageName()));
            String text = textView.getText().toString().trim();
            if (text.isEmpty()) {
                // If any box is empty, display the message and return
                showCustomPrompt("Please fill all the boxes", false);
                return;
            } else {
                // If box is not empty, parse the number and add it to the list
                try {
                    int number = Integer.parseInt(text);
                    sortedList.add(number);
                } catch (NumberFormatException e) {
                    // Handle invalid input, such as non-integer text
                    e.printStackTrace();
                    showCustomPrompt("Invalid input: " + text, false);
                    return;
                }
            }
        }

        // Check if the sorted list is in ascending order
        boolean isAscending = true;
        for (int i = 0; i < sortedList.size() - 1; i++) {
            if (sortedList.get(i) > sortedList.get(i + 1)) {
                isAscending = false;
                break;
            }
        }

        // Display appropriate message based on whether the list is in ascending order or not
        if (isAscending) {
            showCustomPrompt("Numbers are in ascending order!", true);
        } else {
            showCustomPrompt("Numbers are not in ascending order!", false);
        }
    }

    private void checkDescendingOrder() {
        ArrayList<Integer> sortedList = new ArrayList<>();
        // Loop through each box and check if it's empty
        for (int i = 1; i <= 5; i++) {
            TextView textView = findViewById(getResources().getIdentifier("box" + i, "id", getPackageName()));
            String text = textView.getText().toString().trim();
            if (text.isEmpty()) {
                // If any box is empty, display the message and return
                showCustomPrompt("Please fill all the boxes", false);
                return;
            } else {
                // If box is not empty, parse the number and add it to the list
                try {
                    int number = Integer.parseInt(text);
                    sortedList.add(number);
                } catch (NumberFormatException e) {
                    // Handle invalid input, such as non-integer text
                    e.printStackTrace();
                    showCustomPrompt("Invalid input: " + text, false);
                    return;
                }
            }
        }

        // Check if the sorted list is in descending order
        boolean isDescending = true;
        for (int i = 0; i < sortedList.size() - 1; i++) {
            // Compare numbers from right to left
            if (sortedList.get(i) < sortedList.get(i + 1)) {
                isDescending = false;
                break;
            }
        }

        // Display appropriate message based on whether the list is in descending order or not
        if (isDescending) {
            showCustomPrompt("Numbers are in descending order!", true);
        } else {
            showCustomPrompt("Numbers are not in descending order!", false);
        }
    }


    private void restartActivity() {
        // Restart the activity
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void showCustomPrompt(String message, boolean isCorrect) {
        // Inflate the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.custom_alert_dialog, null);

        // Find the root layout of the custom dialog
        LinearLayout rootLayout = dialogView.findViewById(R.id.dialog_root_layout);

        // Set the background color dynamically based on correctness
        int backgroundColor = isCorrect ? Color.GREEN : Color.RED;
        rootLayout.setBackgroundColor(backgroundColor);

        // Find views within the custom layout
        TextView messageTextView = dialogView.findViewById(R.id.messageTextView);
        messageTextView.setText(message);

        // Create the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(dialogView)

                .setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with the game
                        generateRandomNumbers();
                        displayNumbers();
                    }
                });

        // Show the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
