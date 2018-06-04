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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import gh.out386.timer.NotificationActivity;
import gh.out386.timer.R;
import gh.out386.timer.timer.TimerStatus;

public class TimerService extends Service implements ServiceListener {

    public static final String TIMER_PAUSE_RESUME = "TIMER_PAUSE_RESUME";
    public static final String TIMER_RESET = "TIMER_RESET";

    private final IBinder binder = new TimerBinder();
    private Timer timer;
    private FragmentListener fragmentListener;
    private NotificationCompat.Builder notificationBuilder;
    private boolean showNotif;
    private String lastTime;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        timer = new Timer(getApplicationContext(), this);
        timer.initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            if (action.equals(TIMER_PAUSE_RESUME)) {
                boolean ispaused = timer.getPaused();
                setNotifActions(!ispaused);
                if (showNotif && notificationBuilder != null)
                    notificationManager.notify(1, notificationBuilder.build());
                timer.pauseResumeTimer();
            } else if (action.equals(TIMER_RESET)) {
                timer.stopTimer();
                unforgroundify();
                stopSelf();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onUpdateStatus(boolean isPaused, String time, String notifTime) {
        if (fragmentListener != null)
            fragmentListener.onUpdateStatus(isPaused, time);
        if (showNotif && !notifTime.equals(lastTime)) {
            lastTime = notifTime;
            notificationBuilder.setContentText(notifTime);
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    public void setFragmentListener(FragmentListener listener) {
        fragmentListener = listener;
    }

    public void unsetActivityListener() {
        fragmentListener = null;
    }

    public TimerStatus getStatus() {
        return timer.getStatus();
    }

    /**
     * Use to check if timer is running and/or paused, or if it has been reset
     *
     * @return True if timer is not reset
     */
    public boolean isRunningOrPaused() {
        return timer.isRunningOrPaused();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.stopTimer();
        timer = null;
    }

    public void forgroundify() {
        final String NOTIF_CHANNEL_ID = "channelStandard";
        if (notificationBuilder == null) {
            notificationBuilder =
                    new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);

            Intent notificationIntent = new Intent(getApplicationContext(), NotificationActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            notificationBuilder
                    .setVibrate(null)
                    .setAutoCancel(false)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(contentIntent)
                    .setContentText(timer.getTime())
                    .setSmallIcon(R.drawable.ic_stat_timer)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNotif));
        }

        setNotifActions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String CHANNEL_NAME = getString(R.string.notif_channel_name);
            final String CHANNEL_DESC = getString(R.string.notif_channel_desc);
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(CHANNEL_DESC);
            channel.enableLights(false);
            channel.enableVibration(false);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }

        startForeground(1, notificationBuilder.build());
        showNotif = true;
    }

    public void unforgroundify() {
        showNotif = false;
        lastTime = null;
        stopForeground(true);
    }

    private void setNotifActions() {
        setNotifActions(timer.getPaused());
    }

    private void setNotifActions(boolean isPaused) {
        if (notificationBuilder != null) {
            notificationBuilder.mActions.clear();
            if (isPaused) {
                notificationBuilder
                        .addAction(R.drawable.ic_notif_start,
                                getString(R.string.notif_stopwatch_resume),
                                buildPendingIntent(TIMER_PAUSE_RESUME))
                        .setContentTitle(getString(R.string.notif_stopwatch_paused_title))
                        .setTicker(getString(R.string.notif_stopwatch_paused_title));
            } else {
                notificationBuilder
                        .addAction(R.drawable.ic_notif_pause,
                                getString(R.string.notif_stopwatch_pause),
                                buildPendingIntent(TIMER_PAUSE_RESUME))
                        .setContentTitle(getString(R.string.notif_stopwatch_running_title))
                        .setTicker(getString(R.string.notif_stopwatch_running_title));
            }

            notificationBuilder
                    .addAction(R.drawable.ic_notif_stop,
                            getString(R.string.notif_stopwatch_stop),
                            buildPendingIntent(TIMER_RESET));
        }
    }

    private PendingIntent buildPendingIntent(String action) {
        Intent i = new Intent(this, getClass());
        i.setAction(action);
        return (PendingIntent.getService(this, 0, i, 0));
    }

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }
}
