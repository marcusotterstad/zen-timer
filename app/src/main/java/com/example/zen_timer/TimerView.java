package com.example.zen_timer;

import android.animation.ValueAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class TimerView {
    private TextView timerText;
    private ImageView logoImageView;
    private ValueAnimator rotationAnimator;

    public TimerView(TextView timerText, ImageView logoImageView) {
        this.timerText = timerText;
        this.logoImageView = logoImageView;
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

        rotationAnimator = ValueAnimator.ofFloat(0f, 360f);
        rotationAnimator.setDuration(80000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
        rotationAnimator.addUpdateListener(animation -> {
            float rotation = (float) animation.getAnimatedValue();
            logoImageView.setRotation(rotation);
        });
        rotationAnimator.start();
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

    public void resetView() {
        updateTimerText(0);
        stopRotation();
        setLogoRotation(0f);
    }
}