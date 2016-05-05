package com.freedcam.apis.camera2.camera.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.parameters.ParameterHandlerApi2;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;

import java.util.ArrayList;

/**
 * Created by troop on 06.03.2015.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualExposureApi2 extends AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{
    final String TAG = ManualExposureApi2.class.getSimpleName();
    CameraHolderApi2 cameraHolder;

    public ManualExposureApi2(ParameterHandlerApi2 camParametersHandler, CameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.camParametersHandler = camParametersHandler;
        this.cameraHolder = cameraHolder;
        int max = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
        int min = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
        float step = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();
        stringvalues = createStringArray(min, max, step);
        currentInt = stringvalues.length/2;
    }

    protected String[] createStringArray(int min,int max, float stepp)
    {
        ArrayList<String> ar = new ArrayList<>();
        for (int i = min; i <= max; i++)
        {
            String s = String.format("%.1f",i*stepp );
            ar.add(s);
        }
        return ar.toArray(new String[ar.size()]);
    }

    @Override
    public int GetValue() {
        return super.GetValue();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void SetValue(int valueToSet)
    {
        if (cameraHolder == null || cameraHolder.mPreviewRequestBuilder == null || cameraHolder.mCaptureSession == null)
            return;
        currentInt = valueToSet;
        if(stringvalues == null || stringvalues.length == 0)
            return;
        int t = valueToSet-(stringvalues.length/2);
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, t);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException | NullPointerException e) {
            Logger.exception(e);
        }
    }

    @Override
    public boolean IsSupported()
    {
        return cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE) != null;
    }

    @Override
    public boolean IsSetSupported() {
        return true;
    }

    @Override
    public boolean IsVisible() {
        return true;
    }

    @Override
    public void onValueChanged(String val) {
        boolean canSet = false;
        if (val.equals("off"))
        {
            canSet = false;
            BackgroundIsSetSupportedChanged(false);
        }
        else {
            canSet = true;
            BackgroundIsSetSupportedChanged(true);
            SetValue(currentInt);
        }
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
}
