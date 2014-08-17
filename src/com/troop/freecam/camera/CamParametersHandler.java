package com.troop.freecam.camera;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.camera.parameters.BrightnessManualParameter;
import com.troop.freecam.camera.parameters.ContrastManualParameter;
import com.troop.freecam.camera.parameters.ConvergenceManualParameter;
import com.troop.freecam.camera.parameters.ExposureManualParameter;
import com.troop.freecam.camera.parameters.FocusManualParameter;
import com.troop.freecam.camera.parameters.SaturationManualParameter;
import com.troop.freecam.camera.parameters.SharpnessManualParameter;
import com.troop.freecam.camera.parameters.ShutterManualParameter;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler
{
    BaseCameraHolder cameraHolder;
    Camera.Parameters cameraParameters;


    public BrightnessManualParameter ManualBrightness;
    public SharpnessManualParameter ManualSharpness;
    public ContrastManualParameter ManualContrast;
    public SaturationManualParameter ManualSaturation;
    public ExposureManualParameter ManualExposure;
    public ConvergenceManualParameter ManualConvergence;
    public FocusManualParameter ManualFocus;
    public ShutterManualParameter ManualShutter;

    public CamParametersHandler(BaseCameraHolder cameraHolder)
    {
        this.cameraHolder = cameraHolder;
    }

    public void GetParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
        initParameters();
    }

    /*boolean supportManualSharpness = false;
    public boolean getSupportManualSharpness() { return supportManualSharpness;}*/

   /* boolean supportManualContrast = false;
    public boolean getSupportManualContrast() { return  supportManualContrast;}*/

   /* boolean supportManualBrightness = false;
    public boolean getSupportManualBrightness() { return  supportManualBrightness;}
*/
    /*boolean supportManualSaturation = false;
    public boolean getSupportManualSaturation() { return  supportManualSaturation;}*/

    boolean supportManualWhiteBalance = false;
    public boolean getSupportWhiteBalance() { return supportManualWhiteBalance; }

   /* boolean supportManualConvergence = false;
    public boolean getSupportManualConvergence() { return supportManualConvergence;}*/

    boolean supportManualFocus = false;
    public boolean getSupportManualFocus(){ return  supportManualFocus;}

    boolean supportManualShutter = false;
    public boolean getSupportManualShutter(){ return  supportManualShutter;}

    boolean supportFlash = false;
    public boolean getSupportFlash() { return  supportFlash;}

    boolean supportVNF = false;
    public boolean getSupportVNF() { return supportVNF;}

    boolean supportAutoExposureModes = false;
    public boolean getSupportAutoExposureModes() { return supportAutoExposureModes;}

    boolean supportAfpPriorityModes = false;
    public boolean getSupportAfpPriority() { return supportAfpPriorityModes;}

    boolean supportIPP = false;
    public boolean getSupportIPP() { return supportIPP;}

    boolean supportZSL = false;
    public boolean getSupportZSL() { return supportZSL;}

    boolean supportIso = false;
    public boolean getSupportIso() { return  supportIso; }

    boolean supportSceneModes = false;
    public boolean getSupportSceneModes() { return supportSceneModes;}


    private void logParameters()
    {
        String[] paras =  cameraParameters.flatten().split(";");
        for(int i = 0; i < paras.length; i++)
            Log.d("freecam.CameraParametersHandler", paras[i]);
    }

    private void initParameters()
    {
        logParameters();
        ManualBrightness = new BrightnessManualParameter(cameraParameters, "","","");
        ManualContrast = new ContrastManualParameter(cameraParameters, "", "", "");
        ManualConvergence =new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min");
        ManualExposure = new ExposureManualParameter(cameraParameters,"","","");
        ManualFocus = new FocusManualParameter(cameraParameters,"","","");
        ManualSaturation = new SaturationManualParameter(cameraParameters,"","","");
        ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "");
        ManualShutter = new ShutterManualParameter(cameraParameters,"","","");



    }
}
