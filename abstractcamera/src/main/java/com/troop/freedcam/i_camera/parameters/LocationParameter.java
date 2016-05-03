package com.troop.freedcam.i_camera.parameters;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.troop.filelogger.Logger;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.utils.StringUtils;

/**
 * Created by troop on 21.07.2015.
 * if you get fine loaction error ignore it, permission are set in app project where everything
 * gets builded
 */
public class LocationParameter extends AbstractModeParameter implements LocationListener
{
    private final AbstractCameraHolder cameraHolder;
    private LocationManager locationManager;

    private final int updateTime = 60*1000;
    private final int updateDistance = 15;


    public LocationParameter(Handler uiHandler, AbstractCameraHolder cameraHolder) {
        super(uiHandler);
        this.cameraHolder = cameraHolder;
        locationManager = (LocationManager)AppSettingsManager.APPSETTINGSMANAGER.context.getSystemService(Context.LOCATION_SERVICE);
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
        if (AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_LOCATION).equals(""))
            AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_LOCATION, StringUtils.OFF);
        return AppSettingsManager.APPSETTINGSMANAGER.getString(AppSettingsManager.SETTING_LOCATION);
    }

    @Override
    public String[] GetValues() {
        return new String[] { StringUtils.OFF, StringUtils.ON };
    }

    @Override
    public void SetValue(String valueToSet, boolean setToCamera)
    {
        AppSettingsManager.APPSETTINGSMANAGER.setString(AppSettingsManager.SETTING_LOCATION, valueToSet);
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
            locationManager.removeUpdates(LocationParameter.this);
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
                        LocationParameter.this);
                locnet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(gps)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        updateTime,
                        updateDistance,
                        LocationParameter.this);
                locgps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (locgps != null && cameraHolder != null)
                cameraHolder.SetLocation(locgps);
            else if(locnet != null && cameraHolder != null)
                cameraHolder.SetLocation(locnet);
        }
        else
        {
            Toast.makeText(AppSettingsManager.APPSETTINGSMANAGER.context, "Gps and Network are deactivated", Toast.LENGTH_LONG).show();
            Logger.d("Location", "Gps and Network are deactivated");
        }
    }
}
