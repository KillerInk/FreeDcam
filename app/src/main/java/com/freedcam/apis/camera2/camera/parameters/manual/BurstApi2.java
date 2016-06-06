package com.freedcam.apis.camera2.camera.parameters.manual;

import android.os.Handler;
import android.os.Looper;

import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.camera2.camera.CameraHolder;

/**
 * Created by troop on 10.09.2015.
 */
public class BurstApi2 extends AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    int current = 1;
    CameraHolder cameraHolder;

    public BurstApi2(AbstractParameterHandler camParametersHandler, CameraHolder cameraHolder) {
        super(camParametersHandler);
        this.cameraHolder = cameraHolder;
    }

    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    @Override
    public boolean IsSupported() {
        return false;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public int GetValue() {
        return current;
    }

    @Override
    public String GetStringValue() {
        return (current)+"";
    }

    @Override
    public String[] getStringValues() {
        return null;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        current = valueToSet;
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                cameraHolder.StopPreview();
                //TODO FIX BURST
                //cameraHolder.SetBurst(current+1);
            }
        });


    }
}
