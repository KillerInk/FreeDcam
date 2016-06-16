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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.utils.AppSettingsManager;
import freed.utils.Logger;

/**
 * Created by troop on 21.07.2015.
 * if you get fine loaction error ignore it, permission are set in app project where everything
 * gets builded
 */
public class LocationParameter extends AbstractModeParameter implements LocationListener
{
    private final LocationManager locationManager;
    private final CameraWrapperInterface cameraUiWrapper;


    private final int updateTime = 60*1000;
    private final int updateDistance = 15;


    public LocationParameter(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        locationManager = (LocationManager) cameraUiWrapper.getContext().getSystemService(Context.LOCATION_SERVICE);
        if (GetValue().equals(KEYS.ON))
            startLocationListing();
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public String GetValue()
    {
        if (cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION).equals(""))
            cameraUiWrapper.GetAppSettingsManager().setString(AppSettingsManager.SETTING_LOCATION, KEYS.OFF);
        return cameraUiWrapper.GetAppSettingsManager().getString(AppSettingsManager.SETTING_LOCATION);
    }

    @Override
    public String[] GetValues() {
        return new String[] { KEYS.OFF, KEYS.ON };
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        cameraUiWrapper.GetAppSettingsManager().setString(AppSettingsManager.SETTING_LOCATION, valueToSet);
        if (valueToSet.equals(KEYS.OFF))
            stopLocationListining();
        if (valueToSet.equals(KEYS.ON))
            startLocationListing();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Logger.d("Location", "updated location");
        if (cameraUiWrapper.GetCameraHolder() != null)
            cameraUiWrapper.GetCameraHolder().SetLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void stopLocationListining()
    {
        Logger.d("Location", "stop location");
        if(locationManager != null)
        {
            locationManager.removeUpdates(this);
        }
    }

    private void startLocationListing()
    {
        Logger.d("Location", "start location");
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Logger.d("Location", "Gps:"+gps + "Network:"+network);
        if (gps || network)
        {

            Location locnet = null;
            Location locgps = null;
            if (network)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        updateTime,
                        updateDistance,
                        this);
                locnet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(gps)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        updateTime,
                        updateDistance,
                        this);
                locgps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (locgps != null && cameraUiWrapper.GetCameraHolder() != null)
                cameraUiWrapper.GetCameraHolder().SetLocation(locgps);
            else if(locnet != null && cameraUiWrapper.GetCameraHolder() != null)
                cameraUiWrapper.GetCameraHolder().SetLocation(locnet);
        }
        else
        {
            Toast.makeText(cameraUiWrapper.getContext(), "Gps and Network are deactivated", Toast.LENGTH_LONG).show();
            Logger.d("Location", "Gps and Network are deactivated");
        }
    }
}
