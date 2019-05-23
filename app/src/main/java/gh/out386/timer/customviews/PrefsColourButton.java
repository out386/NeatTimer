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
import android.support.v4.graphics.ColorUtils;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import gh.out386.timer.R;

import static gh.out386.timer.customviews.PrefsColourManager.COLOR_PREFS_FILE;
import static gh.out386.timer.customviews.PrefsColourManager.DEF_COLOUR_ACCENT;
import static gh.out386.timer.customviews.PrefsColourManager.KEY_COLOUR_ACCENT;
import static gh.out386.timer.customviews.PrefsColourManager.LIGHT_TEXT_LUM_THRESH;

/**
 * A subclass of {@link AppCompatButton} has a {@link SharedPreferences} listener that allows all
 * instances of this class to always have the same persistent colour.
 */
public class PrefsColourButton extends AppCompatButton {
    private SharedPreferences prefs;
    private ColorListener colourListener;

    public PrefsColourButton(Context context) {
        this(context, null);
    }

    public PrefsColourButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.buttonStyle);
    }

    public PrefsColourButton(Context context, AttributeSet attrs, int defStyleAttr) {
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

        int bgColour = prefs.getInt(KEY_COLOUR_ACCENT, DEF_COLOUR_ACCENT);
        setColour(bgColour);
    }

    private void setButtonTextColour(int colour) {
        double lum = ColorUtils.calculateLuminance(colour);
        if (lum > LIGHT_TEXT_LUM_THRESH)
            setTextColor(getResources().getColor(R.color.text_dark));
        else
            setTextColor(getResources().getColor(R.color.text_light));
    }

    private void setColour(int colour) {
        setButtonTextColour(colour);
        ColorStateList csl = new ColorStateList(
                new int[][]{new int[]{}},
                new int[]{colour}
        );
        setBackgroundTintList(csl);
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
