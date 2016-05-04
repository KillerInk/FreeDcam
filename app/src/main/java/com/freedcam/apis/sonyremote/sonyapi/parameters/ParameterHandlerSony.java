package com.freedcam.apis.sonyremote.sonyapi.parameters;

import android.os.Handler;
import android.os.Looper;

import com.freedcam.apis.sonyremote.sonyapi.CameraHolderSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.manual.BaseManualParameterSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.manual.ExposureCompManualParameterSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.manual.PreviewZoomManual;
import com.freedcam.apis.sonyremote.sonyapi.parameters.manual.ProgramShiftManualSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.manual.WbCTManualSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.manual.ZoomManualSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.BaseModeParameterSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.ContShootModeParameterSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.FocusPeakSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.I_SonyApi;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.NightModeSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.ObjectTrackingSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.PictureFormatSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.PictureSizeSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.WhiteBalanceModeSony;
import com.freedcam.apis.sonyremote.sonyapi.parameters.modes.ZoomSettingSony;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleRemoteApi;
import com.freedcam.apis.sonyremote.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.utils.Logger;
import com.freedcam.apis.i_camera.AbstractCameraUiWrapper;
import com.freedcam.apis.i_camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.i_camera.parameters.ModuleParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by troop on 13.12.2014.
 */
public class ParameterHandlerSony extends AbstractParameterHandler
{
    private static String TAG = ParameterHandlerSony.class.getSimpleName();
    private CameraHolderSony cameraHolder;
    public SimpleRemoteApi mRemoteApi;
    public Set<String> mAvailableCameraApiSet;
    private Set<String> mSupportedApiSet;
    private List<I_SonyApi> parametersChangedList;
    private SimpleStreamSurfaceView surfaceView;
    private AbstractCameraUiWrapper wrapper;

    public ParameterHandlerSony(AbstractCameraUiWrapper cameraHolder, Handler uiHandler, SimpleStreamSurfaceView surfaceView)
    {
        super(cameraHolder.cameraHolder, uiHandler);
        this.cameraHolder = (CameraHolderSony)cameraHolder.cameraHolder;
        parametersChangedList  = new ArrayList<>();
        this.surfaceView = surfaceView;
        this.wrapper = cameraHolder;
    }

    public void SetCameraApiSet(final Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;

        Logger.d(TAG, "Throw parametersChanged");
        throwSonyApiChanged(mAvailableCameraApiSet);

    }

    private void throwSonyApiChanged(Set<String> mAvailableCameraApiSet) {
        for (int i = 0; i < parametersChangedList.size(); i++)
        {
            if (parametersChangedList.get(i) == null)
            {
                parametersChangedList.remove(i);
                i--;
            }
            else
                parametersChangedList.get(i).SonyApiChanged(mAvailableCameraApiSet);

        }
    }

    private void createParameters()
    {
        Module = new ModuleParameters(uiHandler, wrapper);
        PictureSize = new PictureSizeSony(uiHandler, "setStillSize", "getAvailableStillSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureSize);

        PictureFormat = new PictureFormatSony(uiHandler, "setStillQuality", "getAvailableStillQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureFormat);

        FlashMode = new BaseModeParameterSony(uiHandler,"getFlashMode", "setFlashMode", "getAvailableFlashMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FlashMode);

        ExposureMode = new BaseModeParameterSony(uiHandler,"getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ExposureMode);

        ContShootMode = new ContShootModeParameterSony(uiHandler, "setContShootingMode", "getAvailableContShootingMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootMode);

        ContShootModeSpeed = new BaseModeParameterSony(uiHandler,"getContShootingSpeed", "setContShootingSpeed", "getAvailableContShootingSpeed", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootModeSpeed);

        FocusMode = new BaseModeParameterSony(uiHandler,"getFocusMode", "setFocusMode", "getAvailableFocusMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FocusMode);

        ObjectTracking = new ObjectTrackingSony(uiHandler, "setTrackingFocus", "getAvailableTrackingFocus", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ObjectTracking);

        ZoomSetting = new ZoomSettingSony(uiHandler, "setZoomSetting", "getAvailableZoomSetting", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ZoomSetting);


        Zoom = new ZoomManualSony("","actZoom", this);
        parametersChangedList.add((ZoomManualSony)Zoom);
        ManualShutter = new BaseManualParameterSony("getShutterSpeed", "getAvailableShutterSpeed","setShutterSpeed", this);
        parametersChangedList.add((BaseManualParameterSony) ManualShutter);
        ManualFNumber = new BaseManualParameterSony("getFNumber","getAvailableFNumber","setFNumber",this);
        parametersChangedList.add((BaseManualParameterSony) ManualFNumber);
        ISOManual = new BaseManualParameterSony("getIsoSpeedRate", "getAvailableIsoSpeedRate","setIsoSpeedRate", this);
        parametersChangedList.add((BaseManualParameterSony) ISOManual);

        ManualExposure = new ExposureCompManualParameterSony("getAvailableExposureCompensation", "setExposureCompensation", this);
        parametersChangedList.add((BaseManualParameterSony) ManualExposure);

        ProgramShift = new ProgramShiftManualSony("getSupportedProgramShift", "setProgramShift", this);
        parametersChangedList.add((BaseManualParameterSony)ProgramShift);

        CCT = new WbCTManualSony("","", this);
        parametersChangedList.add((BaseManualParameterSony) CCT);

        WhiteBalanceMode = new WhiteBalanceModeSony(uiHandler, "setWhiteBalance", "getAvailableWhiteBalance", mRemoteApi, (WbCTManualSony)CCT);
        parametersChangedList.add((BaseModeParameterSony) WhiteBalanceMode);

        PostViewSize = new BaseModeParameterSony(uiHandler, "getPostviewImageSize","setPostviewImageSize","getAvailablePostviewImageSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PostViewSize);

        VideoSize = new BaseModeParameterSony(uiHandler, "getMovieQuality", "setMovieQuality", "getAvailableMovieQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) VideoSize);

        Focuspeak = new FocusPeakSony(uiHandler, null,null,null, surfaceView);
        parametersChangedList.add((BaseModeParameterSony) Focuspeak);

        NightMode = new NightModeSony(uiHandler, null,null,null,surfaceView);
        parametersChangedList.add((BaseModeParameterSony) NightMode);

        PreviewZoom = new PreviewZoomManual(surfaceView,this);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run()
            {
                    Logger.d(TAG, "Throw ParametersHasLoaded");
                    ParametersHasLoaded();
                }

        });

    }

    public void SetRemoteApi(SimpleRemoteApi api)
    {
        this.mRemoteApi = api;
        createParameters();
    }


    @Override
    public void SetParametersToCamera(HashMap<String, String> list) {

    }

    @Override
    public void LockExposureAndWhiteBalance(boolean lock) {

    }


}
