package com.freedcam.apis.camera2.camera.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualParameter;
import com.freedcam.apis.basecamera.camera.parameters.manual.AbstractManualShutter;
import com.freedcam.apis.camera2.camera.CameraHolder;
import com.freedcam.apis.camera2.camera.parameters.modes.BaseModeApi2;
import com.freedcam.utils.DeviceUtils;
import com.freedcam.utils.Logger;

import java.util.ArrayList;

/**
 * Created by troop on 18.05.2016.
 * Handels all related stuff when ae gets turned off/on and hide/show in that case the manual stuff in ui
 */
public class AeHandlerApi2
{
    private CameraHolder cameraHolder;
    private ParameterHandlerApi2 parameterHandler;
    private AeModeApi2 aeModeApi2;
    private ManualExposureApi2 manualExposureApi2;
    private ManualExposureTimeApi2 manualExposureTimeApi2;
    private ManualISoApi2 manualISoApi2;

    private AEModes activeAeMode = AEModes.on;

    public AeHandlerApi2(Handler handler, CameraHolder cameraHolder, ParameterHandlerApi2 parameterHandler)
    {
        this.cameraHolder = cameraHolder;
        this.parameterHandler = parameterHandler;
        aeModeApi2 = new AeModeApi2(this.cameraHolder);
        manualExposureApi2 = new ManualExposureApi2(parameterHandler);
        manualExposureTimeApi2 = new ManualExposureTimeApi2(parameterHandler);
        manualISoApi2 = new ManualISoApi2(parameterHandler);
        //pass stuff to the parameterhandler that it get used by the ui
        parameterHandler.ExposureMode = aeModeApi2;
        parameterHandler.ManualShutter = manualExposureTimeApi2;
        parameterHandler.ManualExposure = manualExposureApi2;
        parameterHandler.ManualIso = manualISoApi2;

    }

    public enum AEModes
    {
        off,
        on,
        on_auto_flash,
        on_always_flash,
        on_auto_flash_redeye,
    }

