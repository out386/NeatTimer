package gh.out386.timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


public class MainActivity extends Activity 
{
    private TextView tv;
    private int minutes = 0;
    private int seconds = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    tv = (TextView) findViewById(R.id.tv);
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				tick();
                handler.postDelayed(this, 1000);
			}
		}, 1000);
    }
    
    private void tick() {
        if (seconds < 60)
            seconds ++;
        else {
            seconds = 0;
            if (minutes < 60)
                minutes++;
            else
                minutes = 0;
        }
        
        String strMinutes = minutes < 10 ? "0" + minutes : String.valueOf(minutes);
        String strSeconds = seconds < 10 ? "0" + seconds : String.valueOf(seconds);
        tv.setText(strMinutes + " : " + strSeconds);
    }
}
