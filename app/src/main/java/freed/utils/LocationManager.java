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
import android.widget.Toast;

import freed.ActivityInterface;

/**
 * Created by troop on 02.08.2016.
 */
public class LocationManager implements LocationListener
{
    private final String TAG = LocationManager.class.getSimpleName();
    private final android.location.LocationManager locationManager;
    private ActivityInterface activityInterface;
    private Location currentLocation;

    public LocationManager(ActivityInterface activityInterface)
    {
        this.activityInterface = activityInterface;
        locationManager = (android.location.LocationManager) activityInterface.getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }

    public void stopLocationListining()
    {
        Log.d(TAG, "stop location");
        locationManager.removeUpdates(this);

    }

    public void startLocationListing()
    {
        Log.d(TAG, "start location");
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
            Toast.makeText(activityInterface.getContext(), "Gps and Network are deactivated", Toast.LENGTH_LONG).show();
            Log.d("Location", "Gps and Network are deactivated");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
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
