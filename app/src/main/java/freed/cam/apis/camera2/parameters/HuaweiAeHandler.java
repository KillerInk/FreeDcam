package freed.cam.apis.camera2.parameters;

import android.annotation.TargetApi;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Rational;

import com.huawei.camera2ex.CaptureRequestEx;
import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.manual.AbstractManualShutter;

/**
 * Created by troop on 08.06.2017.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class HuaweiAeHandler extends AeHandler {
    public HuaweiAeHandler(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
        manualExposureTimeApi2.ThrowBackgroundIsSetSupportedChanged(true);
    }

    @Override
    protected void setManualItemsSetSupport(boolean off) {
        /*if (off)
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
        }*/
    }


    @Override
    protected void setExposureTime(int valueToSet) {
        if (valueToSet > 0) {
            if (manualISoApi2.GetValue() == 0 && cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_SHUTTER_PRIORITY)
            {
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_SHUTTER_PRIORITY);
            }
            else if (cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_MANUAL)
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_MANUAL);
            String shutter = manualExposureTimeApi2.getStringValues()[valueToSet];
            Rational rational;
            if (shutter.contains("/"))
            {
                String[]split = shutter.split("/");
                rational = new Rational(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
            }
            else if (shutter.contains("."))
            {
                double sh = Double.parseDouble(shutter);
                double sh10 = sh*10;
                rational = new Rational((int)sh10,10);
            }
            else
            {
                rational = new Rational(Integer.parseInt(shutter),1);
            }
            int val = (int) AbstractManualShutter.getMilliSecondStringFromShutterString(manualExposureTimeApi2.getStringValues()[valueToSet]);
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME, val);
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROF_EXPOSURE_TIME, rational);
            manualExposureTimeApi2.ThrowCurrentValueChanged(valueToSet);

        }
        else
        {
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_EXPOSURE_TIME, 0);
            if (manualISoApi2.GetValue() == 0 && cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_AE_ACTIVE)
            {
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_AE_ACTIVE);
            }
            else if (cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ISO_PRIORITY)
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ISO_PRIORITY);
        }
        cameraHolder.captureSessionHandler.capture();
    }

    @Override
    protected void setIso(int valueToSet) {
        if (cameraHolder == null || cameraHolder.captureSessionHandler.GetActiveCameraCaptureSession() == null)
            return;
        if (valueToSet == 0)
        {
            if (manualExposureTimeApi2.GetValue() == 0 && cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_AE_ACTIVE)
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_AE_ACTIVE);
            else if (cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_SHUTTER_PRIORITY)
                cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_SHUTTER_PRIORITY);
            aeModeApi2.SetValue(cameraUiWrapper.getResString(R.string.on),true);
            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, 0);
        }
        else
        {

                if (manualExposureTimeApi2.GetValue() == 0 && cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ISO_PRIORITY)
                    cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_ISO_PRIORITY);
                else if (cameraHolder.captureSessionHandler.get(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE) != CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_MANUAL)
                    cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE,CaptureRequestEx.HUAWEI_PROFESSIONAL_MODE_MANUAL);
                aeModeApi2.SetValue(cameraUiWrapper.getResString(R.string.off),true);

            cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_SENSOR_ISO_VALUE, Integer.parseInt(manualISoApi2.getStringValues()[valueToSet]));
        }
        cameraHolder.captureSessionHandler.capture();
    }

    @Override
    protected void setExpoCompensation(int valueToSet) {
        float t = Float.parseFloat(manualExposureApi2.getStringValues()[valueToSet].replace(",","."));
        cameraHolder.captureSessionHandler.SetParameterRepeating(CaptureRequestEx.HUAWEI_EXPOSURE_COMP_VALUE, t);
    }
}
