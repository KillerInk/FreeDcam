package freed.cam.apis.camera1;

import freed.cam.apis.basecamera.AbstractCamera;
import freed.cam.apis.basecamera.CameraThreadHandler;
import freed.cam.apis.basecamera.Size;
import freed.cam.apis.camera1.cameraholder.CameraHolderLG;
import freed.cam.apis.camera1.cameraholder.CameraHolderLegacy;
import freed.cam.apis.camera1.cameraholder.CameraHolderMTK;
import freed.cam.apis.camera1.cameraholder.CameraHolderMotoX;
import freed.cam.apis.camera1.cameraholder.CameraHolderSony;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;

public class Camera1 extends AbstractCamera<ParametersHandler,CameraHolder,ModuleHandler,FocusHandler> {
    private static final String TAG = Camera1.class.getSimpleName();

    private boolean cameraIsOpen = false;

    public Camera1()
    {
        super();
        createCamera();
    }

    @Override
    public boolean isCameraOpen() {
        return cameraIsOpen;
    }

    private void createCamera() {
        Log.d(TAG,"FrameWork:" + settingsManager.getFrameWork() + " openlegacy:" + settingsManager.get(SettingKeys.openCamera1Legacy).get());

        if (settingsManager.getFrameWork() == Frameworks.LG) {
            cameraHolder = new CameraHolderLG(this, Frameworks.LG);
            Log.d(TAG, "create LG camera");
        }
        else if (settingsManager.getFrameWork() == Frameworks.Moto_Ext) {
            cameraHolder = new CameraHolderMotoX(this, Frameworks.Moto_Ext);
            Log.d(TAG, "create MotoExt camera");
        }
        else if (settingsManager.getFrameWork() == Frameworks.MTK) {
            cameraHolder = new CameraHolderMTK(this, Frameworks.MTK);
            Log.d(TAG, "create Mtk camera");
        }
        else if (settingsManager.getFrameWork() == Frameworks.SonyCameraExtension)
        {
            cameraHolder = new CameraHolderSony(this, Frameworks.SonyCameraExtension);
        }
        else if (settingsManager.get(SettingKeys.openCamera1Legacy).get()) {
            cameraHolder = new CameraHolderLegacy(this, Frameworks.Default);
            Log.d(TAG, "create Legacy camera");
        }
        else {
            cameraHolder = new CameraHolder(this, Frameworks.Default);
            Log.d(TAG, "create Normal camera");
        }
        moduleHandler = new ModuleHandler(this);

        parametersHandler = new ParametersHandler(this);

        //moduleHandler.addListner(Camera1Fragment.this);
        focusHandler = new FocusHandler(this);

        Log.d(TAG, "initModules");
        moduleHandler.initModules();
        Log.d(TAG, "Check Focuspeak");
    }

    @Override
    public void initCamera() {
        parametersHandler.LoadParametersFromCamera();
        cameraHolder.fireCameraOpenFinished();
    }

    @Override
    public void startCamera() {
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(settingsManager.getCameraIds()[settingsManager.GetCurrentCamera()]);
        Log.d(TAG, "startCamera");
    }

    @Override
    public void stopCamera() {
        Log.d(TAG, "Stop Camera");
        if (cameraHolder != null)
            cameraHolder.CloseCamera();
        cameraIsOpen = false;
    }

    @Override
    public void restartCamera() {
        Log.d(TAG, "Stop Camera");
        cameraHolder.CloseCamera();
        cameraIsOpen = false;
        if (!cameraIsOpen)
            cameraIsOpen = cameraHolder.OpenCamera(settingsManager.getCameraIds()[settingsManager.GetCurrentCamera()]);
        Log.d(TAG, "startCamera");
    }

    @Override
    public void startPreview() {
        Log.d(TAG, "Start Preview");
        cameraHolder.StartPreview();
    }

    @Override
    public void stopPreview() {
        try {
            Log.d(TAG, "Stop Preview");
            if (cameraHolder != null)
                cameraHolder.StopPreview();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }
    }

    @Override
    public void onCameraOpen() {
        CameraThreadHandler.initCameraAsync();
    }

    @Override
    public void onCameraOpenFinished() {

    }

    @Override
    public void onCameraClose() {
    }

    @Override
    public void onCameraError(String error) {

    }

    @Override
    public void onCameraChangedAspectRatioEvent(Size size) {

    }
}
