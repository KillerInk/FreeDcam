package com.troop.freedcam.camera.modules;

import android.media.MediaRecorder;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.troop.filelogger.Logger;
import com.troop.freedcam.camera.BaseCameraHolder;
import com.troop.freedcam.i_camera.modules.AbstractModule;
import com.troop.freedcam.i_camera.modules.I_RecorderStateChanged;
import com.troop.freedcam.i_camera.modules.ModuleEventHandler;
import com.troop.freedcam.manager.MediaScannerManager;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.FileUtils;
import com.troop.freedcam.utils.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Ingo on 06.01.2016.
 */
public abstract class AbstractVideoModule extends AbstractModule
{
    protected MediaRecorder recorder;
    protected String mediaSavePath;
    protected BaseCameraHolder baseCameraHolder;
    private static String TAG = AbstractVideoModule.class.getSimpleName();
    private ParcelFileDescriptor fileDescriptor;

    public AbstractVideoModule(BaseCameraHolder cameraHandler, ModuleEventHandler eventHandler) {
        super(cameraHandler, eventHandler);
        name  = ModuleHandler.MODULE_VIDEO;
        this.baseCameraHolder = cameraHandler;
    }

    @Override
    public String ShortName() {
        return "Mov";
    }

    @Override
    public String LongName() {
        return "Movie";
    }

    //I_Module START
    @Override
    public String ModuleName() {
        return name;
    }

    @Override
    public boolean DoWork()
    {
        if (!isWorking)
            startRecording();
        else
            stopRecording();
        return true;

    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }
//I_Module END


    protected void startRecording()
    {
        prepareRecorder();
        workstarted();

    }

    protected void prepareRecorder()
    {
        try
        {
            Logger.d(TAG, "InitMediaRecorder");
            isWorking = true;
            baseCameraHolder.GetCamera().unlock();
            recorder =  initRecorder();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Logger.e("MediaRecorder", "ErrorCode: " + what + " Extra: " + extra);
                }
            });

            mediaSavePath = StringUtils.getFilePath(AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal(), ".mp4");

            setRecorderOutPutFile(mediaSavePath);

            if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
                recorder.setOrientationHint(180);
            else
                recorder.setOrientationHint(0);

            // baseCameraHolder.StopPreview();
            //ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);

            recorder.setPreviewDisplay(baseCameraHolder.getSurfaceHolder());
            // baseCameraHolder.StartPreview();

            try {
                Logger.d(TAG,"Preparing Recorder");
                recorder.prepare();
                Logger.d(TAG, "Recorder Prepared, Starting Recording");
                recorder.start();
                Logger.d(TAG, "Recording started");
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);

            } catch (Exception e)
            {
                Logger.e(TAG,"Recording failed");
                baseCameraHolder.errorHandler.OnError("Start Recording failed");
                Logger.exception(e);
                recorder.reset();
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
                isWorking = false;
                baseCameraHolder.GetCamera().lock();
                recorder.release();
            }
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
            baseCameraHolder.errorHandler.OnError("Start Recording failed");
            recorder.reset();
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            isWorking = false;
            baseCameraHolder.GetCamera().lock();
            recorder.release();
        }
    }

    protected abstract MediaRecorder initRecorder();

    protected void stopRecording()
    {
        try {
            recorder.stop();
            Logger.e(TAG, "Stop Recording");
        }
        catch (Exception ex)
        {
            Logger.e(TAG, "Stop Recording failed, was called bevor start");
            baseCameraHolder.errorHandler.OnError("Stop Recording failed, was called bevor start");
            Logger.e(TAG,ex.getMessage());
        }
        finally
        {
            recorder.reset();
            baseCameraHolder.GetCamera().lock();
            recorder.release();
            isWorking = false;
            try {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT && fileDescriptor != null) {
                    fileDescriptor.close();
                    fileDescriptor = null;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            final File file = new File(mediaSavePath);
            MediaScannerManager.ScanMedia(AppSettingsManager.APPSETTINGSMANAGER.context.getApplicationContext(), file);
            eventHandler.WorkFinished(file);
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        }
        workfinished(true);
    }

    protected void setRecorderOutPutFile(String s)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                || (!AppSettingsManager.APPSETTINGSMANAGER.GetWriteExternal() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT))
            recorder.setOutputFile(s);
        else
        {
            File f = new File(s);
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(AppSettingsManager.APPSETTINGSMANAGER);
            DocumentFile wr = df.createFile("*/*", f.getName());
            try {
                fileDescriptor = AppSettingsManager.APPSETTINGSMANAGER.context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                recorder.setOutputFile(fileDescriptor.getFileDescriptor());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                try {
                    fileDescriptor.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
