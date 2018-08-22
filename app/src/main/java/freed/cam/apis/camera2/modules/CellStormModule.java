package freed.cam.apis.camera2.modules;

import android.os.Handler;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;

public class CellStormModule extends PictureModuleApi2 {


    private boolean continueCapture = false;
    private final int cropSize = 100;

    public CellStormModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_cellstorm);
    }

    @Override
    public String LongName() {
        return "CellStorm";
    }

    @Override
    public String ShortName() {
        return "Cell";
    }

    @Override
    public void DoWork() {
        if (continueCapture)
            continueCapture = false;
        else {
            continueCapture = true;
            super.DoWork();
        }
    }

    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        currentCaptureHolder.setCropSize(cropSize,cropSize);
    }

    @Override
    protected void finishCapture() {
        if (!continueCapture)
            super.finishCapture();
        else
            captureStillPicture();
    }
}
