package freed.settings;


public enum Settings
{
    M_Brightness,
    M_Sharpness,
    M_Contrast,
    M_Saturation,
    M_ExposureCompensation,
    M_3D_Convergence, //seen on optimus 3d
    M_Focus,
    M_ExposureTime,
    M_Fnumber,
    M_Burst,
    M_Whitebalance,
    M_FX, //seen on zte devices
    M_ManualIso,
    M_Zoom,
    M_ProgramShift, //sony api
    M_PreviewZoom, //sony api
    M_ToneCurve,
    M_Aperture,

    ColorMode,
    ExposureMode,
    AE_PriorityMode,
    FlashMode,
    IsoMode,
    AntiBandingMode,
    WhiteBalanceMode,
    PictureSize,
    PictureFormat,
    HDRMode,
    JpegQuality,
    GuideList,
    ImagePostProcessing, //seen on optimus 3d
    PreviewSize,
    PreviewFPS,
    PreviewFormat,
    PreviewFpsRange,
    SceneMode,
    FocusMode,
    RedEye,
    LensShade, //vignett correction on cam1
    ZSL, //zero shutter lag
    SceneDetect,
    Denoise,

    PDAF,
    TNR, //temporal noise reduction
    TNR_V, //temporal noise reduction video
    RDI, //raw dump interface, needed for newer qcom socs to enable q3a on raw capture
    TruePotrait,
    ReFocus,
    SeeMore, // advanced wb correction?
    OptiZoom,
    ChromaFlash,

    DigitalImageStabilization,
    VideoStabilization,
    MemoryColorEnhancement,
    NightMode,
    NonZslManualMode, //used on htc devices to enable q3a on raw capture
    AE_Bracket,
    ExposureLock,
    CDS_Mode, //correlated double sampling
    HTCVideoMode,
    HTCVideoModeHSR,

    VideoProfiles,
    VideoSize,
    VideoHDR,
    VideoHighFramerate,
    LensFilter,
    HorizontLvl,
    Ae_TargetFPS,

    ContShootMode,
    ContShootModeSpeed,
    ObjectTracking,
    PostViewSize,
    Focuspeak,
    Module,
    ZoomSetting,
    NightOverlay,
    dualPrimaryCameraMode,

    EdgeMode,
    ColorCorrectionMode,
    HotPixelMode,
    ToneMapMode,
    black,
    shadows,
    midtones,
    highlights,
    white,
    toneCurveParameter,
    ControlMode,
    oismode,
    SdSaveLocation,
    locationParameter,

    IntervalDuration,
    IntervalShutterSleep,

    opcode,
    bayerformat,
    matrixChooser,
    tonemapChooser,
    scalePreview,

    rawPictureFormatSetting,
    openCamera1Legacy,
    useHuaweiCamera2Extension,
    support12bitRaw,
    useQcomFocus,
    dngSupportManuals,
    forceRawToDng,
    needRestartAfterCapture,
    orientationHack,
    selfTimer,
}
