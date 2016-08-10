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

package freed.cam.apis.camera2.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;

import java.util.ArrayList;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.utils.Logger;

/**
 * Created by troop on 18.05.2016.
 * Handels all related stuff when ae gets turned off/on and hide/show in that case the manual stuff in ui
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class AeHandler
{
    private final CameraHolderApi2 cameraHolder;
    private final CameraWrapperInterface cameraUiWrapper;
    private final ParameterHandler parameterHandler;
    private final AeModeApi2 aeModeApi2;
    private final ManualExposureApi2 manualExposureApi2;
    private final ManualExposureTimeApi2 manualExposureTimeApi2;
    private final ManualISoApi2 manualISoApi2;

    private AEModes activeAeMode = AEModes.on;

    public AeHandler(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.GetCameraHolder();
        this.parameterHandler = (ParameterHandler) cameraUiWrapper.GetParameterHandler();
        aeModeApi2 = new AeModeApi2(cameraUiWrapper);
        manualExposureApi2 = new ManualExposureApi2(cameraUiWrapper);
        manualExposureTimeApi2 = new ManualExposureTimeApi2(cameraUiWrapper);
        manualISoApi2 = new ManualISoApi2(cameraUiWrapper);
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
            manualExposureApi2.ThrowBackgroundIsSupportedChanged(false);
            //turn flash off when ae is off. else on some devices it applys only manual stuff only for a few frames
            //apply it direct to the preview that old value can get loaded from FocusModeParameter when Ae gets set back to auto
            cameraHolder.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            //hide flash ui item its not supported in manual mode
            cameraUiWrapper.GetParameterHandler().FlashMode.BackgroundIsSupportedChanged(false);
            //enable manualiso item in ui
            manualISoApi2.ThrowBackgroundIsSetSupportedChanged(true);
            //enable manual exposuretime in ui
            manualExposureTimeApi2.ThrowBackgroundIsSetSupportedChanged(true);
            manualExposureTimeApi2.ThrowCurrentValueStringCHanged(manualExposureTimeApi2.GetStringValue());
        }
        else
        {
            //back in auto mode
            //set flash back to its old state
            cameraUiWrapper.GetParameterHandler().FlashMode.SetValue(cameraUiWrapper.GetParameterHandler().FlashMode.GetValue(),true);
            //show flashmode ui item
            cameraUiWrapper.GetParameterHandler().FlashMode.BackgroundIsSupportedChanged(true);
            //set exposure ui item to enable
            manualExposureApi2.ThrowBackgroundIsSupportedChanged(true);
            manualExposureApi2.ThrowBackgroundIsSetSupportedChanged(true);
            manualISoApi2.ThrowBackgroundIsSetSupportedChanged(true);
            manualExposureTimeApi2.ThrowBackgroundIsSetSupportedChanged(false);
        }
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
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

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class AeModeApi2 extends BaseModeApi2
    {
        private boolean isSupported;
        private final String[] aemodeStringValues;
        public AeModeApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            int[] values = AeHandler.this.cameraHolder.characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
            aemodeStringValues = new String[values.length];
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
                isSupported = true;
        }

        @Override
        public boolean IsSupported()
        {
            return isSupported;
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

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ManualExposureApi2 extends AbstractManualParameter
    {
        final String TAG = ManualExposureApi2.class.getSimpleName();

        public ManualExposureApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
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

        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void SetValue(int valueToSet) {
            if (cameraHolder == null || cameraHolder.CaptureSessionH.GetActiveCameraCaptureSession() == null)
                return;
            currentInt = valueToSet;
            if (stringvalues == null || stringvalues.length == 0)
                return;
            int t = valueToSet - stringvalues.length / 2;
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
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ManualExposureTimeApi2 extends AbstractManualShutter
    {
        final String TAG = ManualExposureTimeApi2.class.getSimpleName();
        private int millimax;
        public ManualExposureTimeApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            isSupported = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE) != null;
            try {
                findMinMaxValue();
            }
            catch (NullPointerException ex)
            {
                ex.printStackTrace();
                isSupported = false;
            }
        }

        private void findMinMaxValue()
        {

            Logger.d(TAG, "max exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper());
            Logger.d(TAG, "min exposuretime:" + cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower());
            //866 975 130 = 0,8sec
            switch(cameraUiWrapper.GetAppSettingsManager().getDevice())
            {
                case LG_G4:
                    if (VERSION.SDK_INT <= VERSION_CODES.LOLLIPOP_MR1)
                        millimax = 60000000;
                    else
                        millimax = 45000000;
                    break;
                case OnePlusTwo:
                    millimax = 32000000;
                    break;
                case Samsung_S6_edge_plus:
                    millimax = 10000000;
                    break;
                case Moto_X_Style_Pure_Play:
                    millimax = 10000000;
                    break;
                default:
                    millimax = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getUpper().intValue() / 1000;
                    if (millimax == 0)
                        millimax = 800000;
                    break;
            }
            int millimin = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).getLower().intValue() / 1000;
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

        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void SetValue(int valueToSet)
        {
            if (cameraHolder == null)
                return;
            if (valueToSet >= stringvalues.length)
                valueToSet = stringvalues.length - 1;
            currentInt = valueToSet;
            if (valueToSet > 0) {
                long val = (long) (AbstractManualShutter.getMilliSecondStringFromShutterString(stringvalues[valueToSet]) * 1000f);
                Logger.d(TAG, "ExposureTimeToSet:" + val);
                if (val > 800000000 &&!cameraUiWrapper.GetParameterHandler().Module.GetValue().equals("Stack")) {
                    Logger.d(TAG, "ExposureTime Exceed 0,8sec for preview, set it to 0,8sec");
                    val = 800000000;
                }
                //check if calced value is not bigger then max returned from cam
                if (val > millimax *1000)
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
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ManualISoApi2 extends ManualExposureTimeApi2
    {
        final String TAG = ManualISoApi2.class.getSimpleName();
        public ManualISoApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            currentInt = 0;
            ArrayList<String> ar = new ArrayList<>();
            try {
                for (int i = 0; i <= cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper(); i += 50) {
                    if (i == 0)
                        ar.add("auto");
                    else
                        ar.add(i + "");
                }
                stringvalues = new String[ar.size()];
                ar.toArray(stringvalues);
            }
            catch (NullPointerException ex)
            {
                isSupported = false;
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
