package gh.out386.timer.clock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
