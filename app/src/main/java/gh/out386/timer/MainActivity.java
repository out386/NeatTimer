package gh.out386.timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TimerTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Looper;
import java.text.SimpleDateFormat;
import com.hanks.htextview.evaporate.EvaporateTextView;
import android.graphics.Color;
import gh.out386.timer.FontManager;


public class MainActivity extends Activity 
{
    private EvaporateTextView tv;
    private Timer timer;
    private long initialTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    tv = (EvaporateTextView) findViewById(R.id.tv);
        final int [] timerLength = {-1};
        if (savedInstanceState == null)
            initialTime = SystemClock.elapsedRealtime();
        else
            initialTime = savedInstanceState.getLong("initial");
            
        tv.setTextColor(Color.BLACK);
        tv.setTypeface(FontManager.getInstance(getAssets()).getFont("fonts/NotCourierSans-webfont.ttf"));
        

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
                        tv.animateText(String.format(Locale.ENGLISH, "%2s", sdf.format(date)));
                    }
                });
            }
        }, 205, 205);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putLong("initial", initialTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}