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

package gh.out386.timer.customviews;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.widget.Switch;

import gh.out386.timer.R;

import static gh.out386.timer.customviews.PrefsColourManager.COLOR_PREFS_FILE;
import static gh.out386.timer.customviews.PrefsColourManager.DEF_COLOUR_ACCENT;
import static gh.out386.timer.customviews.PrefsColourManager.KEY_COLOUR_ACCENT;

/**
 * A subclass of {@link Switch} has a {@link SharedPreferences} listener that allows all instances
 * of this class to always have the same persistent colour.
 */
public class PrefsColourSwitch extends Switch {
    private SharedPreferences prefs;
    private ColorListener colourListener;

    public PrefsColourSwitch(Context context) {
        this(context, null);
    }

    public PrefsColourSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.switchStyle);
    }

    public PrefsColourSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupPreferenceListener();
    }

    private void setupPreferenceListener() {
        if (colourListener == null) {
            colourListener = new ColorListener();
        }
        if (prefs == null) {
            Context context = getContext().getApplicationContext();
            prefs = context.getSharedPreferences(COLOR_PREFS_FILE, Context.MODE_PRIVATE);
        }
        prefs.registerOnSharedPreferenceChangeListener(colourListener);
        setColour(prefs.getInt(KEY_COLOUR_ACCENT, DEF_COLOUR_ACCENT));
    }

    private void setColour(int colour) {
        ColorStateList csl = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{
                        colour,
                        getResources().getColor(R.color.sw_track_unchecked)
                }
        );
        getThumbDrawable().setTintList(csl);
        getTrackDrawable().setTintList(csl);
    }

    private class ColorListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_COLOUR_ACCENT)) {
                setColour(sharedPreferences.getInt(KEY_COLOUR_ACCENT, DEF_COLOUR_ACCENT));
            }
        }
    }

}
