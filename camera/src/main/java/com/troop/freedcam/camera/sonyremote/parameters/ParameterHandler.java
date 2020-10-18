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

package com.troop.freedcam.camera.sonyremote.parameters;

import android.graphics.Rect;
import android.text.TextUtils;

import com.troop.freedcam.camera.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.troop.freedcam.eventbus.events.CameraStateEvents;
import com.troop.freedcam.processor.RenderScriptManager;
import com.troop.freedcam.utils.ContextApplication;
import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameterHandler;
import com.troop.freedcam.camera.basecamera.parameters.modes.ModuleParameters;
import com.troop.freedcam.camera.sonyremote.CameraHolderSony;
import com.troop.freedcam.camera.sonyremote.FocusHandler;
import com.troop.freedcam.camera.sonyremote.PreviewStreamDrawer;
import com.troop.freedcam.camera.sonyremote.SonyCameraRemoteFragment;
import com.troop.freedcam.camera.sonyremote.modules.I_CameraStatusChanged;
import com.troop.freedcam.camera.sonyremote.modules.PictureModuleSony;
import com.troop.freedcam.camera.sonyremote.parameters.manual.BaseManualParameterSony;
import com.troop.freedcam.camera.sonyremote.parameters.manual.ExposureCompManualParameterSony;
import com.troop.freedcam.camera.sonyremote.parameters.manual.PreviewZoomManual;
import com.troop.freedcam.camera.sonyremote.parameters.manual.ProgramShiftManualSony;
import com.troop.freedcam.camera.sonyremote.parameters.manual.WbCTManualSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.BaseModeParameterSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.ContShootModeParameterSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.FocusModeSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.FocusPeakSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.I_SonyApi;
import com.troop.freedcam.camera.sonyremote.parameters.modes.NightModeSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.ObjectTrackingSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.PictureFormatSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.PictureSizeSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.ScalePreviewModeSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.WhiteBalanceModeSony;
import com.troop.freedcam.camera.sonyremote.parameters.modes.ZoomSettingSony;
import com.troop.freedcam.camera.sonyremote.sonystuff.SimpleCameraEventObserver;
import com.troop.freedcam.camera.sonyremote.sonystuff.SimpleRemoteApi;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import freed.utils.FreeDPool;
import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 13.12.2014.
 */
public class ParameterHandler extends AbstractParameterHandler implements SimpleCameraEventObserver.ChangeListener
{
    private final String TAG = ParameterHandler.class.getSimpleName();
    public SimpleRemoteApi mRemoteApi;
    public Set<String> mAvailableCameraApiSet;
    private final List<I_SonyApi> parametersChangedList;
    private final PreviewStreamDrawer surfaceView;
    private final CameraControllerInterface cameraUiWrapper;
    private String cameraStatus = "IDLE";

    public I_CameraStatusChanged CameraStatusListner;
    public CameraHolderSony.I_CameraShotMode cameraShotMode;


