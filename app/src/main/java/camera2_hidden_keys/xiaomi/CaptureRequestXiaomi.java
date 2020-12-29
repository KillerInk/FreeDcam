package camera2_hidden_keys.xiaomi;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import camera2_hidden_keys.AbstractCaptureRequest;

@SuppressWarnings("unchecked")
@TargetApi(Build.VERSION_CODES.N)
public class CaptureRequestXiaomi extends AbstractCaptureRequest {

    /*public static final CaptureRequest.Key<Boolean> SUPER_NIGHT;

    public static final CaptureRequest.Key<String> BOKEH_F_NUMBER;

    public static final CaptureRequest.Key<Boolean> SUPER_RESOLUTION;

    public static final CaptureRequest.Key<Integer> BURST_CAPTURE_HINT;

    public static final CaptureRequest.Key<Integer> BURST_FPS;

    public static final CaptureRequest.Key<Byte> FIX_CHROMATIC_ABBERATION;

    public static final CaptureRequest.Key<Boolean> FRONT_MIRROR;

    public static final CaptureRequest.Key<Boolean> HDR_CHECKER_ENABLED;
    */

    public static final CaptureRequest.Key<Boolean> HDR_ENABLED;
    /*
    public static final CaptureRequest.Key<Boolean> HHT_ENABLED;

    public static final CaptureRequest.Key<Boolean> DIRTY_LENS_DETECTOR;

    public static final CaptureRequest.Key<Boolean> MFNR_BOKEH;

    public static final CaptureRequest.Key<Boolean> MFNR_ENABLED;

    public static final CaptureRequest.Key<Byte> LENS_DISTOTION_CORRECTION_LEVEL;

    public static final CaptureRequest.Key<Boolean> PARALLEL_ENABLED;

    public static final CaptureRequest.Key<Integer> POTRAIT_LIGHTNING;

    public static final CaptureRequest.Key<Byte> SNAP_SHOT_TORCH;

    public static final CaptureRequest.Key<byte[]> PARALLEL_PATH;

    public static final CaptureRequest.Key<Byte> ST_FAST_ZOOM;

    public static final CaptureRequest.Key<Boolean> SW_MFNR_ENABLED;

    public static final CaptureRequest.Key<Byte> ULTRA_WIDE_CORRECTION_LEVEL;

    public static final CaptureRequest.Key<String> WATERMARK_APPLIEDTYPE;

    public static final CaptureRequest.Key<String> WATERMARK_FACE;

    public static final CaptureRequest.Key<String> WATERMARK_TIME;*/

    public static final CaptureRequest.Key<Byte> QCFA_ENABLED;

    public static final CaptureRequest.Key<Boolean> CONTROL_ENABLE_REMOSAIC;

    public static final CaptureRequest.Key<Byte> PRO_VIDEO_LOG_ENABLED;


    static {
        /*BOKEH_F_NUMBER = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        SUPER_NIGHT = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        SUPER_RESOLUTION = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        BURST_CAPTURE_HINT = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        BURST_FPS = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        FIX_CHROMATIC_ABBERATION = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        FRONT_MIRROR = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        HDR_CHECKER_ENABLED = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);*/
        HDR_ENABLED = getKeyType("xiaomi.hdr.enabled", Boolean.TYPE);
       /* HHT_ENABLED = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        DIRTY_LENS_DETECTOR = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        MFNR_BOKEH = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        MFNR_ENABLED = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        LENS_DISTOTION_CORRECTION_LEVEL = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        PARALLEL_ENABLED = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        PARALLEL_PATH = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        POTRAIT_LIGHTNING = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        SNAP_SHOT_TORCH = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        ST_FAST_ZOOM = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        SW_MFNR_ENABLED = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        ULTRA_WIDE_CORRECTION_LEVEL = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        WATERMARK_APPLIEDTYPE = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        WATERMARK_FACE = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);
        WATERMARK_TIME = getKeyType("com.huawei.capture.metadata.faceBeautyLevel", String.class);*/
        QCFA_ENABLED = getKeyType("xiaomi.quadcfa.enabled", Byte.class);
        CONTROL_ENABLE_REMOSAIC = getKeyType("xiaomi.remosaic.enabled", Boolean.class);
        PRO_VIDEO_LOG_ENABLED = getKeyType("xiaomi.pro.video.log.enabled", Byte.class);
    }
}
