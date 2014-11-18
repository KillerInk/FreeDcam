package com.lge.media;

import android.hardware.*;

public class CamcorderProfileEx
{
    public static final int QUALITY_1080P = 6;
    public static final int QUALITY_480P = 4;
    public static final int QUALITY_4kDCI = 13;
    public static final int QUALITY_4kUHD = 12;
    public static final int QUALITY_720P = 5;
    public static final int QUALITY_960P = 14;
    public static final int QUALITY_CIF = 3;
    public static final int QUALITY_FWVGA = 8;
    public static final int QUALITY_HFR1080P = 16;
    public static final int QUALITY_HFR720P = 17;
    public static final int QUALITY_HIGH = 1;
    private static final int QUALITY_LIST_END = 17;
    private static final int QUALITY_LIST_START = 0;
    public static final int QUALITY_LOW = 0;
    public static final int QUALITY_QCIF = 2;
    public static final int QUALITY_QVGA = 7;
    public static final int QUALITY_SVGA = 15;
    public static final int QUALITY_TIME_LAPSE_1080P = 1006;
    public static final int QUALITY_TIME_LAPSE_480P = 1004;
    public static final int QUALITY_TIME_LAPSE_4kDCI = 1013;
    public static final int QUALITY_TIME_LAPSE_4kUHD = 1012;
    public static final int QUALITY_TIME_LAPSE_720P = 1005;
    public static final int QUALITY_TIME_LAPSE_960P = 1014;
    public static final int QUALITY_TIME_LAPSE_CIF = 1003;
    public static final int QUALITY_TIME_LAPSE_FWVGA = 1008;
    public static final int QUALITY_TIME_LAPSE_HFR1080P = 1016;
    public static final int QUALITY_TIME_LAPSE_HFR720P = 1017;
    public static final int QUALITY_TIME_LAPSE_HIGH = 1001;
    private static final int QUALITY_TIME_LAPSE_LIST_END = 1017;
    private static final int QUALITY_TIME_LAPSE_LIST_START = 1000;
    public static final int QUALITY_TIME_LAPSE_LOW = 1000;
    public static final int QUALITY_TIME_LAPSE_QCIF = 1002;
    public static final int QUALITY_TIME_LAPSE_QVGA = 1007;
    public static final int QUALITY_TIME_LAPSE_SVGA = 1015;
    public static final int QUALITY_TIME_LAPSE_VGA = 1010;
    public static final int QUALITY_TIME_LAPSE_WQVGA = 1011;
    public static final int QUALITY_TIME_LAPSE_WVGA = 1009;
    public static final int QUALITY_TIME_LAPSE_hfr1080P = 1016;
    public static final int QUALITY_VGA = 10;
    public static final int QUALITY_WQVGA = 11;
    public static final int QUALITY_WVGA = 9;
    public static final int QUALITY_hfr1080P = 16;
    public int audioBitRate;
    public int audioChannels;
    public int audioCodec;
    public int audioSampleRate;
    public int duration;
    public int fileFormat;
    public int quality;
    public int videoBitRate;
    public int videoCodec;
    public int videoFrameHeight;
    public int videoFrameRate;
    public int videoFrameWidth;

    static {
        System.loadLibrary("hook_jni");
        native_init();
    }

    private CamcorderProfileEx(final int duration, final int quality, final int fileFormat, final int videoCodec, final int videoBitRate, final int videoFrameRate, final int videoFrameWidth, final int videoFrameHeight, final int audioCodec, final int audioBitRate, final int audioSampleRate, final int audioChannels) {
        super();
        this.duration = duration;
        this.quality = quality;
        this.fileFormat = fileFormat;
        this.videoCodec = videoCodec;
        this.videoBitRate = videoBitRate;
        this.videoFrameRate = videoFrameRate;
        this.videoFrameWidth = videoFrameWidth;
        this.videoFrameHeight = videoFrameHeight;
        this.audioCodec = audioCodec;
        this.audioBitRate = audioBitRate;
        this.audioSampleRate = audioSampleRate;
        this.audioChannels = audioChannels;
    }

    public static CamcorderProfileEx get(final int n) {
        final int numberOfCameras = Camera.getNumberOfCameras();
        final Camera.CameraInfo camera$CameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; ++i) {
            Camera.getCameraInfo(i, camera$CameraInfo);
            if (camera$CameraInfo.facing == 0) {
                return get(i, n);
            }
        }
        return null;
    }

    public static CamcorderProfileEx get(final int n, final int n2) {
        if ((n2 >= 0 && n2 <= 17) || (n2 >= 1000 && n2 <= 1017)) {
            return native_get_camcorder_profile(n, n2);
        }
        throw new IllegalArgumentException("Unsupported quality level: " + n2);
    }

    public static boolean hasProfile(final int n) {
        final int numberOfCameras = Camera.getNumberOfCameras();
        final Camera.CameraInfo camera$CameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; ++i) {
            Camera.getCameraInfo(i, camera$CameraInfo);
            if (camera$CameraInfo.facing == 0) {
                return hasProfile(i, n);
            }
        }
        return false;
    }

    public static boolean hasProfile(final int n, final int n2) {
        return native_has_camcorder_profile(n, n2);
    }

    private static final native CamcorderProfileEx native_get_camcorder_profile(final int p0, final int p1);

    private static final native boolean native_has_camcorder_profile(final int p0, final int p1);

    private static final native void native_init();
}
