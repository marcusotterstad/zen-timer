package com.example.zen_timer;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    private TimerView timerView;
    private TimerController timerController;
    private ZenGestureListener gestureListener;
    private ImageButton resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView timerText = findViewById(R.id.timerText);
        ImageView logoImageView = findViewById(R.id.image_1);
        resetButton = findViewById(R.id.resetButton);
        resetButton.setVisibility(View.INVISIBLE);

        ConstraintLayout mainLayout = findViewById(R.id.main);

        timerView = new TimerView(timerText, logoImageView);
        timerController = new TimerController(this, timerView, resetButton, mainLayout);
        gestureListener = new ZenGestureListener(timerController, timerView);

        mainLayout.setOnTouchListener((v, event) -> {
            gestureListener.onTouch(v, event);
            return true;
        });

        resetButton.setOnClickListener(v -> resetTimer());
    }

    private void resetTimer() {
        timerController.resetTimer();
        timerView.resetView();
        gestureListener.resetGesture();
        resetButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerController.release();
    }
}