package com.troop.freedcam.camera;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.camera.parameters.CamParametersHandler;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.interfaces.I_Module;
import com.troop.freedcam.i_camera.interfaces.I_error;
import com.troop.freedcam.i_camera.modules.I_Callbacks;
import com.troop.freedcam.i_camera.parameters.I_ParametersLoaded;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;


/**
 * Created by troop on 16.08.2014.
 */
public class CameraUiWrapper extends AbstractCameraUiWrapper implements SurfaceHolder.Callback, I_ParametersLoaded, I_Callbacks.ErrorCallback
{
    protected ExtendedSurfaceView preview;
    protected I_error errorHandler;
    public AppSettingsManager appSettingsManager;
    private static String TAG = CameraUiWrapper.class.getSimpleName();
    public BaseCameraHolder cameraHolder;


    @Override
    public String CameraApiName() {
        return AppSettingsManager.API_1;
    }

    public CameraUiWrapper(SurfaceView preview, AppSettingsManager appSettingsManager)
    {
        super(preview,appSettingsManager);
        this.preview = (ExtendedSurfaceView)preview;
        this.appSettingsManager = appSettingsManager;
        //attache the callback to the Campreview
        preview.getHolder().addCallback(this);

        this.errorHandler = this;
        this.cameraHolder = new BaseCameraHolder(this, uiHandler);
        super.cameraHolder = cameraHolder;
        this.cameraHolder.errorHandler = errorHandler;

        camParametersHandler = new CamParametersHandler(this, appSettingsManager, uiHandler);
        this.cameraHolder.ParameterHandler = camParametersHandler;
        camParametersHandler.ParametersEventHandler.AddParametersLoadedListner(this);
        this.preview.ParametersHandler = camParametersHandler;
        moduleHandler = new ModuleHandler(cameraHolder, appSettingsManager);

        Focus = new FocusHandler(this);
        this.cameraHolder.Focus = Focus;
        Log.d(TAG, "Ctor done");


    }

    //this get handled in backgroundThread when StartPreviewAndCamera() was called
    @Override
    protected void startCamera() {
        cameraHolder.OpenCamera(appSettingsManager.GetCurrentCamera());
        Log.d(TAG, "opencamera");
    }

    @Override
    protected void stopCamera()
    {
        Log.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
    }

    @Override
    protected void startPreview()
    {
        Log.d(TAG, "Stop Preview");
        cameraHolder.StartPreview();
    }

    @Override
    protected void stopPreview()
    {
        Log.d(TAG, "Stop Preview");
        cameraHolder.StopPreview();
    }



    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Log.d(TAG, "surface created");
        PreviewSurfaceRdy = true;
        StartCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        PreviewSurfaceRdy =false;
        StopCamera();
    }

    @Override
    public void ParametersLoaded()
    {
        camParametersHandler.PictureSize.addEventListner(preview);
    }

    @Override
    public void onError(int i)
    {
        errorHandler.OnError("Got Error from camera: " + i);
        try
        {
            cameraHolder.CloseCamera();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    //this gets called when the cameraholder has open the camera
    @Override
    public void onCameraOpen(String message)
    {
        super.onCameraOpen(message);
        cameraHolder.SetErrorCallback(this);
        cameraHolder.SetSurface(preview.getHolder());

        cameraHolder.StartPreview();
        CamParametersHandler camParametersHandler1 = (CamParametersHandler) camParametersHandler;
        camParametersHandler1.LoadParametersFromCamera();
        super.onCameraOpenFinish("");


    }

    @Override
    public void onCameraClose(String message) {

    }

    @Override
    public void onPreviewOpen(String message) {
        super.onPreviewOpen(message);
    }

    @Override
    public void onPreviewClose(String message) {
        super.onPreviewClose(message);
    }

    @Override
    public void onCameraError(String error) {
        super.onCameraError(error);
    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        super.onCameraStatusChanged(status);
    }

    @Override
    public void onModuleChanged(I_Module module) {
        super.onModuleChanged(module);
    }

    @Override
    public void onCameraOpenFinish(String message) {
        super.onCameraOpenFinish(message);
    }

    @Override
    public void OnError(String error) {
        super.onCameraError(error);
    }
}
