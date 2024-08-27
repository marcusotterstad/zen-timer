package com.example.zen_timer;

import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

/**
 * MainActivity is the entry point of the Zen Timer application.
 * It sets up the user interface and initializes the core components of the app.
 * This class is responsible for:
 * 1. Creating and managing instances of TimerView, TimerController, and ZenGestureListener.
 * 2. Setting up the touch listener for the main layout to handle user interactions.
 * 3. Managing the lifecycle of the app, including proper resource cleanup on destroy.
 */
public class MainActivity extends AppCompatActivity {
    private TimerView timerView;
    private TimerController timerController;
    private ZenGestureListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerView = new TimerView(this);
        timerController = new TimerController(this, timerView);
        gestureListener = new ZenGestureListener(timerController, timerView);

        ConstraintLayout mainLayout = findViewById(R.id.main);
        mainLayout.setOnTouchListener((v, event) -> {
            gestureListener.onTouch(v, event);
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerController.release();
    }
}