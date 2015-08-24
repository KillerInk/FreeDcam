package com.troop.freedcam.sonyapi.parameters;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.parameters.AbstractParameterHandler;
import com.troop.freedcam.i_camera.parameters.CameraParametersEventHandler;
import com.troop.freedcam.sonyapi.CameraHolderSony;
import com.troop.freedcam.sonyapi.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.sonyapi.parameters.manual.ExposureCompManualParameterSony;
import com.troop.freedcam.sonyapi.parameters.manual.ProgramShiftManualSony;
import com.troop.freedcam.sonyapi.parameters.manual.WbCTManualSony;
import com.troop.freedcam.sonyapi.parameters.manual.ZoomManualSony;
import com.troop.freedcam.sonyapi.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.ContShootModeParameterSony;
import com.troop.freedcam.sonyapi.parameters.modes.ExposureModeSony;
import com.troop.freedcam.sonyapi.parameters.modes.FocusPeakSony;
import com.troop.freedcam.sonyapi.parameters.modes.I_SonyApi;
import com.troop.freedcam.sonyapi.parameters.modes.ObjectTrackingSony;
import com.troop.freedcam.sonyapi.parameters.modes.PictureFormatSony;
import com.troop.freedcam.sonyapi.parameters.modes.PictureSizeSony;
import com.troop.freedcam.sonyapi.parameters.modes.WhiteBalanceModeSony;
import com.troop.freedcam.sonyapi.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.sonyapi.sonystuff.SimpleStreamSurfaceView;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.ArrayList;
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

    public ParameterHandlerSony(AbstractCameraHolder cameraHolder, AppSettingsManager appSettingsManager, Handler uiHandler, SimpleStreamSurfaceView surfaceView)
    {
        super(cameraHolder, appSettingsManager, uiHandler);
        this.cameraHolder = (CameraHolderSony)cameraHolder;
        ParametersEventHandler = new CameraParametersEventHandler();
        parametersChangedList  = new ArrayList<I_SonyApi>();
        this.surfaceView = surfaceView;
    }

    public void SetCameraApiSet(final Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;

        Log.d(TAG, "Throw parametersChanged");
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
        PictureSize = new PictureSizeSony(uiHandler,"getStillSize", "setStillSize", "getAvailableStillSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureSize);

        PictureFormat = new PictureFormatSony(uiHandler,"getStillQuality", "setStillQuality", "getAvailableStillQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PictureFormat);

        FlashMode = new BaseModeParameterSony(uiHandler,"getFlashMode", "setFlashMode", "getAvailableFlashMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FlashMode);

        ExposureMode = new ExposureModeSony(uiHandler,"getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ExposureMode);

        ContShootMode = new ContShootModeParameterSony(uiHandler,"getContShootingMode", "setContShootingMode", "getAvailableContShootingMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootMode);

        ContShootModeSpeed = new BaseModeParameterSony(uiHandler,"getContShootingSpeed", "setContShootingSpeed", "getAvailableContShootingSpeed", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ContShootModeSpeed);

        FocusMode = new BaseModeParameterSony(uiHandler,"getFocusMode", "setFocusMode", "getAvailableFocusMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)FocusMode);

        ObjectTracking = new ObjectTrackingSony(uiHandler,"getTrackingFocus","setTrackingFocus", "getAvailableTrackingFocus", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)ObjectTracking);

        WhiteBalanceMode = new WhiteBalanceModeSony(uiHandler,"getWhiteBalance","setWhiteBalance", "getAvailableWhiteBalance", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) WhiteBalanceMode);

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

        PostViewSize = new BaseModeParameterSony(uiHandler, "getPostviewImageSize","setPostviewImageSize","getAvailablePostviewImageSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony)PostViewSize);

        VideoSize = new BaseModeParameterSony(uiHandler, "getMovieQuality", "setMovieQuality", "getAvailableMovieQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) VideoSize);

        Focuspeak = new FocusPeakSony(uiHandler, null,null,null,null, surfaceView);
        parametersChangedList.add((BaseModeParameterSony) Focuspeak);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (ParametersEventHandler != null) {
                    Log.d(TAG, "Throw ParametersHasLoaded");
                    ParametersEventHandler.ParametersHasLoaded();
                }
            }
        });

    }

    public void SetRemoteApi(SimpleRemoteApi api)
    {
        this.mRemoteApi = api;
        createParameters();
    }

    public void SetSupportedApiSet(Set<String> mSupportedApiSet)
    {
        this.mSupportedApiSet = mSupportedApiSet;

    }

    @Override
    public void SetParametersToCamera() {
        super.SetParametersToCamera();
    }

    @Override
    public void LockExposureAndWhiteBalance(boolean lock) {
        super.LockExposureAndWhiteBalance(lock);
    }
}
