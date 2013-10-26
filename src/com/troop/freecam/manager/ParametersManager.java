package com.troop.freecam.manager;

import android.graphics.Camera;
import android.util.Log;

import com.troop.freecam.CameraManager;

/**
 * Created by troop on 16.10.13.
 */
public class ParametersManager
{
    CameraManager cameraManager;

    public ParametersManager(CameraManager cameraManager)
    {
        this.cameraManager = cameraManager;
    }

    public void SetExposureCompensation(int exp)
    {
        //cameraManager.parameters.setExposureCompensation(exp);
        cameraManager.parameters.set("exposure-compensation", exp);
        try
        {
            cameraManager.mCamera.setParameters(cameraManager.parameters);
            cameraManager.activity.exposureTextView.setText("Exposure: " + String.valueOf(cameraManager.parameters.getExposureCompensation()));
            Log.d("ParametersMAnager", "Exposure:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("Exposure Set Fail", ex.getMessage());
        }
    }

    public void SetContrast(int contrast)
    {
        cameraManager.parameters.set("contrast", contrast);
        try
        {
            cameraManager.mCamera.setParameters(cameraManager.parameters);
            Log.d("ParametersMAnager", "Contrast:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("Contrast Set Fail", ex.getMessage());
        }
        cameraManager.activity.contrastTextView.setText(String.valueOf(cameraManager.parameters.get("contrast")));

    }

    public void SetBrightness(int bright)
    {
        cameraManager.parameters.set("brightness", bright);
        try
        {
            cameraManager.mCamera.setParameters(cameraManager.parameters);
            Log.d("ParametersMAnager", "brightness:"+String.valueOf(cameraManager.mCamera.getParameters().getExposureCompensation()));
        }
        catch (Exception ex)
        {
            Log.e("brightness Set Fail", ex.getMessage());
        }
        cameraManager.activity.brightnessTextView.setText(String.valueOf(cameraManager.parameters.get("brightness")));

    }
}
