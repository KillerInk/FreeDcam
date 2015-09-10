package com.troop.freedcam.camera2.parameters.manual;

import android.os.Handler;
import android.os.Looper;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;

/**
 * Created by troop on 10.09.2015.
 */
public class BurstApi2 extends AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    int current = 0;
    BaseCameraHolderApi2 cameraHolderApi2;

    public BurstApi2(AbstractParameterHandler camParametersHandler, BaseCameraHolderApi2 cameraHolderApi2) {
        super(camParametersHandler);
        this.cameraHolderApi2 = cameraHolderApi2;
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
    public boolean IsSupported() {
        return true;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public int GetMaxValue() {
        return 50;
    }

    @Override
    public int GetMinValue() {
        return 0;
    }

    @Override
    public int GetValue() {
        return current;
    }

    @Override
    public String GetStringValue() {
        return (current +1)+"";
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
                cameraHolderApi2.StopPreview();
                cameraHolderApi2.SetBurst(current+1);
            }
        });


    }
}
