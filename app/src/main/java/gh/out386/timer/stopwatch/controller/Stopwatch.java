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

package gh.out386.timer.stopwatch.controller;

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
import gh.out386.timer.stopwatch.StopwatchStatus;

public class Stopwatch {

    private static final int INTERVAL = 205;

    private ScheduledFuture timeHandle;
    private volatile long initialTime = 0L;
    private volatile long diff;
    private SimpleDateFormat sdf;
    private SimpleDateFormat notifSdf;
    private volatile int stopwatchLength;
    private boolean isPaused = true;
    private volatile String formattedDiff;
    private volatile String notifFormattedDiff;
    private ScheduledExecutorService scheduledExecutorService;
    private SetTextsRunnable setTextsRunnable;
    private StopwatchRunnable stopwatchRunnable;
    private Date stopwatchDate;
    private Handler mainHandler;
    private Context context;
    private ServiceListener serviceListener;

    @SuppressLint("SimpleDateFormat")
    Stopwatch(Context context, ServiceListener listener) {
        serviceListener = listener;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        mainHandler = new Handler();
        this.context = context;
        sdf = new SimpleDateFormat(context.getResources().getString(R.string.stopwatch_format_1));
        notifSdf = new SimpleDateFormat(context.getResources().getString(R.string.stopwatch_format_notif));
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        notifSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        stopwatchDate = new Date();
        setTextsRunnable = new SetTextsRunnable();
        stopwatchRunnable = new StopwatchRunnable();
    }

    void initialize() {
        resetState();
    }

    private void resetState() {
        diff = 0L;
        formattedDiff = context.getResources().getString(R.string.stopwatch_initial_text);
        stopwatchLength = 0;
        sdf.applyPattern(context.getString(R.string.stopwatch_format_1));
        notifSdf.applyPattern(context.getString(R.string.stopwatch_format_notif));
    }

    void pauseResumeStopwatch() {
        if (isPaused) {
            if (initialTime == 0L) {
                initialTime = SystemClock.elapsedRealtime();
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(stopwatchRunnable, 0, INTERVAL,
                                TimeUnit.MILLISECONDS);

                isPaused = false;
            } else {
                initialTime = SystemClock.elapsedRealtime() - diff;
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(stopwatchRunnable, 0,
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

    void stopStopwatch() {
        if (timeHandle != null && !timeHandle.isCancelled())
            timeHandle.cancel(true);
        isPaused = true;
        resetState();
        notifyService(true, formattedDiff);
    }


    private void applyStopwatchPattern() {
        if (stopwatchLength == 1 && diff > 2750000) {
            sdf.applyPattern(context.getString(R.string.stopwatch_format_3));
            notifSdf.applyPattern(context.getString(R.string.stopwatch_format_3));
            stopwatchLength = 2;
        } else if (stopwatchLength == 0 && diff > 50000) {
            sdf.applyPattern(context.getResources().getString(R.string.stopwatch_format_2));
            stopwatchLength = 1;
        }
    }

    StopwatchStatus getStatus() {
        return new StopwatchStatus(isPaused, formattedDiff);
    }

    /**
     * Use to check whether stopwatch is running and/or paused, or if it has been reset
     *
     * @return True if stopwatch has not been reset
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

    private class StopwatchRunnable implements Runnable {
        @Override
        public void run() {
            diff = SystemClock.elapsedRealtime() - initialTime;
            applyStopwatchPattern();

            stopwatchDate.setTime(diff);
            formattedDiff = sdf.format(stopwatchDate);
            notifFormattedDiff = notifSdf.format(stopwatchDate);
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
