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
import android.graphics.Paint;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.camera1.CameraHolder;
import freed.cam.apis.camera2.CameraHolderApi2;
import freed.utils.Logger;

/**
 * Created by troop on 17.08.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AeBracketApi2 extends PictureModuleApi2 implements CameraHolderApi2.AeCompensationListner
{

    private final String TAG = AeBracketApi2.class.getSimpleName();
    private int imagecount = 0;

    private final int WAIT_FOR_EXPO_SET = 2;
    private final int WAIT_NOTHING = 3;
    private int WAIT_EXPOSURE_STATE = WAIT_NOTHING;


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
        cameraUiWrapper.GetParameterHandler().Burst.SetValue(0);
        super.InitModule();
    }

    @Override
    public boolean DoWork() {
        Logger.d(TAG,"NEW BRACKET CAPTURE START");
        return super.DoWork();
    }

    @Override
    protected void captureStillPicture()
    {
        Logger.d(TAG,"captureStillPicture:"+imagecount);
        super.captureStillPicture();
    }

    @Override
    protected void prepareCaptureBuilder(Builder captureBuilder)
    {
        super.prepareCaptureBuilder(captureBuilder);
    }

    @Override
    protected void finishCapture(Builder captureBuilder)
    {
        super.finishCapture(captureBuilder);
        Logger.d(TAG,"finishCapture:"+imagecount);
        imagecount++;
        if (imagecount < 3)
        {
            cameraHolder.SetAeCompensationListner(this);
            WAIT_EXPOSURE_STATE = WAIT_FOR_EXPO_SET;
            Logger.d(TAG,"Set next Exposure for Image: "+ imagecount);
            switch (imagecount)
            {
                case 0:
                    Logger.d(TAG, "should not happen");
                    break;
                case 1:
                    cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, -10);
                    Logger.d(TAG,"SetExposure to: -10");
                    break;
                case 2:
                    cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 10);
                    Logger.d(TAG,"SetExposure to: 10");
                    break;
            }
        }
        else
        {
            Logger.d(TAG,"Finished Capture");
            cameraHolder.SetAeCompensationListner(null);
            imagecount = 0;
            cameraHolder.SetParameterRepeating(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
        }
    }

    @Override
    public void onAeCompensationChanged(int aecompensation)
    {
        Logger.d(TAG, "FrameExposureCompensation:" + aecompensation);
        if (WAIT_EXPOSURE_STATE == WAIT_NOTHING)
        {
            Logger.d(TAG, "onAeCompensationChanged:WAIT_FORNOTHING");
            cameraHolder.SetAeCompensationListner(null);
            return;
        }
        else
        {
            Logger.d(TAG, "onAeCompensationChanged:WAIT_FOR_EXPO");
            switch (imagecount)
            {
                case 1:
                    if (aecompensation == -10) {
                        Logger.d(TAG,"AE:-10 Capture Image " + imagecount);
                        WAIT_EXPOSURE_STATE = WAIT_NOTHING;
                        captureStillPicture();
                    }
                    break;
                case 2:
                    if (aecompensation == 10) {
                        WAIT_EXPOSURE_STATE = WAIT_NOTHING;
                        Logger.d(TAG,"AE:10 Capture Image " + imagecount);
                        captureStillPicture();
                    }
                    break;
            }
        }
    }
}
