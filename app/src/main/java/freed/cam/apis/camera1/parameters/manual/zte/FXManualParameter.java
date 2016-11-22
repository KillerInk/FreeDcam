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

package freed.cam.apis.camera1.parameters.manual.zte;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera.Parameters;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera1.parameters.ParametersHandler;
import freed.cam.apis.camera1.parameters.manual.BaseManualParameter;
import freed.cam.apis.camera1.parameters.modes.PictureFormatHandler;
import freed.utils.DeviceUtils.Devices;
import freed.utils.Logger;

public class FXManualParameter extends BaseManualParameter {

    public FXManualParameter(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, "", "", "", cameraUiWrapper,1);
        issupported();
    }

    private class ModuleChangedReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            String module = intent.getStringExtra("INTENT_EXTRA_MODULENAME");
            if (module.equals(KEYS.MODULE_VIDEO) && isSupported)
                ThrowBackgroundIsSupportedChanged(true);
            else if (module.equals(KEYS.MODULE_PICTURE)
                    || module.equals(KEYS.MODULE_INTERVAL)
                    || module.equals(KEYS.MODULE_HDR))
            {
                ThrowBackgroundIsSupportedChanged(isVisible);
            }
        }
    }

    private void issupported()
    {
        if(cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214)
        {
            isSupported = true;
            isVisible = true;
            stringvalues = createStringArray(0,38,1);
            cameraUiWrapper.getActivityInterface().getContext().registerReceiver(new ModuleChangedReciever(), new IntentFilter("troop.com.freedcam.MODULE_CHANGED"));
        }
        else
            isSupported = false;
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;

    }

    @Override
    public boolean IsVisible() {
        return IsSupported();
    }

    @Override
    public int GetValue()
    {
        int i = 0;
        try {
            if (cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTE_ADV
                    || cameraUiWrapper.GetAppSettingsManager().getDevice() == Devices.ZTEADVIMX214);
                i = 0;
        }
        catch (Exception ex)
        {
            Logger.exception(ex);
        }

        return i;
    }

    @Override
    public void SetValue(int valueToSet)
    {
        parameters.set(KEYS.MORPHO_EFFECT_TYPE, String.valueOf(valueToSet));
        ((ParametersHandler) cameraUiWrapper.GetParameterHandler()).SetParametersToCamera(parameters);

    }

}