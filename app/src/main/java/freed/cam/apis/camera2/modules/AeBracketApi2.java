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

package freed.cam.apis.camera2.modules;

import android.annotation.TargetApi;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.settings.Settings;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 17.08.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeBracketApi2 extends PictureModuleApi2
{

    private final String TAG = AeBracketApi2.class.getSimpleName();

    private final int WAIT_FOR_EXPO_SET = 2;
    private final int WAIT_NOTHING = 3;
    private int WAIT_EXPOSURE_STATE = WAIT_NOTHING;
    long currentExposureTime = 0;
    long exposureTimeStep = 0;
    private boolean aeWasOn = false;
    int maxiso;
    int currentiso;


    public AeBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_hdr);
    }

    @Override
    public String ShortName() {
        return "AeBracket";
    }

    @Override
    public String LongName() {
        return "Ae-Bracket";
    }

    @Override
    public void InitModule() {
        super.InitModule();
        cameraUiWrapper.getParameterHandler().get(Settings.M_Burst).fireIsReadOnlyChanged(false);
        cameraUiWrapper.getParameterHandler().get(Settings.M_Burst).SetValue(3-1, true);
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);
    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
        cameraUiWrapper.getParameterHandler().get(Settings.M_Burst).fireIsReadOnlyChanged(true);

    }

    @Override
    protected void onStartTakePicture() {
        maxiso = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper();
        currentExposureTime = cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.SENSOR_EXPOSURE_TIME);
        currentiso = cameraUiWrapper.captureSessionHandler.getPreviewParameter(CaptureRequest.SENSOR_SENSITIVITY);
        if (currentExposureTime == 0)
        {
            currentExposureTime = cameraUiWrapper.cameraBackroundValuesChangedListner.currentExposureTime;
        }
        if (currentiso == 0)
            currentiso = cameraUiWrapper.cameraBackroundValuesChangedListner.currentIso;
        exposureTimeStep = currentExposureTime/2;
        aeWasOn = !SettingsManager.get(Settings.ExposureMode).get().equals(cameraUiWrapper.getActivityInterface().getContext().getString(R.string.off));
    }

    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        long expotimeToSet = currentExposureTime;
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_OFF);

        if (currentiso >= maxiso)
            currentiso = maxiso;
        if (currentiso == 0)
            currentiso = 100;
        Log.d(TAG, "set iso to :" + currentiso);
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.SENSOR_SENSITIVITY, currentiso);
        if (0 == captureNum)
            expotimeToSet = currentExposureTime - exposureTimeStep;
        else if (1== captureNum)
            expotimeToSet = currentExposureTime;
        else if (2 == captureNum)
            expotimeToSet = currentExposureTime + exposureTimeStep;
        Log.d(TAG,"Set shutter to:" + expotimeToSet);
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.SENSOR_EXPOSURE_TIME,expotimeToSet);
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.SENSOR_FRAME_DURATION, expotimeToSet);
        Log.d(TAG, "request: " +captureNum + " AE Mode:" + cameraUiWrapper.captureSessionHandler.getImageCaptureParameter(CaptureRequest.CONTROL_AE_MODE) + " Expotime:" + cameraUiWrapper.captureSessionHandler.getImageCaptureParameter(CaptureRequest.SENSOR_EXPOSURE_TIME) + " iso:" + cameraUiWrapper.captureSessionHandler.getImageCaptureParameter(CaptureRequest.SENSOR_SENSITIVITY));
    }


    @Override
    protected void finishCapture() {
        super.finishCapture();

        if (imagecount == 3) {
            if (aeWasOn && parameterHandler.get(Settings.ExposureMode) != null)
                parameterHandler.get(Settings.ExposureMode).SetValue(cameraUiWrapper.getActivityInterface().getContext().getString(R.string.on),true);

        }
    }

}
