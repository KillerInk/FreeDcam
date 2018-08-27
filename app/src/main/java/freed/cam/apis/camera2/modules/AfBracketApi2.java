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
import freed.cam.apis.camera2.parameters.manual.ManualFocus;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 18.08.2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AfBracketApi2 extends PictureModuleApi2
{
    private final String TAG = AfBracketApi2.class.getSimpleName();
    private ManualFocus manualFocus;

    public AfBracketApi2(CameraWrapperInterface cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper,mBackgroundHandler,mainHandler);
        name = cameraUiWrapper.getResString(R.string.module_afbracket);
    }

    private int PICSTOTAKE = 10;

    private int focusStep;
    private int currentFocusPos;
    private int focusCaptureRange;
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
        manualFocus = (ManualFocus) cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Focus);
        cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Burst).SetValue(PICSTOTAKE-1, true);
        focusCaptureRange = parameterHandler.get(SettingKeys.M_Focus).getStringValues().length -1;
        focusStep =  focusCaptureRange /PICSTOTAKE;
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
        PICSTOTAKE = cameraUiWrapper.getParameterHandler().get(SettingKeys.M_Burst).GetValue();
        int max  = 0;
        try {
            min = Integer.parseInt(SettingsManager.getInstance().getApiString(SettingsManager.SETTING_AFBRACKETMIN));
            max = Integer.parseInt(SettingsManager.getInstance().getApiString(SettingsManager.SETTING_AFBRACKETMAX));
        }
        catch (NumberFormatException ex)
        {
            min = 0;
            max = 0;
        }

        if (min == 0 && max == 0)
        {
            focusCaptureRange = SettingsManager.get(SettingKeys.M_Focus).getValues().length -1;
            focusStep = focusCaptureRange /PICSTOTAKE;
            currentFocusPos = 1;
        }
        else {
            if (max > min){
                focusCaptureRange = max - min;
                currentFocusPos = min;
            }
            else {
                focusCaptureRange = min - max;
                currentFocusPos = max;
            }
            focusStep = focusCaptureRange /PICSTOTAKE;

        }
        Log.d(TAG,"onStartTakePicture() min:" + min + " max:" + max +" focusCaptureRange:" + focusCaptureRange + " focusStep:" + focusStep + " currentFocusPos:" + currentFocusPos);
    }

    @Override
    protected void prepareCaptureBuilder(int captureNum) {
        cameraUiWrapper.captureSessionHandler.SetCaptureParameter(CaptureRequest.LENS_FOCUS_DISTANCE, manualFocus.getFloatValue(currentFocusPos));
        Log.d(TAG,"prepareCaptureBuilder() focusCaptureRange:" + focusCaptureRange + " focusStep:" + focusStep + " currentFocusPos:" + currentFocusPos + " :" +  manualFocus.getFloatValue(currentFocusPos) + " :" + manualFocus.getStringValue(currentFocusPos));
        currentFocusPos +=focusStep;
        if (currentFocusPos > parameterHandler.get(SettingKeys.M_Focus).getStringValues().length)
            currentFocusPos = parameterHandler.get(SettingKeys.M_Focus).getStringValues().length-1;
    }


    @Override
    protected void finishCapture() {
        super.finishCapture();
    }

}
