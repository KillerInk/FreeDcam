package com.freedcam.apis.camera2.camera.parameters.modes;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;

/**
 * Created by Ingo on 01.05.2015.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class EdgeModeApi2 extends BaseModeApi2 {
    public EdgeModeApi2(Handler handler, CameraHolderApi2 cameraHolderApi2) {
        super(handler, cameraHolderApi2);
    }

    public enum EdgeModes
    {
        OFF,
        FAST,
        HIGH_QUALITY,
    }


    @Override
    public boolean IsSupported()
    {
        return cameraHolder.characteristics.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES) != null;
    }


    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        if (valueToSet.contains("unknown Focus"))
            return;
        EdgeModes sceneModes = Enum.valueOf(EdgeModes.class, valueToSet);
        cameraHolder.SetParameterToCam(CaptureRequest.EDGE_MODE, sceneModes.ordinal());
        BackgroundValueHasChanged(valueToSet);
        //cameraHolder.mPreviewRequestBuilder.build();
    }

    @Override
    public String GetValue()
    {

        int i = cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.EDGE_MODE);
        EdgeModes sceneModes = EdgeModes.values()[i];
        return sceneModes.toString();


    }


    @Override
    public String[] GetValues()
    {
        int[] values = cameraHolder.characteristics.get(CameraCharacteristics.EDGE_AVAILABLE_EDGE_MODES);
        String[] retvals = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            try {
                EdgeModes sceneModes = EdgeModes.values()[values[i]];
                retvals[i] = sceneModes.toString();
            }
            catch (Exception ex)
            {
                retvals[i] = "unknown Focus" + values[i];
            }

        }
        return retvals;
    }
}
