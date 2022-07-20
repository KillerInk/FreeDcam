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

import com.troop.freedcam.R;

import freed.ActivityAbstract;
import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.settings.SettingKeys;
import freed.settings.mode.BooleanSettingModeInterface;
import freed.utils.LocationManager;
import freed.utils.PermissionManager;

/**
 * Created by troop on 21.07.2015.
 * if you get fine loaction error ignore it, permission are set in app project where everything
 * gets builded
 */
public class GpsParameter extends AbstractParameter implements BooleanSettingModeInterface
{
    private final CameraWrapperInterface cameraUiWrapper;

    private boolean userAcceptedPermission = false;
    private boolean askedForPermission = false;
    private final PermissionManager permissionManager;
    private final LocationManager locationManager;

    public GpsParameter(CameraWrapperInterface cameraUiWrapper)
    {
        super(SettingKeys.LOCATION_MODE);
        permissionManager = ActivityAbstract.permissionManager();
        locationManager = ActivityFreeDcamMain.locationManager();
        this.cameraUiWrapper = cameraUiWrapper;
        userAcceptedPermission = permissionManager.isPermissionGranted(PermissionManager.Permissions.Location);
    }

    @Override
    public ViewState getViewState() {
        return ViewState.Visible;
    }

    @Override
    public String getStringValue()
    {
        if (cameraUiWrapper == null && !settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get())
            return FreedApplication.getStringFromRessources(R.string.off_);
        return FreedApplication.getStringFromRessources(R.string.on_);
    }

    @Override
    public String[] getStringValues() {
        return new String[] { FreedApplication.getStringFromRessources(R.string.off_), FreedApplication.getStringFromRessources(R.string.on_) };
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        if (permissionManager.isPermissionGranted(PermissionManager.Permissions.Location) &&
                valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_)))
        {
            settingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(true);
            if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.off_))) {
                locationManager.stopLocationListining();
                fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
            }
            if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_))) {
                locationManager.startListing();
                fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
            }
        }
        else
        {
            if (!userAcceptedPermission && !askedForPermission)
            if (valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_))
                    && !permissionManager.isPermissionGranted(PermissionManager.Permissions.Location))
                permissionManager.requestPermission(PermissionManager.Permissions.Location);
            settingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(false);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
            askedForPermission = false;
        }
    }

    @Override
    public boolean get() {
        return settingsManager.getGlobal(SettingKeys.LOCATION_MODE).get();
    }

    @Override
    public void set(boolean bool) {
        if (permissionManager.isPermissionGranted(PermissionManager.Permissions.Location) &&
                bool)
        {
            settingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(bool);
            if (!bool) {
                locationManager.stopLocationListining();
                fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
            }
            if (bool) {
                locationManager.startListing();
                fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.on_));
            }
        }
        else
        {
            if (!userAcceptedPermission && !askedForPermission)
                if (bool
                        && !permissionManager.isPermissionGranted(PermissionManager.Permissions.Location))
                    permissionManager.requestPermission(PermissionManager.Permissions.Location);
            settingsManager.getGlobal(SettingKeys.LOCATION_MODE).set(false);
            fireStringValueChanged(FreedApplication.getStringFromRessources(R.string.off_));
            askedForPermission = false;
        }
    }
}
