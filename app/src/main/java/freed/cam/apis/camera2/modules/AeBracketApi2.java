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
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera2.parameters.AeHandler;

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
    long currentExposureTime = 0;
    long exposureTimeStep = 0;


    public AeBracketApi2(CameraWrapperInterface cameraUiWrapper) {
        super(cameraUiWrapper);
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
        cameraUiWrapper.GetParameterHandler().Burst.SetValue(2);
        super.InitModule();
    }

    @Override
    protected void initBurstCapture(Builder captureBuilder, CameraCaptureSession.CaptureCallback captureCallback)
    {
        currentExposureTime = cameraHolder.get(CaptureRequest.SENSOR_EXPOSURE_TIME);
        exposureTimeStep = currentExposureTime/2;
        List<CaptureRequest> captureList = new ArrayList<>();
        captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, AeHandler.AEModes.off.ordinal());
        captureBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, cameraHolder.get(CaptureRequest.SENSOR_SENSITIVITY));
        for (int i = 0; i < parameterHandler.Burst.GetValue()+1; i++)
        {
            if (0 == i)
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, currentExposureTime - exposureTimeStep);
            else if (1== i)
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, currentExposureTime);
            else if (2 == i)
                captureBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME,currentExposureTime + exposureTimeStep);
            captureBuilder.setTag(i);
            ImageSaver.ImageSaverBuilder jpegBuilder = new ImageSaver.ImageSaverBuilder(cameraUiWrapper.getContext(), cameraUiWrapper)
                    .setCharacteristics(cameraHolder.characteristics);
            mJpegResultQueue.put(i, jpegBuilder);
            captureList.add(captureBuilder.build());
        }
        cameraHolder.CaptureSessionH.StopRepeatingCaptureSession();
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);
        cameraHolder.CaptureSessionH.StartCaptureBurst(captureList, captureCallback);
    }

}
