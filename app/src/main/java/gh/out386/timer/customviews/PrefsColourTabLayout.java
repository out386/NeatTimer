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
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;

import gh.out386.timer.R;

import static gh.out386.timer.customviews.PrefsColourManager.COLOR_PREFS_FILE;
import static gh.out386.timer.customviews.PrefsColourManager.DEF_COLOUR_ACCENT;
import static gh.out386.timer.customviews.PrefsColourManager.DEF_COLOUR_PRIMARY;
import static gh.out386.timer.customviews.PrefsColourManager.KEY_COLOUR_ACCENT;
import static gh.out386.timer.customviews.PrefsColourManager.KEY_COLOUR_PRIMARY;
import static gh.out386.timer.customviews.PrefsColourManager.LIGHT_TEXT_LUM_THRESH;

/**
 * A subclass of {@link TabLayout} has a {@link SharedPreferences} listener that allows all
 * instances of this class to always have the same persistent {@code selectedTabIndicatorColor}.
 */
public class PrefsColourTabLayout extends TabLayout {
    private SharedPreferences prefs;
    private ColorListener colourListener;

    public PrefsColourTabLayout(Context context) {
        this(context, null);
    }

    public PrefsColourTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefsColourTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
        setBackgroundColor(0x11000000);
        setColours(prefs, true);
    }

    private void setColours(SharedPreferences sharedPreferences, boolean setIndicator) {
        int accent = sharedPreferences.getInt(KEY_COLOUR_ACCENT, DEF_COLOUR_ACCENT);
        int primary = sharedPreferences.getInt(KEY_COLOUR_PRIMARY, DEF_COLOUR_PRIMARY);
        if (setIndicator)
            setIndicatorColour(accent);
        setTabTextColours(accent, primary);
    }

    private void setTabTextColours(int accentColour, int primaryColour) {
        double lum = ColorUtils.calculateLuminance(primaryColour);
        if (lum > LIGHT_TEXT_LUM_THRESH)
            setTabTextColors(getResources().getColor(R.color.text_dark), accentColour);
        else
            setTabTextColors(getResources().getColor(R.color.text_light), accentColour);
    }

    private void setIndicatorColour(int colour) {
        setSelectedTabIndicatorColor(colour);
    }

    private class ColorListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_COLOUR_ACCENT)) {
                setColours(sharedPreferences, true);
            } else if (key.equals(KEY_COLOUR_PRIMARY)) {
                setColours(sharedPreferences, false);
            }
        }
    }

}
