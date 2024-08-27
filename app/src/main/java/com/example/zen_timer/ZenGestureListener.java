package com.example.zen_timer;

import android.view.MotionEvent;
import android.view.View;

/**
 * ZenGestureListener handles touch events for the Zen Timer app.
 * It calculates rotations based on user touch input and updates the timer accordingly.
 * This class is responsible for:
 * 1. Detecting and interpreting touch events on the main layout.
 * 2. Calculating the rotation angle based on touch positions.
 * 3. Smoothing the rotation using a buffer for a more fluid user experience.
 * 4. Updating the TimerView and TimerController based on user interactions.
 */

public class ZenGestureListener {
    private TimerController timerController;
    private TimerView timerView;
    private float lastTouchAngle = 0f;
    private float cumulativeRotation = 0f;
    private float[] rotationBuffer = new float[5];
    private int rotationBufferIndex = 0;
    private boolean isRotating = false;

    public ZenGestureListener(TimerController timerController, TimerView timerView) {
        this.timerController = timerController;
        this.timerView = timerView;
    }

    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float centerX = timerView.getLogoCenterX();
        float centerY = timerView.getLogoCenterY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchAngle = calculateAngle(x, y, centerX, centerY);
                isRotating = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRotating) {
                    float currentAngle = calculateAngle(x, y, centerX, centerY);
                    float angleDiff = lastTouchAngle - currentAngle; // Changed this line

                    if (angleDiff > 180) angleDiff -= 360;
                    if (angleDiff < -180) angleDiff += 360;

                    if (Math.abs(angleDiff) > 0.5) {
                        cumulativeRotation += angleDiff;
                        cumulativeRotation = Math.max(0, cumulativeRotation);

                        rotationBuffer[rotationBufferIndex] = cumulativeRotation;
                        rotationBufferIndex = (rotationBufferIndex + 1) % rotationBuffer.length;
                        float smoothRotation = calculateAverageRotation();

                        timerView.setLogoRotation(-smoothRotation); // Added negative sign
                        timerController.updateTimerFromRotation(smoothRotation);
                        lastTouchAngle = currentAngle;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isRotating) {
                    isRotating = false;
                    timerController.startTimerSequence();
                }
                break;
        }
        return true;
    }

    private float calculateAngle(float xTouch, float yTouch, float centerX, float centerY) {
        float deltaX = xTouch - centerX;
        float deltaY = yTouch - centerY;
        return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
    }

    private float calculateAverageRotation() {
        float sum = 0;
        for (float rotation : rotationBuffer) {
            sum += rotation;
        }
        return sum / rotationBuffer.length;
    }
}