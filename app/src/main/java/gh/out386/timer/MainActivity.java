package gh.out386.timer;

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
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.hanks.htextview.evaporate.EvaporateTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    private EvaporateTextView tv;
    private Timer timer;
    private long initialTime;
    private int colourPrimary;
    private int colourAccent;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        sp = getPreferences(Context.MODE_PRIVATE);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.rl);
        tv = (EvaporateTextView) findViewById(R.id.tv);
        final int[] timerLength = {-1};


        if (savedInstanceState == null) {
            initialTime = SystemClock.elapsedRealtime();
            colourPrimary = sp.getInt(Constants.colourPrimary, 0xffffff);
            colourAccent = sp.getInt(Constants.colourAccent, 0x000000);
        } else {
            initialTime = savedInstanceState.getLong(Constants.initialTime);
            colourPrimary = savedInstanceState.getInt(Constants.colourPrimary);
            colourAccent = savedInstanceState.getInt(Constants.colourAccent);
        }

        container.setBackgroundColor(colourPrimary);
        tv.setTextColor(colourAccent);
        tv.setTypeface(FontManager.getInstance(getAssets()).getFont("fonts/NotCourierSans-webfont.ttf"));

        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new ColorChooserDialog.Builder(MainActivity.this, R.string.accent)
                        .accentMode(true)
                        .show();
                return true;
            }
        });


        final SimpleDateFormat sdf = new SimpleDateFormat("ss:SS");
        final Date date = new Date();
        timer = new Timer();
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final long diff = SystemClock.elapsedRealtime() - initialTime;
                if (timerLength[0] == -1 && diff > 50000)
                    timerLength[0] = 1;
                else if (timerLength[0] != 0 && diff > 2750000)
                    timerLength[0] = 2;

                if (timerLength[0] == 1) {
                    sdf.applyPattern("mm:ss:SS");
                    timerLength[0] = -2;
                } else if (timerLength[0] == 2) {
                    sdf.applyPattern("HH:mm:ss");
                    timerLength[0] = 0;
                }
                date.setTime(diff);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        tv.animateText(sdf.format(date));
                    }
                });
            }
        }, 205, 205);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(Constants.initialTime, initialTime);
        outState.putInt(Constants.colourPrimary, colourPrimary);
        outState.putInt(Constants.colourAccent, colourAccent);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int color) {
        if (R.string.primary == dialog.getTitle()) {
            findViewById(R.id.rl).setBackgroundColor(color);
            colourPrimary = color;
            sp.edit().putInt(Constants.colourPrimary, color).apply();
        } else if (R.string.accent == dialog.getTitle()) {
            tv.setTextColor(color);
            colourAccent = color;
            sp.edit().putInt(Constants.colourAccent, color).apply();
        }
    }

    @Override
    public void onColorChooserDismissed(@NonNull ColorChooserDialog dialog) {

        if (dialog.getTitle() == R.string.accent) {

            new ColorChooserDialog.Builder(MainActivity.this, R.string.primary)
                    .accentMode(false)
                    .show();
        }
    }
}