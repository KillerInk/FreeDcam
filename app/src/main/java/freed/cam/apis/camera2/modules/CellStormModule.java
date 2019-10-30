package freed.cam.apis.camera2.modules;

import android.os.Handler;

import com.troop.freedcam.R;

import java.io.File;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.SettingsManager;
import freed.utils.Log;

public class CellStormModule extends PictureModuleApi2 {


    private boolean continueCapture = false;
    private final int cropSize = 100;

    private final  String TAG = CellStormModule.class.getSimpleName();

    public CellStormModule(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = SettingsManager.getInstance().getResString(R.string.module_cellstorm);
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
    public void internalFireOnWorkDone(File file)
    {
            Log.d(TAG, "internalFireOnWorkDone BurstCount:" + BurstCounter.getBurstCount() + " imageCount:" + BurstCounter.getImageCaptured());
            if (continueCapture) {
                filesSaved.add(file);
                captureStillPicture();
                Log.d(TAG, "internalFireOnWorkDone Burst addFile");
            }
            else {
                Log.d(TAG, "internalFireOnWorkDone Burst done");
                fireOnWorkFinish(filesSaved.toArray(new File[filesSaved.size()]));
                filesSaved.clear();
            }
    }

}
