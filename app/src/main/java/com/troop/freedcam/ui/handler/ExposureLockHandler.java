package com.troop.freedcam.ui.handler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.I_ModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.MainActivity_v2;

/**
 * Created by Ingo on 25.12.2014.
 */
public class ExposureLockHandler implements View.OnClickListener, I_ParametersLoaded
{
    MainActivity_v2 activity;
    AbstractCameraUiWrapper cameraUiWrapper;
    TextView textView;
    AppSettingsManager appSettingsManager;
    I_ModeParameter exposureLock;
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
        Bitmap back = BitmapFactory.decodeResource(activity.getResources(), R.drawable.button_clean);
        bitmaps[0] = back;
        Bitmap front = BitmapFactory.decodeResource(activity.getResources(), R.drawable.button_clean);
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
        if (exposureLock != null && exposureLock.IsSupported()) {

            String val = exposureLock.GetValue();
            view.setVisibility(View.VISIBLE);
            setBitmap(val);
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

    private void setBitmap(String value)
    {
        if (value.equals("true"))
            view.setImageBitmap(bitmaps[0]);
        else
            view.setImageBitmap(bitmaps[1]);
    }
}
