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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;
import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.settings.Frameworks;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 05.02.2016.
 */
public class PictureFormatHandler extends BaseModeParameter
{
    private final String TAG = PictureFormatHandler.class.getSimpleName();
    private String captureMode = cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_);
    private String rawFormat;

    private String[] rawFormats;


    /***
     * @param parameters   Hold the Camera Parameters
     * @param cameraUiWrapper Hold the camera object
     */
    public PictureFormatHandler(Parameters parameters, CameraWrapperInterface cameraUiWrapper, ParametersHandler parametersHandler)
    {
        super(parameters, cameraUiWrapper,SettingKeys.PictureFormat);
        SettingsManager.get(SettingKeys.PictureFormat).isSupported();
        setViewState(ViewState.Visible);
        boolean rawpicformatsupported = SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).isSupported();
        boolean dngprofilessupported = false;
        if(SettingsManager.getInstance().getDngProfilesMap() != null && SettingsManager.getInstance().getDngProfilesMap().size() > 0)
            dngprofilessupported = true;
        boolean rawSupported = rawpicformatsupported || dngprofilessupported;
        if (rawSupported) {
            rawFormat = SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).get();
            rawFormats = SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).getValues();
            BayerFormat bayerFormats = new BayerFormat(parameters, cameraUiWrapper, "");
            if (bayerFormats.getStringValues().length > 0)
                bayerFormats.setViewState(ViewState.Visible);
            if (TextUtils.isEmpty(rawFormat))
                rawFormat = rawFormats[0];
            parametersHandler.add(SettingKeys.BAYERFORMAT, bayerFormats);
            if (rawFormats.length  > 0 || SettingsManager.getInstance().getFrameWork() == freed.settings.Frameworks.MTK)
                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(true);
            else
                SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).setIsSupported(false);
            if (!contains(SettingsManager.get(SettingKeys.RAW_PICTURE_FORMAT_SETTING).getValues(), SettingsManager.getInstance().getResString(R.string.dng_))
                    && dngprofilessupported)
            SettingsManager.get(SettingKeys.PictureFormat).setValues(new String[]
                        {
                                SettingsManager.getInstance().getResString(R.string.jpeg_),
                                SettingsManager.getInstance().getResString(R.string.dng_),
                                SettingsManager.getInstance().getResString(R.string.bayer_)
                        });
        }
        Log.d(TAG, "rawsupported:" + rawSupported + "isSupported:"+ (getViewState() == ViewState.Visible));
    }

    private boolean contains(String[] arr, String tofind)
    {
        for (int i = 0; i< arr.length; i++)
        {
            if (arr[i].equals(tofind))
                return true;
        }
        return false;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        Log.d(TAG, "SetValue:" + valueToSet);
        SettingsManager.get(SettingKeys.PictureFormat).set(valueToSet);
        captureMode = valueToSet;
        if (SettingsManager.getInstance().getFrameWork() != Frameworks.MTK)
        {
            if (valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.jpeg_)))
                setString(valueToSet,setToCam);
            else if(valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.bayer_)))
            {
                setString(rawFormat,setToCam);
            }
            else if(valueToSet.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.dng_)))
            {
                setString(rawFormat,setToCam);
            }
        }
        fireStringValueChanged(valueToSet);
    }

    private void setString(String val, boolean setTocam)
    {
        Log.d(TAG, "setApiString:" +val);
        parameters.set(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.picture_format), val);
        if (setTocam)
            ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
    }


    @Override
    public String GetStringValue() {
        return captureMode;
    }

    @Override
    public String[] getStringValues()
    {
        return SettingsManager.get(SettingKeys.PictureFormat).getValues();
    }

    @Override
    public void onModuleChanged(String module)
    {
        if (module.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.module_video)))
        {
            setViewState(ViewState.Hidden);
        }
        else
        {
            setViewState(ViewState.Visible);
        }
    }

    public class BayerFormat extends BaseModeParameter
    {

        /***
         * @param parameters   Hold the Camera Parameters
         * @param cameraHolder Hold the camera object
         * @param values
         */
        public BayerFormat(Parameters parameters, CameraWrapperInterface cameraHolder, String values) {
            super(parameters, cameraHolder,SettingKeys.BAYERFORMAT);
        }

        @Override
        public String GetStringValue()
        {
            return rawFormat;
        }

        @Override
        public String[] getStringValues() {
            return rawFormats;
        }

        @Override
        public ViewState getViewState() {
            if ( rawFormats != null && rawFormats.length>0)
                return ViewState.Visible;
            else
                return ViewState.Hidden;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCam)
        {
            rawFormat = valueToSet;
            if (captureMode.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.bayer_))|| captureMode.equals(cameraUiWrapper.getActivityInterface().getStringFromRessources(R.string.dng_))) {
                PictureFormatHandler.this.SetValue(captureMode, true);
            }
        }
    }
}
