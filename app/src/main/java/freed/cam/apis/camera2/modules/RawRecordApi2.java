package freed.cam.apis.camera2.modules;

import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.media.ImageWriter;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.camera2.Camera2;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.modules.capture.RawImageCapture;
import freed.cam.apis.camera2.modules.helper.CaptureType;
import freed.cam.apis.camera2.modules.helper.FindOutputHelper;
import freed.cam.apis.camera2.modules.helper.Output;
import freed.cam.apis.camera2.modules.ring.CaptureResultRingBuffer;
import freed.cam.apis.camera2.modules.ring.ImageRingBuffer;
import freed.cam.apis.camera2.modules.ring.RingBuffer;
import freed.cam.event.capture.CaptureStates;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.dng.DngProfile;
import freed.file.holder.BaseHolder;
import freed.image.ImageManager;
import freed.image.ImageSaveTask;
import freed.image.ImageTask;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.utils.Log;
import freed.utils.StorageFileManager;

@RequiresApi(api = Build.VERSION_CODES.M)
public class RawRecordApi2 extends RawZslModuleApi2{

    private final String TAG = RawRecordApi2.class.getSimpleName();
    private RawProcessor rawProcessor;
    private RejectedExecutionHandler defaultRejectedExecutionHandler;
    private UserMessageHandler userMessageHandler;

    public RawRecordApi2(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
        name = FreedApplication.getStringFromRessources(R.string.module_rawcapture);
    }

    @Override
    public void InitModule() {
        super.InitModule();
        changeCaptureState(CaptureStates.video_recording_stop);
        rawProcessor = new RawProcessor();
        defaultRejectedExecutionHandler = imageManager.getRejectedExecutionHandler();
        imageManager.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                Log.d(TAG, "rejected task");
                rawProcessor.droppedFramesCounter++;
                ImageSaveTask task = (ImageSaveTask) r;
                task.clear();
                //executor.remove(task);
            }
        });
        if (parameterHandler.get(SettingKeys.PictureFormat) != null)
            parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Hidden);
        if (parameterHandler.get(SettingKeys.M_Burst) != null)
            parameterHandler.get(SettingKeys.M_Burst).setViewState(AbstractParameter.ViewState.Hidden);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        rawProcessor.stop();
        rawProcessor = null;
        imageManager.setRejectedExecutionHandler(defaultRejectedExecutionHandler);
        if (parameterHandler.get(SettingKeys.PictureFormat) != null)
            parameterHandler.get(SettingKeys.PictureFormat).setViewState(AbstractParameter.ViewState.Visible);
        if (parameterHandler.get(SettingKeys.M_Burst) != null)
            parameterHandler.get(SettingKeys.M_Burst).setViewState(AbstractParameter.ViewState.Visible);
    }

    @Override
    public String LongName() {
        return "RawRecord";
    }

    @Override
    public String ShortName() {
        return "RawRec";
    }

    @Override
    public void DoWork() {
        takePicture();
    }

    private void takePicture()
    {
        if (!rawProcessor.doWork) {
            changeCaptureState(CaptureStates.video_recording_start);
            new Thread(rawProcessor).start();
            Log.d(TAG,"start Recording");
        }
        else {
            changeCaptureState(CaptureStates.video_recording_stop);
            rawProcessor.stop();
            Log.d(TAG,"stop Recording");
        }
    }


    private class RawProcessor implements Runnable {
        private boolean doWork = false;
        private long frameCounter = 1;
        private long droppedFramesCounter =1;
        String name;

        public void stop()
        {
            doWork = false;
        }

        @Override
        public void run() {
            doWork = true;
            Date date = new Date();
            name = StorageFileManager.getStringDatePAttern().format(date);
            long sleep = 1000/25;
            long starttime = System.currentTimeMillis();

            while (doWork)
            {
                TotalCaptureResult result = captureResultRingBuffer.pollLast();
                Image img = imageRingBuffer.pollLast();
                if (img != null && result != null && img.getFormat() == ImageFormat.RAW_SENSOR) {
                    File file = new File(fileListController.getNewFilePath((name + "__" + frameCounter), ".dng"));
                    ImageTask task = RawImageCapture.process_rawWithDngConverter(img,
                            DngProfile.Plain,
                            file,
                            result,
                            cameraHolder.characteristics,
                            img.getWidth(),
                            img.getHeight(),
                            RawRecordApi2.this,
                            null,
                            orientationManager.getCurrentOrientation(),
                            settingsManager.GetWriteExternal(),
                            null);
                    imageManager.putImageSaveTask(task);
                }
                else
                {
                    if (img != null) {
                        img.close();
                    }
                    Log.d(TAG,"failed to process frame " + frameCounter
                            + " Reason is null: img:" + (img == null) + " result:" + (result == null)
                            + " saveQueueLeft: " + imageManager.getImageSaveManagerRemainingCapacity());
                }
                frameCounter++;
                /*long timegone = System.currentTimeMillis() - starttime;
                userMessageHandler.sendMSG((timegone/frameCounter)+"fps/dropped " + (timegone/droppedFramesCounter)+"fps" ,false);*/
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