    public ParameterHandler(CameraControllerInterface cameraUiWrapper, PreviewStreamDrawer surfaceView)
    {
        super(cameraUiWrapper);
        parametersChangedList = new ArrayList<>();
        this.surfaceView = surfaceView;
        this.cameraUiWrapper =cameraUiWrapper;
        if (RenderScriptManager.isSupported()) {
            SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setValues(ContextApplication.getContext().getResources().getStringArray(R.array.focuspeakColors));
            SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).set(SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).getValues()[0]);
            SettingsManager.get(SettingKeys.FOCUSPEAK_COLOR).setIsSupported(true);
        }
    }

    public void SetCameraApiSet(Set<String> mAvailableCameraApiSet)
    {
        this.mAvailableCameraApiSet = mAvailableCameraApiSet;
        logApiSet(mAvailableCameraApiSet);

        Log.d(TAG, "Throw parametersChanged");
        throwSonyApiChanged(mAvailableCameraApiSet);

    }

    private void logApiSet(Set<String> mAvailableCameraApiSet)
    {
        Log.d(TAG,Arrays.toString(mAvailableCameraApiSet.toArray()));
    }

    public boolean canStartBulbCapture()
    {
        return  mAvailableCameraApiSet.contains("startBulbShooting");
    }

    public boolean canStopBulbCapture()
    {
        return  mAvailableCameraApiSet.contains("stopBulbShooting");
    }

    public String GetCameraStatus()
    { return cameraStatus;}

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

    public void addApiChangedListner(I_SonyApi sonyApi)
    {
        parametersChangedList.add(sonyApi);
    }

    private void createParameters()
    {
        add(SettingKeys.Module, new ModuleParameters(cameraUiWrapper));
        add(SettingKeys.PictureSize, new PictureSizeSony(mRemoteApi,cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.PictureSize));

        add(SettingKeys.PictureFormat, new PictureFormatSony(mRemoteApi,cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.PictureFormat));

        add(SettingKeys.FlashMode, new BaseModeParameterSony("getFlashMode", "setFlashMode", "getAvailableFlashMode", mRemoteApi,cameraUiWrapper,SettingKeys.FlashMode));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.FlashMode));

        add(SettingKeys.ExposureMode, new BaseModeParameterSony("getExposureMode", "setExposureMode", "getAvailableExposureMode", mRemoteApi,cameraUiWrapper,SettingKeys.ExposureMode));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.ExposureMode));

        add(SettingKeys.ContShootMode, new ContShootModeParameterSony(mRemoteApi, cameraUiWrapper.getModuleHandler(),cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.ContShootMode));

        add(SettingKeys.ContShootModeSpeed, new BaseModeParameterSony("getContShootingSpeed", "setContShootingSpeed", "getAvailableContShootingSpeed", mRemoteApi,cameraUiWrapper, SettingKeys.ContShootModeSpeed));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.ContShootModeSpeed));

        add(SettingKeys.FocusMode, new FocusModeSony("getFocusMode", "setFocusMode", "getAvailableFocusMode", mRemoteApi,cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.FocusMode));

        add(SettingKeys.ObjectTracking, new ObjectTrackingSony(mRemoteApi,cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.ObjectTracking));

        add(SettingKeys.ZoomSetting, new ZoomSettingSony(mRemoteApi,cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.ZoomSetting));


        /*Zoom = new ZoomManualSony(cameraUiWrapper);
        parametersChangedList.add((ZoomManualSony) Zoom);*/
        add(SettingKeys.M_ExposureTime, new BaseManualParameterSony("getShutterSpeed", "getAvailableShutterSpeed","setShutterSpeed", cameraUiWrapper,SettingKeys.M_ExposureTime));
        parametersChangedList.add((BaseManualParameterSony) get(SettingKeys.M_ExposureTime));

        add(SettingKeys.M_Fnumber, new BaseManualParameterSony("getFNumber","getAvailableFNumber","setFNumber", cameraUiWrapper,SettingKeys.M_Fnumber));
        parametersChangedList.add((BaseManualParameterSony) get(SettingKeys.M_Fnumber));

        add(SettingKeys.M_ManualIso, new BaseManualParameterSony("getIsoSpeedRate", "getAvailableIsoSpeedRate","setIsoSpeedRate", cameraUiWrapper,SettingKeys.M_ManualIso));
        parametersChangedList.add((BaseManualParameterSony) get(SettingKeys.M_ManualIso));

        add(SettingKeys.M_ExposureCompensation, new ExposureCompManualParameterSony(cameraUiWrapper));
        parametersChangedList.add((BaseManualParameterSony) get(SettingKeys.M_ExposureCompensation));

        add(SettingKeys.M_ProgramShift, new ProgramShiftManualSony(cameraUiWrapper));
        parametersChangedList.add((BaseManualParameterSony) get(SettingKeys.M_ProgramShift));

        add(SettingKeys.M_Whitebalance, new WbCTManualSony(cameraUiWrapper));
        parametersChangedList.add((BaseManualParameterSony) get(SettingKeys.M_Whitebalance));

        add(SettingKeys.WhiteBalanceMode, new WhiteBalanceModeSony(mRemoteApi, (WbCTManualSony) get(SettingKeys.M_Whitebalance),cameraUiWrapper));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.WhiteBalanceMode));

        add(SettingKeys.PostViewSize, new BaseModeParameterSony("getPostviewImageSize","setPostviewImageSize","getAvailablePostviewImageSize", mRemoteApi,cameraUiWrapper,SettingKeys.PostViewSize));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.PostViewSize));

        add(SettingKeys.VideoSize, new BaseModeParameterSony("getMovieQuality", "setMovieQuality", "getAvailableMovieQuality", mRemoteApi,cameraUiWrapper,SettingKeys.VideoSize));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.VideoSize));

        add(SettingKeys.Focuspeak, new FocusPeakSony(surfaceView));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.Focuspeak));

        add(SettingKeys.NightMode, new NightModeSony(surfaceView));
        parametersChangedList.add((BaseModeParameterSony) get(SettingKeys.NightMode));
        //get(SettingKeys.EnableRenderScript).addEventListner((ParameterEvents) get(SettingKeys.NightMode));

        add(SettingKeys.M_PreviewZoom, new PreviewZoomManual(surfaceView, cameraUiWrapper));

        add(SettingKeys.SCALE_PREVIEW, new ScalePreviewModeSony(surfaceView));

    }

    public void SetRemoteApi(SimpleRemoteApi api)
    {
        mRemoteApi = api;
        createParameters();
    }

    @Override
    public void SetFocusAREA(Rect focusAreas) {

    }

    @Override
    public void SetPictureOrientation(int or) {

    }

    @Override
    public float[] getFocusDistances() {
        return new float[0];
    }

    @Override
    public float getCurrentExposuretime() {
        return 0;
    }

    @Override
    public int getCurrentIso() {
        return 0;
    }


    @Override
    public void onShootModeChanged(String shootMode) {
        if(cameraShotMode != null )
            cameraShotMode.onShootModeChanged(shootMode);
    }

    @Override
    public void onCameraStatusChanged(String status)
    {
        //if (cameraStatus.equals(status))
        //    return;
        cameraStatus = status;
        Log.d(TAG, "Camerastatus:" + cameraStatus);
        if (CameraStatusListner != null)
            CameraStatusListner.onCameraStatusChanged(status);
    }

    @Override
    public void onTimout() {
        CameraStateEvents.fireCameraErrorEvent("Camera connection timed out");
        ((SonyCameraRemoteFragment)cameraUiWrapper).stopEventObserver();
    }

    @Override
    public void onApiListModified(List<String> apis) {

        synchronized (mAvailableCameraApiSet) {
            mAvailableCameraApiSet.clear();
            mAvailableCameraApiSet.addAll(apis);
            SetCameraApiSet(mAvailableCameraApiSet);
        }
    }

    @Override
    public void onZoomPositionChanged(int zoomPosition)
    {
        //((ZoomManualSony)Zoom).setZoomsHasChanged(zoomPosition);
    }

    @Override
    public void onIsoChanged(String iso)
    {
        get(SettingKeys.M_ManualIso).fireStringValueChanged(iso);
    }

    @Override
    public void onIsoValuesChanged(String[] isovals) {
        get(SettingKeys.M_ManualIso).fireStringValuesChanged(isovals);
    }

    @Override
    public void onFnumberValuesChanged(String[] fnumbervals) {
        get(SettingKeys.M_Fnumber).fireStringValuesChanged(fnumbervals);
    }

    @Override
    public void onExposureCompensationMaxChanged(int epxosurecompmax) {
        //parameterHandler.ManualExposure.BackgroundMaxValueChanged(epxosurecompmax);
    }

    @Override
    public void onExposureCompensationMinChanged(int epxosurecompmin) {
        //parameterHandler.ManualExposure.BackgroundMinValueChanged(epxosurecompmin);
    }

    @Override
    public void onExposureCompensationChanged(int epxosurecomp) {
        get(SettingKeys.M_ExposureCompensation).fireIntValueChanged(epxosurecomp);
    }

    @Override
    public void onShutterSpeedChanged(String shutter) {
        get(SettingKeys.M_ExposureTime).fireStringValueChanged(shutter);
    }

    @Override
    public void onShutterSpeedValuesChanged(String[] shuttervals) {
        get(SettingKeys.M_ExposureTime).fireStringValuesChanged(shuttervals);
    }

    @Override
    public void onFlashChanged(String flash)
    {
        Log.d(TAG, "Fire ONFLashCHanged");
        get(SettingKeys.FlashMode).fireStringValueChanged(flash);
    }

    @Override
    public void onFocusLocked(boolean locked) {
        ((FocusHandler) cameraUiWrapper.getFocusHandler()).onFocusLock(locked);
    }

    @Override
    public void onWhiteBalanceValueChanged(String wb)
    {
        get(SettingKeys.WhiteBalanceMode).fireStringValueChanged(wb);
        if (get(SettingKeys.WhiteBalanceMode).GetStringValue().equals("Color Temperature") && get(SettingKeys.M_Whitebalance) != null)
            get(SettingKeys.M_Whitebalance).setViewState(AbstractParameter.ViewState.Visible);
        else
            get(SettingKeys.M_Whitebalance).setViewState(AbstractParameter.ViewState.Hidden);
    }

    @Override
    public void onImagesRecieved(final String[] url)
    {
        FreeDPool.Execute(() -> {
            for (String s : url)
            {
                if (cameraUiWrapper.getModuleHandler().getCurrentModule() instanceof PictureModuleSony)
                {
                    PictureModuleSony pictureModuleSony = (PictureModuleSony) cameraUiWrapper.getModuleHandler().getCurrentModule();
                    try {
                        pictureModuleSony.onPictureTaken(new URL(s));
                    }catch (MalformedURLException ex) {
                        Log.WriteEx(ex);
                    }
                }
            }
        });
    }

    @Override
    public void onFnumberChanged(String fnumber) {
        get(SettingKeys.M_Fnumber).fireStringValueChanged(fnumber);
    }

    @Override
    public void onLiveviewStatusChanged(boolean status) {

    }

    @Override
    public void onStorageIdChanged(String storageId) {

    }

    @Override
    public void onExposureModesChanged(String[] expomode)
    {
        get(SettingKeys.ExposureMode).fireStringValuesChanged(expomode);
    }

    @Override
    public void onImageFormatChanged(String imagesize) {
        if (imagesize!= null && !TextUtils.isEmpty(imagesize))
            get(SettingKeys.PictureFormat).fireStringValueChanged(imagesize);
    }

    @Override
    public void onImageFormatsChanged(String[] imagesize) {
        get(SettingKeys.PictureFormat).fireStringValuesChanged(imagesize);
    }

    @Override
    public void onImageSizeChanged(String imagesize) {
        if (imagesize!= null && !TextUtils.isEmpty(imagesize))
            get(SettingKeys.PictureSize).fireStringValueChanged(imagesize);
    }

    @Override
    public void onContshotModeChanged(String imagesize) {
        if (imagesize!= null && !TextUtils.isEmpty(imagesize))
            get(SettingKeys.ContShootMode).fireStringValueChanged(imagesize);
    }

    @Override
    public void onContshotModesChanged(String[] imagesize) {
        get(SettingKeys.ContShootMode).fireStringValuesChanged(imagesize);
    }

    @Override
    public void onFocusModeChanged(String imagesize) {
        if (imagesize!= null && !TextUtils.isEmpty(imagesize))
            get(SettingKeys.FocusMode).fireStringValueChanged(imagesize);
    }

    @Override
    public void onFocusModesChanged(String[] imagesize) {
        get(SettingKeys.FocusMode).fireStringValuesChanged(imagesize);
    }

    @Override
    public void onPostviewModeChanged(String imagesize) {
        if (imagesize!= null && !TextUtils.isEmpty(imagesize))
            get(SettingKeys.PostViewSize).fireStringValueChanged(imagesize);
    }

    @Override
    public void onPostviewModesChanged(String[] imagesize) {
        get(SettingKeys.PostViewSize).fireStringValuesChanged(imagesize);
    }

    @Override
    public void onTrackingFocusModeChanged(String imagesize) {
        get(SettingKeys.ObjectTracking).fireStringValueChanged(imagesize);
    }

    @Override
    public void onTrackingFocusModesChanged(String[] imagesize) {
        get(SettingKeys.ObjectTracking).fireStringValuesChanged(imagesize);
    }

    @Override
    public void onZoomSettingValueCHanged(String value) {
        get(SettingKeys.ZoomSetting).fireStringValueChanged(value);
    }

    @Override
    public void onZoomSettingsValuesCHanged(String[] values) {
        get(SettingKeys.ZoomSetting).fireStringValuesChanged(values);
    }

    @Override
    public void onExposureModeChanged(String expomode) {
        if ((expomode == null && TextUtils.isEmpty(expomode)) || get(SettingKeys.ExposureMode).GetStringValue() == null)
            return;
        if (!get(SettingKeys.ExposureMode).GetStringValue().equals(expomode))
            get(SettingKeys.ExposureMode).fireStringValueChanged(expomode);
        if (expomode.equals("Intelligent Auto")|| expomode.equals("Superior Auto"))
            get(SettingKeys.WhiteBalanceMode).setViewState(AbstractParameter.ViewState.Hidden);
        else
            get(SettingKeys.WhiteBalanceMode).setViewState(AbstractParameter.ViewState.Visible);
    }
}
