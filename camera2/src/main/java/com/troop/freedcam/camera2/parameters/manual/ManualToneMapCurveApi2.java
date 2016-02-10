package com.troop.freedcam.camera2.parameters.manual;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.TonemapCurve;
import android.os.Build;
import android.util.Log;

import com.troop.freedcam.camera2.BaseCameraHolderApi2;
import com.troop.freedcam.camera2.parameters.ParameterHandlerApi2;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 05.05.2015.
 */
//http://www.cambridgeincolour.com/tutorials/photoshop-curves.htm
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ManualToneMapCurveApi2 implements AbstractModeParameter.I_ModeParameterEvent
{
    private final String TAG = ManualToneMapCurveApi2.class.getSimpleName();
    //  linearcurve       x/y
    private float[] blackpoint = { 0f,0f};
    private float[] shadows = {0.25f,0.25f};
    private float[] midtones = {0.5f,0.5f};
    private float[] highlights = { 0.75f,0.75f};
    private float[] whitepoint = {1.0f,1.0f};
    public Contrast contrast;
    public Brightness brightness;
    private boolean visible = false;


    public ManualToneMapCurveApi2(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder)
    {
        this.contrast = new Contrast(camParametersHandler,cameraHolder);
        this.brightness = new Brightness(camParametersHandler,cameraHolder);
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
    public void onVisibilityChanged() {

    }


    public class Contrast extends ManualExposureApi2
    {
        int current = -1;
        boolean firststart = true;
        public Contrast(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
            super(camParametersHandler, cameraHolder);

        }


        @Override
        public int GetMaxValue() {
            return 100;
        }

        @Override
        public int GetMinValue() {
            return -1;
        }

        @Override
        public int GetValue() {
            return current/3;
        }

        @Override
        public String GetStringValue() {
            return null;
        }

        @Override
        public String[] getStringValues() {
            return null;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            Log.d(TAG, "Contrast value to set:" + valueToSet);
            if (valueToSet == -1)
            {
                Log.d(TAG, "Current TonemapMode:" + camParametersHandler.ToneMapMode.GetValue());
                if (camParametersHandler.ToneMapMode.GetValue().equals("CONTRAST_CURVE"))
                {
                    camParametersHandler.ToneMapMode.SetValue("FAST", true);
                    Log.d(TAG, "Disabled Contrast Curve");
                }
            }
            else {
                Log.d(TAG, "Current TonemapMode:" + camParametersHandler.ToneMapMode.GetValue());
                if (!camParametersHandler.ToneMapMode.GetValue().equals("CONTRAST_CURVE") && !firststart)
                {
                    camParametersHandler.ToneMapMode.SetValue("CONTRAST_CURVE", true);
                    Log.d(TAG, "Enabled Contrast Curve");
                }
                valueToSet = valueToSet * 3;
                current = valueToSet;

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

                Log.d(TAG, "toset:" + toset + " val:" + valueToSet + " hx:" + highlights[0] + " hy:" + highlights[1] + " sx:" + shadows[0] + " sy:" + shadows[1]);

                float[] tonemap = {blackpoint[0], blackpoint[1], shadows[0], shadows[1], midtones[0], midtones[1], highlights[0], highlights[1], whitepoint[0], whitepoint[1]};
                TonemapCurve tonemapCurve = new TonemapCurve(tonemap, tonemap, tonemap);
                cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.TONEMAP_CURVE, tonemapCurve);
                try {
                    cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                            null);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
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
    }

    public class Brightness extends ManualExposureApi2
    {
        int current = 50;
        public Brightness(ParameterHandlerApi2 camParametersHandler, BaseCameraHolderApi2 cameraHolder) {
            super(camParametersHandler, cameraHolder);
        }

        @Override
        public int GetMaxValue() {
            return 100;
        }

        @Override
        public int GetMinValue() {
            return 0;
        }

        @Override
        public int GetValue() {
            return current/4;
        }

        @Override
        public String GetStringValue() {
            return null;
        }

        @Override
        public String[] getStringValues() {
            return null;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            valueToSet = valueToSet *4;
            current = valueToSet;

            float toset = 0;
            if (current > 200)
            {
                toset = (current - 200) * 0.001f;
                midtones[0] = 0.5f - toset;
                midtones[1] = 0.5f + toset;

            }
            if (current == 100)
            {
                midtones[0] = 0.5f;
                midtones[1] = 0.5f;
            }
            else
            {
                toset = (200 - current) * 0.001f;
                midtones[0] = 0.5f + toset;
                midtones[1] = 0.5f - toset;
            }

            Log.d(TAG, "toset:" + toset + " val:" + valueToSet+ " x:" + midtones[0] + " y:"+ midtones[1]);

            float[]tonemap = {blackpoint[0], blackpoint[1], shadows[0],shadows[1], midtones[0], midtones[1], highlights[0], highlights[1],whitepoint[0], whitepoint[1]};
            TonemapCurve tonemapCurve = new TonemapCurve(tonemap,tonemap,tonemap);
            cameraHolder.mPreviewRequestBuilder.set(CaptureRequest.TONEMAP_CURVE, tonemapCurve);
            try {
                cameraHolder.mCaptureSession.setRepeatingRequest(cameraHolder.mPreviewRequestBuilder.build(), cameraHolder.mCaptureCallback,
                        null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e)
            {
                e.printStackTrace();
            }

        }

        @Override
        public boolean IsSupported()
        {
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
    }
}
