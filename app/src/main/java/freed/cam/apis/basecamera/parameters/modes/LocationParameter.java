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

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.PermissionManager;

/**
 * Created by troop on 21.07.2015.
 * if you get fine loaction error ignore it, permission are set in app project where everything
 * gets builded
 */
public class LocationParameter extends AbstractParameter
{
    private final CameraWrapperInterface cameraUiWrapper;


    public LocationParameter(CameraWrapperInterface cameraUiWrapper)
    {
        super(SettingKeys.LOCATION_MODE);
        this.cameraUiWrapper = cameraUiWrapper;
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public String GetStringValue()
    {
        if (cameraUiWrapper == null)
            return cameraUiWrapper.getResString(R.string.off_);
        if (TextUtils.isEmpty(SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION)))
            SettingsManager.getInstance().setApiString(SettingsManager.SETTING_LOCATION, cameraUiWrapper.getResString(R.string.off_));
        return SettingsManager.getInstance().getApiString(SettingsManager.SETTING_LOCATION);
    }

    @Override
    public String[] getStringValues() {
        return new String[] { cameraUiWrapper.getResString(R.string.off_), cameraUiWrapper.getResString(R.string.on_) };
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        SettingsManager.getInstance().setApiString(SettingsManager.SETTING_LOCATION, valueToSet);
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.off_)))
            cameraUiWrapper.getActivityInterface().getLocationManager().stopLocationListining();
        if (valueToSet.equals(cameraUiWrapper.getResString(R.string.on_)))
            cameraUiWrapper.getActivityInterface().getPermissionManager().hasLocationPermission(onLocationPermission);
        fireStringValueChanged(valueToSet);
    }

    private PermissionManager.PermissionCallback onLocationPermission = new PermissionManager.PermissionCallback() {
        @Override
        public void permissionGranted(boolean granted) {
            if (granted)
                if (cameraUiWrapper != null && cameraUiWrapper.getActivityInterface() != null && cameraUiWrapper.getActivityInterface().getLocationManager() != null) {
                    cameraUiWrapper.getActivityInterface().getLocationManager().startLocationListing();
                    fireStringValueChanged(cameraUiWrapper.getResString(R.string.on_));
                }
            else
            {
                SettingsManager.getInstance().setApiString(SettingsManager.SETTING_LOCATION, cameraUiWrapper.getResString(R.string.off_));
                fireStringValueChanged(cameraUiWrapper.getResString(R.string.off_));
            }
        }
    };

}
