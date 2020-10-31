package com.troop.freedcam.cameraui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.databinding.CameraUiManualButtonBinding;

public class ManualButton extends ConstraintLayout {


    public CameraUiManualButtonBinding manualButtonBinding;

    public ManualButton(@NonNull Context context) {
        super(context);
        bind(context);
    }

    public ManualButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bind(context);
        setArrts(context,attrs);
    }

    public ManualButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bind(context);
        setArrts(context,attrs);
    }

    public ManualButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        bind(context);
        setArrts(context,attrs);
    }

    private void bind(Context context)
    {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        manualButtonBinding = DataBindingUtil.inflate(inflater, R.layout.camera_ui_manual_button, this, false);
        addView(manualButtonBinding.getRoot());
    }

    private void setArrts(Context context, AttributeSet attrs)
    {
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ManualButton,
                0, 0
        );
        //try to set the attributs
        try
        {
            manualButtonBinding.manualTextview.setText(a.getText(R.styleable.ManualButton_setTexttoView));
            Drawable drawable = a.getDrawable(R.styleable.ManualButton_setImageToView);
            if (drawable != null)
                manualButtonBinding.manualImageview.setImageDrawable(drawable);
        }
        finally {
            a.recycle();
        }
    }
}
