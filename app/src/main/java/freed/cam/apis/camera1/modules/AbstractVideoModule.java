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

package freed.cam.apis.camera1.modules;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.I_RecorderStateChanged;
import freed.cam.apis.basecamera.modules.ModuleAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.camera1.CameraHolder;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 06.01.2016.
 */
public abstract class AbstractVideoModule extends ModuleAbstract
{
    protected MediaRecorder recorder;
    protected String mediaSavePath;
    private final String TAG = AbstractVideoModule.class.getSimpleName();
    private ParcelFileDescriptor fileDescriptor;

    public AbstractVideoModule(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        name = KEYS.MODULE_VIDEO;
    }

    @Override
    public String ShortName() {
        return "Mov";
    }

    @Override
    public String LongName() {
        return "Movie";
    }

    //ModuleInterface START
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
//ModuleInterface END


    protected void startRecording()
    {
        if (cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(KEYS.ON))
            cameraUiWrapper.GetCameraHolder().SetLocation(cameraUiWrapper.getActivityInterface().getLocationHandler().getCurrentLocation());
        prepareRecorder();
        changeCaptureState(CaptureStates.video_recording_start);

    }

    protected void prepareRecorder()
    {
        try
        {
            Logger.d(TAG, "InitMediaRecorder");
            isWorking = true;
            ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera().unlock();
            recorder = initRecorder();
            recorder.setOnErrorListener(new OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Logger.e("MediaRecorder", "ErrorCode: " + what + " Extra: " + extra);
                }
            });

            mediaSavePath = cameraUiWrapper.getActivityInterface().getStorageHandler().getNewFilePath(appSettingsManager.GetWriteExternal(), ".mp4");

            setRecorderOutPutFile(mediaSavePath);

            if (appSettingsManager.getString(AppSettingsManager.SETTING_OrientationHack).equals("true"))
                recorder.setOrientationHint(180);
            else
                recorder.setOrientationHint(0);

            // cameraHolder.StopPreview();
            //ParameterHandler.PreviewFormat.SetValue("nv12-venus", true);

            recorder.setPreviewDisplay(((CameraHolder) cameraUiWrapper.GetCameraHolder()).getSurfaceHolder());
            // cameraHolder.StartPreview();

            try {
                Logger.d(TAG,"Preparing Recorder");
                recorder.prepare();
                Logger.d(TAG, "Recorder Prepared, Starting Recording");
                recorder.start();
                Logger.d(TAG, "Recording started");
                cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_START);

            } catch (Exception e)
            {
                Logger.e(TAG,"Recording failed");
                cameraUiWrapper.GetCameraHolder().SendUIMessage("Start Recording failed");
                Logger.exception(e);
                recorder.reset();
                cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
                isWorking = false;
                ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera().lock();
                recorder.release();
                isWorking = false;
                changeCaptureState(CaptureStates.video_recording_stop);
            }
        }
        catch (NullPointerException ex)
        {
            Logger.exception(ex);
            cameraUiWrapper.GetCameraHolder().SendUIMessage("Start Recording failed");
            recorder.reset();
            cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);
            isWorking = false;
            ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera().lock();
            recorder.release();
            isWorking = false;
            changeCaptureState(CaptureStates.video_recording_stop);
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
            cameraUiWrapper.GetCameraHolder().SendUIMessage("Stop Recording failed, was called bevor start");
            Logger.e(TAG,ex.getMessage());
            isWorking = false;
        }
        finally
        {
            recorder.reset();
            ((CameraHolder) cameraUiWrapper.GetCameraHolder()).GetCamera().lock();
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
            scanAndFinishFile(file);
            cameraUiWrapper.GetModuleHandler().onRecorderstateChanged(I_RecorderStateChanged.STATUS_RECORDING_STOP);

        }
        changeCaptureState(CaptureStates.video_recording_stop);
    }

    protected void setRecorderOutPutFile(String s)
    {
        if (VERSION.SDK_INT < VERSION_CODES.KITKAT
                || !appSettingsManager.GetWriteExternal() && VERSION.SDK_INT >= VERSION_CODES.KITKAT)
            recorder.setOutputFile(s);
        else
        {
            File f = new File(s);
            DocumentFile df = cameraUiWrapper.getActivityInterface().getFreeDcamDocumentFolder();
            DocumentFile wr = df.createFile("*/*", f.getName());
            try {
                fileDescriptor = cameraUiWrapper.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
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
