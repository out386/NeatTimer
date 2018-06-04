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

import android.os.Handler;

import java.text.DateFormat;
import java.util.Date;

public class ClockManager {

    private DateFormat clockFormat;
    private Date clockDate;
    private Handler handler;
    private ClockRunnable runnable;
    private OnTimeUpdatedListener listener;

    ClockManager() {
        clockFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        clockDate = new Date();
        handler = new Handler();
        runnable = new ClockRunnable();
    }

    void start(OnTimeUpdatedListener listener) {
        this.listener = listener;
        handler.post(runnable);
    }

    void stop() {
        handler.removeCallbacks(runnable);
    }

    private class ClockRunnable implements Runnable {
        @Override
        public void run() {
            clockDate.setTime(System.currentTimeMillis());
            listener.onUpdate(clockFormat.format(clockDate));
            // This delay is not exact, so sometimes, this can skip formatting a second
            handler.postDelayed(this, 1000);
        }
    }

    interface OnTimeUpdatedListener {
        void onUpdate(String formattedTime);
    }
}
