package com.example.zen_timer;

import android.os.Bundle;
import android.view.MotionEvent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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