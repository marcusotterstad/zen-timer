package com.example.zen_timer;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * TimerView manages the visual representation of the Zen Timer.
 * It handles updating the timer text display and the rotation of the logo image.
 *
 * This class is responsible for:
 * 1. Updating the timer text display with the current time.
 * 2. Rotating the logo image based on the timer progress.
 * 3. Providing information about the logo's position for gesture calculations.
 */
public class TimerView {
    private TextView timerText;
    private ImageView logoImageView;
    private ValueAnimator rotationAnimator;

    public TimerView(MainActivity activity) {
        timerText = activity.findViewById(R.id.timerText);
        logoImageView = activity.findViewById(R.id.image_1);
    }

    public void updateTimerText(long timeMillis) {
        int minutes = (int) (timeMillis / 60000);
        int seconds = (int) ((timeMillis % 60000) / 1000);
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }

    public void startSmoothRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }

        rotationAnimator = ValueAnimator.ofFloat(0f, 360f); // 1 full rotation
        rotationAnimator.setDuration(80000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.addUpdateListener(animation -> {
            float rotation = (float) animation.getAnimatedValue();
            logoImageView.setRotation(rotation);
        });
        rotationAnimator.start();
    }

    public void pauseRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.pause();
        }
    }

    public void resumeRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.resume();
        }
    }

    public void stopRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
    }

    public void setLogoRotation(float rotation) {
        logoImageView.setRotation(rotation);
    }

    public float getLogoCenterX() {
        return logoImageView.getX() + logoImageView.getWidth() / 2f;
    }

    public float getLogoCenterY() {
        return logoImageView.getY() + logoImageView.getHeight() / 2f;
    }
}