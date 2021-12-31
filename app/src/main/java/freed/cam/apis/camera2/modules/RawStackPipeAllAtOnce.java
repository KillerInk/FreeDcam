package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.capture.ContinouseRawCapture;
import freed.cam.apis.camera2.modules.capture.ContinouseYuvCapture;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.modules.ring.CaptureResultRingBuffer;
import freed.cam.apis.camera2.modules.ring.ImageRingBuffer;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.featuredetector.Camera2FeatureDetectorTask;
import freed.cam.apis.featuredetector.camera2.PictureSizeDetector;
import freed.cam.event.capture.CaptureStates;
import freed.dng.DngProfile;
import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.jni.RawStack;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StorageFileManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RawStackPipeAllAtOnce extends RawZslModuleApi2 {
    private final static String TAG = RawStackPipe.class.getSimpleName();
    private StackAllRunner stackAllRunner;

    public RawStackPipeAllAtOnce(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        stackAllRunner = new StackAllRunner();
        name = "HDR2";
    }

    @Override
    public String LongName() {
        return "HDR+AllAtOnce";
    }

    @Override
    public String ShortName() {
        return "HDR+AaO";
    }


    @Override
    public void DoWork() {
        if (!stackAllRunner.doWork)
        {
            int burst = Integer.parseInt(parameterHandler.get(SettingKeys.M_Burst).getStringValue());
            stackAllRunner.setBurst(burst);
            if (settingsManager.get(SettingKeys.support12bitRaw).get()) {
                stackAllRunner.setUpshift(2);
            }
            else
                stackAllRunner.setUpshift(0);
            new Thread(stackAllRunner).start();
            changeCaptureState(CaptureStates.image_capture_start);
        }
    }

    @Override
    public void InitModule() {
        super.InitModule();
        parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Hidden);
        parameterHandler.get(SettingKeys.M_Burst).setIntValue(14,true);
        BurstApi2 burstApi2 = (BurstApi2) parameterHandler.get(SettingKeys.M_Burst);
        burstApi2.overwriteValues(2,30);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        BurstApi2 burstApi2 = (BurstApi2) parameterHandler.get(SettingKeys.M_Burst);
        burstApi2.overwriteValues(1,60);
        parameterHandler.get(SettingKeys.M_Burst).setIntValue(0,true);
        parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Visible);
    }

    private class StackAllRunner implements Runnable
    {

        private int burst;
        private int upshift;
        private boolean doWork = false;

        public StackAllRunner()
        {
        }

        public void setBurst(int burst)
        {
            this.burst = burst;
        }

        public void setUpshift(int upshift) {
            this.upshift = upshift;
        }

        @Override
        public void run() {
            doWork = true;
            RawStack rawStack = new RawStack();
            rawStack.setShift(upshift);
            int count = 0;
            ByteBuffer buffer;
            boolean inFocus = false;
            TotalCaptureResult result = null;
            Image img = null;
            int w = 0;
            int h = 0;
            while (!inFocus)
            {
                result = captureResultRingBuffer.pollLast();
                img = imageRingBuffer.pollLast();
                if (result != null && img != null
                    && result.get(CaptureResult.CONTROL_AF_STATE) == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                    || result.get(CaptureResult.CONTROL_AF_STATE) == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED)
                {
                    w = img.getWidth();
                    h = img.getHeight();
                    buffer = img.getPlanes()[0].getBuffer();
                    rawStack.setFirstFrame(buffer,img.getWidth(),img.getHeight(),burst);
                    img.close();
                    count++;
                    inFocus = true;
                }
            }

            while (count <= burst)
            {
                result = captureResultRingBuffer.pollLast();
                img = imageRingBuffer.pollLast();
                if (result.get(CaptureResult.CONTROL_AF_STATE) == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                    || result.get(CaptureResult.CONTROL_AF_STATE) == CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED) {
                    buffer = img.getPlanes()[0].getBuffer();
                    rawStack.setNextFrame(buffer);
                    img.close();
                    count++;
                }
            }
            changeCaptureState(CaptureStates.image_capture_stop);
            long starTime = SystemClock.uptimeMillis();
            byte[] bytes = rawStack.stackAll();
            long endTime = SystemClock.uptimeMillis();
            Log.d(TAG, "Stacked " + count +"/"+burst +" stacktime: " + (endTime -starTime) +"ms");
            Date date = new Date();
            String name = StorageFileManager.getStringDatePAttern().format(date);
            File file = new File(fileListController.getNewFilePath((name + "_HDRAoE__" + burst), ".dng"));
            ImageTask task = RawImageCapture.process_rawWithDngConverter(bytes,
                    DngProfile.Plain,
                    file,
                    result,
                    cameraHolder.characteristics,
                    w,
                    h,
                    RawStackPipeAllAtOnce.this,
                    null,
                    orientationManager.getCurrentOrientation(),
                    settingsManager.GetWriteExternal(),
                    null);

            ImageSaveTask itask = (ImageSaveTask)task;
            if (upshift > 0) {
                int bl = itask.getDngProfile().getBlacklvl();
                itask.getDngProfile().setBlackLevel(bl << upshift);
                int wl = itask.getDngProfile().getWhitelvl();
                itask.getDngProfile().setWhiteLevel(wl << upshift);
            }
            if (itask.getDngProfile().getRawType() != DngProfile.QuadBayerTo16bit)
                itask.getDngProfile().setRawType(DngProfile.Pure16bitTo16bit);

            if (task != null) {
                imageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
            }
            doWork = false;
        }
    }
}
