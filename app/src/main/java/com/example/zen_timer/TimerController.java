package com.example.zen_timer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;

/**
 * TimerController manages the core logic of the Zen Timer.
 * It handles timer calculations, countdown sequences, and sound playback.
 * This class is responsible for:
 * 1. Calculating timer duration based on user input (rotation).
 * 2. Managing the countdown sequence, including the initial 10-second countdown.
 * 3. Updating the TimerView with current timer values.
 * 4. Playing sounds at appropriate times during the timer sequence.
 * 5. Releasing resources when no longer needed.
 */
public class TimerController {
    private Context context;
    private TimerView timerView;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private long totalTimeMillis = 0;

    public TimerController(Context context, TimerView timerView) {
        this.context = context;
        this.timerView = timerView;
        mediaPlayer = MediaPlayer.create(context, R.raw.singing_bowl);
    }

    public void updateTimerFromRotation(float rotation) {
        int totalMinutes = (int) (Math.abs(rotation) / 360 * 30);
        totalMinutes = Math.min(totalMinutes, 120);
        totalTimeMillis = totalMinutes * 60 * 1000L;
        timerView.updateTimerText(totalTimeMillis);
    }

    public void startTimerSequence() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Start the 10-second prep countdown
        start10SecondCountdown();
    }

    private void start10SecondCountdown() {
        countDownTimer = new CountDownTimer(10000, 1000) {
            int secondsRemaining = 10;

            @Override
            public void onTick(long millisUntilFinished) {
                timerView.updateTimerText(secondsRemaining * 1000L);
                secondsRemaining--;
            }

            @Override
            public void onFinish() {
                playSound();
                new Handler().postDelayed(() -> startMainCountdownTimer(), 1000);
            }
        }.start();
    }

    private void startMainCountdownTimer() {
        timerView.startSmoothRotation(); // Start constant rotation

        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerView.updateTimerText(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timerView.updateTimerText(0);
                timerView.stopRotation();
                playSound();
            }
        }.start();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        timerView.stopRotation();
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}