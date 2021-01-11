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

package freed.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.troop.freedcam.R;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 02.08.2016.
 */
public class LocationManager implements LocationListener, LifecycleObserver
{
    private final String TAG = LocationManager.class.getSimpleName();
    private final android.location.LocationManager locationManager;
    private ActivityInterface activityInterface;
    private Lifecycle lifecycle;
    private Location currentLocation;
    private boolean isStarted = false;

    public LocationManager(ActivityInterface activityInterface, Lifecycle lifecycle)
    {
        this.activityInterface = activityInterface;
        this.lifecycle = lifecycle;
        locationManager = (android.location.LocationManager) FreedApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
        lifecycle.addObserver(this);
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause()
    {
        stopLocationListining();
    }

    /*@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume()
    {
        startLocationListing();
    }*/


    public void stopLocationListining()
    {
        Log.d(TAG, "stop location");
        locationManager.removeUpdates(this);
        currentLocation = null;
        isStarted = false;
    }

    public void startListing()
    {
        boolean isON = SettingsManager.getGlobal(SettingKeys.LOCATION_MODE).get().equals(FreedApplication.getStringFromRessources(R.string.on_));
        boolean permissiongranted = activityInterface.getPermissionManager().isPermissionGranted(PermissionManager.Permissions.Location);
        if (isON && permissiongranted)
            startLocationListing();
    }


    private void startLocationListing()
    {
        Log.d(TAG, "start location");
        isStarted = true;
        boolean gps = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, "Gps:"+gps + "Network:"+network);
        if (gps || network)
        {

            Location locnet = null;
            Location locgps = null;
            int updateDistance = 15;
            int updateTime = 60 * 1000;
            if (network)
            {
                locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER,
                        updateTime,
                        updateDistance,
                        this);
                locnet = locationManager.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER);
            }
            if(gps)
            {
                locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER,
                        updateTime,
                        updateDistance,
                        this);
                locgps = locationManager.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
            }
            if (locgps != null)
                currentLocation = locgps;
            else if(locnet != null)
                currentLocation = locnet;
        }
        else
        {
            UserMessageHandler.sendMSG("Gps and Network are deactivated",true);
            Log.d(TAG, "Gps and Network are deactivated");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isStarted)
            currentLocation = location;
        else
            currentLocation = null;
        Log.d(TAG, "onLocationChanged:" + (currentLocation == null) + " isListing:" + isStarted);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
