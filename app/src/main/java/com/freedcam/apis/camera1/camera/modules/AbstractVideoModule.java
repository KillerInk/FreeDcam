package com.freedcam.apis.camera1.camera.modules;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.camera1.camera.CameraHolderApi1;
import com.freedcam.apis.basecamera.camera.modules.AbstractModule;
import com.freedcam.apis.basecamera.camera.modules.I_RecorderStateChanged;
import com.freedcam.apis.basecamera.camera.modules.ModuleEventHandler;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.FileUtils;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by troop on 06.01.2016.
 */
public abstract class AbstractVideoModule extends AbstractModule
{
    protected MediaRecorder recorder;
    protected String mediaSavePath;
    protected CameraHolderApi1 cameraHolderApi1;
    private static String TAG = AbstractVideoModule.class.getSimpleName();
    private ParcelFileDescriptor fileDescriptor;
    private Context context;

    public AbstractVideoModule(CameraHolderApi1 cameraHandler, ModuleEventHandler eventHandler, Context context, AppSettingsManager appSettingsManager) {
        super(cameraHandler, eventHandler,context,appSettingsManager);
        name  = ModuleHandler.MODULE_VIDEO;
        this.context = context;
        this.cameraHolderApi1 = cameraHandler;
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
            cameraHolderApi1.GetCamera().unlock();
            recorder =  initRecorder();
            recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Logger.e("MediaRecorder", "ErrorCode: " + what + " Extra: " + extra);
                }
            });

            mediaSavePath = StringUtils.getFilePath(appSettingsManager.GetWriteExternal(), ".mp4");

            setRecorderOutPutFile(mediaSavePath);

            if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
                recorder.setOrientationHint(180);
            else
                recorder.setOrientationHint(0);

            // cameraHolderApi1.StopPreview();
            //ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);

            recorder.setPreviewDisplay(cameraHolderApi1.getSurfaceHolder());
            // cameraHolderApi1.StartPreview();

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
                cameraHolderApi1.errorHandler.OnError("Start Recording failed");
                Logger.exception(e);
                recorder.reset();
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
                isWorking = false;
                cameraHolderApi1.GetCamera().lock();
                recorder.release();
            }
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
            cameraHolderApi1.errorHandler.OnError("Start Recording failed");
            recorder.reset();
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            isWorking = false;
            cameraHolderApi1.GetCamera().lock();
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
            cameraHolderApi1.errorHandler.OnError("Stop Recording failed, was called bevor start");
            Logger.e(TAG,ex.getMessage());
        }
        finally
        {
            recorder.reset();
            cameraHolderApi1.GetCamera().lock();
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
            MediaScannerManager.ScanMedia(context, file);
            eventHandler.WorkFinished(file);
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
        }
        workfinished(true);
    }

    protected void setRecorderOutPutFile(String s)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT
                || (!appSettingsManager.GetWriteExternal() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT))
            recorder.setOutputFile(s);
        else
        {
            File f = new File(s);
            DocumentFile df = FileUtils.getFreeDcamDocumentFolder(appSettingsManager, context);
            DocumentFile wr = df.createFile("*/*", f.getName());
            try {
                fileDescriptor = context.getContentResolver().openFileDescriptor(wr.getUri(), "rw");
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
