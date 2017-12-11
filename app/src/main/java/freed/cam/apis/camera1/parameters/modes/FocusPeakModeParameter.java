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

import android.os.Build.VERSION;

import com.troop.freedcam.R;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.settings.Settings;
import freed.cam.apis.camera1.Camera1Fragment;
import freed.cam.apis.camera1.renderscript.FocusPeakProcessorAp1;


/**
 * Created by troop on 27.08.2015.
 */
public class FocusPeakModeParameter extends BaseModeParameter {

    private final FocusPeakProcessorAp1 focusPeakProcessorAp1;
    public FocusPeakModeParameter(CameraWrapperInterface cameraUiWrapper, FocusPeakProcessorAp1 focusPeakProcessorAp1)
    {
        super(null, cameraUiWrapper);
        this.focusPeakProcessorAp1 = focusPeakProcessorAp1;
    }

    @Override
    public boolean IsSupported() {
        return VERSION.SDK_INT >= 18 && cameraUiWrapper.getRenderScriptManager().isSucessfullLoaded();
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCam) {
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
        {
            //set foucs mode at same stage again else on some devices the camera preview gets green
            ParameterInterface fm = cameraUiWrapper.getParameterHandler().get(Settings.FocusMode);
            fm.SetValue(fm.GetStringValue(),true);
            focusPeakProcessorAp1.Enable(true);
        }
        else
            focusPeakProcessorAp1.Enable(false);
        ((Camera1Fragment)cameraUiWrapper).onModuleChanged("");
    }

    @Override
    public String GetStringValue()
    {
        if (focusPeakProcessorAp1 == null) {
            onIsSupportedChanged(false);
            return cameraUiWrapper.getResString(R.string.off_);
        }
        if (focusPeakProcessorAp1.isEnable())
            return cameraUiWrapper.getResString(R.string.on_);
        else
            return cameraUiWrapper.getResString(R.string.off_);
    }

    @Override
    public String[] getStringValues() {
        return new String[] {cameraUiWrapper.getResString(R.string.on_), cameraUiWrapper.getResString(R.string.off_)};
    }


    @Override
    public void onIsSupportedChanged(boolean value) {

    }

    @Override
    public void onStringValueChanged(String value) {
        super.onStringValueChanged(value);
        if (value.equals(cameraUiWrapper.getResString(R.string.true_)))
            super.onStringValueChanged(cameraUiWrapper.getResString(R.string.on));
        else if (value.equals(cameraUiWrapper.getResString(R.string.false_)))
            super.onStringValueChanged(cameraUiWrapper.getResString(R.string.off));
    }
}
