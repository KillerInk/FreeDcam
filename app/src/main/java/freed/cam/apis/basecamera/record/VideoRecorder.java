package freed.cam.apis.basecamera.record;

import android.hardware.Camera;
import android.location.Location;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Build;
import android.text.TextUtils;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.troop.freedcam.R;

import org.chickenhook.restrictionbypass.RestrictionBypass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.cam.ui.videoprofileeditor.MediaCodecInfoParser;
import freed.file.FileListController;
import freed.file.holder.BaseHolder;
import freed.settings.SettingKeys;
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
    private Surface inputSurface;
    private SettingsManager settingsManager;
    private FileListController fileListController;
    private UserMessageHandler userMessageHandler;

    public VideoRecorder(CameraWrapperInterface cameraWrapperInterface,MediaRecorder recorder)
    {
        mediaRecorder = recorder;
        this.cameraWrapperInterface = cameraWrapperInterface;
        settingsManager = FreedApplication.settingsManager();
        fileListController = FreedApplication.fileListController();
        userMessageHandler = ActivityFreeDcamMain.userMessageHandler();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setNextFile(File file) throws IOException {
        BaseHolder baseHolder = fileListController.getNewMovieFileHolder(file);
        try {
            baseHolder.setNextToMediaRecorder(mediaRecorder);
        } catch (FileNotFoundException e) {
            Log.WriteEx(e);
        }
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
        try {
            mediaRecorder.stop();
        }
        catch (RuntimeException e)
        {
            Log.WriteEx(e);
        }

    }

    public void setInputSurface(Surface inputSurface) {
        this.inputSurface = inputSurface;
    }

    public boolean prepare()
    {
        mediaRecorder.reset();
        if (camera != null)
            mediaRecorder.setCamera(camera);
        if (previewSurface != null)
            mediaRecorder.setPreviewDisplay(previewSurface);
        try {
            if (this.currentVideoProfile.maxRecordingSize != 0)
                mediaRecorder.setMaxFileSize(this.currentVideoProfile.maxRecordingSize);
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
            userMessageHandler.sendMSG("Failed to set Max Recording size",true);
            //return false;
        }
        try {
            if (this.currentVideoProfile.duration != 0)
                mediaRecorder.setMaxDuration(this.currentVideoProfile.duration);
        }
        catch (RuntimeException ex)
        {
            Log.WriteEx(ex);
            Log.e(TAG,"Failed to set Duration");
            userMessageHandler.sendMSG("Failed to set Duration",true);
        }

        if (errorListener != null)
            mediaRecorder.setOnErrorListener(errorListener);

        if (infoListener != null)
            mediaRecorder.setOnInfoListener(infoListener);

        if (location != null)
            mediaRecorder.setLocation((float)location.getLatitude(),(float)location.getLongitude());

        mediaRecorder.setOrientationHint(orientation);

        switch (this.currentVideoProfile.Mode)
        {

            case Normal:
            case Highspeed:
                if (this.currentVideoProfile.isAudioActive)
                {
                    try {
                        mediaRecorder.setAudioSource(getAudioSource());
                        //mediaRecorder.setAudioEncoder(currentVideoProfile.audioCodec);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        mediaRecorder.reset();
                        userMessageHandler.sendMSG("AudioSource not Supported",true);
                        return false;
                    }
                    catch (IllegalStateException ex)
                    {
                        mediaRecorder.reset();
                        userMessageHandler.sendMSG("AudioSource not Supported",true);
                        return false;
                    }
                }
                break;
            case Timelapse:
                break;
        }

        mediaRecorder.setVideoSource(outputSource);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        setRecorderFilePath();

        try {
            mediaRecorder.setVideoEncodingBitRate(this.currentVideoProfile.videoBitRate);
        }
        catch (IllegalArgumentException ex)
        {
            userMessageHandler.sendMSG("Failed to set Bitrate",true);
        }
        catch (IllegalStateException ex)
        {
            userMessageHandler.sendMSG("Failed to set Bitrate",true);
        }

        try {
            mediaRecorder.setVideoFrameRate(this.currentVideoProfile.videoFrameRate);
        }
        catch (IllegalArgumentException ex)
        {
            userMessageHandler.sendMSG("Failed to set Framerate",true);
        }
        catch (IllegalStateException ex)
        {
            userMessageHandler.sendMSG("Failed to set Framerate",true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this.currentVideoProfile.level != -1 && this.currentVideoProfile.profile != -1)
        {
            mediaRecorder.setVideoEncodingProfileLevel(this.currentVideoProfile.profile,this.currentVideoProfile.level);
        }


         /*setCaptureRate

        Added in API level 11
        void setCaptureRate (double fps)
        Set video frame capture rate. This can be used to set a different video frame capture rate than the recorded video's playback rate.
        !!!!!! This method also sets the recording mode to time lapse.!!!!!
        In time lapse video recording, only video is recorded.
        Audio related parameters are ignored when a time lapse recording session starts, if an application sets them.*/
        //mediaRecorder.setCaptureRate((double)currentVideoProfile.videoFrameRate);

        mediaRecorder.setVideoSize(this.currentVideoProfile.videoFrameWidth, this.currentVideoProfile.videoFrameHeight);
        try {
            mediaRecorder.setVideoEncoder(this.currentVideoProfile.videoCodec);
        }
        catch (IllegalArgumentException ex)
        {
            mediaRecorder.reset();
            userMessageHandler.sendMSG("VideoCodec not Supported",false);
        }

        if (this.currentVideoProfile.videoCodec == MediaRecorder.VideoEncoder.HEVC)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                String prof = MediaCodecInfoParser.getHevcProfileString(currentVideoProfile.profile);
                if (prof.toLowerCase().contains("still"))
                {
                    setParameterExtra(mediaRecorder,"video-param-i-frames-interval=0");
                    //setParameterExtra(mediaRecorder,MediaFormat.KEY_BITRATE_MODE+"="+MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ);
                }
            }
        }

        if (inputSurface != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            mediaRecorder.setInputSurface(inputSurface);


        switch (this.currentVideoProfile.Mode)
        {
            case Normal:
            case Highspeed:
                if (this.currentVideoProfile.isAudioActive)
                {
                    try {
                        mediaRecorder.setAudioEncoder(this.currentVideoProfile.audioCodec);
                    }
                    catch (IllegalArgumentException ex)
                    {
                        mediaRecorder.reset();
                        userMessageHandler.sendMSG("AudioCodec not Supported",false);
                        return false;
                    }
                    mediaRecorder.setAudioChannels(this.currentVideoProfile.audioChannels);
                    mediaRecorder.setAudioEncodingBitRate(this.currentVideoProfile.audioBitRate);
                    mediaRecorder.setAudioSamplingRate(this.currentVideoProfile.audioSampleRate);
                }
                break;
            case Timelapse:
                float frame = 30;
                if (!TextUtils.isEmpty(settingsManager.get(SettingKeys.TIMELAPSE_FRAMES).get()))
                    frame = Float.parseFloat(settingsManager.get(SettingKeys.TIMELAPSE_FRAMES).get().replace(",", "."));
                else
                    settingsManager.get(SettingKeys.TIMELAPSE_FRAMES).set(String.valueOf(frame));
                mediaRecorder.setCaptureRate(frame);
                break;
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException ex) {
            Log.WriteEx(ex);
            userMessageHandler.sendMSG("Prepare failed: " + ex.getMessage(),false);
            return false;
        }
        catch (IllegalStateException ex)
        {
            Log.WriteEx(ex);
            userMessageHandler.sendMSG("Prepare failed: " + ex.getMessage(),false);
            return false;
        }
        return true;
    }

    private void setRecorderFilePath() {
        BaseHolder baseHolder = fileListController.getNewMovieFileHolder(recordingFile);
        try {
            baseHolder.setToMediaRecorder(mediaRecorder);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void release()
    {
        mediaRecorder.release();
    }

    public void reset(){
        mediaRecorder.reset();
    }

    public void setNextOutputFile(BaseHolder baseHolder)
    {

    }

    public int getAudioSource()
    {
        String as = settingsManager.get(SettingKeys.VIDEO_AUDIO_SOURCE).get();
        if (as.equals(FreedApplication.getStringFromRessources(R.string.video_audio_source_mic)))
            return MediaRecorder.AudioSource.MIC;
        if (as.equals(FreedApplication.getStringFromRessources(R.string.video_audio_source_camcorder)))
            return MediaRecorder.AudioSource.CAMCORDER;
        if (as.equals(FreedApplication.getStringFromRessources(R.string.video_audio_source_voice_recognition)))
            return MediaRecorder.AudioSource.VOICE_RECOGNITION;
        if (as.equals(FreedApplication.getStringFromRessources(R.string.video_audio_source_voice_communication)))
            return MediaRecorder.AudioSource.VOICE_COMMUNICATION;
        if (as.equals(FreedApplication.getStringFromRessources(R.string.video_audio_source_unprocessed)))
            return MediaRecorder.AudioSource.UNPROCESSED;
        return MediaRecorder.AudioSource.DEFAULT;
    }

    private void setParameterExtra(MediaRecorder mediaRecorder, String str) {
        Method method = null;
        try {
            method = RestrictionBypass.getDeclaredMethod(MediaRecorder.class, "setParameter",  String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (method != null) {
            try {
                method.setAccessible(true);
                method.invoke(mediaRecorder, str);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
