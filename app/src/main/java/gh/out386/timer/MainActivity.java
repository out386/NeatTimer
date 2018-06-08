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

package gh.out386.timer;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.afollestad.materialdialogs.color.ColorChooserDialog;

import gh.out386.timer.clock.ClockFragment;
import gh.out386.timer.customviews.PrefsColourEvaporateTextView;
import gh.out386.timer.customviews.PrefsColourRelativeLayout;
import gh.out386.timer.stopwatch.StopwatchFragment;
import gh.out386.timer.stopwatch.StopwatchActivityListener;

public class MainActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {

    private StopwatchActivityListener timerFragmentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (savedInstanceState == null)
            showFragments();
        getFragmentListener();
        setViewListeners();
    }

    private void showFragments() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .replace(R.id.clock_container, new ClockFragment())
                .commit();
        fm.beginTransaction()
                .replace(R.id.timer_container, new StopwatchFragment(), StopwatchFragment.TAG)
                .commitNow();
    }

    private void getFragmentListener() {
        Fragment timerFragment = (getSupportFragmentManager()
                .findFragmentByTag(StopwatchFragment.TAG));
        if (timerFragment != null && timerFragment instanceof StopwatchFragment)
            timerFragmentListener = (StopwatchActivityListener) timerFragment;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            goImmersive();
    }

    @Override
    protected void onResume() {
        super.onResume();
        goImmersive();
    }

    private void goImmersive() {
        getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int colour) {
        if (R.string.primary == dialog.getTitle()) {
            PrefsColourRelativeLayout.setDynamicColour(getApplicationContext(), colour);
        } else if (R.string.accent == dialog.getTitle()) {
            PrefsColourEvaporateTextView.setDynamicColour(getApplicationContext(), colour);
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {
        if (dialog.getTitle() == R.string.primary) {
            showColourDialog(false);
        }
    }

    private void showColourDialog(boolean accentMode) {
        int titleRes = accentMode ? R.string.primary : R.string.accent;
        new ColorChooserDialog.Builder(MainActivity.this, titleRes)
                .accentMode(accentMode)
                .show(getSupportFragmentManager());
    }

    private void setViewListeners() {
        PrefsColourRelativeLayout container = findViewById(R.id.rl);
        container.setOnSingleTapListener(() -> {
            if (timerFragmentListener != null)
                timerFragmentListener.onTapped();
        });
        container.setOnLongPressListener(() -> showColourDialog(true));
    }

}