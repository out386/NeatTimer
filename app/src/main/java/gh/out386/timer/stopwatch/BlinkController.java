/*
 * Copyright (C) 2018 Ritayan Chakraborty
 *
 * This file is a part of Timer.
 *
 * Timer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License only.
 *
 * Timer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Timer. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package gh.out386.timer.stopwatch;

import android.os.Handler;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import gh.out386.timer.customviews.PrefsColourEvaporateTextView;
import gh.out386.timer.listeners.AnimatorListener.AnimatorListener;

public class BlinkController {

    private final int BLINK_INTERVAL = 1000;

    private BlinkTimerRunnable blinkTimerRunnable;
    private Handler mainHandler;
    private DecelerateInterpolator decelerateInterpolator;
    private AccelerateInterpolator accelerateInterpolator;
    private PrefsColourEvaporateTextView timerTv;
    private AnimatorListener fadeOutListener;
    private AnimatorListener repeatFadeListener;
    private boolean isRunning;

    BlinkController(PrefsColourEvaporateTextView textView) {
        timerTv = textView;
        mainHandler = new Handler();
        blinkTimerRunnable = new BlinkTimerRunnable();
        decelerateInterpolator = new DecelerateInterpolator();
        accelerateInterpolator = new AccelerateInterpolator();
        setFadeListeners();
    }

    private void setFadeListeners() {
        repeatFadeListener = new AnimatorListener(() ->
                mainHandler.postDelayed(blinkTimerRunnable, BLINK_INTERVAL));

        fadeOutListener = new AnimatorListener(() ->
                timerTv.animate()
                        .alpha(1f)
                        .setDuration(300L)
                        .setInterpolator(accelerateInterpolator)
                        .setListener(repeatFadeListener));
    }

    public synchronized void startBlink() {
        if (!isRunning) {
            mainHandler.post(blinkTimerRunnable);
            isRunning = true;
        }
    }

    public synchronized void stopBlink() {
        if (isRunning) {
            timerTv.animate().cancel();
            mainHandler.removeCallbacks(blinkTimerRunnable);
            timerTv.setAlpha(1f);
            isRunning = false;
        }
    }

    private class BlinkTimerRunnable implements Runnable {
        @Override
        public void run() {
            timerTv.animate()
                    .alpha(0f)
                    .setDuration(700L)
                    .setInterpolator(decelerateInterpolator)
                    .setListener(fadeOutListener);
        }
    }
}
