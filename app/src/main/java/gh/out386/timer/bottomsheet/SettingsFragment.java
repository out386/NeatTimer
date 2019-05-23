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

package gh.out386.timer.bottomsheet;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import gh.out386.timer.MainActivity;
import gh.out386.timer.R;

public class SettingsFragment extends Fragment {

    public static final String KEY_SETT_ORIENTATION = "settings_orientation";

    private Switch orientationSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        orientationSwitch = v.findViewById(R.id.sw_sett_orientation);

        setup();
        return v;
    }

    private void setup() {
        if (!(getActivity() instanceof MainActivity)) // Panic
            return;

        MainActivity mainActivity = (MainActivity) getActivity();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        setupOrientation(mainActivity, prefs);
    }

    private void setupOrientation(MainActivity mainActivity, SharedPreferences prefs) {
        boolean isAutoRotate = prefs.getBoolean(KEY_SETT_ORIENTATION, true);
        orientationSwitch.setChecked(isAutoRotate);

        orientationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                prefs.edit()
                        .putBoolean(KEY_SETT_ORIENTATION, true)
                        .apply();
                mainActivity.changeOrientationSetting(true);
            } else {
                prefs.edit()
                        .putBoolean(KEY_SETT_ORIENTATION, false)
                        .apply();
                mainActivity.changeOrientationSetting(false);
            }
        });
    }

}
