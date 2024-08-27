package com.example.zen_timer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;

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

        // 1-second pause
        new Handler().postDelayed(this::start10SecondCountdown, 1000);
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
        final long totalSeconds = totalTimeMillis / 1000;
        countDownTimer = new CountDownTimer(totalTimeMillis, 1000) {
            long secondsRemaining = totalSeconds;

            @Override
            public void onTick(long millisUntilFinished) {
                timerView.updateTimerText(secondsRemaining * 1000);
                timerView.updateWheelRotation(secondsRemaining * 1000, totalTimeMillis);
                secondsRemaining--;
            }

            @Override
            public void onFinish() {
                timerView.updateTimerText(0);
                timerView.updateWheelRotation(0, totalTimeMillis);
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
    }

    private void playSound() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}