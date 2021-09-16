package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.modes.ToneMapChooser;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.capture.AbstractImageCapture;
import freed.cam.apis.camera2.modules.capture.ByteImageCapture;
import freed.cam.apis.camera2.modules.capture.ContinouseRawCapture;
import freed.cam.apis.camera2.modules.capture.ContinouseYuvCapture;
import freed.cam.apis.camera2.modules.capture.JpegCapture;
import freed.cam.apis.camera2.modules.capture.StillImageCapture;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.cam.apis.featuredetector.camera2.PictureSizeDetector;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RawStackPipe extends PictureModuleApi2 {

    private final static String TAG = RawStackPipe.class.getSimpleName();

    ContinouseRawCapture continouseRawCapture;
    private final int max_images = 56;

    public RawStackPipe(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = FreedApplication.getStringFromRessources(R.string.module_stacking);
    }

    @Override
    public String LongName() {
        return "HDR+OneByOne";
    }

    @Override
    public String ShortName() {
        return "HDR+ObO";
    }

    @Override
    protected void createImageCaptureListners() {
        captureType = CaptureType.Dng16;
        List<Size> yuvsizes = Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.YUV_420_888));
        Size min = (Size) Collections.min(yuvsizes,new PictureSizeDetector.SizeComparer());
        ContinouseYuvCapture byteImageCapture = new ContinouseYuvCapture(min,ImageFormat.YUV_420_888,false,this,".yuv",max_images);
        captureController.add(byteImageCapture);
        Size largestImageSize = Collections.max(Arrays.asList(cameraHolder.map.getOutputSizes(ImageFormat.RAW_SENSOR)), new CameraHolderApi2.CompareSizesByArea());
        continouseRawCapture = new ContinouseRawCapture(largestImageSize,ImageFormat.RAW_SENSOR,false,this,".dng",max_images);
        captureController.add(continouseRawCapture);
    }

    @Override
    protected void takePicture() {
        super.takePicture();
        internalTakePicture();
    }

    protected void internalTakePicture()
    {
        if (settingsManager.get(SettingKeys.forceRawToDng).get()) {
            if (settingsManager.get(SettingKeys.support12bitRaw).get())
                continouseRawCapture.startStack(BurstCounter.getBurstCount(), 2);
            else
                continouseRawCapture.startStack(BurstCounter.getBurstCount(), 4);
        }
        else
            continouseRawCapture.startStack(BurstCounter.getBurstCount(), 0);
    }

    @Override
    public void InitModule() {
        super.InitModule();
        parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Hidden);
        parameterHandler.get(SettingKeys.M_Burst).setIntValue(14,true);
    }

    @Override
    public void DestroyModule() {
        parameterHandler.get(SettingKeys.M_Burst).setIntValue(0,true);
        parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Visible);
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

}
