package gh.out386.timer.customviews;

/*
 * Copyright (C) 2019 Ritayan Chakraborty <ritayanout@gmail.com>
 *
 * This file is part of Timer
 *
 * Timer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 2 of the License only.
 *
 * Timer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Timer.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsColourManager {
    static final String COLOR_PREFS_FILE = "viewsColours";
    static final String KEY_COLOUR_ACCENT = "accentColor";
    static final String KEY_COLOUR_PRIMARY = "primaryColor";
    static final int DEF_COLOUR_ACCENT = 0xFF000000;
    static final int DEF_COLOUR_PRIMARY = 0xFFFFFFFF;
    static final double LIGHT_TEXT_LUM_THRESH = 0.40;

    /**
     * When this method is called, all instances of all {@code PrefsColour*} views set their primary
     * colour to the provided {@code color}. {@link SharedPreferences} is used, so the colour set
     * here is persistent.
     *
     * @param colour The new primary colour to set
     */
    public static void setPrimaryColour(Context context, int colour) {
        context.getSharedPreferences(COLOR_PREFS_FILE, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_COLOUR_PRIMARY, colour)
                .apply();
    }

    /**
     * When this method is called, all instances of all {@code PrefsColour*} views set their accent
     * colour to the provided {@code color}. {@link SharedPreferences} is used, so the colour set
     * here is persistent.
     *
     * @param colour The new accent colour to set
     */
    public static void setAccentColour(Context context, int colour) {
        context.getSharedPreferences(COLOR_PREFS_FILE, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_COLOUR_ACCENT, colour)
                .apply();
    }
}
