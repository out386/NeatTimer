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

package gh.out386.timer.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.hanks.htextview.evaporate.EvaporateTextView;

import gh.out386.timer.FontManager;
import gh.out386.timer.customviews.listeners.LongPressListener;
import gh.out386.timer.customviews.listeners.SingleTapListener;

import static gh.out386.timer.customviews.PrefsColourManager.COLOR_PREFS_FILE;
import static gh.out386.timer.customviews.PrefsColourManager.DEF_COLOUR_ACCENT;
import static gh.out386.timer.customviews.PrefsColourManager.KEY_COLOUR_ACCENT;

/**
 * A subclass of {@link EvaporateTextView} that uses a custom font and has a {@link
 * SharedPreferences} listener that allows all instances of this class to always have the same
 * persistent {@code textColor}.
 */
public class PrefsColourEvaporateTextView extends EvaporateTextView {
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
            prefs = context.getSharedPreferences(COLOR_PREFS_FILE, Context.MODE_PRIVATE);
        }
        prefs.registerOnSharedPreferenceChangeListener(colourListener);
        setColour(prefs.getInt(KEY_COLOUR_ACCENT, DEF_COLOUR_ACCENT));
    }

    private void setColour(int colour) {
        setTextColor(colour);
        // Needed because the colours will not be changed until the HTextViews update
        animateText(getText());
    }

    /**
     * Register a callback to be invoked when a single tap event is sent to this view.
     *
     * @param listener the single tap listener to attach to this view. {@code null} to unset the
     *                 listener.
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
     * @param listener the long press listener to attach to this view. {@code null} to unset the
     *                 listener.
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
            if (key.equals(KEY_COLOUR_ACCENT)) {
                setColour(sharedPreferences.getInt(KEY_COLOUR_ACCENT, DEF_COLOUR_ACCENT));
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
