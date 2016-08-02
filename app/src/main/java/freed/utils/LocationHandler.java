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
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import freed.ActivityInterface;

/**
 * Created by troop on 02.08.2016.
 */
public class LocationHandler implements LocationListener
{
    private final String TAG = LocationHandler.class.getSimpleName();
    private final LocationManager locationManager;
    private ActivityInterface activityInterface;
    private final int updateTime = 60*1000;
    private final int updateDistance = 15;
    private Location currentLocation;

    public LocationHandler(ActivityInterface activityInterface)
    {
        this.activityInterface = activityInterface;
        locationManager = (LocationManager) activityInterface.getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getCurrentLocation()
    {
        return currentLocation;
    }

    public void stopLocationListining()
    {
        Logger.d(TAG, "stop location");
        locationManager.removeUpdates(this);

    }

    public void startLocationListing()
    {
        Logger.d(TAG, "start location");
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Logger.d(TAG, "Gps:"+gps + "Network:"+network);
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
            if (locgps != null)
                currentLocation = locgps;
            else if(locnet != null)
                currentLocation = locnet;
        }
        else
        {
            Toast.makeText(activityInterface.getContext(), "Gps and Network are deactivated", Toast.LENGTH_LONG).show();
            Logger.d("Location", "Gps and Network are deactivated");
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
