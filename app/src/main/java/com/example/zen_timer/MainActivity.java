package com.example.zen_timer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView timerText;
    private ImageView imageView;
    private float lastTouchAngle = 0f;
    private float cumulativeRotation = 0f;
    private CountDownTimer countDownTimer;
    private long remainingTimeMillis = 0;
    private boolean isTimerRunning = false;
    private float[] rotationBuffer = new float[5];
    private int rotationBufferIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        imageView = findViewById(R.id.image_1);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isTimerRunning) return false;

                float x = event.getX();
                float y = event.getY();
                float centerX = v.getWidth() / 2f;
                float centerY = v.getHeight() / 2f;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchAngle = calculateAngle(x, y, centerX, centerY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentAngle = calculateAngle(x, y, centerX, centerY);
                        float angleDiff = currentAngle - lastTouchAngle;

                        // Adjust for the case when the angle jumps between 0 and 360 degrees
                        if (angleDiff > 180) angleDiff -= 360;
                        if (angleDiff < -180) angleDiff += 360;

                        // Apply a small threshold to ignore very small movements
                        if (Math.abs(angleDiff) > 0.5) {
                            cumulativeRotation += angleDiff;

                            // Use a moving average for smoother rotation
                            rotationBuffer[rotationBufferIndex] = cumulativeRotation;
                            rotationBufferIndex = (rotationBufferIndex + 1) % rotationBuffer.length;
                            float smoothRotation = calculateAverageRotation();

                            imageView.setRotation(smoothRotation);
                            updateTimerFromRotation(smoothRotation);
                            lastTouchAngle = currentAngle;
                        }
                        break;
                }
                return true;
            }
        });
    }

    private float calculateAverageRotation() {
        float sum = 0;
        for (float rotation : rotationBuffer) {
            sum += rotation;
        }
        return sum / rotationBuffer.length;
    }

    private float calculateAngle(float xTouch, float yTouch, float centerX, float centerY) {
        float deltaX = xTouch - centerX;
        float deltaY = yTouch - centerY;
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private void updateTimerFromRotation(float rotation) {
        // Calculate total minutes based on full rotations (30 minutes per rotation)
        int totalMinutes = (int) (Math.abs(rotation) / 360 * 30);

        // Limit to a maximum of 120 minutes (4 full rotations)
        totalMinutes = Math.min(totalMinutes, 120);

        remainingTimeMillis = totalMinutes * 60 * 1000L;
        updateTimerText(remainingTimeMillis);
    }

    private void updateTimerText(long timeMillis) {
        int hours = (int) (timeMillis / (3600 * 1000));
        int minutes = (int) ((timeMillis % (3600 * 1000)) / 60000);
        int seconds = (int) ((timeMillis % 60000) / 1000);

        String timeFormatted;
        if (hours > 0) {
            timeFormatted = String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeFormatted = String.format("%02d:%02d", minutes, seconds);
        }
        timerText.setText(timeFormatted);
    }

    private void startTimer() {
        if (isTimerRunning) {
            return;
        }

        isTimerRunning = true;
        countDownTimer = new CountDownTimer(remainingTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                updateTimerText(millisUntilFinished);
                updateWheelRotation(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                remainingTimeMillis = 0;
                updateTimerText(0);
                updateWheelRotation(0);
            }
        }.start();
    }

    private void updateWheelRotation(long timeMillis) {
        float progress = (float) timeMillis / (120 * 60 * 1000); // Now based on max 120 minutes
        float rotation = progress * 1440; // 1440 degrees = 4 full rotations
        imageView.setRotation(rotation);
    }

    public void onStartTimerClick(View view) {
        startTimer();
    }
}