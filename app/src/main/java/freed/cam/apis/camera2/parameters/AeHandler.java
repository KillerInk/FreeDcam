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
import android.graphics.Paint;
import android.hardware.camera2.CaptureRequest;
import android.os.Build.VERSION_CODES;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualParameter;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.cam.apis.camera2.parameters.modes.BaseModeApi2;
import freed.utils.Log;
import freed.utils.StringFloatArray;

/**
 * Created by troop on 18.05.2016.
 * Handels all related stuff when ae gets turned off/on and hide/show in that case the manual stuff in ui
 */
@TargetApi(VERSION_CODES.LOLLIPOP)
public class AeHandler
{
    protected final CameraHolderApi2 cameraHolder;
    protected final CameraWrapperInterface cameraUiWrapper;
    public final AeModeApi2 aeModeApi2;
    public final ManualExposureApi2 manualExposureApi2;
    public final ManualExposureTimeApi2 manualExposureTimeApi2;
    public final ManualISoApi2 manualISoApi2;
    protected boolean ae_active = true;

    public final static long MAX_PREVIEW_EXPOSURETIME = 100000000;

    public AeHandler(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = (CameraHolderApi2) cameraUiWrapper.getCameraHolder();
        aeModeApi2 = new AeModeApi2(cameraUiWrapper);
        manualExposureApi2 = new ManualExposureApi2(cameraUiWrapper);
        manualExposureTimeApi2 = new ManualExposureTimeApi2(cameraUiWrapper);
        manualISoApi2 = new ManualISoApi2(cameraUiWrapper);


    }

