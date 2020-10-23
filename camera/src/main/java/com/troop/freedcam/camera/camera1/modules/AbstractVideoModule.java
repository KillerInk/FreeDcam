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

package com.troop.freedcam.camera.camera1.modules;

import android.media.MediaRecorder;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.ParcelFileDescriptor;

import com.troop.freedcam.camera.R;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.modules.ModuleAbstract;
import com.troop.freedcam.camera.basecamera.record.VideoRecorder;
import com.troop.freedcam.camera.camera1.CameraHolder;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.eventbus.enums.CaptureStates;
import com.troop.freedcam.eventbus.events.UserMessageEvent;
import com.troop.freedcam.file.holder.FileHolder;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.utils.Log;
import com.troop.freedcam.utils.PermissionManager;

import java.io.File;
import java.io.IOException;

/**
 * Created by troop on 06.01.2016.
 */
public abstract class AbstractVideoModule<C extends CameraControllerInterface> extends ModuleAbstract<C> implements MediaRecorder.OnInfoListener
{
    VideoRecorder recorder;
    private String mediaSavePath;
    private final String TAG = AbstractVideoModule.class.getSimpleName();
    private ParcelFileDescriptor fileDescriptor;

    AbstractVideoModule(C cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = ContextApplication.getStringFromRessources(R.string.module_video);
    }

    @Override
    public void InitModule() {
        super.InitModule();
        changeCaptureState(CaptureStates.video_recording_stop);
        initRecorder();
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
    public void DoWork()
    {
        startStopRecording();
    }

    private void startStopRecording()
    {
        if (!isWorking && !isLowStorage) {
            startRecording();
        }
        else if( isWorking ) {
            stopRecording();
        }
        if( isLowStorage ) {
            EventBusHelper.post(new UserMessageEvent("Can't Record due to low storage space. Free some and try again.", false));
        }
    }

    @Override
    public boolean IsWorking() {
        return isWorking;
    }

    @Override
    public void IsLowStorage(Boolean x) {
        isLowStorage = x;
    }
//ModuleInterface END


    private void startRecording()
    {
        if (cameraUiWrapper.getPermissionManager().isPermissionGranted(PermissionManager.Permissions.RecordAudio)) {
            //TODO check if its needed
            /*if (SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
                cameraUiWrapper.getCameraHolder().SetLocation(cameraUiWrapper.getActivityInterface().getLocationManager().getCurrentLocation());*/
            prepareRecorder();
        }
        else
            cameraUiWrapper.getPermissionManager().requestPermission(PermissionManager.Permissions.RecordAudio);

    }

    private void prepareRecorder()
    {
        try
        {
            Log.d(TAG, "InitMediaRecorder");
            isWorking = true;
            ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera().unlock();
            mediaSavePath = cameraUiWrapper.getFileListController().getStorageFileManager().getNewFilePath(SettingsManager.getInstance().GetWriteExternal(), ".mp4");
            File tosave = new File(mediaSavePath);
            recorder.setRecordingFile(tosave);
            recorder.setErrorListener((mr, what, extra) -> {
                Log.e("MediaRecorder", "ErrorCode: " + what + " Extra: " + extra);
                changeCaptureState(CaptureStates.video_recording_stop);
                ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera().lock();
            });


            if (!tosave.getParentFile().exists())
                tosave.getParentFile().mkdirs();

            recorder.setInfoListener(this);

            if (SettingsManager.get(SettingKeys.orientationHack).get())
                recorder.setOrientation(180);
            else
                recorder.setOrientation(0);

            recorder.setPreviewSurface(((CameraHolder) cameraUiWrapper.getCameraHolder()).getSurfaceHolder());


                Log.d(TAG,"Preparing Recorder");
                if(recorder.prepare()) {
                    Log.d(TAG, "Recorder Prepared, Starting Recording");
                    recorder.start();

                //fix exposer flick after video recording on first set parameters to camera.
                // first call will under expose then second call will fix exposure.
                cameraUiWrapper.getParameterHandler().SetParameters();
                cameraUiWrapper.getParameterHandler().SetParameters();

                    Log.d(TAG, "Recording started");
                    sendStartToUi();
                }
                else
                {
                    Log.e(TAG,"Recording failed");
                    EventBusHelper.post(new UserMessageEvent("Start Recording failed ",false));
                    isWorking = false;
                    ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera().lock();
                    recorder.reset();
                    isWorking = false;
                    sendStopToUi();
                }
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
            EventBusHelper.post(new UserMessageEvent("Start Recording failed",false));
            isWorking = false;
            ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera().lock();
            recorder.reset();
            isWorking = false;
            sendStopToUi();

        }
    }

    private void sendStopToUi()
    {
        changeCaptureState(CaptureStates.video_recording_stop);
    }

    private void sendStartToUi()
    {
        changeCaptureState(CaptureStates.video_recording_start);
    }

    protected abstract void initRecorder();

    void stopRecording()
    {
        try {
            recorder.stop();
            Log.e(TAG, "Stop Recording");
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Stop Recording failed, was called bevor start");
            EventBusHelper.post(new UserMessageEvent("Stop Recording failed, was called bevor start",false));
            Log.e(TAG,ex.getMessage());
            isWorking = false;
        }
        finally
        {
            ((CameraHolder) cameraUiWrapper.getCameraHolder()).GetCamera().lock();
            recorder.reset();
            isWorking = false;
            try {
                if (VERSION.SDK_INT > VERSION_CODES.KITKAT && fileDescriptor != null) {
                    fileDescriptor.close();
                    fileDescriptor = null;
                }
            } catch (IOException e1) {
                Log.WriteEx(e1);
            }
            File file = new File(mediaSavePath);
            fireOnWorkFinish(new FileHolder(ContextApplication.getContext(),file,SettingsManager.getInstance().GetWriteExternal()));
            sendStopToUi();
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
        {
            recordnextFile(mr);
        }
        else if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
        {
            recordnextFile(mr);
        }
    }

    private void recordnextFile(MediaRecorder mr) {
        stopRecording();
        startRecording();
    }
}
