package com.example.zen_timer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    private TextView timerText;
    private ImageView logoImageView;
    private float lastTouchAngle = 0f;
    private float cumulativeRotation = 0f;
    private long totalTimeMillis = 0;
    private float[] rotationBuffer = new float[5];
    private int rotationBufferIndex = 0;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private boolean isRotating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timerText = findViewById(R.id.timerText);
        logoImageView = findViewById(R.id.image_1);

        mediaPlayer = MediaPlayer.create(this, R.raw.singing_bowl);

        ConstraintLayout mainLayout = findViewById(R.id.main);
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                float centerX = logoImageView.getX() + logoImageView.getWidth() / 2f;
                float centerY = logoImageView.getY() + logoImageView.getHeight() / 2f;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouchAngle = calculateAngle(x, y, centerX, centerY);
                        isRotating = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isRotating) {
                            float currentAngle = calculateAngle(x, y, centerX, centerY);
                            float angleDiff = currentAngle - lastTouchAngle;

                            if (angleDiff > 180) angleDiff -= 360;
                            if (angleDiff < -180) angleDiff += 360;

                            if (totalTimeMillis == 0 && angleDiff < 0) {
                                break;
                            }

                            if (Math.abs(angleDiff) > 0.5) {
                                cumulativeRotation += angleDiff;
                                cumulativeRotation = Math.max(0, cumulativeRotation);

                                rotationBuffer[rotationBufferIndex] = cumulativeRotation;
                                rotationBufferIndex = (rotationBufferIndex + 1) % rotationBuffer.length;
                                float smoothRotation = calculateAverageRotation();

                                logoImageView.setRotation(smoothRotation);
                                updateTimerFromRotation(smoothRotation);
                                lastTouchAngle = currentAngle;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isRotating) {
                            isRotating = false;
                            startTimerSequence();
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
        int totalMinutes = (int) (Math.abs(rotation) / 360 * 30);
        totalMinutes = Math.min(totalMinutes, 120);
        totalTimeMillis = totalMinutes * 60 * 1000L;
        updateTimerText(totalTimeMillis);
    }

    private void startTimerSequence() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // 1-second pause
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                start5SecondCountdown();
            }
        }, 1000);
    }

    private void start5SecondCountdown() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                playSound();
                startMainCountdownTimer();
            }
        }.start();
    }

    private void startMainCountdownTimer() {
        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
                updateWheelRotation(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                updateTimerText(0);
                updateWheelRotation(0);
                playSound();
            }
        }.start();
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

    private void updateWheelRotation(long timeMillis) {
        float progress = (float) timeMillis / totalTimeMillis;
        float rotation = progress * 1440; // 1440 degrees = 4 full rotations
        logoImageView.setRotation(rotation);
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}