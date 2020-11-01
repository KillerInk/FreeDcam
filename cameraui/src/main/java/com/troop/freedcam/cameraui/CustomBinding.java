package com.troop.freedcam.cameraui;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.troop.freedcam.cameraui.models.RotatingSeekbarModel;
import com.troop.freedcam.cameraui.views.RotatingSeekbar;

public class CustomBinding {
    @BindingAdapter("bindTouchListner")
    public static void bindTouchListner(View view, View.OnTouchListener touchListener)
    {
        if (touchListener != null)
            view.setOnTouchListener(touchListener);
    }

    @BindingAdapter("bindModelToRotatingSeekbar")
    public static void bindModelToRotatingSeekbar(RotatingSeekbar rotatingSeekbar, RotatingSeekbarModel manualButtonModel)
    {
        if (manualButtonModel != null) {
            rotatingSeekbar.SetStringValues(manualButtonModel.getValues());
            rotatingSeekbar.setProgress(manualButtonModel.getProgress(), false);
            if (rotatingSeekbar.getVisibility() == View.GONE)
                rotatingSeekbar.setVisibility(View.VISIBLE);
        }
        else
            rotatingSeekbar.setVisibility(View.GONE);
    }
}
