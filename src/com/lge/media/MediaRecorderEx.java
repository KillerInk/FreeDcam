package com.lge.media;

import android.media.*;
import android.util.*;
import java.io.*;
import android.os.*;
import java.lang.reflect.*;

public class MediaRecorderEx extends MediaRecorder
{
    public static final int MEDIA_RECORDER_TARS_STATE_INFO = 999;
    public static final int OUTPUTFORMAT_AAC_TARS = 99;
    private static final String TAG = "LGMediaRecorder";

    static {
        System.loadLibrary("hook_jni");
    }

    public MediaRecorderEx() {
        super();
        Log.e("LGMediaRecorder", "MediaRecorder constructor");
    }

    private native void native_audiozoom() throws IllegalStateException;

    private native void native_changeMaxFileSize(final long p0) throws IllegalStateException;

    private native boolean native_isRecording() throws IllegalStateException;

    private native void native_pause() throws IllegalStateException;

    private native void native_resume() throws IllegalStateException;

    private native void native_setAudioZoomExceptionCase() throws IllegalStateException;

    private native void native_setOutputFileFD(final FileDescriptor p0, final long p1, final long p2) throws IllegalStateException;

    private native void native_setParameter(final String p0) throws IllegalStateException;

    private native void native_setRecordAngle(final int p0) throws IllegalStateException;

    private native void native_setRecordZoomEnable(final int p0, final int p1) throws IllegalStateException;

    public void changeMaxFileSize(final long n) throws IllegalStateException {
        Log.d("LGMediaRecorder", "changeMaxFileSize : " + n);
        this.native_changeMaxFileSize(n);
    }

    protected void finalize() {
        super.finalize();
    }

    public boolean isRecording() throws IllegalStateException {
        final boolean native_isRecording = this.native_isRecording();
        Log.d("LGMediaRecorder", "isRecording = " + native_isRecording);
        return native_isRecording;
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

    public void setOutputFileFD(final FileDescriptor fileDescriptor) throws IllegalStateException {

            Log.d("LGMediaRecorder", "mediarecorder setOutputFileFD:" + fileDescriptor);

        this.native_setOutputFileFD(fileDescriptor, 0L, 0L);
    }

    public void setParameter(final String s) throws IllegalStateException {

            Log.d("LGMediaRecorder", "mediarecorder setParameter:" + s);

        this.native_setParameter(s);
    }

    public void setProfile(final CamcorderProfileEx camcorderProfileEx) {
        this.setOutputFormat(camcorderProfileEx.fileFormat);
        this.setVideoFrameRate(camcorderProfileEx.videoFrameRate);
        this.setVideoSize(camcorderProfileEx.videoFrameWidth, camcorderProfileEx.videoFrameHeight);
        this.setVideoEncodingBitRate(camcorderProfileEx.videoBitRate);
        this.setVideoEncoder(camcorderProfileEx.videoCodec);
        if ((camcorderProfileEx.quality < 1000 || camcorderProfileEx.quality > 1007) && camcorderProfileEx.audioCodec >= 0) {
            this.setAudioEncodingBitRate(camcorderProfileEx.audioBitRate);
            this.setAudioChannels(camcorderProfileEx.audioChannels);
            this.setAudioSamplingRate(camcorderProfileEx.audioSampleRate);
            this.setAudioEncoder(camcorderProfileEx.audioCodec);
        }
    }

    public void setRecordAngle(final int n) {
        this.native_setRecordAngle(n);
        Log.v("LGMediaRecorder", "MediaRecorder setRecordAngle");
    }

    public void setRecordZoomEnable(final int n, final int n2) {
        this.native_setRecordZoomEnable(n, n2);
        Log.v("LGMediaRecorder", "MediaRecorder setRecordZoomEnable");
    }

    public static class LGAudioSource
    {
        public static int FM_RX;

        static {
            try {
                final Field field = Class.forName("android.media.MediaRecorder$AudioSource").getField("FM_RX");
                field.setAccessible(true);
                LGAudioSource.FM_RX = Integer.parseInt(field.get(null).toString());
                Log.d("LGMediaRecorder", "FM_RX : " + LGAudioSource.FM_RX);
            }
            catch (Exception ex) {
                Log.d("LGMediaRecorder", "FM_RX Exception : " + ex.toString());
            }
        }
    }
}
