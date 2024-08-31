package com.example.zen_timer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

public class TimerController {
    private Context context;
    private TimerView timerView;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;
    private long totalTimeMillis = 0;
    private ImageButton resetButton;
    private boolean isTimerRunning = false;

    public TimerController(Context context, TimerView timerView, ImageButton resetButton) {
        this.context = context;
        this.timerView = timerView;
        this.resetButton = resetButton;
        mediaPlayer = MediaPlayer.create(context, R.raw.singing_bowl);
    }

    public void updateTimerFromRotation(float rotation) {
        if (!isTimerRunning) {
            int totalMinutes = (int) (Math.abs(rotation) / 360 * 30);
            totalMinutes = Math.min(totalMinutes, 120);
            totalTimeMillis = totalMinutes * 60 * 1000L;
            timerView.updateTimerText(totalTimeMillis);
        }
    }

    public void startTimerSequence() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (totalTimeMillis == 0) {
            return;
        }
        isTimerRunning = true;
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
                resetButton.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> startMainCountdownTimer(), 1000);
            }
        }.start();
    }

    private void startMainCountdownTimer() {
        timerView.startSmoothRotation();

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
                resetButton.setVisibility(View.INVISIBLE);
                isTimerRunning = false;
            }
        }.start();
    }

    public void resetTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        totalTimeMillis = 0;
        timerView.updateTimerText(totalTimeMillis);
        resetButton.setVisibility(View.INVISIBLE);
        isTimerRunning = false;
        timerView.resetView();
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

    public boolean isTimerRunning() {
        return isTimerRunning;
    }
}