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

package gh.out386.timer.timer.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import gh.out386.timer.R;
import gh.out386.timer.timer.TimerStatus;

public class Timer {

    private static final int INTERVAL = 205;

    private ScheduledFuture timeHandle;
    private volatile long initialTime = 0L;
    private volatile long diff;
    private SimpleDateFormat sdf;
    private SimpleDateFormat notifSdf;
    private volatile int timerLength;
    private boolean isPaused = true;
    private volatile String formattedDiff;
    private volatile String notifFormattedDiff;
    private ScheduledExecutorService scheduledExecutorService;
    private SetTextsRunnable setTextsRunnable;
    private TimerRunnable timerRunnable;
    private Date timerDate;
    private Handler mainHandler;
    private Context context;
    private ServiceListener serviceListener;

    @SuppressLint("SimpleDateFormat")
    Timer(Context context, ServiceListener listener) {
        serviceListener = listener;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        mainHandler = new Handler();
        this.context = context;
        sdf = new SimpleDateFormat(context.getResources().getString(R.string.timer_format_1));
        notifSdf = new SimpleDateFormat(context.getResources().getString(R.string.timer_format_notif));
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        notifSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        timerDate = new Date();
        setTextsRunnable = new SetTextsRunnable();
        timerRunnable = new TimerRunnable();
    }

    void initialize() {
        resetState();
    }

    private void resetState() {
        diff = 0L;
        formattedDiff = context.getResources().getString(R.string.timer_initial_text);
        timerLength = 0;
        sdf.applyPattern(context.getString(R.string.timer_format_1));
        notifSdf.applyPattern(context.getString(R.string.timer_format_notif));
    }

    void pauseResumeTimer() {
        if (isPaused) {
            if (initialTime == 0L) {
                initialTime = SystemClock.elapsedRealtime();
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(timerRunnable, 0, INTERVAL,
                                TimeUnit.MILLISECONDS);

                isPaused = false;
            } else {
                initialTime = SystemClock.elapsedRealtime() - diff;
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(timerRunnable, 0,
                                INTERVAL, TimeUnit.MILLISECONDS);

                isPaused = false;
            }
        } else {
            if (timeHandle != null && !timeHandle.isCancelled())
                timeHandle.cancel(true);
            isPaused = true;
            notifyService(true, formattedDiff);
        }
    }

    void stopTimer() {
        if (timeHandle != null && !timeHandle.isCancelled())
            timeHandle.cancel(true);
        isPaused = true;
        resetState();
        notifyService(true, formattedDiff);
    }


    private void applyTimerPattern() {
        if (timerLength == 1 && diff > 2750000) {
            sdf.applyPattern(context.getString(R.string.timer_format_3));
            notifSdf.applyPattern(context.getString(R.string.timer_format_3));
            timerLength = 2;
        } else if (timerLength == 0 && diff > 50000) {
            sdf.applyPattern(context.getResources().getString(R.string.timer_format_2));
            timerLength = 1;
        }
    }

    TimerStatus getStatus() {
        return new TimerStatus(isPaused, formattedDiff);
    }

    /**
     * Use to check whether timer is running and/or paused, or if it has been reset
     *
     * @return True if timer has not been reset
     */
    boolean isRunningOrPaused() {
        return diff > 0;
    }

    String getTime() {
        return formattedDiff;
    }

    boolean getPaused() {
        return isPaused;
    }

    private void notifyService(boolean isPaused, String formattedDiff) {
        serviceListener.onUpdateStatus(isPaused, formattedDiff, notifFormattedDiff);
    }

    private class TimerRunnable implements Runnable {
        @Override
        public void run() {
            diff = SystemClock.elapsedRealtime() - initialTime;
            applyTimerPattern();

            timerDate.setTime(diff);
            formattedDiff = sdf.format(timerDate);
            notifFormattedDiff = notifSdf.format(timerDate);
            mainHandler.post(setTextsRunnable);
        }
    }

    private class SetTextsRunnable implements Runnable {
        @Override
        public void run() {
            notifyService(isPaused, formattedDiff);
        }
    }

}
