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

package com.freedcam.apis.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.TonemapCurve;
import android.os.Build.VERSION_CODES;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.apis.camera2.CameraHolderApi2;
import com.freedcam.utils.Logger;

/**
 * Created by troop on 05.05.2015.
 */
//http://www.cambridgeincolour.com/tutorials/photoshop-curves.htm
@TargetApi(VERSION_CODES.LOLLIPOP)
public class ManualToneMapCurveApi2 implements I_ModeParameterEvent
{
    final String TAG = ManualToneMapCurveApi2.class.getSimpleName();
    //  linearcurve       x/y
    private float[] blackpoint = { 0f,0f};
    private float[] shadows = {0.25f,0.25f};
    private float[] midtones = {0.5f,0.5f};
    private float[] highlights = { 0.75f,0.75f};
    private float[] whitepoint = {1.0f,1.0f};
    public Contrast contrast;
    public Brightness brightness;
    private boolean visible = false;


    public ManualToneMapCurveApi2(CameraWrapperInterface cameraUiWrapper)
    {
        contrast = new Contrast(cameraUiWrapper);
        brightness = new Brightness(cameraUiWrapper);
    }

    private boolean canSet = false;
    private boolean isSupported = false;

    @Override
    public void onValueChanged(String val) {
        if (val.equals("CONTRAST_CURVE"))
        {
            canSet = true;
            isSupported = true;
            visible = true;
        }
        else {
            canSet = false;
            isSupported = false;
            visible = false;
        }
        contrast.ThrowBackgroundIsSetSupportedChanged(canSet);
        contrast.ThrowBackgroundIsSupportedChanged(isSupported);
        brightness.ThrowBackgroundIsSupportedChanged(isSupported);
        brightness.ThrowBackgroundIsSetSupportedChanged(canSet);
    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }


    public class Contrast extends AbstractManualParameter
    {
        boolean firststart = true;
        public Contrast(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            stringvalues = createStringArray(0,100,1);
            currentInt = 50;
        }



        @Override
        public int GetValue() {
            return currentInt;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            Logger.d(TAG, "Contrast value to set:" + valueToSet);
            if (valueToSet == -1)
            {
                Logger.d(TAG, "Current TonemapMode:" + cameraUiWrapper.GetParameterHandler().ToneMapMode.GetValue());
                if (cameraUiWrapper.GetParameterHandler().ToneMapMode.GetValue().equals("CONTRAST_CURVE"))
                {
                    cameraUiWrapper.GetParameterHandler().ToneMapMode.SetValue("FAST", true);
                    Logger.d(TAG, "Disabled Contrast Curve");
                }
            }
            else {
                Logger.d(TAG, "Current TonemapMode:" + cameraUiWrapper.GetParameterHandler().ToneMapMode.GetValue());
                if (!cameraUiWrapper.GetParameterHandler().ToneMapMode.GetValue().equals("CONTRAST_CURVE") && !firststart)
                {
                    cameraUiWrapper.GetParameterHandler().ToneMapMode.SetValue("CONTRAST_CURVE", true);
                    Logger.d(TAG, "Enabled Contrast Curve");
                }
                valueToSet = valueToSet * 3;
                currentInt = valueToSet;

                float toset = 0;
                if (valueToSet > 150) {
                    toset = (valueToSet - 100) * 0.001f;
                    highlights[0] = 0.75f - toset;
                    highlights[1] = 0.75f + toset;
                    shadows[0] = 0.25f - toset;
                    shadows[1] = 0.25f + toset;
                }
                if (valueToSet == 150) {
                    highlights[0] = 0.75f;
                    highlights[1] = 0.75f;
                    shadows[0] = 0.25f;
                    shadows[1] = 0.25f;
                } else {
                    toset = (150 - valueToSet) * 0.001f;
                    highlights[0] = 0.75f + toset;
                    highlights[1] = 0.75f - toset;
                    shadows[0] = 0.25f + toset;
                    shadows[1] = 0.25f - toset;
                }

                Logger.d(TAG, "toset:" + toset + " val:" + valueToSet + " hx:" + highlights[0] + " hy:" + highlights[1] + " sx:" + shadows[0] + " sy:" + shadows[1]);

                float[] tonemap = {blackpoint[0], blackpoint[1], shadows[0], shadows[1], midtones[0], midtones[1], highlights[0], highlights[1], whitepoint[0], whitepoint[1]};
                TonemapCurve tonemapCurve = new TonemapCurve(tonemap, tonemap, tonemap);
                ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.TONEMAP_CURVE, tonemapCurve);
            }
            firststart = false;
        }

        @Override
        public boolean IsSupported() {
            return !(cameraUiWrapper.GetCameraHolder() == null || ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).characteristics == null) && ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES) != null && ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.TONEMAP_MODE) == CaptureRequest.TONEMAP_MODE_CONTRAST_CURVE;
        }

        @Override
        public boolean IsVisible() {
            return IsSupported();
        }

        @Override
        public boolean IsSetSupported() {
            return true;
        }

        @Override
        public String GetStringValue() {
            return super.GetStringValue();
        }
    }

    public class Brightness extends AbstractManualParameter
    {

        public Brightness(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            stringvalues = createStringArray(0,100,1);
            currentInt = 50;
        }

        @Override
        public int GetValue() {
            return currentInt /4;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            valueToSet = valueToSet *4;
            currentInt = valueToSet;

            float toset = 0;
            if (currentInt > 200)
            {
                toset = (currentInt - 200) * 0.001f;
                midtones[0] = 0.5f - toset;
                midtones[1] = 0.5f + toset;

            }
            if (currentInt == 100)
            {
                midtones[0] = 0.5f;
                midtones[1] = 0.5f;
            }
            else
            {
                toset = (200 - currentInt) * 0.001f;
                midtones[0] = 0.5f + toset;
                midtones[1] = 0.5f - toset;
            }

            Logger.d(TAG, "toset:" + toset + " val:" + valueToSet+ " x:" + midtones[0] + " y:"+ midtones[1]);

            float[]tonemap = {blackpoint[0], blackpoint[1], shadows[0],shadows[1], midtones[0], midtones[1], highlights[0], highlights[1],whitepoint[0], whitepoint[1]};
            TonemapCurve tonemapCurve = new TonemapCurve(tonemap,tonemap,tonemap);
            ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).SetParameterRepeating(CaptureRequest.TONEMAP_CURVE, tonemapCurve);

        }

        @Override
        public boolean IsSupported()
        {
            if (((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()) == null || ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).characteristics == null || ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES) == null )
                return false;
            return  ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.TONEMAP_MODE) == null || ((CameraHolderApi2)cameraUiWrapper.GetCameraHolder()).get(CaptureRequest.TONEMAP_MODE) == CaptureRequest.TONEMAP_MODE_CONTRAST_CURVE;
        }

        @Override
        public boolean IsVisible() {
            return IsSupported();
        }

        @Override
        public boolean IsSetSupported() {
            return true;
        }

        @Override
        public String GetStringValue() {
            return super.GetStringValue();
        }
    }
}
