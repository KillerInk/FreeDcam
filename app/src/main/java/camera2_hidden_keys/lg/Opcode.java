package camera2_hidden_keys.lg;

public class Opcode {
    public final static int LG_VIDEO_HDR =  57408;
    public final static int LG_VIDEO_HDR_PREVIEW =  57360;
    public final static int LG_VIDEO_HDR_STEADY =  0xe068;
    public final static int LG_VIDEO_HDR_STEADY_60;

    public static final int OP_SETTING_120_FPS_RECORDING = 57345;
    public static final int OP_SETTING_240_FPS_RECORDING = 57347;
    public static final int OP_SETTING_60_FPS_RECORDING = 57346;
    public static final int OP_SETTING_EIS_LOOK_AHEAD = 57352;
    public static final int OP_SETTING_EIS_REAL_TIME = 57348;
    public static final int OP_SETTING_HDR10 = 57408;
    public static final int OP_SETTING_HDR_PREVIEW = 57360;
    public static final int OP_SETTING_VIDEO_HDR = 57376;
    public static final int OP_SHOT_MODE_360_PANORAMA = 58752;
    public static final int OP_SHOT_MODE_AI_CAMERA = 59520;
    public static final int OP_SHOT_MODE_CINE_VIDEO = 59136;
    public static final int OP_SHOT_MODE_DUAL_POP = 59264;
    public static final int OP_SHOT_MODE_FOOD = 57728;
    public static final int OP_SHOT_MODE_GRID_SHOT = 58240;
    public static final int OP_SHOT_MODE_GUIDE_SHOT = 58112;
    public static final int OP_SHOT_MODE_LG_LENS = 59776;
    public static final int OP_SHOT_MODE_MANUAL = 57600;
    public static final int OP_SHOT_MODE_MANUAL_VIDEO = 58496;
    public static final int OP_SHOT_MODE_MATCH_SHOT = 58368;
    public static final int OP_SHOT_MODE_NIGHT = 60928;
    public static final int OP_SHOT_MODE_NORMAL = 57344;
    public static final int OP_SHOT_MODE_NORMAL_VIDEO = 57472;
    public static final int OP_SHOT_MODE_OUT_FOCUS = 59648;
    public static final int OP_SHOT_MODE_PANORAMA = 58624;
    public static final int OP_SHOT_MODE_PENTA_SHOT = 60544;
    public static final int OP_SHOT_MODE_POPOUT = 59392;
    public static final int OP_SHOT_MODE_SLOW_MOTION = 59008;
    public static final int OP_SHOT_MODE_SNAP_MOVIE = 58880;
    public static final int OP_SHOT_MODE_SNAP_SHOT = 57984;
    public static final int OP_SHOT_MODE_STEADY_CAM_MODE = 60800;
    public static final int OP_SHOT_MODE_STUDIO_MODE = 60672;
    public static final int OP_SHOT_MODE_SUM_BINNING = 59904;
    public static final int OP_SHOT_MODE_SUM_BINNING_REC = 60032;
    public static final int OP_SHOT_MODE_SUM_BINNING_SENSOR = 60160;
    public static final int OP_SHOT_MODE_SUM_BINNING_SENSOR_REC = 60288;
    public static final int OP_SHOT_MODE_TIME_LAPSE = 57856;
    public static final int OP_SHOT_MODE_TRIPLE_SHOT = 60416;

    static
    {
        LG_VIDEO_HDR_STEADY_60 = OP_SETTING_60_FPS_RECORDING | OP_SETTING_EIS_REAL_TIME | OP_SETTING_VIDEO_HDR;
    }

}
