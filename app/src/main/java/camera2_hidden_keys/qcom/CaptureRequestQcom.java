package camera2_hidden_keys.qcom;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.AbstractCaptureRequest;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CaptureRequestQcom extends AbstractCaptureRequest {
    public static final CaptureRequest.Key<Byte> eis_mode;
    public static final CaptureRequest.Key<Byte> eis_realtime_mode;
    public static final CaptureRequest.Key<Integer> sharpness;
    public static final CaptureRequest.Key<Integer> saturation;
    public static final CaptureRequest.Key MFNR;
    public static final CaptureRequest.Key AWB_CCT;
    public static final CaptureRequest.Key AWB_Range;

    public static final CaptureRequest.Key METERING_MODE;
    public static final CaptureRequest.Key<Byte> HDR10_VIDEO;
    public static final byte HDR10_VIDEO_PQ = 2;
    public static final byte HDR10_VIDEO_HLG = 1;
    public static final byte HDR10_VIDEO_OFF = 0;

    public static final CaptureRequest.Key QCOM_VIDEO_HDR_ENABLED;
    public static final int QCOM_VIDEO_HDR_ON = 1;
    public static final int QCOM_VIDEO_HDR_OFF = 0;

    public static final CaptureRequest.Key SUPERNIGHT_RAW_ENABLED;

    public static final CaptureRequest.Key<Byte> CINEMATIC_VIDEO_ENABLED;
    public static final Byte CINEMATIC_VIDEO_ON = (byte)1;
    public static final Byte CINEMATIC_VIDEO_OFF= (byte)0;








    static {
        eis_mode = getKeyType("org.quic.camera.eis3enable.EISV3Enable", Byte.TYPE);
        eis_realtime_mode = getKeyType("org.quic.camera.eisrealtime.Enabled", Byte.TYPE);
        sharpness = getKeyType("org.codeaurora.qcamera3.sharpness.strength", Integer.TYPE);
        saturation = getKeyType("org.codeaurora.qcamera3.saturation.use_saturation", Integer.TYPE);
        //Multiframe Temporal FIltering Set Noise Reduction to High Qiuality to Enable
        MFNR = getKeyType("org.quic.camera.CustomNoiseReduction.CustomNoiseReduction", byte.class);
        //CCT TEMP
        AWB_CCT = getKeyType("org.codeaurora.qcamera3.manualWB.color_temperature", Integer.class);
        AWB_Range = getKeyType("org.codeaurora.qcamera3.manualWB.color_temperature_range", Integer.class);

        METERING_MODE = getKeyType("org.codeaurora.qcamera3.exposure_metering.exposure_metering_mode", Integer.class);
        HDR10_VIDEO = getKeyType("org.quic.camera2.streamconfigs.HDRVideoMode", Byte.class);
        QCOM_VIDEO_HDR_ENABLED = getKeyType("org.codeaurora.qcamera3.sessionParameters.EnableMFHDR", Integer.class);
        SUPERNIGHT_RAW_ENABLED = getKeyType("xiaomi.supernight.raw.enabled", Boolean.class);
        CINEMATIC_VIDEO_ENABLED = getKeyType("xiaomi.pro.video.movie.enabled", Byte.class);
    }



}
