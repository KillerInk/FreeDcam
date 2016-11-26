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
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.os.Build;
import android.os.Handler;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.AppSettingsManager;

/**
 * Created by troop on 18.08.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AfBracketApi2 extends PictureModuleApi2
{
    public AfBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler) {
        super(cameraUiWrapper,mBackgroundHandler);
        name = KEYS.MODULE_AFBRACKET;
    }

    private final int PICSTOTAKE = 10;

    private int focusStep;
    private int currentFocusPos;
    private int focuslength;
    private int min;

    @Override
    public String ShortName() {
        return "AfBracket";
    }

    @Override
    public String LongName() {
        return "Af-Bracket";
    }

    @Override
    public void InitModule() {
        super.InitModule();
        cameraUiWrapper.GetParameterHandler().Burst.SetValue(PICSTOTAKE-1);
        focuslength = parameterHandler.ManualFocus.getStringValues().length -1;
        focusStep =  focuslength/PICSTOTAKE;
        currentFocusPos = 1;

    }


    @Override
    protected void initBurstCapture(Builder captureBuilder, CaptureCallback captureCallback)
    {
        int max  = 0;
        try {
            min = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AFBRACKETMIN));
            max = Integer.parseInt(appSettingsManager.getString(AppSettingsManager.SETTING_AFBRACKETMAX));
        }
        catch (NumberFormatException ex)
        {
            min = 0;
            max = 0;
        }

        if (min == 0 && max == 0)
        {
            focuslength = parameterHandler.ManualFocus.getStringValues().length -1;
            focusStep = focuslength /PICSTOTAKE;
            currentFocusPos = 1;
        }
        else
        {
            focuslength = max - min;
            focusStep = focuslength /PICSTOTAKE;
            currentFocusPos = min;
        }
        super.initBurstCapture(captureBuilder,captureCallback);
        /*List<CaptureRequest> captureList = new ArrayList<>();
        captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, FocusModeApi2.FocusModes.off.ordinal());
        for (int i = 0; i < parameterHandler.Burst.GetValue()+1; i++)
        {
            captureBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, (float) currentFocusPos / 10);
            captureList.add(captureBuilder.build());
            currentFocusPos +=focusStep;
            if (currentFocusPos > focuslength+min)
                currentFocusPos = focuslength+min;
        }
        if (cameraHolder.get(CaptureRequest.SENSOR_EXPOSURE_TIME) > 500000*1000)
            cameraHolder.CaptureSessionH.StopRepeatingCaptureSession();
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_start);
        cameraHolder.CaptureSessionH.StartCaptureBurst(captureList, captureCallback, mBackgroundHandler);*/
    }

    @Override
    protected void setupBurstCaptureBuilder(Builder captureBuilder, int captureNum) {
        captureBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, (float) currentFocusPos / 10);
        currentFocusPos +=focusStep;
        if (currentFocusPos > focuslength+min)
            currentFocusPos = focuslength+min;
    }
}
