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

package com.freedcam.apis.basecamera.parameters.modes;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.freedcam.apis.basecamera.AbstractCameraHolder;
import com.freedcam.apis.basecamera.interfaces.I_CameraHolder;
import com.freedcam.utils.AppSettingsManager;
import com.freedcam.utils.Logger;
import com.freedcam.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 * if you get fine loaction error ignore it, permission are set in app project where everything
 * gets builded
 */
public class LocationParameter extends AbstractModeParameter implements LocationListener
{
    private I_CameraHolder cameraHolder;
    private LocationManager locationManager;
    private Context context;
    private AppSettingsManager appSettingsManager;

    private final int updateTime = 60*1000;
    private final int updateDistance = 15;


    public LocationParameter(I_CameraHolder cameraHolder, Context context, AppSettingsManager appSettingsManager) {
        this.context = context;
        this.cameraHolder = cameraHolder;
        this.appSettingsManager = appSettingsManager;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (GetValue().equals(StringUtils.ON))
            startLocationListing();
    }

    @Override
    public boolean IsSupported() {
        return true;
    }

    @Override
    public String GetValue()
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_LOCATION).equals(""))
            appSettingsManager.setString(AppSettingsManager.SETTING_LOCATION, StringUtils.OFF);
        return appSettingsManager.getString(AppSettingsManager.SETTING_LOCATION);
    }

    @Override
    public String[] GetValues() {
        return new String[] { StringUtils.OFF, StringUtils.ON };
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        appSettingsManager.setString(AppSettingsManager.SETTING_LOCATION, valueToSet);
        if (valueToSet.equals(StringUtils.OFF))
            stopLocationListining();
        if (valueToSet.equals(StringUtils.ON))
            startLocationListing();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        Logger.d("Location", "updated location");
        if (cameraHolder != null)
            cameraHolder.SetLocation(location);
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
            if (locgps != null && cameraHolder != null)
                cameraHolder.SetLocation(locgps);
            else if(locnet != null && cameraHolder != null)
                cameraHolder.SetLocation(locnet);
        }
        else
        {
            Toast.makeText(context, "Gps and Network are deactivated", Toast.LENGTH_LONG).show();
            Logger.d("Location", "Gps and Network are deactivated");
        }
    }
}
