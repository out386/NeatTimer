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

package gh.out386.timer.timer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gh.out386.timer.R;
import gh.out386.timer.customviews.PrefsColourEvaporateTextView;
import gh.out386.timer.timer.controller.FragmentListener;
import gh.out386.timer.timer.controller.TimerService;

public class TimerFragment extends Fragment implements TimerActivityListener, FragmentListener {

    public static String TAG = "TIMER_TAG";

    private PrefsColourEvaporateTextView timerTv;
    private BlinkController blinkController;
    private TimerService timerService;
    private Intent servicePRIntent;
    private Intent serviceResetIntent;
    private Activity activity;
    private boolean isBound;
    private boolean isPaused = false;
    private ServiceConnection timerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            timerService = ((TimerService.TimerBinder) service).getService();
            isBound = true;
            updateStatus(timerService.getStatus());
            timerService.setFragmentListener(TimerFragment.this);
            timerService.unforgroundify();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            timerService = null;
            isBound = false;
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_timer, container, false);
        timerTv = v.findViewById(R.id.timerTv);
        blinkController = new BlinkController(timerTv);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        servicePRIntent = new Intent(getContext(), TimerService.class)
                .setAction(TimerService.TIMER_PAUSE_RESUME);
        serviceResetIntent = new Intent(getContext(), TimerService.class)
                .setAction(TimerService.TIMER_RESET);
        setTextListeners();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            activity = (Activity) context;
        else
            activity = null; // Definitely shouldn't happen
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent timerIntent = new Intent(getActivity(), TimerService.class);
        activity.bindService(timerIntent, timerConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // No problem if these have already been called by onServiceConnected
        if (isBound) {
            updateStatus(timerService.getStatus());
            timerService.setFragmentListener(this);
            timerService.unforgroundify();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isBound) {
            timerService.unsetActivityListener();
            if (timerService.isRunningOrPaused())
                timerService.forgroundify();
        }
        isPaused = false;
        blinkController.stopBlink();
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unbindService(timerConnection);
        isBound = false;
        timerService = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setTextListeners() {
        timerTv.setOnSingleTapListener(() -> activity.startService(servicePRIntent));
        timerTv.setOnLongPressListener(() -> activity.startService(serviceResetIntent));
    }

    @Override
    public void onTapped() {
        activity.startService(servicePRIntent);
    }

    private void updateStatus(TimerStatus status) {
        onUpdateStatus(status.getPaused(), status.getTime());
    }

    @Override
    public void onUpdateStatus(boolean isPaused, String time) {
        if (this.isPaused) {
            if (!isPaused) {
                blinkController.stopBlink();
                this.isPaused = false;
            }
        } else if (isPaused) {
            blinkController.startBlink();
            this.isPaused = true;
        }
        timerTv.animateText(time);
    }
}
