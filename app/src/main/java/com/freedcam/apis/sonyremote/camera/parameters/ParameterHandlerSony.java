package com.freedcam.apis.sonyremote.camera.parameters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.parameters.AbstractParameterHandler;
import com.freedcam.apis.basecamera.camera.parameters.modes.ModuleParameters;
import com.freedcam.apis.sonyremote.camera.CameraHolderSony;
import com.freedcam.apis.sonyremote.camera.CameraUiWrapperSony;
import com.freedcam.apis.sonyremote.camera.parameters.manual.BaseManualParameterSony;
import com.freedcam.apis.sonyremote.camera.parameters.manual.ExposureCompManualParameterSony;
import com.freedcam.apis.sonyremote.camera.parameters.manual.PreviewZoomManual;
import com.freedcam.apis.sonyremote.camera.parameters.manual.ProgramShiftManualSony;
import com.freedcam.apis.sonyremote.camera.parameters.manual.WbCTManualSony;
import com.freedcam.apis.sonyremote.camera.parameters.manual.ZoomManualSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.BaseModeParameterSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.ContShootModeParameterSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.FocusPeakSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.I_SonyApi;
import com.freedcam.apis.sonyremote.camera.parameters.modes.NightModeSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.ObjectTrackingSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.PictureFormatSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.PictureSizeSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.WhiteBalanceModeSony;
import com.freedcam.apis.sonyremote.camera.parameters.modes.ZoomSettingSony;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleRemoteApi;
import com.freedcam.apis.sonyremote.camera.sonystuff.SimpleStreamSurfaceView;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;

import java.util.ArrayList;
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
    private CameraUiWrapperSony wrapper;

    public ParameterHandlerSony(CameraUiWrapperSony wrapper, SimpleStreamSurfaceView surfaceView, Context context, AppSettingsManager appSettingsManager)
    {
        super(wrapper.cameraHolder,context,appSettingsManager);
        this.cameraHolder = wrapper.cameraHolder;
        parametersChangedList  = new ArrayList<>();
        this.surfaceView = surfaceView;
        this.wrapper = wrapper;
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
        Module = new ModuleParameters(wrapper,appSettingsManager);
        PictureSize = new PictureSizeSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureSize);

        PictureFormat = new PictureFormatSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureFormat);

        FlashMode = new BaseModeParameterSony("getFlashMode", "setFlashMode", "getAvailableFlashMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FlashMode);

        ExposureMode = new BaseModeParameterSony("getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ExposureMode);

        ContShootMode = new ContShootModeParameterSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootMode);

        ContShootModeSpeed = new BaseModeParameterSony("getContShootingSpeed", "setContShootingSpeed", "getAvailableContShootingSpeed", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootModeSpeed);

        FocusMode = new BaseModeParameterSony("getFocusMode", "setFocusMode", "getAvailableFocusMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FocusMode);

        ObjectTracking = new ObjectTrackingSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ObjectTracking);

        ZoomSetting = new ZoomSettingSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ZoomSetting);


        Zoom = new ZoomManualSony(this);
        parametersChangedList.add((ZoomManualSony)Zoom);
        ManualShutter = new BaseManualParameterSony("getShutterSpeed", "getAvailableShutterSpeed","setShutterSpeed", this);
        parametersChangedList.add((BaseManualParameterSony) ManualShutter);
        ManualFNumber = new BaseManualParameterSony("getFNumber","getAvailableFNumber","setFNumber",this);
        parametersChangedList.add((BaseManualParameterSony) ManualFNumber);
        ManualIso = new BaseManualParameterSony("getIsoSpeedRate", "getAvailableIsoSpeedRate","setIsoSpeedRate", this);
        parametersChangedList.add((BaseManualParameterSony) ManualIso);

        ManualExposure = new ExposureCompManualParameterSony(this);
        parametersChangedList.add((BaseManualParameterSony) ManualExposure);

        ProgramShift = new ProgramShiftManualSony(this);
        parametersChangedList.add((BaseManualParameterSony)ProgramShift);

        CCT = new WbCTManualSony(this);
        parametersChangedList.add((BaseManualParameterSony) CCT);

        WhiteBalanceMode = new WhiteBalanceModeSony(mRemoteApi, (WbCTManualSony)CCT);
        parametersChangedList.add((BaseModeParameterSony) WhiteBalanceMode);

        PostViewSize = new BaseModeParameterSony("getPostviewImageSize","setPostviewImageSize","getAvailablePostviewImageSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PostViewSize);

        VideoSize = new BaseModeParameterSony("getMovieQuality", "setMovieQuality", "getAvailableMovieQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) VideoSize);

        Focuspeak = new FocusPeakSony(surfaceView);
        parametersChangedList.add((BaseModeParameterSony) Focuspeak);

        NightMode = new NightModeSony(surfaceView);
        parametersChangedList.add((BaseModeParameterSony) NightMode);

        PreviewZoom = new PreviewZoomManual(surfaceView,this);

        uiHandler.post(new Runnable() {
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
    public void LockExposureAndWhiteBalance(boolean lock) {

    }


}
