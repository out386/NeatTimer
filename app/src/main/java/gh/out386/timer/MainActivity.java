package gh.out386.timer;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.hanks.htextview.HTextView;
import com.hanks.htextview.HTextViewType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback, View.OnClickListener {
    private final int INTERVAL = 205;
    private final int BLINK_INTERVAL = 1000;

    private HTextView tv;
    private HTextView time;
    private ScheduledFuture<?> timeHandle;
    private long initialTime;
    private long diff;
    private int colourPrimary;
    private int colourAccent;
    private SharedPreferences sp;
    private SimpleDateFormat sdf;
    private SimpleDateFormat timeSdf;
    private Date date;
    private int timerLength = -1;
    private boolean isPaused = true;
    private String formattedDiff;
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sp = getPreferences(Context.MODE_PRIVATE);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);

        sdf = new SimpleDateFormat("ss:SS");
        timeSdf = new SimpleDateFormat("hh:mm:ss a");
        date = new Date();
        tv = (HTextView) findViewById(R.id.tv);
        time = (HTextView) findViewById(R.id.time);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.rl);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        tv.animateText(getResources().getString(R.string.timer_initial_text));

        if (savedInstanceState == null) {
            colourPrimary = sp.getInt(Constants.COLOUR_PRIMARY, 0xffffff);
            colourAccent = sp.getInt(Constants.COLOUR_ACCENT, 0x000000);
            diff = 0;
            formattedDiff = "00:00";
            timeHandle = scheduledExecutorService
                    .scheduleAtFixedRate(new BlinkTimerRunnable(), 0, BLINK_INTERVAL, TimeUnit.MILLISECONDS);
        } else {
            initialTime = savedInstanceState.getLong(Constants.INITIAL_TIME);
            colourPrimary = savedInstanceState.getInt(Constants.COLOUR_PRIMARY);
            colourAccent = savedInstanceState.getInt(Constants.COLOUR_ACCENT);
            isPaused = savedInstanceState.getBoolean(Constants.IS_PAUSED);
            diff = savedInstanceState.getLong(Constants.DIFF);
            if (isPaused) {
                formattedDiff = savedInstanceState.getString(Constants.FORMATTED_DIFF);
                tv.animateText(formattedDiff);
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(new BlinkTimerRunnable(), 0, BLINK_INTERVAL, TimeUnit.MILLISECONDS);
            } else
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(new StopTimerRunnable(), 0, INTERVAL, TimeUnit.MILLISECONDS);
        }

        container.setBackgroundColor(colourPrimary);
        tv.setTextColor(colourAccent);
        tv.setTypeface(FontManager.getInstance(getAssets()).getFont("fonts/NotCourierSans-webfont.ttf"));
        tv.setAnimateType(HTextViewType.EVAPORATE);
        tv.setOnClickListener(this);

        time.setTextColor(colourAccent);
        time.setTypeface(FontManager.getInstance(getAssets()).getFont("fonts/NotCourierSans-webfont.ttf"));
        time.setAnimateType(HTextViewType.EVAPORATE);
        container.setOnClickListener(this);

        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new ColorChooserDialog.Builder(MainActivity.this, R.string.primary)
                        .accentMode(true)
                        .show();
                return true;
            }
        });

        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                diff = 0L;
                formattedDiff = "00:00";
                isPaused = true;
                tv.animateText(formattedDiff);
                if (timeHandle != null && !timeHandle.isCancelled())
                    timeHandle.cancel(true);
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(new BlinkTimerRunnable(), 0, BLINK_INTERVAL, TimeUnit.MILLISECONDS);
                return true;
            }
        });

    }

    @Override
    public void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (isPaused) {
            if (timeHandle == null) {
                initialTime = SystemClock.elapsedRealtime();
                timeHandle = scheduledExecutorService
                        .scheduleAtFixedRate(new StopTimerRunnable(), 0, INTERVAL, TimeUnit.MILLISECONDS);
                isPaused = false;
                return;
            } else if (!timeHandle.isCancelled())
                timeHandle.cancel(true);
            initialTime = SystemClock.elapsedRealtime() - diff;
            timeHandle = scheduledExecutorService
                    .scheduleAtFixedRate(new StopTimerRunnable(), 0, INTERVAL, TimeUnit.MILLISECONDS);
            isPaused = false;
        } else {
            if (timeHandle != null && !timeHandle.isCancelled())
                timeHandle.cancel(true);
            timeHandle = scheduledExecutorService
                    .scheduleAtFixedRate(new BlinkTimerRunnable(), 0, BLINK_INTERVAL, TimeUnit.MILLISECONDS);
            isPaused = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.FORMATTED_DIFF, formattedDiff);
        outState.putLong(Constants.INITIAL_TIME, initialTime);
        outState.putLong(Constants.DIFF, diff);
        outState.putInt(Constants.COLOUR_PRIMARY, colourPrimary);
        outState.putInt(Constants.COLOUR_ACCENT, colourAccent);
        outState.putBoolean(Constants.IS_PAUSED, isPaused);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (timeHandle != null && !timeHandle.isCancelled())
            timeHandle.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int colour) {
        if (R.string.primary == dialog.getTitle()) {
            colourPrimary = colour;
            findViewById(R.id.rl).setBackgroundColor(colourPrimary);
            sp.edit().putInt(Constants.COLOUR_PRIMARY, colourPrimary).apply();
        } else if (R.string.accent == dialog.getTitle()) {
            colourAccent = colour;
            tv.setTextColor(colourAccent);
            sp.edit().putInt(Constants.COLOUR_ACCENT, colourAccent).apply();
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

        if (dialog.getTitle() == R.string.primary) {
            new ColorChooserDialog.Builder(MainActivity.this, R.string.accent)
                    .accentMode(false)
                    .show();
        }
    }

    private class StopTimerRunnable implements Runnable {
        @Override
        public void run() {
            diff = SystemClock.elapsedRealtime() - initialTime;
            if (timerLength == -1 && diff > 50000)
                timerLength = 1;
            else if (timerLength != 0 && diff > 2750000)
                timerLength = 2;

            if (timerLength == 1) {
                sdf.applyPattern("mm:ss:SS");
                timerLength = -2;
            } else if (timerLength == 2) {
                sdf.applyPattern("HH:mm:ss");
                timerLength = 0;
            }
            date.setTime(diff);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    formattedDiff = sdf.format(date);
                    tv.animateText(formattedDiff);
                    time.animateText(timeSdf.format(new Date(Calendar.getInstance().getTimeInMillis())));
                }
            });
        }
    }

    private class BlinkTimerRunnable implements Runnable {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    time.animateText(timeSdf.format(new Date(Calendar.getInstance().getTimeInMillis())));
                    tv.animate()
                            .alpha(0.0f)
                            .setDuration(700L)
                            .setInterpolator(new DecelerateInterpolator())
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    tv.animate()
                                            .alpha(1.0f)
                                            .setDuration(300L)
                                            .setInterpolator(new AccelerateInterpolator());
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                }
            });
        }
    }
}