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

package freed.cam.apis.camera1.parameters.modes;

import android.hardware.Camera.Parameters;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.settings.Settings;
import freed.cam.apis.camera1.parameters.ParametersHandler;

/**
 * Created by troop on 26.09.2014.
 */
public class NightModeZTE extends BaseModeParameter
{
    final String TAG = NightModeZTE.class.getSimpleName();
    private final boolean visible = true;
    private final String state = "";
    private final String curmodule = "";
    public NightModeZTE(Parameters parameters, CameraWrapperInterface cameraUiWrapper) {
        super(parameters, cameraUiWrapper);
        isSupported = true;
        isVisible =true;
        cameraUiWrapper.getModuleHandler().addListner(this);
        cameraUiWrapper.getParameterHandler().get(Settings.PictureFormat).addEventListner(this);
    }

    @Override
    public boolean IsSupported()
    {
        return isSupported;
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam)
    {
        parameters.set(cameraUiWrapper.getResString(R.string.night_key), valueToSet);
        ((ParametersHandler) cameraUiWrapper.getParameterHandler()).SetParametersToCamera(parameters);
        onStringValueChanged(valueToSet);

    }

    @Override
    public String GetStringValue() {
            return parameters.get(cameraUiWrapper.getResString(R.string.night_key));
    }

    @Override
    public String[] getStringValues() {
        return new String[] {
                cameraUiWrapper.getResString(R.string.off_),
                cameraUiWrapper.getResString(R.string.on_),
                cameraUiWrapper.getResString(R.string.night_mode_tripod)};
    }

    @Override
    public void onStringValueChanged(String value) {
        String format = value;
    }
}