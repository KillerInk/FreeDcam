package com.freedcam.ui.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by troop on 18.03.2016.
 */
public class PagingView extends ViewPager
{

    private boolean allowScroll;

    public PagingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.allowScroll = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.allowScroll) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.allowScroll) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    public void EnableScroll(boolean enabled) {
        this.allowScroll = enabled;
    }
}
