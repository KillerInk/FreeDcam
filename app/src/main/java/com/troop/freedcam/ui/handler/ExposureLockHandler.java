package com.troop.freedcam.ui.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockHandler implements View.OnClickListener, I_ParametersLoaded, AbstractModeParameter.I_ModeParameterEvent
{
    MainActivity_v2 activity;
    AbstractCameraUiWrapper cameraUiWrapper;
    TextView textView;
    AppSettingsManager appSettingsManager;
    AbstractModeParameter exposureLock;
    ImageView view;
    Bitmap[] bitmaps;

    public ExposureLockHandler(MainActivity_v2 activity, AppSettingsManager appSettingsManager)
    {
        this.activity = activity;
        this.appSettingsManager = appSettingsManager;
        this.view = (ImageView) activity.findViewById(R.id.imageView_exposurelock);
        view.setVisibility(View.GONE);
        view.setClickable(true);
        view.setOnClickListener(this);
        bitmaps = new Bitmap[2];
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.button_expolockfalse);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.button_expolocktrue);
        bitmaps[1] = front;
    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);

    }

    @Override
    public void ParametersLoaded()
    {
        exposureLock = cameraUiWrapper.camParametersHandler.ExposureLock;
        exposureLock.addEventListner(this);
        cameraUiWrapper.camParametersHandler.FocusMode.addEventListner(onFocusmodeChanged);

        if (exposureLock != null && exposureLock.IsSupported() && !cameraUiWrapper.camParametersHandler.FocusMode.GetValue().contains("continuous")) {


            String val = exposureLock.GetValue();
            view.setVisibility(View.VISIBLE);
            setBitmap(val);
        }
        else
        {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view)
    {
        String toSet = "";
        if (exposureLock.GetValue().equals("true"))
        {
           toSet = "false";
        }
        else
            toSet = "true";

        exposureLock.SetValue(toSet, true);
        setBitmap(toSet);
    }

    private void setBitmap(final String value)
    {
        view.post(new Runnable() {
            @Override
            public void run() {
                if (value.equals("true"))
                    view.setImageBitmap(bitmaps[1]);
                else
                    view.setImageBitmap(bitmaps[0]);
            }
        });

    }

    @Override
    public void onModeParameterChanged(String val) {
        exposureLock.SetValue(val, false);
        setBitmap(val);
    }

    AbstractModeParameter.I_ModeParameterEvent onFocusmodeChanged = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onModeParameterChanged(String val)
        {
            exposureLock.SetValue("false", true);
            setBitmap("false");
            if (!val.contains("continuous"))
                view.setVisibility(View.VISIBLE);
            else
            {
                view.setVisibility(View.GONE);
            }

        }
    };


}
