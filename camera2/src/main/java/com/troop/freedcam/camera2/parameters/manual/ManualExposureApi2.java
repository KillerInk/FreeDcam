package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractManualParameter;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

import java.util.ArrayList;

/**
 * Created by troop on 06.03.2015.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualExposureApi2 extends AbstractManualParameter implements AbstractModeParameter.I_ModeParameterEvent
{

    protected BaseCameraHolderApi2 cameraHolder;
    private boolean canSet = false;

    public ManualExposureApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
        super(camParametersHandler);
        this.camParametersHandler = camParametersHandler;
        this.cameraHolder = cameraHolder;
        int max = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
        int min = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
        float step = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();
        stringvalues = createStringArray(min, max, step);
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
        if (cameraHolder == null || cameraHolder.mPreviewRequestBuilder == null)
            return;
        currentInt = valueToSet;
        if(stringvalues == null || stringvalues.length == 0)
            return;
        int t = valueToSet-(stringvalues.length/2);
        cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, t);
        try {
            cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                    null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
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
