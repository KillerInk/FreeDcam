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

package freed.cam.apis.basecamera.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;

/**
 * Created by troop on 08.01.2016.
 */
public class IntervalShutterSleepParameter extends AbstractParameter
{
    private String current = "1 sec";
    private final CameraWrapperInterface cameraUiWrapper;
    public IntervalShutterSleepParameter(CameraWrapperInterface cameraUiWrapper)
    {
        super(SettingKeys.INTERVAL_SHUTTER_SLEEP);
        this.cameraUiWrapper = cameraUiWrapper;
        if (TextUtils.isEmpty(settingsManager.get(SettingKeys.INTERVAL_SHUTTER_SLEEP).get()))
            settingsManager.get(SettingKeys.INTERVAL_SHUTTER_SLEEP).set(current);
        else
            current = settingsManager.get(SettingKeys.INTERVAL_SHUTTER_SLEEP).get();
        fireStringValueChanged(current);
    }

    @Override
    public ViewState getViewState() {
        return ViewState.Visible;
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera) {
        current = valueToSet;
        settingsManager.get(SettingKeys.INTERVAL_SHUTTER_SLEEP).set(current);
        fireStringValueChanged(current);
    }

    @Override
    public String getStringValue() {
        return current;
    }

    @Override
    public String[] getStringValues() {
        return FreedApplication.getStringArrayFromRessource(R.array.interval_shutter_sleep);
    }
}
