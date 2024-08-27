package com.example.zen_timer;

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

    public TimerView(MainActivity activity) {
        timerText = activity.findViewById(R.id.timerText);
        logoImageView = activity.findViewById(R.id.image_1);
    }

    public void updateTimerText(long timeMillis) {
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

    public void updateWheelRotation(long timeMillis, long totalTimeMillis) {
        float progress = (float) timeMillis / totalTimeMillis;
        float rotation = progress * 1440; // 1440 degrees = 4 full rotations
        logoImageView.setRotation(rotation);
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