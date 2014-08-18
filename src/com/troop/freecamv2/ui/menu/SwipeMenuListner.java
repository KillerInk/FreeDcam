package com.troop.freecamv2.ui.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner
{
    LinearLayout settingsLayout;
    LinearLayout manualSettingsLayout;

    final int distance = 200;
    int startX;
    int startY;
    int currentX;
    int currentY;
    boolean verticalSwipe = false;
    boolean horizontalSwipe = false;
    int mShortAnimationDuration;
    boolean animationRunning = false;
    LinearLayout animationLayout;

    public SwipeMenuListner(LinearLayout settingsLayout, LinearLayout manualSettingsLayout)
    {
        this.manualSettingsLayout = manualSettingsLayout;
        this.settingsLayout = settingsLayout;
        mShortAnimationDuration = settingsLayout.getResources().getInteger(
                android.R.integer.config_mediumAnimTime);

    }


    public boolean onTouchEvent(MotionEvent event)
    {
        boolean fireagain = true;
        if (!animationRunning && animationLayout == null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = (int) event.getX();
                    startY = (int) event.getY();
                    currentX = (int) event.getX();
                    currentY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    currentX = (int) event.getX();
                    currentY = (int) event.getY();
                    detectSwipeDirection();
                    break;
                case MotionEvent.ACTION_UP:
                    fireagain = false;
                    break;
            }
        }

        return fireagain;
    }

    private void detectSwipeDirection()
    {
        int x = getDistance(startX, currentX);
        int y = getDistance(startY, currentY);
        if (x >= distance || y >= distance) {
            if (x >= y)
                doHorizontalSwipe();
            else
                doVerticalSwipe();
        }
    }


    private int getDistance(int startvalue, int currentvalue)
    {
        int dis = startvalue - currentvalue;
        if (dis < 0)
            dis = dis *-1;
        return dis;
    }

    private void doHorizontalSwipe()
    {
        if (startX - currentX > 0)
        {
            if (settingsLayout.getVisibility() == View.VISIBLE)
                hideAnimation(settingsLayout);
        }
        else
        {
            if (settingsLayout.getVisibility() == View.GONE)
                showAnimation(settingsLayout);
        }
    }

    private void doVerticalSwipe()
    {
        if (startY - currentY > 0)
        {
            if (manualSettingsLayout.getVisibility() == View.VISIBLE)
                hideAnimation(manualSettingsLayout);
        }
        else
        {
            if (manualSettingsLayout.getVisibility() == View.GONE)
                showAnimation(manualSettingsLayout);
        }
    }

    private void showAnimation(LinearLayout layout)
    {
        animationLayout = layout;
        layout.setAlpha(0f);
        layout.setVisibility(View.VISIBLE);
        layout.animate()
                .alpha(1f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        //animationLayout.animate().setListener(null);
                        animationLayout = null;
                        animationRunning = false;
                    }
                });
    }

    private void hideAnimation(final LinearLayout layout) {
        animationRunning = true;
        animationLayout = layout;
        layout.setAlpha(1f);
        layout.animate()
                .alpha(0f)
                .setDuration(mShortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animationLayout.setVisibility(View.GONE);
                        //animationLayout.animate().setListener(null);
                        animationLayout = null;
                        animationRunning = false;
                    }
                });
    }


}
