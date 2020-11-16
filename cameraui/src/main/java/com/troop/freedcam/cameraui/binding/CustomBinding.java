package com.troop.freedcam.cameraui.binding;

import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.models.ButtonModel;
import com.troop.freedcam.cameraui.models.ManualButtonModel;
import com.troop.freedcam.cameraui.models.TextValuesButtonModel;
import com.troop.freedcam.cameraui.models.ValuesHolderModel;
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

    @BindingAdapter("bindModelToLinearLayout")
    public static void bindModelToLinearLayout(LinearLayout rotatingSeekbar, ValuesHolderModel valuesHolderModel)
    {
        rotatingSeekbar.removeAllViews();
        if (valuesHolderModel != null && valuesHolderModel.getButtonModel() != null) {
            if (valuesHolderModel.getButtonModel().getValues() != null) {
                for (String s : valuesHolderModel.getButtonModel().getValues())
                {
                    Button button = new Button(rotatingSeekbar.getContext());
                    button.setText(s);
                    button.setOnClickListener(valuesHolderModel);
                    rotatingSeekbar.addView(button);
                }
                valuesHolderModel.setVisibility(View.VISIBLE);
            }
            else {
                valuesHolderModel.setVisibility(View.GONE);
                rotatingSeekbar.setOnClickListener(null);
            }
        }
        else {
            valuesHolderModel.setVisibility(View.GONE);
            rotatingSeekbar.setOnClickListener(null);
        }
    }

    @BindingAdapter("bindPositionToScrollview")
    public static void bindValuesHolderModelToScrollView(ScrollView scrollView, ValuesHolderModel valuesHolderModel)
    {
        if (valuesHolderModel != null)
        {
            if (valuesHolderModel.isFromLeft()) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
                params.startToEnd = R.id.camera_ui_left_bar;
                params.endToStart = ConstraintLayout.LayoutParams.UNSET;
                scrollView.setLayoutParams(params);
            }
            else {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) scrollView.getLayoutParams();
                params.endToStart = R.id.camera_ui_right_bar;
                params.startToEnd = ConstraintLayout.LayoutParams.UNSET;
                scrollView.setLayoutParams(params);
            }
        }
    }
}
