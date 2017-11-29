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
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.settings.AppSettingsManager;

/**
 * Created by troop on 18.08.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AfBracketApi2 extends PictureModuleApi2
{
    public AfBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_afbracket);
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
        cameraUiWrapper.getParameterHandler().Burst.SetValue(PICSTOTAKE-1);
        focuslength = parameterHandler.ManualFocus.getStringValues().length -1;
        focusStep =  focuslength/PICSTOTAKE;
        currentFocusPos = 1;
        changeCaptureState(ModuleHandlerAbstract.CaptureStates.image_capture_stop);

    }

    @Override
    public void DestroyModule() {
        super.DestroyModule();
    }

    @Override
    protected void onStartTakePicture() {
        super.onStartTakePicture();
        int max  = 0;
        try {
            min = Integer.parseInt(AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_AFBRACKETMIN));
            max = Integer.parseInt(AppSettingsManager.getInstance().getApiString(AppSettingsManager.SETTING_AFBRACKETMAX));
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
    }

    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        cameraHolder.captureSessionHandler.SetCaptureParameter(CaptureRequest.LENS_FOCUS_DISTANCE, (float) currentFocusPos / 10);
        currentFocusPos +=focusStep;
        if (currentFocusPos > focuslength+min)
            currentFocusPos = focuslength+min;
    }


    @Override
    protected void finishCapture() {
        super.finishCapture();
    }

}
