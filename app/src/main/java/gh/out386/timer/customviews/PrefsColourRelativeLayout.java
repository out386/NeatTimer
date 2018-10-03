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
import androidx.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import gh.out386.timer.customviews.listeners.LongPressListener;
import gh.out386.timer.customviews.listeners.SingleTapListener;


/**
 * A subclass of {@link RelativeLayout} that has a {@link SharedPreferences} listener that allows
 * all instances of this class to always have the same persistent {@code backgroundColor}.
 */
public class PrefsColourRelativeLayout extends RelativeLayout {

    public static final String PREFERENCE_FILE = "viewsColours";
    public static final String KEY_COLOUR = "rLayoutColor";

    private SharedPreferences prefs;
    private ColorListener colourListener;
    private SingleTapListener singleTapListener;
    private LongPressListener longPressListener;
    private GestureDetector layoutGesture;

    public PrefsColourRelativeLayout(Context context) {
        this(context, null);
    }

    public PrefsColourRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrefsColourRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PrefsColourRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupPreferenceListener();
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
        setBackgroundColor(prefs.getInt(KEY_COLOUR, 0x000000));
    }

    /**
     * When this method is called, all instances of {@link #PrefsColourRelativeLayout} set their
     * {@code backgroundColor} to the provided {@code color}. {@link SharedPreferences} is used,
     * so the colour set here is persistent.
     *
     * @param colour The colour all instances of PrefsColourRelativeLayout will use as their
     *               {@code backgroundColor}
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
            layoutGesture = null;
            setOnTouchListener(null);
            return;
        } else if (layoutGesture == null)
            layoutGesture = new GestureDetector(getContext(), new LayoutListener());
        singleTapListener = listener;
        setOnTouchListener((v, event) ->
                layoutGesture != null && layoutGesture.onTouchEvent(event));
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
            layoutGesture = null;
            setOnTouchListener(null);
            return;
        } else if (layoutGesture == null)
            layoutGesture = new GestureDetector(getContext(), new LayoutListener());
        longPressListener = listener;
        setOnTouchListener((v, event) ->
                layoutGesture != null && layoutGesture.onTouchEvent(event));
    }

    private class ColorListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_COLOUR)) {
                setBackgroundColor(sharedPreferences.getInt(KEY_COLOUR, 0x000000));
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
