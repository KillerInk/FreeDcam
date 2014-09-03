package com.troop.freecamv2.ui.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import com.troop.freecam.R;


/**
 * Created by troop on 18.08.2014.
 */
public class SwipeMenuListner extends TouchHandler
{
    LinearLayout settingsLayout;
    LinearLayout manualSettingsLayout;
    LinearLayout seekbarLayout;



    int animationSpeed = 200;




    public SwipeMenuListner(final LinearLayout settingsLayout, final LinearLayout manualSettingsLayout, LinearLayout seekbarLayout)
    {
        this.manualSettingsLayout = manualSettingsLayout;
        this.settingsLayout = settingsLayout;
        this.manualSettingsLayout.setVisibility(View.GONE);
        this.settingsLayout.setVisibility(View.GONE);
        this.seekbarLayout = seekbarLayout;


    }

    protected void doHorizontalSwipe()
    {
        if (startX - currentX > 0)
        {
            if (settingsLayout.getVisibility() == View.VISIBLE)
                hideAnimationHorizontal();
        }
        else
        {
            if (settingsLayout.getVisibility() == View.GONE)
                showAnimationHorizontal();
        }
    }

    protected void doVerticalSwipe()
    {
        if (startY - currentY > 0)
        {
            if (manualSettingsLayout.getVisibility() == View.VISIBLE)
                hideVerticalAnimation();
        }
        else
        {
            if (manualSettingsLayout.getVisibility() == View.GONE)
                showVerticalAnimation();
        }
    }

    private void showAnimationHorizontal()
    {

            Animation hide = AnimationUtils.loadAnimation(manualSettingsLayout.getContext(), R.anim.move_top_to_bottom);
            hide.setDuration(animationSpeed);
            settingsLayout.setVisibility(View.VISIBLE);
            settingsLayout.startAnimation(hide);
    }

    private void hideAnimationHorizontal()
    {
        Animation hide = AnimationUtils.loadAnimation(manualSettingsLayout.getContext(), R.anim.move_right_to_left);
        hide.setDuration(animationSpeed);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                settingsLayout.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        settingsLayout.startAnimation(hide);
    }

    private void showVerticalAnimation()
    {
        Animation hide = AnimationUtils.loadAnimation(manualSettingsLayout.getContext(), R.anim.move_left_to_right);
        hide.setDuration(animationSpeed);
        manualSettingsLayout.setVisibility(View.VISIBLE);
        manualSettingsLayout.startAnimation(hide);
        seekbarLayout.setVisibility(View.VISIBLE);
        seekbarLayout.startAnimation(hide);
    }

    private void hideVerticalAnimation()
    {
        Animation hide = AnimationUtils.loadAnimation(manualSettingsLayout.getContext(), R.anim.move_bottom_to_top);
        Animation hide2 = AnimationUtils.loadAnimation(manualSettingsLayout.getContext(), R.anim.move_bottom_to_bottom);
        hide.setDuration(animationSpeed);
        hide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                manualSettingsLayout.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hide2.setDuration(animationSpeed);
        hide2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                seekbarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        manualSettingsLayout.startAnimation(hide);
        seekbarLayout.startAnimation(hide2);
        //layout.startAnimation(move_bottom_to_top);
    }
}
