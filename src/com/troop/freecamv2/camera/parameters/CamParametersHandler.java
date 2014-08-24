package com.troop.freecamv2.camera.parameters;

import android.hardware.Camera;
import android.util.Log;

import com.troop.freecam.manager.camera_parameters.ParametersManager;
import com.troop.freecamv2.camera.BaseCameraHolder;
import com.troop.freecamv2.camera.parameters.manual.BrightnessManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ContrastManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ConvergenceManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ExposureManualParameter;
import com.troop.freecamv2.camera.parameters.manual.FocusManualParameter;
import com.troop.freecamv2.camera.parameters.manual.SaturationManualParameter;
import com.troop.freecamv2.camera.parameters.manual.SharpnessManualParameter;
import com.troop.freecamv2.camera.parameters.manual.ShutterManualParameter;
import com.troop.freecamv2.camera.parameters.modes.AntiBandingModeParameter;
import com.troop.freecamv2.camera.parameters.modes.ColorModeParameter;
import com.troop.freecamv2.camera.parameters.modes.ExposureModeParameter;
import com.troop.freecamv2.camera.parameters.modes.FlashModeParameter;
import com.troop.freecamv2.camera.parameters.modes.ImagePostProcessingParameter;
import com.troop.freecamv2.camera.parameters.modes.IsoModeParameter;
import com.troop.freecamv2.camera.parameters.modes.JpegQualityParameter;
import com.troop.freecamv2.camera.parameters.modes.PictureFormatParameter;
import com.troop.freecamv2.camera.parameters.modes.PictureSizeParameter;
import com.troop.freecamv2.camera.parameters.modes.PreviewFormatParameter;
import com.troop.freecamv2.camera.parameters.modes.PreviewFpsParameter;
import com.troop.freecamv2.camera.parameters.modes.PreviewSizeParameter;

/**
 * Created by troop on 17.08.2014.
 */
public class CamParametersHandler implements I_ParameterChanged
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

    public ColorModeParameter ColorMode;
    public ExposureModeParameter ExposureMode;
    public FlashModeParameter FlashMode;
    public IsoModeParameter IsoMode;
    public AntiBandingModeParameter AntiBandingMode;
    public PictureSizeParameter PictureSize;
    public PictureFormatParameter PictureFormat;
    public JpegQualityParameter JpegQuality;
    public ImagePostProcessingParameter ImagePostProcessing;
    public PreviewSizeParameter PreviewSize;
    public PreviewFpsParameter PreviewFPS;
    public PreviewFormatParameter PreviewFormat;

    //public I_ParametersLoaded OnParametersLoaded;

    public CameraParametersListner ParametersEventHandler;

    boolean moreParametersToSet = false;

    SetParameterRunner setParameterRunner;

    public CamParametersHandler(BaseCameraHolder cameraHolder)
    {
        this.cameraHolder = cameraHolder;
        ParametersEventHandler = new CameraParametersListner();
    }

    public void GetParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
    }

    public void SetParametersToCamera()
    {
        cameraHolder.SetCameraParameters(cameraParameters);
    }

    public void LoadParametersFromCamera()
    {
        cameraParameters = cameraHolder.GetCameraParameters();
        setParameterRunner = new SetParameterRunner();
        initParameters();
    }

    boolean supportManualWhiteBalance = false;
    public boolean getSupportWhiteBalance() { return supportManualWhiteBalance; }

    boolean supportFlash = false;
    public boolean getSupportFlash() { return  supportFlash;}

    boolean supportVNF = false;
    public boolean getSupportVNF() { return supportVNF;}

    boolean supportAfpPriorityModes = false;
    public boolean getSupportAfpPriority() { return supportAfpPriorityModes;}

    boolean supportIPP = false;
    public boolean getSupportIPP() { return supportIPP;}

    boolean supportZSL = false;
    public boolean getSupportZSL() { return supportZSL;}





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
        ManualConvergence = new ConvergenceManualParameter(cameraParameters, "manual-convergence", "supported-manual-convergence-max", "supported-manual-convergence-min");
        ManualExposure = new ExposureManualParameter(cameraParameters,"","","");
        ManualFocus = new FocusManualParameter(cameraParameters,"","","");
        ManualSaturation = new SaturationManualParameter(cameraParameters,"","","");
        ManualSharpness = new SharpnessManualParameter(cameraParameters, "", "", "");
        ManualShutter = new ShutterManualParameter(cameraParameters,"","","");

        ColorMode = new ColorModeParameter(cameraParameters,this, "", "");
        ExposureMode = new ExposureModeParameter(cameraParameters,this,"","");
        FlashMode = new FlashModeParameter(cameraParameters,this,"","");
        IsoMode = new IsoModeParameter(cameraParameters,this,"","");
        AntiBandingMode = new AntiBandingModeParameter(cameraParameters,this, "antibanding", "antibanding-values");
        PictureSize = new PictureSizeParameter(cameraParameters,this, "", "");
        PictureFormat = new PictureFormatParameter(cameraParameters, this, "picture-format", "picture-format-values");
        JpegQuality = new JpegQualityParameter(cameraParameters, this, "jpeg-quality", "");
        ImagePostProcessing = new ImagePostProcessingParameter(cameraParameters,this, "ipp", "ipp-values");
        PreviewSize = new PreviewSizeParameter(cameraParameters, this, "preview-size", "preview-size-values", cameraHolder);
        PreviewFPS = new PreviewFpsParameter(cameraParameters, this, "preview-frame-rate", "preview-frame-rate-values", cameraHolder);
        PreviewFormat = new PreviewFormatParameter(cameraParameters, this, "preview-format", "preview-format-values", cameraHolder);



        ParametersEventHandler.ParametersHasLoaded();
    }

    @Override
    public void ParameterChanged()
    {
        if (!setParameterRunner.isRunning)
            setParameterRunner.run();
        else
            moreParametersToSet = true;

    }

    class SetParameterRunner implements Runnable
    {
        private boolean isRunning = false;

        @Override
        public void run()
        {
            isRunning = true;
            cameraHolder.SetCameraParameters(cameraParameters);
            try {
                //maybe need to incrase the sleeptime if a device crash when setting the manual parameters like manual exposure or manual saturation
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            isRunning = false;
            if (moreParametersToSet)
            {
                moreParametersToSet = false;
                run();
            }
        }
    }
}