    //when the ae mode change set the visiblity to the ui items
    private void setManualItemsSetSupport(AEModes aeModes)
    {
        if (aeModes == AEModes.off)
        {
            //hide manualexposuretime ui item
            manualExposureApi2.BackgroundIsSupportedChanged(false);
            //enable manualiso item in ui
            manualISoApi2.BackgroundIsSetSupportedChanged(true);
            //enable manual exposuretime in ui
            manualExposureTimeApi2.BackgroundIsSetSupportedChanged(true);
        }
        else
        {
            manualExposureApi2.BackgroundIsSupportedChanged(true);
            manualExposureApi2.BackgroundIsSetSupportedChanged(true);
            manualISoApi2.BackgroundIsSetSupportedChanged(true);
            manualExposureTimeApi2.BackgroundIsSetSupportedChanged(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    /**
     * set the new aemode to the camera
     */
    private void setAeMode(AEModes aeMode)
    {
        activeAeMode = aeMode;
        cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE, activeAeMode.ordinal());
        aeModeApi2.BackgroundValueHasChanged(activeAeMode.toString());
        setManualItemsSetSupport(activeAeMode);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class AeModeApi2 extends BaseModeApi2
    {
        private boolean isSupported = false;
        private String[] aemodeStringValues;
        public AeModeApi2(CameraHolder cameraHolder) {
            super(cameraHolder);
            int[] values = AeHandlerApi2.this.cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
            aemodeStringValues= new String[values.length];
            for (int i = 0; i < values.length; i++)
            {
                try {
                    AEModes sceneModes = AEModes.values()[values[i]];
                    aemodeStringValues[i] = sceneModes.toString();
                }
                catch (Exception ex)
                {
                    aemodeStringValues[i] = "unknown Scene" + values[i];
                }
            }
            if (aemodeStringValues.length > 1)
                this.isSupported = true;
        }

        @Override
        public boolean IsSupported()
        {
            return this.isSupported;
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            if (valueToSet.contains("unknown Scene"))
                return;
            setAeMode(Enum.valueOf(AEModes.class, valueToSet));
        }

        @Override
        public String GetValue()
        {
            if (cameraHolder == null)
                return null;
            int i = cameraHolder.get(CaptureRequest.CONTROL_AE_MODE);
            AEModes sceneModes = AEModes.values()[i];
            return sceneModes.toString();
        }

        @Override
        public String[] GetValues()
        {
            return aemodeStringValues;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class ManualExposureApi2 extends AbstractManualParameter
    {
        final String TAG = ManualExposureApi2.class.getSimpleName();

        public ManualExposureApi2(ParameterHandlerApi2 parameterHandlerApi2) {
            super(parameterHandlerApi2);
            int max = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getUpper();
            int min = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE).getLower();
            float step = cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_STEP).floatValue();
            stringvalues = createStringArray(min, max, step);
            currentInt = stringvalues.length / 2;
        }

        protected String[] createStringArray(int min, int max, float stepp) {
            ArrayList<String> ar = new ArrayList<>();
            for (int i = min; i <= max; i++) {
                String s = String.format("%.1f", i * stepp);
                ar.add(s);
            }
            return ar.toArray(new String[ar.size()]);
        }

        @Override
        public int GetValue() {
            return super.GetValue();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void SetValue(int valueToSet) {
            if (cameraHolder == null || cameraHolder.CaptureSessionH.GetActiveCameraCaptureSession() == null)
                return;
            currentInt = valueToSet;
            if (stringvalues == null || stringvalues.length == 0)
                return;
            int t = valueToSet - (stringvalues.length / 2);
            cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, t);
        }

        @Override
        public boolean IsSupported() {
            return cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE) != null;
        }

        @Override
        public boolean IsSetSupported() {
            return activeAeMode != AEModes.off;
        }

        @Override
        public boolean IsVisible() {
            return true;
        }

    }

    /**
     * Created by troop on 06.03.2015.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class ManualExposureTimeApi2 extends AbstractManualShutter
    {
        boolean isSupported = false;
        final String TAG = ManualExposureTimeApi2.class.getSimpleName();
        private int millimax = 0;
        public ManualExposureTimeApi2(ParameterHandlerApi2 camParametersHandler) {
            super(camParametersHandler);
            this.isSupported = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE) != null;
            try {
                findMinMaxValue();
            }
            catch (NullPointerException ex)
            {
                this.isSupported = false;
            }
        }

        private void findMinMaxValue()
        {

            Logger.d(TAG, "max exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper());
            Logger.d(TAG, "min exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower());
            //866 975 130 = 0,8sec
            if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4) && Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1)
                millimax = 60000000;
            else if (DeviceUtils.IS(DeviceUtils.Devices.LG_G4) && Build.VERSION.SDK_INT == Build.VERSION_CODES.M)
                millimax = 45000000;
            else if (DeviceUtils.IS(DeviceUtils.Devices.Samsung_S6_edge_plus))
                millimax = 10000000;
            else if (DeviceUtils.IS(DeviceUtils.Devices.Moto_MSM8982_8994))
                millimax = 10000000;
            else
                millimax = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper()).intValue() / 1000;
            int millimin = (cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower()).intValue() / 1000;
            stringvalues = getSupportedShutterValues(millimin, millimax,false);
        }

        @Override
        public int GetValue()
        {
            return currentInt;
        }

        @Override
        public String GetStringValue()
        {
            if (stringvalues == null || stringvalues.length == 0 || currentInt > stringvalues.length)
                return "error";
            return stringvalues[currentInt];
        }


        @Override
        public String[] getStringValues() {
            return stringvalues;
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void SetValue(int valueToSet)
        {
            if (cameraHolder == null)
                return;
            if (valueToSet >= stringvalues.length)
                valueToSet = stringvalues.length - 1;
            currentInt = valueToSet;
            if (valueToSet > 0) {
                long val = (long) (getMilliSecondStringFromShutterString(stringvalues[valueToSet]) * 1000f);
                Logger.d(TAG, "ExposureTimeToSet:" + val);
                if (val > 800000000 &&!camParametersHandler.Module.GetValue().equals("Stack")) {
                    Logger.d(TAG, "ExposureTime Exceed 0,8sec for preview, set it to 0,8sec");
                    val = 800000000;
                }
                //check if calced value is not bigger then max returned from cam
                if (val > millimax*1000)
                    val = millimax *1000;
                cameraHolder.SetParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
                ThrowCurrentValueChanged(valueToSet);
            }
        }

        @Override
        public boolean IsSupported()
        {
            return isSupported;
        }

        @Override
        public boolean IsVisible() {
            return isSupported;
        }

        @Override
        public boolean IsSetSupported() {
            return activeAeMode == AEModes.off;
        }
    }

    /**
     * Created by troop on 28.04.2015.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class ManualISoApi2 extends ManualExposureTimeApi2
    {
        final String TAG = ManualISoApi2.class.getSimpleName();
        public ManualISoApi2(ParameterHandlerApi2 camParametersHandler) {
            super(camParametersHandler);
            currentInt = 0;
            ArrayList<String> ar = new ArrayList<>();
            try {
                for (int i = 0; i <= cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper(); i += 50) {
                    if (i == 0)
                        ar.add("auto");
                    else
                        ar.add(i + "");
                }
                this.stringvalues = new String[ar.size()];
                ar.toArray(stringvalues);
            }
            catch (NullPointerException ex)
            {
                this.isSupported = false;
            }
        }

        @Override
        public boolean IsVisible() {
            return true;
        }

        @Override
        public boolean IsSupported() {
            return cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE) != null;
        }


        @Override
        public void SetValue(int valueToSet)
        {
            //workaround when value was -1 to avoid outofarray ex
            Logger.d(TAG, "set Manual Iso: " +valueToSet);
            if (valueToSet == -1)
                valueToSet = 0;
            //////////////////////
            currentInt = valueToSet;
            if (cameraHolder == null || cameraHolder.CaptureSessionH.GetActiveCameraCaptureSession() == null)
                return;
            if (valueToSet == 0)
            {
                setAeMode(AEModes.on);
            }
            else
            {
                if (activeAeMode != AEModes.off)
                    setAeMode(AEModes.off);
                cameraHolder.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, Integer.parseInt(stringvalues[valueToSet]));
            }
        }

        @Override
        public boolean IsSetSupported() {
            return true;
        }
    }

}
