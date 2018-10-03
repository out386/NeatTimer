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

package gh.out386.timer.clock;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import gh.out386.timer.R;
import gh.out386.timer.customviews.PrefsColourEvaporateTextView;

public class ClockFragment extends Fragment {
    private PrefsColourEvaporateTextView clockTv;
    private ClockManager manager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        manager = new ClockManager();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_clock, viewGroup, false);
        clockTv = v.findViewById(R.id.time);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        manager.start(s -> clockTv.animateText(s));
    }

    @Override
    public void onPause() {
        super.onPause();
        manager.stop();
    }

}
