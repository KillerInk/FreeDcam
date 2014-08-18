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
        if (!animationRunning) {
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
            animationRunning = true;
            settingsLayout.setAlpha(1f);
            settingsLayout.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            settingsLayout.setVisibility(View.GONE);
                            settingsLayout.animate().setListener(null);
                            animationRunning =false;
                        }
                    });
        }
        else
        {
            settingsLayout.setAlpha(0f);
            settingsLayout.setVisibility(View.VISIBLE);
            settingsLayout.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            settingsLayout.animate().setListener(null);
                            animationRunning = false;
                        }
                    });
        }
    }

    private void doVerticalSwipe()
    {
        if (startY - currentY > 0)
        {
            animationRunning = true;
            manualSettingsLayout.setAlpha(1f);
            manualSettingsLayout.animate()
                    .alpha(0f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            manualSettingsLayout.setVisibility(View.GONE);
                            manualSettingsLayout.animate().setListener(null);
                            animationRunning = false;
                        }
                    });
        }
        else
        {
            animationRunning = true;
            manualSettingsLayout.setAlpha(0f);
            manualSettingsLayout.setVisibility(View.VISIBLE);
            manualSettingsLayout.animate()
                    .alpha(1f)
                    .setDuration(mShortAnimationDuration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            manualSettingsLayout.animate().setListener(null);
                            animationRunning = false;
                        }
                    });
        }
    }
}
