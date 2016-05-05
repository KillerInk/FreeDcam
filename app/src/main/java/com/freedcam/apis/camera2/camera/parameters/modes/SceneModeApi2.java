package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

/**
 * Created by troop on 13.12.2014.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SceneModeApi2 extends  BaseModeApi2
{

    public enum SceneModes
    {
        disable,
        face,
        action,
        portrait,
        landscape,
        night,
        night_portrait,
        theatre,
        beach,
        snow,
        sunset,
        steadyphoto,
        fireworks,
        sports,
        party,
        candlelight,
        barcode,
        high_speed_video,

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SceneModeApi2(Handler handler,CameraHolderApi2 cameraHolderApi2)
    {
        super(handler, cameraHolderApi2);
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        if (values.length > 1)
            this.isSupported = true;
    }

    @Override
    public boolean IsSupported()
    {
        return this.isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Scene"))
            return;
        SceneModes sceneModes = Enum.valueOf(SceneModes.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.CONTROL_SCENE_MODE, sceneModes.ordinal());
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {

            int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.CONTROL_SCENE_MODE);
            SceneModes sceneModes = SceneModes.values()[i];
            return sceneModes.toString();


    }

    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                SceneModes sceneModes = SceneModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Scene" + values[i];
            }

        }
        return retvals;
    }
}