    //when the ae mode change set the visiblity to the ui items
    protected void setManualItemsSetSupport(boolean off)
    {
        if (off)
        {
            ae_active = false;
            //hide manualexposuretime ui item
            manualExposureApi2.ThrowBackgroundIsSupportedChanged(false);
            //turn flash off when ae is off. else on some devices it applys only manual stuff only for a few frames
            //apply it direct to the preview that old value can get loaded from FocusModeParameter when Ae gets set back to auto
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
            //hide flash ui item its not supported in manual mode
            cameraUiWrapper.getParameterHandler().FlashMode.onIsSupportedChanged(false);
            //enable manualiso item in ui
            manualISoApi2.ThrowBackgroundIsSetSupportedChanged(true);
            //enable manual exposuretime in ui
            manualExposureTimeApi2.ThrowBackgroundIsSetSupportedChanged(true);
            manualExposureTimeApi2.ThrowCurrentValueStringCHanged(manualExposureTimeApi2.GetStringValue());
        }
        else
        {
            ae_active = true;
            //back in auto mode
            //set flash back to its old state
            cameraUiWrapper.getParameterHandler().FlashMode.SetValue(cameraUiWrapper.getParameterHandler().FlashMode.GetValue(),true);
            //show flashmode ui item
            cameraUiWrapper.getParameterHandler().FlashMode.onIsSupportedChanged(true);
            //set exposure ui item to enable
            manualExposureApi2.ThrowBackgroundIsSupportedChanged(true);
            manualExposureApi2.ThrowBackgroundIsSetSupportedChanged(true);
            manualISoApi2.ThrowBackgroundIsSetSupportedChanged(true);
            manualExposureTimeApi2.ThrowBackgroundIsSetSupportedChanged(false);
        }
    }


    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class AeModeApi2 extends BaseModeApi2
    {
        public AeModeApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper,cameraUiWrapper.getAppSettingsManager().exposureMode,CaptureRequest.CONTROL_AE_MODE);
        }

        @Override
        public void SetValue(String valueToSet, boolean setToCamera)
        {
            super.SetValue(valueToSet,setToCamera);
            if (valueToSet.equals(cameraUiWrapper.getContext().getString(R.string.off))) {
                setManualItemsSetSupport(true);
            }
            else {
                setManualItemsSetSupport(false);
            }
            aeModeApi2.onValueHasChanged(valueToSet);
        }
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ManualExposureApi2 extends AbstractManualParameter
    {
        final String TAG = ManualExposureApi2.class.getSimpleName();
        private StringFloatArray expocompvalues;

        public ManualExposureApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            expocompvalues = new StringFloatArray(cameraUiWrapper.getAppSettingsManager().manualExposureCompensation.getValues());
            currentInt = expocompvalues.getSize() / 2;
        }

        @Override
        public int GetValue() {
            return super.GetValue();
        }

        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void SetValue(int valueToSet) {
            if (cameraHolder == null || cameraHolder.captureSessionHandler.GetActiveCameraCaptureSession() == null)
                return;
            currentInt = valueToSet;
            if (expocompvalues == null || expocompvalues.getSize() == 0)
                return;
            setExpoCompensation(valueToSet);
        }



        @Override
        public String GetStringValue() {
            return expocompvalues.getKey(currentInt);
        }

        @Override
        public String[] getStringValues() {
            return expocompvalues.getKeys();
        }

        @Override
        public boolean IsSupported() {
            return cameraUiWrapper.getAppSettingsManager().manualExposureCompensation.isSupported();
        }

        @Override
        public boolean IsSetSupported() {
            return ae_active;
        }

        @Override
        public boolean IsVisible() {
            return true;
        }

    }

    protected void setExpoCompensation(int valueToSet) {
        int t = valueToSet - manualExposureApi2.getStringValues().length / 2;
        cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, t);
    }

    /**
     * Created by troop on 06.03.2015.
     */
    @TargetApi(VERSION_CODES.LOLLIPOP)
    public class ManualExposureTimeApi2 extends AbstractManualShutter
    {
        public final String TAG = ManualExposureTimeApi2.class.getSimpleName();
        public ManualExposureTimeApi2(CameraWrapperInterface cameraUiWrapper) {
            super(cameraUiWrapper);
            isSupported = cameraUiWrapper.getAppSettingsManager().manualExposureTime.isSupported();
            if (isSupported)
                stringvalues = cameraUiWrapper.getAppSettingsManager().manualExposureTime.getValues();
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
            if (valueToSet >= manualExposureTimeApi2.getStringValues().length)
                valueToSet = manualExposureTimeApi2.getStringValues().length - 1;
            currentInt = valueToSet;
            setExposureTime(valueToSet);

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
            return true;
        }
    }

    protected void setExposureTime(int valueToSet)
    {
        if (valueToSet > 0) {
            long val = AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTimeApi2.getStringValues()[valueToSet]) * 1000;
            Log.d(manualExposureTimeApi2.TAG, "ExposureTimeToSet:" + val);
            cameraHolder.captureSessionHandler.SetCaptureParameter(CaptureRequest.SENSOR_EXPOSURE_TIME,val);
            if (val > MAX_PREVIEW_EXPOSURETIME && !cameraUiWrapper.getAppSettingsManager().GetCurrentModule().equals(cameraUiWrapper.getResString(R.string.module_video))) {
                Log.d(manualExposureTimeApi2.TAG, "ExposureTime Exceed 0,8sec for preview, set it to 0,8sec");
                val = MAX_PREVIEW_EXPOSURETIME;
            }

            cameraHolder.captureSessionHandler.SetPreviewParameterRepeating(CaptureRequest.SENSOR_EXPOSURE_TIME, val);
            manualExposureTimeApi2.ThrowCurrentValueChanged(valueToSet);
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
            isSupported = cameraUiWrapper.getAppSettingsManager().manualIso.isSupported();
            if (isSupported)
                stringvalues = cameraUiWrapper.getAppSettingsManager().manualIso.getValues();
        }

        @Override
        public boolean IsVisible() {
            return true;
        }

        @Override
        public void SetValue(int valueToSet)
        {
            //workaround when value was -1 to avoid outofarray ex
            Log.d(TAG, "set Manual Iso: " +valueToSet);
            if (valueToSet == -1)
                valueToSet = 0;
            //////////////////////
            currentInt = valueToSet;
            setIso(valueToSet);
        }

        @Override
        public boolean IsSetSupported() {
            return true;
        }
    }

    protected void setIso(int valueToSet)
    {
        if (cameraHolder == null || cameraHolder.captureSessionHandler.GetActiveCameraCaptureSession() == null)
            return;
        if (valueToSet == 0)
        {
            aeModeApi2.SetValue(cameraUiWrapper.getAppSettingsManager().exposureMode.get(),true);
        }
        else
        {
            if (ae_active)
                aeModeApi2.SetValue(cameraUiWrapper.getContext().getString(R.string.off),true);
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, Integer.parseInt(manualISoApi2.getStringValues()[valueToSet]));
        }
    }

}
