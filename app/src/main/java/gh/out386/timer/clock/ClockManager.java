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
