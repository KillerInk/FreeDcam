package freed.cam.apis.camera2.modules;

import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraValuesChangedCaptureCallback;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.parameters.manual.BurstApi2;
import freed.cam.event.capture.CaptureStates;
import freed.dng.DngProfile;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.jni.RawStack;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StorageFileManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MultiFrame14bitDngModule extends RawZslModuleApi2 {




    private Merge14bit merge14bit;
    public MultiFrame14bitDngModule(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        merge14bit = new Merge14bit();
        name = "MFDng";
    }

    @Override
    public String LongName() {
        return "MultiFrame14bitDng";
    }

    @Override
    public String ShortName() {
        return "MF14bit";
    }

    @Override
    public void InitModule() {
        super.InitModule();
        parameterHandler.get(SettingKeys.PICTURE_FORMAT).setViewState(AbstractParameter.ViewState.Hidden);
        parameterHandler.get(SettingKeys.M_BURST).setViewState(AbstractParameter.ViewState.Hidden);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        parameterHandler.get(SettingKeys.M_BURST).setViewState(AbstractParameter.ViewState.Visible);
        parameterHandler.get(SettingKeys.PICTURE_FORMAT).setViewState(AbstractParameter.ViewState.Visible);
    }

    @Override
    public void DoWork() {
        if (!merge14bit.doWork)
        {
            startPreCapture();
        }
        else
            merge14bit.doWork = false;
    }

    @Override
    protected void captureStillPicture()
    {
        new Thread(merge14bit).start();
        super.captureStillPicture();
    }

    private class Merge14bit implements Runnable {


        private final int imagecount = 17;
        private boolean doWork = false;

        @Override
        public void run() {
            doWork = true;
            RawStack rawStack = new RawStack();
            ByteBuffer buffer;
            TotalCaptureResult result = null;
            Image img = null;
            int count = 0;
            int w =0,h=0;
            byte[] bytes;
            result = captureResultRingBuffer.pollLast();
            img = imageRingBuffer.pollLast();
            if (result != null && img != null)
            {
                w = img.getWidth();
                h = img.getHeight();
                buffer = img.getPlanes()[0].getBuffer();
                rawStack.setFirstFrame(buffer,img.getWidth(),img.getHeight(),imagecount);
                img.close();
                count++;
            }
            while (doWork && count <= imagecount) {
                result = captureResultRingBuffer.pollLast();
                img = imageRingBuffer.pollLast();
                if (result != null && img != null) {
                    buffer = img.getPlanes()[0].getBuffer();
                    rawStack.setNextFrame(buffer);
                    img.close();
                    count++;
                } else if (img != null) {
                    img.close();
                }
            }
            bytes = new byte[w * h * 16 / 8];
            rawStack.mergeTo14bit(bytes);
            rawStack.clear();
            changeCaptureState(CaptureStates.image_capture_stop);


            Date date = new Date();
            String name = StorageFileManager.getStringDatePAttern().format(date);
            File file = new File(fileListController.getNewFilePath((name + "_MF"), ".dng"));
            ImageTask task = RawImageCapture.process_rawWithDngConverter(bytes,
                    DngProfile.Pure16bitTo16bit,
                    file,
                    result,
                    cameraHolder.characteristics,
                    w,
                    h,
                    MultiFrame14bitDngModule.this,
                    null,
                    orientationManager.getCurrentOrientation(),
                    settingsManager.GetWriteExternal(),
                    null);

            ImageSaveTask itask = (ImageSaveTask)task;
            int bl = itask.getDngProfile().getBlacklvl();
            itask.getDngProfile().setBlackLevel((bl) << 4);
            int wl = itask.getDngProfile().getWhitelvl();
            itask.getDngProfile().setWhiteLevel(wl<<4);

            if (task != null) {
                imageManager.putImageSaveTask(task);
            }
            doWork = false;
            cameraUiWrapper.captureSessionHandler.StartRepeatingCaptureSession();
        }
    }
}
