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

package com.troop.freedcam.camera.basecamera.parameters.modes;

import android.text.TextUtils;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.settings.SettingKeys;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.ContextApplication;

/**
 * Created by troop on 21.07.2015.
 * if you get fine loaction error ignore it, permission are set in app project where everything
 * gets builded
 */
public class GpsParameter extends AbstractParameter
{
    private final CameraControllerInterface cameraUiWrapper;

    private boolean userAcceptedPermission = false;
    private boolean askedForPermission = false;
    //private ActivityInterface activityInterface;

    public GpsParameter(CameraControllerInterface cameraUiWrapper)
    {
        super(SettingKeys.LOCATION_MODE);
        this.cameraUiWrapper = cameraUiWrapper;
        /*this.activityInterface = cameraUiWrapper.getActivityInterface();
        userAcceptedPermission = activityInterface.getPermissionManager().isPermissionGranted(PermissionManager.Permissions.Location);*/
    }

    @Override
    public ViewState getViewState() {
        return ViewState.Visible;
    }

    @Override
    public String GetStringValue()
    {
        if (cameraUiWrapper == null)
            return ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_);
        if (TextUtils.isEmpty(SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).get()))
            SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
        return SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).get();
    }

    @Override
    public String[] getStringValues() {
        return new String[] { ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_), ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_) };
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
       /* if (activityInterface.getPermissionManager().isPermissionGranted(PermissionManager.Permissions.Location) &&
                valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_)))
        {
            SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(valueToSet);
            if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_))) {
                activityInterface.getLocationManager().stopLocationListining();
                fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
            }
            if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_))) {
                activityInterface.getLocationManager().startListing();
                fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_));
            }
        }
        else
        {
            if (!userAcceptedPermission && !askedForPermission)
            if (valueToSet.equals(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.on_))
                    && !activityInterface.getPermissionManager().isPermissionGranted(PermissionManager.Permissions.Location))
                activityInterface.getPermissionManager().requestPermission(PermissionManager.Permissions.Location);
            SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
            fireStringValueChanged(ContextApplication.getStringFromRessources(com.troop.freedcam.camera.R.string.off_));
            askedForPermission = false;
        }*/
    }

}
