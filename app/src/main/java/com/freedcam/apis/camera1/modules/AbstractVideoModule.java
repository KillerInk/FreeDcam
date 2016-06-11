/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.apis.camera1.modules;

import android.content.Context;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModule;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.modules.I_RecorderStateChanged;
import com.freedcam.apis.basecamera.modules.ModuleEventHandler;
import com.freedcam.apis.camera1.CameraHolder;
import com.freedcam.ui.handler.MediaScannerManager;
import com.freedcam.utils.AppSettingsManager;
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
    private String TAG = AbstractVideoModule.class.getSimpleName();
    private ParcelFileDescriptor fileDescriptor;
    private Context context;

    public AbstractVideoModule(Context context, I_CameraUiWrapper cameraUiWrapper,ModuleEventHandler eventHandler) {
        super(context, cameraUiWrapper,eventHandler);
        name  = KEYS.MODULE_VIDEO;
        this.context = context;
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
        changeWorkState(CaptureModes.video_recording_start);

    }

    protected void prepareRecorder()
    {
        try
        {
            Logger.d(TAG, "InitMediaRecorder");
            isWorking = true;
            ((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCamera().unlock();
            recorder =  initRecorder();
            recorder.setOnErrorListener(new OnErrorListener() {
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

            // cameraHolder.StopPreview();
            //ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);

            recorder.setPreviewDisplay(((CameraHolder)cameraUiWrapper.GetCameraHolder()).getSurfaceHolder());
            // cameraHolder.StartPreview();

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
                cameraUiWrapper.GetCameraHolder().SendUIMessage("Start Recording failed");
                Logger.exception(e);
                recorder.reset();
                eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
                isWorking = false;
                ((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCamera().lock();
                recorder.release();
                isWorking = false;
                changeWorkState(CaptureModes.video_recording_stop);
            }
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
            cameraUiWrapper.GetCameraHolder().SendUIMessage("Start Recording failed");
            recorder.reset();
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            isWorking = false;
            ((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCamera().lock();
            recorder.release();
            isWorking = false;
            changeWorkState(CaptureModes.video_recording_stop);
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
            ((CameraHolder)cameraUiWrapper.GetCameraHolder()).SendUIMessage("Stop Recording failed, was called bevor start");
            Logger.e(TAG,ex.getMessage());
        }
        finally
        {
            recorder.reset();
            ((CameraHolder)cameraUiWrapper.GetCameraHolder()).GetCamera().lock();
            recorder.release();
            isWorking = false;
            try {
                if (VERSION.SDK_INT > VERSION_CODES.KITKAT && fileDescriptor != null) {
                    fileDescriptor.close();
                    fileDescriptor = null;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            File file = new File(mediaSavePath);
            MediaScannerManager.ScanMedia(context, file);
            eventHandler.WorkFinished(file);
            eventHandler.onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            isWorking = false;
        }
        changeWorkState(CaptureModes.video_recording_stop);
    }

    protected void setRecorderOutPutFile(String s)
    {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT
                || !appSettingsManager.GetWriteExternal() && VERSION.SDK_INT >= VERSION_CODES.KITKAT)
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
                Logger.exception(e);
                try {
                    fileDescriptor.close();
                } catch (IOException e1) {
                    Logger.exception(e1);
                }
            }
        }

    }
}
