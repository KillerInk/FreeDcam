package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Size;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;
import com.troop.halidelib.RawStack;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.capture.ContinouseRawCapture;
import freed.cam.apis.camera2.modules.capture.ContinouseYuvCapture;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.apis.featuredetector.camera2.PictureSizeDetector;
import freed.cam.event.capture.CaptureStates;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.dng.DngProfile;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StorageFileManager;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RawStackPipe extends RawZslModuleApi2 {

    private final static String TAG = RawStackPipe.class.getSimpleName();
    private UserMessageHandler userMessageHandler;
    private StackRunner stackRunner;

    public RawStackPipe(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = FreedApplication.getStringFromRessources(R.string.module_stacking);
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
        stackRunner = new StackRunner();
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
    public void DoWork() {
        if (!stackRunner.run)
        {
            int burst = Integer.parseInt(parameterHandler.get(SettingKeys.M_BURST).getStringValue());
            stackRunner.setBurst(burst);
            new Thread(stackRunner).start();
        }
        else
            stackRunner.stop();
    }

    @Override
    public void InitModule() {
        super.InitModule();
        parameterHandler.get(SettingKeys.PICTURE_FORMAT).setViewState(AbstractParameter.ViewState.Hidden);
        parameterHandler.get(SettingKeys.M_BURST).setIntValue(14,true);
        BurstApi2 burstApi2 = (BurstApi2) parameterHandler.get(SettingKeys.M_BURST);
        burstApi2.overwriteValues(2,60);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        BurstApi2 burstApi2 = (BurstApi2) parameterHandler.get(SettingKeys.M_BURST);
        burstApi2.overwriteValues(1,60);
        parameterHandler.get(SettingKeys.M_BURST).setIntValue(0,true);
        parameterHandler.get(SettingKeys.PICTURE_FORMAT).setViewState(AbstractParameter.ViewState.Visible);
    }

    private class StackRunner implements Runnable
    {
        protected int burst;
        boolean run = false;
        long rawsize = 0;
        protected int upshift = 0;
        int w;
        int h;
        public StackRunner()
        {

        }

        public void setBurst(int burst)
        {
            this.burst = burst;
        }

        public void stop(){ run = false; }

        @Override
        public void run() {
            changeCaptureState(CaptureStates.image_capture_start);
            run = true;
            Log.d(TAG, "start stack");
            RawStack rawStack = new RawStack();
            rawStack.setShift(upshift);
            Image image = imageRingBuffer.pollLast();
            CaptureResult result = captureResultRingBuffer.pollLast();
            int stackCoutn = 0;

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            w = image.getWidth();
            h = image.getHeight();
            rawStack.setFirstFrame(buffer, w, h);
            image.close();
            stackCoutn++;
            while (run && stackCoutn < burst) {
                image = imageRingBuffer.pollLast();
                result = captureResultRingBuffer.pollLast();

                buffer = image.getPlanes()[0].getBuffer();
                Log.d(TAG, "stackframes");
                rawStack.stackNextFrame(buffer);
                image.close();
                buffer.clear();
                stackCoutn++;
                userMessageHandler.sendMSG("Stacked:" +stackCoutn,false);
                Log.d(TAG, "stackframes done " + stackCoutn);
            }
            changeCaptureState(CaptureStates.image_capture_stop);
            Date date = new Date();
            String name = StorageFileManager.getStringDatePAttern().format(date);
            File file = new File(fileListController.getNewFilePath((name + "_HDRObO__" + burst), ".dng"));
            byte[] bytes = new byte[w*h*16/8];
            rawStack.getOutputBuffer(bytes);
            ImageTask task = RawImageCapture.process_rawWithDngConverter(bytes,
                    DngProfile.Plain,
                    file,
                    result,
                    cameraHolder.characteristics,
                    w,
                    h,
                    RawStackPipe.this,
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
                itask.getDngProfile().setRawType(DngProfile.Pure16bit_To_Lossless);

            if (task != null) {
                imageManager.putImageSaveTask(task);
                Log.d(TAG, "Put task to Queue");
            }
            rawStack.clear();
            rawStack = null;
            run = false;
        }
    }

}
