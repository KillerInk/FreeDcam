package com.lge.media;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import freed.utils.VideoMediaProfile;

/**
 * Created by KillerInk on 08.12.2017.
 */

public class CamcorderProfileExRef {


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

    private static Class CLASS_camcorderProfile;
    private static Method METHOD_hasProfile;
    private static Method METHOD_get;
    private static Field FIELD_audioBitrate;
    private static Field FIELD_audioChannels;
    private static Field FIELD_audioCodec;
    private static Field FIELD_audioSampleRate;
    private static Field FIELD_duration;
    private static Field FIELD_fileFormat;
    private static Field FIELD_quality;
    private static Field FIELD_videoBitRate;
    private static Field FIELD_videoCodec;
    private static Field FIELD_videoFrameRate;
    private static Field FIELD_videoFrameHeight;
    private static Field FIELD_videoFrameWidth;
    static {
        try {
            CLASS_camcorderProfile = Class.forName("com.lge.media.CamcorderProfileEx");
            METHOD_hasProfile = CLASS_camcorderProfile.getDeclaredMethod("hasProfile",int.class,int.class);
            METHOD_get = CLASS_camcorderProfile.getDeclaredMethod("get",int.class,int.class);
            FIELD_audioBitrate = CLASS_camcorderProfile.getField("audioBitRate");
            FIELD_audioChannels = CLASS_camcorderProfile.getField("audioChannels");
            FIELD_audioCodec = CLASS_camcorderProfile.getField("audioCodec");
            FIELD_audioSampleRate = CLASS_camcorderProfile.getField("audioSampleRate");
            FIELD_duration = CLASS_camcorderProfile.getField("duration");
            FIELD_fileFormat = CLASS_camcorderProfile.getField("fileFormat");
            FIELD_quality = CLASS_camcorderProfile.getField("quality");
            FIELD_videoBitRate = CLASS_camcorderProfile.getField("videoBitRate");
            FIELD_videoCodec = CLASS_camcorderProfile.getField("videoCodec");
            FIELD_videoFrameRate = CLASS_camcorderProfile.getField("videoFrameRate");
            FIELD_videoFrameHeight = CLASS_camcorderProfile.getField("videoFrameHeight");
            FIELD_videoFrameWidth = CLASS_camcorderProfile.getField("videoFrameWidth");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }



    // public static boolean hasProfile(int var0) {
    public static boolean hasProfile(int cameraid, int profile)
    {
        try {
            return (boolean) METHOD_hasProfile.invoke(null, cameraid,profile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    //public static CamcorderProfileEx get(int var0, int var1) {
    public static Object get(int cameraid, int profile)
    {
        try {
            return METHOD_get.invoke(null, cameraid,profile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static VideoMediaProfile getProfile(int cameraid, int profile,String ProfileName, VideoMediaProfile.VideoMode mode, boolean isAudioActive)
    {
        Object ob = get(cameraid,profile);
        try {
            VideoMediaProfile profile1 = new VideoMediaProfile(
                    (int)FIELD_audioBitrate.get(ob),
                    (int)FIELD_audioChannels.get(ob),
                    (int)FIELD_audioCodec.get(ob),
                    (int)FIELD_audioSampleRate.get(ob),
                    (int)FIELD_duration.get(ob),
                    (int)FIELD_fileFormat.get(ob),
                    (int)FIELD_quality.get(ob),
                    (int)FIELD_videoBitRate.get(ob),
                    (int)FIELD_videoCodec.get(ob),
                    (int)FIELD_videoFrameRate.get(ob),
                    (int)FIELD_videoFrameHeight.get(ob),
                    (int)FIELD_videoFrameWidth.get(ob),
                    ProfileName,mode,isAudioActive

            );
            return profile1;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}

/*
public class CamcorderProfileEx {
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

    private CamcorderProfileEx(int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
        this.duration = var1;
        this.quality = var2;
        this.fileFormat = var3;
        this.videoCodec = var4;
        this.videoBitRate = var5;
        this.videoFrameRate = var6;
        this.videoFrameWidth = var7;
        this.videoFrameHeight = var8;
        this.audioCodec = var9;
        this.audioBitRate = var10;
        this.audioSampleRate = var11;
        this.audioChannels = var12;
    }

    public static CamcorderProfileEx get(int var0) {
        int var1 = Camera.getNumberOfCameras();
        CameraInfo var2 = new CameraInfo();

        for(int var3 = 0; var3 < var1; ++var3) {
            Camera.getCameraInfo(var3, var2);
            if(var2.facing == 0) {
                return get(var3, var0);
            }
        }

        return null;
    }

    public static CamcorderProfileEx get(int var0, int var1) {
        if(var1 >= 0 && var1 <= 17 || var1 >= 1000 && var1 <= 1017) {
            return native_get_camcorder_profile(var0, var1);
        } else {
            throw new IllegalArgumentException("Unsupported quality level: " + var1);
        }
    }

    public static boolean hasProfile(int var0) {
        int var1 = Camera.getNumberOfCameras();
        CameraInfo var2 = new CameraInfo();

        for(int var3 = 0; var3 < var1; ++var3) {
            Camera.getCameraInfo(var3, var2);
            if(var2.facing == 0) {
                return hasProfile(var3, var0);
            }
        }

        return false;
    }

    public static boolean hasProfile(int var0, int var1) {
        return native_has_camcorder_profile(var0, var1);
    }

    private static final native CamcorderProfileEx native_get_camcorder_profile(int var0, int var1);

    private static final native boolean native_has_camcorder_profile(int var0, int var1);

    private static final native void native_init();
}
 */
