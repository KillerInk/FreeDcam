package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.os.Build;
import android.os.Handler;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.capture.ContinouseRawCapture;
import freed.cam.apis.camera2.modules.capture.ContinouseYuvCapture;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RawStackPipeAllAtOnce extends PictureModuleApi2 {
    public RawStackPipeAllAtOnce(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = "HDR2";
    }

    private final static String TAG = RawStackPipe.class.getSimpleName();

    ContinouseRawCapture continouseRawCapture;
    private final int max_images = 30;


    @Override
    public String LongName() {
        return "HDR+2";
    }

    @Override
    public String ShortName() {
        return "HDR+2";
    }


    @Override
    protected void createImageCaptureListners() {
        captureType = CaptureType.Dng16;
        List<Size> yuvsizes = Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888));
        Size min = (Size) Collections.min(yuvsizes,new Camera2FeatureDetectorTask.SizeComparer());
        ContinouseYuvCapture byteImageCapture = new ContinouseYuvCapture(min,ImageFormat.YUV_420_888,false,cameraUiWrapper.getActivityInterface(),this,".yuv",max_images);
        captureController.add(byteImageCapture);
        Size largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CameraHolderApi2.CompareSizesByArea());
        continouseRawCapture = new ContinouseRawCapture(largestImageSize,ImageFormat.RAW_SENSOR,false,cameraUiWrapper.getActivityInterface(),this,".dng",max_images);
        captureController.add(continouseRawCapture);
    }

    @Override
    protected void takePicture() {
        super.takePicture();
    }

    @Override
    public void InitModule() {
        super.InitModule();
        cameraUiWrapper.parametersHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Hidden);
        cameraUiWrapper.parametersHandler.get(SettingKeys.M_Burst).SetValue(14,true);
        BurstApi2 burstApi2 = (BurstApi2) cameraUiWrapper.parametersHandler.get(SettingKeys.M_Burst);
        burstApi2.overwriteValues(2,30);
    }

    @Override
    public void DestroyModule() {
        BurstApi2 burstApi2 = (BurstApi2) cameraUiWrapper.parametersHandler.get(SettingKeys.M_Burst);
        burstApi2.overwriteValues(1,60);
        cameraUiWrapper.parametersHandler.get(SettingKeys.M_Burst).SetValue(0,true);
        cameraUiWrapper.parametersHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Visible);
        super.DestroyModule();
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     *
     */

    @Override
    public void onRdyToSaveImg() {
        //holder.getRunner().run();

        //Log.d(TAG,"onRdyToSaveImg " + BurstCounter.getBurstCount() +"/" +BurstCounter.getImageCaptured() + "/stack " +rawStackCaptureHolder.getStackCoutn());
        finishCapture();
    }

    @Override
    protected void finishCapture() {
        if(BurstCounter.getBurstCount()-1 == BurstCounter.getImageCaptured()) {
            if (SettingsManager.get(SettingKeys.forceRawToDng).get()) {
                if (SettingsManager.get(SettingKeys.support12bitRaw).get())
                    continouseRawCapture.startStackALL(BurstCounter.getBurstCount(), 2);
                else
                    continouseRawCapture.startStackALL(BurstCounter.getBurstCount(), 4);
            }
            else
                continouseRawCapture.startStackALL(BurstCounter.getBurstCount(), 0);
        }
        super.finishCapture();
    }
}
