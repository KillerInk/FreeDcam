package com.lge.media;

import android.media.MediaRecorder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by KillerInk on 08.12.2017.
 */

public class MediaRecorderExRef
{

    private Class CLASS_MEDIARECORDEREX;
    private MediaRecorder mediaRecorder;

    //public MediaRecorderEx() {

    public MediaRecorderExRef()
    {
        try {
            CLASS_MEDIARECORDEREX = Class.forName("com.lge.media.MediaRecorderEx");
            Constructor<?>[] ctors = CLASS_MEDIARECORDEREX.getDeclaredConstructors();
            Constructor<?> constructor = (Constructor<?>) ctors[0];
            mediaRecorder = (MediaRecorder) constructor.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public MediaRecorder getMediaRecorder()
    {
        return mediaRecorder;
    }
}


/*

package com.lge.media;

import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;
import java.io.FileDescriptor;
import java.lang.reflect.Field;

public class MediaRecorderEx extends MediaRecorder {
    public static final int MEDIA_RECORDER_TARS_STATE_INFO = 999;
    public static final int OUTPUTFORMAT_AAC_TARS = 99;
    private static final String TAG = "LGMediaRecorder";

    static {
        System.loadLibrary("hook_jni");
    }

    public MediaRecorderEx() {
        Log.e("LGMediaRecorder", "MediaRecorder constructor");
    }

    private native void native_audiozoom() throws IllegalStateException;

    private native void native_changeMaxFileSize(long var1) throws IllegalStateException;

    private native boolean native_isRecording() throws IllegalStateException;

    private native void native_pause() throws IllegalStateException;

    private native void native_resume() throws IllegalStateException;

    private native void native_setAudioZoomExceptionCase() throws IllegalStateException;

    private native void native_setOutputFileFD(FileDescriptor var1, long var2, long var4) throws IllegalStateException;

    private native void native_setParameter(String var1) throws IllegalStateException;

    private native void native_setRecordAngle(int var1) throws IllegalStateException;

    private native void native_setRecordZoomEnable(int var1, int var2) throws IllegalStateException;

    public void changeMaxFileSize(long var1) throws IllegalStateException {
        Log.d("LGMediaRecorder", "changeMaxFileSize : " + var1);
        this.native_changeMaxFileSize(var1);
    }

    protected void finalize() {
        super.finalize();
    }

    public boolean isRecording() throws IllegalStateException {
        boolean var1 = this.native_isRecording();
        Log.d("LGMediaRecorder", "isRecording = " + var1);
        return var1;
    }

    public void pause() throws IllegalStateException {
        this.native_pause();
        Log.w("LGMediaRecorder", "mediarecorder pause");
    }

    public void resume() throws IllegalStateException {
        this.native_resume();
        Log.w("LGMediaRecorder", "mediarecorder resume");
    }

    public void setAudioZoomExceptionCase() {
        this.native_setAudioZoomExceptionCase();
        Log.v("LGMediaRecorder", "MediaRecorder setAudioZoomExceptionCase");
    }

    public void setAudioZooming() {
        this.native_audiozoom();
        Log.v("LGMediaRecorder", "MediaRecorder setAudioZooming");
    }

    public void setOutputFileFD(FileDescriptor var1) throws IllegalStateException {
        if(Build.IS_DEBUGGABLE) {
            Log.d("LGMediaRecorder", "mediarecorder setOutputFileFD:" + var1);
        }

        this.native_setOutputFileFD(var1, 0L, 0L);
    }

    public void setParameter(String var1) throws IllegalStateException {
        if(Build.IS_DEBUGGABLE) {
            Log.d("LGMediaRecorder", "mediarecorder setParameter:" + var1);
        }

        this.native_setParameter(var1);
    }

    public void setProfile(CamcorderProfileEx var1) {
        this.setOutputFormat(var1.fileFormat);
        this.setVideoFrameRate(var1.videoFrameRate);
        this.setVideoSize(var1.videoFrameWidth, var1.videoFrameHeight);
        this.setVideoEncodingBitRate(var1.videoBitRate);
        this.setVideoEncoder(var1.videoCodec);
        if((var1.quality < 1000 || var1.quality > 1007) && var1.audioCodec >= 0) {
            this.setAudioEncodingBitRate(var1.audioBitRate);
            this.setAudioChannels(var1.audioChannels);
            this.setAudioSamplingRate(var1.audioSampleRate);
            this.setAudioEncoder(var1.audioCodec);
        }
    }

    public void setRecordAngle(int var1) {
        this.native_setRecordAngle(var1);
        Log.v("LGMediaRecorder", "MediaRecorder setRecordAngle");
    }

    public void setRecordZoomEnable(int var1, int var2) {
        this.native_setRecordZoomEnable(var1, var2);
        Log.v("LGMediaRecorder", "MediaRecorder setRecordZoomEnable");
    }

    public static class LGAudioSource {
        public static int FM_RX;

        static {
            try {
                Field var2 = Class.forName("android.media.MediaRecorder$AudioSource").getField("FM_RX");
                var2.setAccessible(true);
                FM_RX = Integer.parseInt(var2.get((Object)null).toString());
                Log.d("LGMediaRecorder", "FM_RX : " + FM_RX);
            } catch (Exception var3) {
                Log.d("LGMediaRecorder", "FM_RX Exception : " + var3.toString());
            }
        }

        public LGAudioSource() {
        }
    }
}

 */
