package com.dani.shmiralist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.TimePicker;

public class CustomTimePicker extends TimePicker {

    public CustomTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (event.getActionMasked()==MotionEvent.ACTION_DOWN){
            ViewParent p = getParent();
            if (p!=null)
                p.requestDisallowInterceptTouchEvent(true);
        }
        return false;
    }
}
