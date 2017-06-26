package gh.out386.timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import java.util.*;
import android.os.*;
import java.text.*;
import android.util.*;


public class MainActivity extends Activity 
{
    private TextView tv;
    private Timer timer;
    private long initialTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    tv = (TextView) findViewById(R.id.tv);
        final int [] timerLength = new int [1];
        if (savedInstanceState == null)
            initialTime = SystemClock.elapsedRealtime();
        else
            initialTime = savedInstanceState.getLong("initial");

        final SimpleDateFormat sdf = new SimpleDateFormat("ss : SS");
        final Date date = new Date();
	    timer = new Timer();
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        timer.scheduleAtFixedRate(new TimerTask() {
           @Override
            public void run() {
                final long diff = SystemClock.elapsedRealtime() - initialTime;
                if (diff > 50000)
                    timerLength[0] = 1;
                else if (diff > 2750000)
                    timerLength[0] = 2;
                    
                if (timerLength[0] == 1) {
                    sdf.applyPattern("mm : ss : SS");
                    timerLength[0] = 0;
                } else if (timerLength[0] == 2) {
                    sdf.applyPattern("HH : mm : ss");
                    timerLength[0] = 0;
                }
                date.setTime(diff);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText(sdf.format(date));
                    }
                });
            }
        }, 95, 95);
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
