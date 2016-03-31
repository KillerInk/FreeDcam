package com.troop.freedcam.sonyapi.parameters;

import android.os.Handler;
import android.os.Looper;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.ModuleParameters;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.sonyapi.parameters.manual.ExposureCompManualParameterSony;
import com.troop.freedcam.sonyapi.parameters.manual.ProgramShiftManualSony;
import com.troop.freedcam.sonyapi.parameters.manual.WbCTManualSony;
import com.troop.freedcam.sonyapi.parameters.manual.ZoomManualSony;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.ContShootModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.FocusPeakSony;
import com.troop.freedcam.sonyapi.parameters.modes.I_SonyApi;
import com.troop.freedcam.sonyapi.parameters.modes.NightModeSony;
import com.troop.freedcam.sonyapi.parameters.modes.ObjectTrackingSony;
import com.troop.freedcam.sonyapi.parameters.modes.PictureFormatSony;
import com.troop.freedcam.sonyapi.parameters.modes.PictureSizeSony;
import com.troop.freedcam.sonyapi.parameters.modes.PreviewZoomSony;
import com.troop.freedcam.sonyapi.parameters.modes.WhiteBalanceModeSony;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;

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
    CameraHolderSony cameraHolder;
    public SimpleRemoteApi mRemoteApi;
    public Set<String> mAvailableCameraApiSet;
    private Set<String> mSupportedApiSet;
    List<I_SonyApi> parametersChangedList;
    SimpleStreamSurfaceView surfaceView;
    AbstractCameraUiWrapper wrapper;

    public ParameterHandlerSony(AbstractCameraUiWrapper cameraHolder, Handler uiHandler, SimpleStreamSurfaceView surfaceView)
    {
        super(cameraHolder.cameraHolder, uiHandler);
        this.cameraHolder = (CameraHolderSony)cameraHolder.cameraHolder;
        parametersChangedList  = new ArrayList<I_SonyApi>();
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
        PictureSize = new PictureSizeSony(uiHandler,"getStillSize", "setStillSize", "getAvailableStillSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureSize);

        PictureFormat = new PictureFormatSony(uiHandler,"getStillQuality", "setStillQuality", "getAvailableStillQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureFormat);

        FlashMode = new BaseModeParameterSony(uiHandler,"getFlashMode", "setFlashMode", "getAvailableFlashMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FlashMode);

        ExposureMode = new BaseModeParameterSony(uiHandler,"getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ExposureMode);

        ContShootMode = new ContShootModeParameterSony(uiHandler,"getContShootingMode", "setContShootingMode", "getAvailableContShootingMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootMode);

        ContShootModeSpeed = new BaseModeParameterSony(uiHandler,"getContShootingSpeed", "setContShootingSpeed", "getAvailableContShootingSpeed", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootModeSpeed);

        FocusMode = new BaseModeParameterSony(uiHandler,"getFocusMode", "setFocusMode", "getAvailableFocusMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FocusMode);

        ObjectTracking = new ObjectTrackingSony(uiHandler,"getTrackingFocus","setTrackingFocus", "getAvailableTrackingFocus", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ObjectTracking);



        Zoom = new ZoomManualSony("actZoom","","actZoom", this);
        parametersChangedList.add((ZoomManualSony)Zoom);
        ManualShutter = new BaseManualParameterSony("getShutterSpeed", "getAvailableShutterSpeed","setShutterSpeed", this);
        parametersChangedList.add((BaseManualParameterSony) ManualShutter);
        ManualFNumber = new BaseManualParameterSony("getFNumber","getAvailableFNumber","setFNumber",this);
        parametersChangedList.add((BaseManualParameterSony) ManualFNumber);
        ISOManual = new BaseManualParameterSony("getIsoSpeedRate", "getAvailableIsoSpeedRate","setIsoSpeedRate", this);
        parametersChangedList.add((BaseManualParameterSony) ISOManual);

        ManualExposure = new ExposureCompManualParameterSony("getExposureCompensation", "getAvailableExposureCompensation", "setExposureCompensation", this);
        parametersChangedList.add((BaseManualParameterSony) ManualExposure);

        ProgramShift = new ProgramShiftManualSony("", "getSupportedProgramShift", "setProgramShift", this);
        parametersChangedList.add((BaseManualParameterSony)ProgramShift);

        CCT = new WbCTManualSony("","","", this);
        parametersChangedList.add((BaseManualParameterSony) CCT);

        WhiteBalanceMode = new WhiteBalanceModeSony(uiHandler,"getWhiteBalance","setWhiteBalance", "getAvailableWhiteBalance", mRemoteApi, (WbCTManualSony)CCT);
        parametersChangedList.add((BaseModeParameterSony) WhiteBalanceMode);

        PostViewSize = new BaseModeParameterSony(uiHandler, "getPostviewImageSize","setPostviewImageSize","getAvailablePostviewImageSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PostViewSize);

        VideoSize = new BaseModeParameterSony(uiHandler, "getMovieQuality", "setMovieQuality", "getAvailableMovieQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) VideoSize);

        Focuspeak = new FocusPeakSony(uiHandler, null,null,null,null, surfaceView);
        parametersChangedList.add((BaseModeParameterSony) Focuspeak);

        NightMode = new NightModeSony(uiHandler,null,null,null,null,surfaceView);
        parametersChangedList.add((BaseModeParameterSony) NightMode);

        PreviewZoom = new PreviewZoomSony(uiHandler, surfaceView);

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
