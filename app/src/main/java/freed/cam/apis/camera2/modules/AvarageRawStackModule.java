package freed.cam.apis.camera2.modules;

import android.hardware.camera2.CaptureResult;
import android.media.Image;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.modules.capture.ContinouseRawCapture;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.event.capture.CaptureStates;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.dng.DngProfile;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.jni.RawStack;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.StorageFileManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AvarageRawStackModule extends RawZslModuleApi2 {

    private UserMessageHandler userMessageHandler;
    private StackAvarageRunner avarageRunner;
    public AvarageRawStackModule(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = ShortName();
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
        avarageRunner = new StackAvarageRunner();
    }

    @Override
    public String LongName() {
        return "Average";
    }

    @Override
    public String ShortName() {
        return "AVG";
    }

    @Override
    public void DoWork() {
        if (!avarageRunner.run)
        {
            int burst = Integer.parseInt(parameterHandler.get(SettingKeys.M_Burst).getStringValue());
            avarageRunner.setBurst(burst);
            new Thread(avarageRunner).start();
        }
        else
            avarageRunner.stop();
    }

    @Override
    public void InitModule() {
        super.InitModule();
        parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Hidden);
        parameterHandler.get(SettingKeys.M_Burst).setIntValue(14,true);
        BurstApi2 burstApi2 = (BurstApi2) parameterHandler.get(SettingKeys.M_Burst);
        burstApi2.overwriteValues(2,60);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        BurstApi2 burstApi2 = (BurstApi2) parameterHandler.get(SettingKeys.M_Burst);
        burstApi2.overwriteValues(1,60);
        parameterHandler.get(SettingKeys.M_Burst).setIntValue(0,true);
        parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Visible);
    }

    private class StackAvarageRunner implements Runnable {

        private final String TAG = StackAvarageRunner.class.getSimpleName();
        private boolean run = false;
        private int burst = 0;
        private int upshift = 0;
        public StackAvarageRunner()
        {
        }

        public void setBurst(int burst) {
            this.burst = burst;
        }

        public void stop(){ run = false; }

        @Override
        public void run() {
            changeCaptureState(CaptureStates.image_capture_start);
            run = true;
            Log.d(TAG, "start stack");
            RawStack rawStack = new RawStack();
            rawStack.setShift(0);
            Image image = imageRingBuffer.pollLast();
            CaptureResult result = captureResultRingBuffer.pollLast();
            int stackCoutn = 0;

            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            int w = image.getWidth();
            int h = image.getHeight();
            rawStack.setFirstFrame(buffer, w, h);
            image.close();
            stackCoutn++;
            while (run && stackCoutn < burst) {
                image = imageRingBuffer.pollLast();
                result = captureResultRingBuffer.pollLast();

                buffer = image.getPlanes()[0].getBuffer();
                Log.d(TAG, "stackframes");
                rawStack.stackNextFrameAvarage(buffer);
                image.close();
                buffer.clear();
                stackCoutn++;
                userMessageHandler.sendMSG("Stacked:" +stackCoutn,false);
                Log.d(TAG, "stackframes done " + stackCoutn);
            }
            changeCaptureState(CaptureStates.image_capture_stop);
            Date date = new Date();
            String name = StorageFileManager.getStringDatePAttern().format(date);
            File file = new File(fileListController.getNewFilePath((name + "_AVG__" + burst), ".dng"));
            byte[] bytes = new byte[w*h*16/8];
            rawStack.getOutputBuffer(bytes);
            ImageTask task = RawImageCapture.process_rawWithDngConverter(bytes,
                    DngProfile.Plain,
                    file,
                    result,
                    cameraHolder.characteristics,
                    w,
                    h,
                    AvarageRawStackModule.this,
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
            rawStack.clear();
            rawStack = null;
            run = false;
        }
    }
}
