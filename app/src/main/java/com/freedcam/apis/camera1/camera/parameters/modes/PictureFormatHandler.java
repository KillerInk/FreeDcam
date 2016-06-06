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

package com.freedcam.apis.camera1.camera.parameters.modes;

import android.hardware.Camera;
import android.os.Build;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.camera1.camera.CameraHolder;
import com.freedcam.apis.camera1.camera.CameraHolder.Frameworks;
import com.freedcam.apis.camera1.camera.parameters.ParametersHandler;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

import static com.freedcam.apis.KEYS.BAYER;

/**
 * Created by troop on 05.02.2016.
 */
public class PictureFormatHandler extends BaseModeParameter
{
    private final String TAG = PictureFormatHandler.class.getSimpleName();
    private boolean rawSupported = false;
    private String captureMode = KEYS.JPEG;
    private String rawFormat;

    private String[] rawFormats;

    final public static int JPEG= 0;
    private final static int RAW = 1;
    private final static int DNG = 2;

    private BayerFormat BayerFormats;
    private ParametersHandler parametersHandler;

    final static public String[] CaptureMode =
    {
            KEYS.JPEG,
        BAYER,
        KEYS.DNG
    };

    /***
     * @param parameters   Hold the Camera Parameters
     * @param cameraHolder Hold the camera object
     */
    public PictureFormatHandler(Camera.Parameters parameters, CameraHolder cameraHolder, ParametersHandler parametersHandler)
    {
        super(parameters, cameraHolder, "", "");
        this.parametersHandler = parametersHandler;
        if (cameraHolder.DeviceFrameWork == Frameworks.MTK)
        {
            Logger.d(TAG,"mtk");
            isSupported = true;
            rawSupported = true;
        }
        else
        {
            Logger.d(TAG,"default");
            isSupported = true;
            if (DeviceUtils.IS(DeviceUtils.Devices.LG_G2))
            {
                isSupported = true;
                rawSupported = true;
                rawFormat = KEYS.BAYER_MIPI_10BGGR;
            }
            else if (DeviceUtils.IS(DeviceUtils.Devices.HTC_OneA9))
            {
                isSupported = true;
                rawSupported = true;
                rawFormat = KEYS.BAYER_MIPI_10RGGB;
            }
            else if(DeviceUtils.IS(DeviceUtils.Devices.Htc_M8) && Build.VERSION.SDK_INT >= 21)
            {
                isSupported = true;
                rawSupported = true;
                rawFormat = KEYS.BAYER_QCOM_10GRBG;}
            else
            {
                String formats = parameters.get(KEYS.PICTURE_FORMAT_VALUES);
                if (DeviceUtils.IS(DeviceUtils.Devices.MotoG3)||DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8974))
                    formats = "bayer-mipi-10bggr,bayer-ideal-qcom-10bggr,bayer-qcom-10bggr,bayer-mipi-10rggb,bayer-ideal-qcom-10rggb,bayer-qcom-10rggb,bayer-mipi-10grbg,bayer-ideal-qcom-10grbg,bayer-qcom-10grbg,bayer-mipi-10gbrg,bayer-ideal-qcom-10gbrg,bayer-qcom-10gbrg";

                if (formats.contains("bayer-mipi") || formats.contains("raw"))
                {
                    rawSupported = true;
                    String forms[] = formats.split(",");
                    for (String s : forms) {
                        if (s.contains("bayer-mipi") || s.contains("raw"))
                        {
                            rawFormat = s;
                            break;
                        }
                    }
                }
                if (formats.contains(KEYS.BAYER))
                {
                    ArrayList<String> tmp = new ArrayList<>();
                    String forms[] = formats.split(",");
                    for (String s : forms) {
                        if (s.contains(KEYS.BAYER))
                        {
                            tmp.add(s);
                        }
                    }
                    rawFormats = new String[tmp.size()];
                    tmp.toArray(rawFormats);
                    if (tmp.size()>0) {
                        BayerFormats = new BayerFormat(parameters, cameraHolder, "");
                        parametersHandler.bayerformat = BayerFormats;
                    }

                }
            }
        }
        Logger.d(TAG, "rawsupported:" + rawSupported + "isSupported:"+isSupported);
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        Logger.d(TAG, "SetValue:" + valueToSet);
        captureMode = valueToSet;
        if (cameraHolder.DeviceFrameWork != Frameworks.MTK)
        {
            switch (valueToSet)
            {
                case KEYS.JPEG:
                    setString(valueToSet,setToCam);
                    break;
                case BAYER:
                    setString(rawFormat,setToCam);
                    cameraHolder.GetParameterHandler().SetDngActive(false);
                    break;
                case KEYS.DNG:
                    setString(rawFormat,setToCam);
                    cameraHolder.GetParameterHandler().SetDngActive(true);
                    break;
            }
        }
        BackgroundValueHasChanged(valueToSet);
    }

    private void setString(String val, boolean setTocam)
    {
        Logger.d(TAG, "setString:" +val);
        parameters.set(KEYS.PICTURE_FORMAT, val);
        cameraHolder.SetCameraParameters(parameters);
    }

    @Override
    public boolean IsSupported()
    {
        Logger.d(TAG,"IsSupported:"+isSupported);
        return isSupported;
    }

    @Override
    public String GetValue() {
        return captureMode;
    }

    @Override
    public String[] GetValues()
    {
        if (rawSupported && DeviceUtils.isCamera1NO_RAW_STREM())
            return new String[]{CaptureMode[JPEG]};
        if (rawSupported && parametersHandler != null && parametersHandler.Device != null && !parametersHandler.Device.IsDngSupported())
            return new String[]{CaptureMode[JPEG],CaptureMode[RAW]};
        else if(rawSupported && parametersHandler != null && parametersHandler.Device != null && parametersHandler.Device.IsDngSupported())
            return new String[]{CaptureMode[JPEG],CaptureMode[DNG],CaptureMode[RAW]};
        else
            return new String[]{CaptureMode[JPEG]};
    }

    @Override
    public void ModuleChanged(String module)
    {
        switch (module)
        {
            case KEYS.MODULE_PICTURE:
            case KEYS.MODULE_INTERVAL:
            case KEYS.MODULE_HDR:
                BackgroundIsSupportedChanged(true);
                break;
            case KEYS.MODULE_VIDEO:
                BackgroundIsSupportedChanged(false);
                break;
        }
    }

    @Override
    public void onValuesChanged(String[] values) {
        super.onValuesChanged(values);
    }

    public class BayerFormat extends BaseModeParameter
    {

        /***
         * @param parameters   Hold the Camera Parameters
         * @param cameraHolder Hold the camera object
         * @param values
         */
        public BayerFormat(Camera.Parameters parameters, CameraHolder cameraHolder, String values) {
            super(parameters, cameraHolder, "", "");
        }

        @Override
        public String GetValue()
        {
            return rawFormat;
        }

        @Override
        public String[] GetValues() {
            return rawFormats;
        }

        @Override
        public boolean IsSupported() {
            return rawFormats != null && rawFormats.length>0;
        }

        @Override
        public boolean IsVisible() {
            return  rawFormats != null && rawFormats.length>0;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCam)
        {
            rawFormat = valueToSet;
            if (captureMode.equals(KEYS.BAYER)|| captureMode.equals(KEYS.DNG)) {
                PictureFormatHandler.this.SetValue(captureMode, true);
            }
        }
    }
}
