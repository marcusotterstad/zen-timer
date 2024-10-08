package com.example.zen_timer;

import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public class ZenGestureListener {
    private TimerController timerController;
    private TimerView timerView;
    private float lastTouchAngle = 0f;
    private float cumulativeRotation = 0f;
    private float[] rotationBuffer = new float[5];
    private int rotationBufferIndex = 0;
    private boolean isRotating = false;
    private float lastHapticRotation = 0f;
    private static final float HAPTIC_ROTATION_THRESHOLD = 30f;

    public ZenGestureListener(TimerController timerController, TimerView timerView) {
        this.timerController = timerController;
        this.timerView = timerView;
    }

    public boolean onTouch(View v, MotionEvent event) {
        if (timerController.isTimerRunning()) {
            return true; // Ignore touch events if the timer is running
        }

        float x = event.getX();
        float y = event.getY();
        float centerX = timerView.getLogoCenterX();
        float centerY = timerView.getLogoCenterY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouchAngle = calculateAngle(x, y, centerX, centerY);
                isRotating = true;
                lastHapticRotation = 0f;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRotating) {
                    float currentAngle = calculateAngle(x, y, centerX, centerY);
                    float angleDiff = lastTouchAngle - currentAngle;

                    if (angleDiff > 180) angleDiff -= 360;
                    if (angleDiff < -180) angleDiff += 360;

                    if (Math.abs(angleDiff) > 0.5) {
                        cumulativeRotation += angleDiff;
                        cumulativeRotation = Math.max(0, cumulativeRotation);

                        rotationBuffer[rotationBufferIndex] = cumulativeRotation;
                        rotationBufferIndex = (rotationBufferIndex + 1) % rotationBuffer.length;
                        float smoothRotation = calculateAverageRotation();

                        timerView.setLogoRotation(-smoothRotation);
                        timerController.updateTimerFromRotation(smoothRotation);
                        lastTouchAngle = currentAngle;

                        // Perform haptic feedback when rotation threshold is reached
                        if (Math.abs(smoothRotation - lastHapticRotation) >= HAPTIC_ROTATION_THRESHOLD) {
                            v.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK);
                            lastHapticRotation = smoothRotation;
                        }
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

    public void resetGesture() {
        lastTouchAngle = 0f;
        cumulativeRotation = 0f;
        rotationBuffer = new float[5];
        rotationBufferIndex = 0;
        isRotating = false;
        lastHapticRotation = 0f;
    }
}