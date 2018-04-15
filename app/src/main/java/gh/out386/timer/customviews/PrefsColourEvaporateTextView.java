package gh.out386.timer.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.hanks.htextview.evaporate.EvaporateTextView;

import gh.out386.timer.FontManager;
import gh.out386.timer.customviews.listeners.LongPressListener;
import gh.out386.timer.customviews.listeners.SingleTapListener;

/**
 * A subclass of {@link EvaporateTextView} that uses a custom font and has a
 * {@link SharedPreferences} listener that allows all instances of this class to always have the
 * same persistent {@code textColor}.
 */
public class PrefsColourEvaporateTextView extends EvaporateTextView {
    public static final String PREFERENCE_FILE = "viewsColours";
    public static final String KEY_COLOUR = "hTextColor";

    private SharedPreferences prefs;
    private ColorListener colourListener;
    private SingleTapListener singleTapListener;
    private LongPressListener longPressListener;
    private GestureDetector textViewGesture;

    public PrefsColourEvaporateTextView(Context context) {
        this(context, null);
    }

    public PrefsColourEvaporateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefsColourEvaporateTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
        setupPreferenceListener();
    }

    private void setFont() {
        setTypeface(FontManager.getInstance(getContext()
                .getAssets())
                .getFont("fonts/NotCourierSans-webfont.ttf")
        );
    }

    private void setupPreferenceListener() {
        if (colourListener == null) {
            colourListener = new ColorListener();
        }
        if (prefs == null) {
            Context context = getContext().getApplicationContext();
            prefs = context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE);
        }
        prefs.registerOnSharedPreferenceChangeListener(colourListener);
        setColour(prefs.getInt(KEY_COLOUR, 0x000000));
    }

    private void setColour(int colour) {
        setTextColor(colour);
        // Needed because the colours will not be changed until the HTextViews update
        animateText(getText());
    }

    /**
     * When this method is called, all instances of {@link #PrefsColourEvaporateTextView} set their
     * {@code TextColor} to the provided {@code color}. {@link SharedPreferences} is used, so the
     * colour set here is persistent.
     *
     * @param colour The colour all instances of PrefsColourEvaporateTextView will use as their
     *               {@code TextColor}
     */
    public static void setDynamicColour(Context context, @ColorInt int colour) {
        context.getSharedPreferences(PREFERENCE_FILE, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_COLOUR, colour)
                .apply();
    }

    /**
     * Register a callback to be invoked when a single tap event is sent to this view.
     *
     * @param listener the single tap listener to attach to this view. {@code null} to
     *                 unset the listener.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setOnSingleTapListener(SingleTapListener listener) {
        if (listener == null && longPressListener == null) {
            singleTapListener = null;
            textViewGesture = null;
            setOnTouchListener(null);
            return;
        } else if (textViewGesture == null)
            textViewGesture = new GestureDetector(getContext(), new LayoutListener());
        singleTapListener = listener;
        setOnTouchListener((v, event) ->
                textViewGesture != null && textViewGesture.onTouchEvent(event));
    }

    /**
     * Register a callback to be invoked when a long press event is sent to this view.
     *
     * @param listener the long press listener to attach to this view. {@code null} to
     *                 unset the listener.
     */
    @SuppressLint("ClickableViewAccessibility")
    public void setOnLongPressListener(LongPressListener listener) {
        if (listener == null && singleTapListener == null) {
            longPressListener = null;
            textViewGesture = null;
            setOnTouchListener(null);
            return;
        } else if (textViewGesture == null)
            textViewGesture = new GestureDetector(getContext(), new LayoutListener());
        longPressListener = listener;
        setOnTouchListener((v, event) ->
                textViewGesture != null && textViewGesture.onTouchEvent(event));
    }

    private class ColorListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_COLOUR)) {
                setColour(sharedPreferences.getInt(KEY_COLOUR, 0x000000));
            }
        }
    }

    private class LayoutListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (singleTapListener != null) {
                singleTapListener.onSingleTap();
                return true;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (longPressListener != null)
                longPressListener.onLongPress();
        }
    }
}
