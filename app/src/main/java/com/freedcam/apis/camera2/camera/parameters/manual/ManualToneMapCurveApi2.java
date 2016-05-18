package com.freedcam.apis.camera2.camera.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.TonemapCurve;
import android.os.Build;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.camera2.camera.CameraHolderApi2;
import com.freedcam.apis.camera2.camera.parameters.AeHandlerApi2;
import com.freedcam.apis.camera2.camera.parameters.ParameterHandlerApi2;
import com.freedcam.utils.Logger;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;

/**
 * Created by troop on 05.05.2015.
 */
//http://www.cambridgeincolour.com/tutorials/photoshop-curves.htm
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualToneMapCurveApi2 implements AbstractModeParameter.I_ModeParameterEvent
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
    private CameraHolderApi2 cameraHolder;


    public ManualToneMapCurveApi2(ParameterHandlerApi2 camParametersHandler, CameraHolderApi2 cameraHolder)
    {
        this.cameraHolder = cameraHolder;
        this.contrast = new Contrast(camParametersHandler);
        this.brightness = new Brightness(camParametersHandler);
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
        contrast.BackgroundIsSetSupportedChanged(canSet);
        contrast.BackgroundIsSupportedChanged(isSupported);
        brightness.BackgroundIsSupportedChanged(isSupported);
        brightness.BackgroundIsSetSupportedChanged(canSet);
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
        public Contrast(ParameterHandlerApi2 camParametersHandler) {
            super(camParametersHandler);
            this.stringvalues = createStringArray(0,100,1);
            this.currentInt = 50;
        }



        @Override
        public int GetValue() {
            return this.currentInt;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            Logger.d(TAG, "Contrast value to set:" + valueToSet);
            if (valueToSet == -1)
            {
                Logger.d(TAG, "Current TonemapMode:" + this.camParametersHandler.ToneMapMode.GetValue());
                if (camParametersHandler.ToneMapMode.GetValue().equals("CONTRAST_CURVE"))
                {
                    camParametersHandler.ToneMapMode.SetValue("FAST", true);
                    Logger.d(TAG, "Disabled Contrast Curve");
                }
            }
            else {
                Logger.d(TAG, "Current TonemapMode:" + camParametersHandler.ToneMapMode.GetValue());
                if (!camParametersHandler.ToneMapMode.GetValue().equals("CONTRAST_CURVE") && !firststart)
                {
                    camParametersHandler.ToneMapMode.SetValue("CONTRAST_CURVE", true);
                    Logger.d(TAG, "Enabled Contrast Curve");
                }
                valueToSet = valueToSet * 3;
                this.currentInt = valueToSet;

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
                cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.TONEMAP_CURVE, tonemapCurve);
                try {
                    cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.cameraBackroundValuesChangedListner,
                            null);
                } catch (CameraAccessException | NullPointerException e) {
                    Logger.exception(e);
                }
            }
            firststart = false;
        }

        @Override
        public boolean IsSupported() {
            return cameraHolder.characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES) != null
                    && cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.TONEMAP_MODE) == CaptureRequest.TONEMAP_MODE_CONTRAST_CURVE;
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

        public Brightness(ParameterHandlerApi2 camParametersHandler) {
            super(camParametersHandler);
            stringvalues = createStringArray(0,100,1);
            this.currentInt = 50;
        }

        @Override
        public int GetValue() {
            return this.currentInt/4;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            valueToSet = valueToSet *4;
            this.currentInt = valueToSet;

            float toset = 0;
            if (this.currentInt > 200)
            {
                toset = (this.currentInt - 200) * 0.001f;
                midtones[0] = 0.5f - toset;
                midtones[1] = 0.5f + toset;

            }
            if (this.currentInt == 100)
            {
                midtones[0] = 0.5f;
                midtones[1] = 0.5f;
            }
            else
            {
                toset = (200 - this.currentInt) * 0.001f;
                midtones[0] = 0.5f + toset;
                midtones[1] = 0.5f - toset;
            }

            Logger.d(TAG, "toset:" + toset + " val:" + valueToSet+ " x:" + midtones[0] + " y:"+ midtones[1]);

            float[]tonemap = {blackpoint[0], blackpoint[1], shadows[0],shadows[1], midtones[0], midtones[1], highlights[0], highlights[1],whitepoint[0], whitepoint[1]};
            TonemapCurve tonemapCurve = new TonemapCurve(tonemap,tonemap,tonemap);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.TONEMAP_CURVE, tonemapCurve);
            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.cameraBackroundValuesChangedListner,
                        null);
            } catch (CameraAccessException | NullPointerException e) {
                Logger.exception(e);
            }

        }

        @Override
        public boolean IsSupported()
        {
            if (cameraHolder == null || cameraHolder.mPreviewRequestBuilder == null)
                return false;
            return cameraHolder.characteristics.get(CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES) != null
                    && cameraHolder.mPreviewRequestBuilder.get(CaptureRequest.TONEMAP_MODE) == CaptureRequest.TONEMAP_MODE_CONTRAST_CURVE;
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
