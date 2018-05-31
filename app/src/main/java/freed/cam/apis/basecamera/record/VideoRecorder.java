package freed.cam.apis.basecamera.record;

import android.hardware.Camera;
import android.location.Location;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.view.Surface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingsManager;
import freed.utils.Log;
import freed.utils.VideoMediaProfile;

/**
 * Created by KillerInk on 22.02.2018.
 */

public class VideoRecorder {
    private final MediaRecorder mediaRecorder;
    private final String TAG = VideoRecorder.class.getSimpleName();
    private MediaRecorder.OnErrorListener errorListener;
    private MediaRecorder.OnInfoListener infoListener;
    private Location location;

    private VideoMediaProfile currentVideoProfile;
    //MediaRecorder.VideoSource.SURFACE=2/CAMERA=1/DEFAULT=0
    private int outputSource;
    private File recordingFile;
    private int orientation;

    private Camera camera;

    CameraWrapperInterface cameraWrapperInterface;
    private Surface previewSurface;

    public VideoRecorder(CameraWrapperInterface cameraWrapperInterface,MediaRecorder recorder)
    {
        mediaRecorder = recorder;
        this.cameraWrapperInterface = cameraWrapperInterface;
    }

    public void setErrorListener(MediaRecorder.OnErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setInfoListener(MediaRecorder.OnInfoListener infoListener) {
        this.infoListener = infoListener;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCurrentVideoProfile(VideoMediaProfile currentVideoProfile) {
        this.currentVideoProfile = currentVideoProfile;
    }

    public void setVideoSource(int outputSource) {
        this.outputSource = outputSource;
    }

    public void setRecordingFile(File recordingFile) {
        this.recordingFile = recordingFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Surface getSurface()
    {
        return mediaRecorder.getSurface();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setPreviewSurface(Surface previewSurface)
    {
        this.previewSurface = previewSurface;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void start()
    {
        mediaRecorder.start();
    }

    public void stop()
    {
        mediaRecorder.stop();
    }

    public void prepare()
    {
        mediaRecorder.reset();
        if (camera != null)
            mediaRecorder.setCamera(camera);
        if (previewSurface != null)
            mediaRecorder.setPreviewDisplay(previewSurface);
        if (currentVideoProfile.maxRecordingSize != 0)
            mediaRecorder.setMaxFileSize(currentVideoProfile.maxRecordingSize);
        if (currentVideoProfile.duration != 0)
            mediaRecorder.setMaxDuration(currentVideoProfile.duration);
        if (errorListener != null)
            mediaRecorder.setOnErrorListener(errorListener);

        if (infoListener != null)
            mediaRecorder.setOnInfoListener(infoListener);

        if (location != null)
            mediaRecorder.setLocation((float)location.getLatitude(),(float)location.getLongitude());

        mediaRecorder.setOrientationHint(orientation);

        switch (currentVideoProfile.Mode)
        {

            case Normal:
            case Highspeed:
                if (currentVideoProfile.isAudioActive)
                {
                    try {
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                        //mediaRecorder.setAudioEncoder(currentVideoProfile.audioCodec);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        mediaRecorder.reset();
                        UserMessageHandler.sendMSG("AudioSource not Supported",true);
                        return;
                    }
                    catch (IllegalStateException ex)
                    {
                        mediaRecorder.reset();
                        UserMessageHandler.sendMSG("AudioSource not Supported",true);
                        return;
                    }
                }
                break;
            case Timelapse:
                break;
        }

        mediaRecorder.setVideoSource(outputSource);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        setRecorderFilePath();

        mediaRecorder.setVideoEncodingBitRate(currentVideoProfile.videoBitRate);
        mediaRecorder.setVideoFrameRate(currentVideoProfile.videoFrameRate);

         /*setCaptureRate

        Added in API level 11
        void setCaptureRate (double fps)
        Set video frame capture rate. This can be used to set a different video frame capture rate than the recorded video's playback rate.
        !!!!!! This method also sets the recording mode to time lapse.!!!!!
        In time lapse video recording, only video is recorded.
        Audio related parameters are ignored when a time lapse recording session starts, if an application sets them.*/
        //mediaRecorder.setCaptureRate((double)currentVideoProfile.videoFrameRate);

        mediaRecorder.setVideoSize(currentVideoProfile.videoFrameWidth, currentVideoProfile.videoFrameHeight);
        try {
            mediaRecorder.setVideoEncoder(currentVideoProfile.videoCodec);
        }
        catch (IllegalArgumentException ex)
        {
            mediaRecorder.reset();
            UserMessageHandler.sendMSG("VideoCodec not Supported",false);
        }

        switch (currentVideoProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if (currentVideoProfile.isAudioActive)
                {
                    try {
                        mediaRecorder.setAudioEncoder(currentVideoProfile.audioCodec);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        mediaRecorder.reset();
                        UserMessageHandler.sendMSG("AudioCodec not Supported",false);
                    }
                    mediaRecorder.setAudioChannels(currentVideoProfile.audioChannels);
                    mediaRecorder.setAudioEncodingBitRate(currentVideoProfile.audioBitRate);
                    mediaRecorder.setAudioSamplingRate(currentVideoProfile.audioSampleRate);
                }
                break;
            case Timelapse:
                float frame = 30;
                if (!TextUtils.isEmpty(SettingsManager.getInstance().getApiString(SettingsManager.TIMELAPSEFRAME)))
                    frame = Float.parseFloat(SettingsManager.getInstance().getApiString(SettingsManager.TIMELAPSEFRAME).replace(",", "."));
                else
                    SettingsManager.getInstance().setApiString(SettingsManager.TIMELAPSEFRAME, "" + frame);
                mediaRecorder.setCaptureRate(frame);
                break;
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException ex) {
            Log.WriteEx(ex);
            UserMessageHandler.sendMSG("Prepare failed :" + ex.getMessage(),false);
            return;
        }
    }

    private void setRecorderFilePath() {
        if (!SettingsManager.getInstance().GetWriteExternal()) {
            mediaRecorder.setOutputFile(recordingFile.getAbsolutePath());
        }
        else
        {
            Uri uri = Uri.parse(SettingsManager.getInstance().GetBaseFolder());
            DocumentFile df = cameraWrapperInterface.getActivityInterface().getFreeDcamDocumentFolder();
            DocumentFile wr = df.createFile("*/*", recordingFile.getName());
            ParcelFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = cameraWrapperInterface.getContext().getContentResolver().openFileDescriptor(wr.getUri(), "rw");
                mediaRecorder.setOutputFile(fileDescriptor.getFileDescriptor());
            } catch (FileNotFoundException e) {
                Log.WriteEx(e);
                try {
                    fileDescriptor.close();
                } catch (IOException e1) {
                    Log.WriteEx(e1);
                }
            }
        }
    }

    public void release()
    {
        mediaRecorder.release();
    }

}
