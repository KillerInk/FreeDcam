package com.troop.freedcam.cameraui.binding;

import android.view.View;

import androidx.databinding.BindingAdapter;

import com.troop.freedcam.cameraui.models.ManualButtonModel;
import com.troop.freedcam.cameraui.views.RotatingSeekbar;

public class CustomBinding {
    @BindingAdapter("bindTouchListner")
    public static void bindTouchListner(View view, View.OnTouchListener touchListener)
    {
        if (touchListener != null)
            view.setOnTouchListener(touchListener);
    }

    @BindingAdapter("bindModelToRotatingSeekbar")
    public static void bindModelToRotatingSeekbar(RotatingSeekbar rotatingSeekbar, ManualButtonModel manualButtonModel)
    {
        if (manualButtonModel != null) {
            if (manualButtonModel.getValues() != null) {
                rotatingSeekbar.SetStringValues(manualButtonModel.getValues());
                rotatingSeekbar.setProgress(manualButtonModel.getIndex(), false);
                rotatingSeekbar.setOnSeekBarChangeListener(manualButtonModel);
            }
            if (rotatingSeekbar.getVisibility() == View.GONE)
                rotatingSeekbar.setVisibility(View.VISIBLE);
        }
        else {
            rotatingSeekbar.setVisibility(View.GONE);
            rotatingSeekbar.setOnSeekBarChangeListener(null);
        }
    }
}
