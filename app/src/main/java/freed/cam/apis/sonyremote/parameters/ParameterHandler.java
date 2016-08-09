/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package freed.cam.apis.sonyremote.parameters;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.FocusRect;
import freed.cam.apis.basecamera.parameters.AbstractParameterHandler;
import freed.cam.apis.basecamera.parameters.modes.ModuleParameters;
import freed.cam.apis.camera1.parameters.device.I_Device;
import freed.cam.apis.sonyremote.parameters.manual.BaseManualParameterSony;
import freed.cam.apis.sonyremote.parameters.manual.ExposureCompManualParameterSony;
import freed.cam.apis.sonyremote.parameters.manual.PreviewZoomManual;
import freed.cam.apis.sonyremote.parameters.manual.ProgramShiftManualSony;
import freed.cam.apis.sonyremote.parameters.manual.WbCTManualSony;
import freed.cam.apis.sonyremote.parameters.manual.ZoomManualSony;
import freed.cam.apis.sonyremote.parameters.modes.BaseModeParameterSony;
import freed.cam.apis.sonyremote.parameters.modes.ContShootModeParameterSony;
import freed.cam.apis.sonyremote.parameters.modes.FocusModeSony;
import freed.cam.apis.sonyremote.parameters.modes.FocusPeakSony;
import freed.cam.apis.sonyremote.parameters.modes.I_SonyApi;
import freed.cam.apis.sonyremote.parameters.modes.NightModeSony;
import freed.cam.apis.sonyremote.parameters.modes.ObjectTrackingSony;
import freed.cam.apis.sonyremote.parameters.modes.PictureFormatSony;
import freed.cam.apis.sonyremote.parameters.modes.PictureSizeSony;
import freed.cam.apis.sonyremote.parameters.modes.WhiteBalanceModeSony;
import freed.cam.apis.sonyremote.parameters.modes.ZoomSettingSony;
import freed.cam.apis.sonyremote.sonystuff.SimpleRemoteApi;
import freed.cam.apis.sonyremote.sonystuff.SimpleStreamSurfaceView;
import freed.utils.Logger;

/**
 * Created by troop on 13.12.2014.
 */
public class ParameterHandler extends AbstractParameterHandler
{
    private final String TAG = ParameterHandler.class.getSimpleName();
    public SimpleRemoteApi mRemoteApi;
    public Set<String> mAvailableCameraApiSet;
    private final List<I_SonyApi> parametersChangedList;
    private final SimpleStreamSurfaceView surfaceView;
    private final CameraWrapperInterface cameraUiWrapper;


    public ParameterHandler(CameraWrapperInterface cameraUiWrapper, SimpleStreamSurfaceView surfaceView, Context context)
    {
        super(cameraUiWrapper);
        parametersChangedList = new ArrayList<>();
        this.surfaceView = surfaceView;
        this.cameraUiWrapper =cameraUiWrapper;
    }

    public void SetCameraApiSet(Set<String> mAvailableCameraApiSet)
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
        Module = new ModuleParameters(cameraUiWrapper, appSettingsManager);
        PictureSize = new PictureSizeSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) PictureSize);

        PictureFormat = new PictureFormatSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) PictureFormat);

        FlashMode = new BaseModeParameterSony("getFlashMode", "setFlashMode", "getAvailableFlashMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) FlashMode);

        ExposureMode = new BaseModeParameterSony("getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ExposureMode);

        ContShootMode = new ContShootModeParameterSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ContShootMode);

        ContShootModeSpeed = new BaseModeParameterSony("getContShootingSpeed", "setContShootingSpeed", "getAvailableContShootingSpeed", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ContShootModeSpeed);

        FocusMode = new FocusModeSony("getFocusMode", "setFocusMode", "getAvailableFocusMode", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) FocusMode);

        ObjectTracking = new ObjectTrackingSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ObjectTracking);

        ZoomSetting = new ZoomSettingSony(mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) ZoomSetting);


        Zoom = new ZoomManualSony(cameraUiWrapper);
        parametersChangedList.add((ZoomManualSony) Zoom);
        ManualShutter = new BaseManualParameterSony("getShutterSpeed", "getAvailableShutterSpeed","setShutterSpeed", cameraUiWrapper);
        parametersChangedList.add((BaseManualParameterSony) ManualShutter);
        ManualFNumber = new BaseManualParameterSony("getFNumber","getAvailableFNumber","setFNumber", cameraUiWrapper);
        parametersChangedList.add((BaseManualParameterSony) ManualFNumber);
        ManualIso = new BaseManualParameterSony("getIsoSpeedRate", "getAvailableIsoSpeedRate","setIsoSpeedRate", cameraUiWrapper);
        parametersChangedList.add((BaseManualParameterSony) ManualIso);

        ManualExposure = new ExposureCompManualParameterSony(cameraUiWrapper);
        parametersChangedList.add((BaseManualParameterSony) ManualExposure);

        ProgramShift = new ProgramShiftManualSony(cameraUiWrapper);
        parametersChangedList.add((BaseManualParameterSony) ProgramShift);

        CCT = new WbCTManualSony(cameraUiWrapper);
        parametersChangedList.add((BaseManualParameterSony) CCT);

        WhiteBalanceMode = new WhiteBalanceModeSony(mRemoteApi, (WbCTManualSony) CCT);
        parametersChangedList.add((BaseModeParameterSony) WhiteBalanceMode);

        PostViewSize = new BaseModeParameterSony("getPostviewImageSize","setPostviewImageSize","getAvailablePostviewImageSize", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) PostViewSize);

        VideoSize = new BaseModeParameterSony("getMovieQuality", "setMovieQuality", "getAvailableMovieQuality", mRemoteApi);
        parametersChangedList.add((BaseModeParameterSony) VideoSize);

        Focuspeak = new FocusPeakSony(surfaceView);
        parametersChangedList.add((BaseModeParameterSony) Focuspeak);

        NightMode = new NightModeSony(surfaceView);
        parametersChangedList.add((BaseModeParameterSony) NightMode);

        PreviewZoom = new PreviewZoomManual(surfaceView, cameraUiWrapper);

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
        mRemoteApi = api;
        createParameters();
    }

    @Override
    public I_Device getDevice() {
        return null;
    }

    @Override
    public void SetFocusAREA(FocusRect focusAreas) {

    }

    @Override
    public void SetMeterAREA(FocusRect meteringAreas) {

    }

    @Override
    public void SetPictureOrientation(int or) {

    }
}
