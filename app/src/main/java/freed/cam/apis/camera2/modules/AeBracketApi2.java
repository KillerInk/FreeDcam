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
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.os.Build;
import android.os.Handler;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.parameters.AeHandler;
import freed.utils.Logger;

/**
 * Created by troop on 17.08.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeBracketApi2 extends PictureModuleApi2
{

    private final String TAG = AeBracketApi2.class.getSimpleName();
    private int imagecount = 0;

    private final int WAIT_FOR_EXPO_SET = 2;
    private final int WAIT_NOTHING = 3;
    private int WAIT_EXPOSURE_STATE = WAIT_NOTHING;
    private long currentExposureTime = 0;
    private long exposureTimeStep = 0;
    private boolean aeWasOn = false;
    private int maxiso;
    private int currentiso;


    public AeBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
        name = KEYS.MODULE_HDR;
    }

    @Override
    public String ShortName() {
        return "Bracket";
    }

    @Override
    public String LongName() {
        return "Bracketing";
    }

    @Override
    public void InitModule() {
        super.InitModule();
        cameraUiWrapper.GetParameterHandler().Burst.SetValue(2);
        maxiso = cameraHolder.characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).getUpper();
    }

    @Override
    public void DestroyModule() {
        cameraUiWrapper.GetParameterHandler().Burst.SetValue(0);
        super.DestroyModule();
    }

    @Override
    protected void initBurstCapture(Builder captureBuilder, CameraCaptureSession.CaptureCallback captureCallback)
    {
        currentExposureTime = cameraHolder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
        exposureTimeStep = currentExposureTime/2;
        if (cameraHolder.get(CaptureRequest.CONTROL_AE_MODE) != AeHandler.AEModes.off.ordinal()) {
            aeWasOn = true;
            cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE, AeHandler.AEModes.off.ordinal());
            currentiso = cameraHolder.get(CaptureRequest.SENSOR_SENSITIVITY);
            if (currentiso >= maxiso)
                currentiso = maxiso;
            cameraHolder.SetParameterRepeating(CaptureRequest.SENSOR_SENSITIVITY, currentiso);
            captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME,currentExposureTime);
        }
        else
            aeWasOn = false;

        super.initBurstCapture(captureBuilder,captureCallback);
    }

    @Override
    protected void setupBurstCaptureBuilder(Builder captureBuilder, int captureNum)
    {
        captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, AeHandler.AEModes.off.ordinal());
        int curIso = cameraHolder.get(CaptureRequest.SENSOR_SENSITIVITY);
        if (curIso >= maxiso)
            curIso = maxiso;
        Logger.d(TAG, "set iso to :" + curIso);
        long expotimeToSet = currentExposureTime;
        captureBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, curIso);
        if (0 == captureNum)
            expotimeToSet = currentExposureTime - exposureTimeStep;
        else if (1== captureNum)
            expotimeToSet = currentExposureTime;
        else if (2 == captureNum)
             expotimeToSet = currentExposureTime + exposureTimeStep;
        Logger.d(TAG,"Set shutter to:" + expotimeToSet);
        captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME,expotimeToSet);
        super.setupBurstCaptureBuilder(captureBuilder, captureNum);
    }

    @Override
    protected void finishCapture(Builder captureBuilder) {
        super.finishCapture(captureBuilder);
        if (aeWasOn)
            cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_MODE, AeHandler.AEModes.on.ordinal());
    }
}
