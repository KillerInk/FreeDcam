package com.troop.freedcam.ui.menu.childs;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.parameters.modes.SimpleModeParameter;
import com.troop.freedcam.i_camera.AbstractCameraHolder;
import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

/**
 * Created by troop on 09.02.2015.
 */
public class ExpandableChildGps extends ExpandableChild implements LocationListener, CompoundButton.OnCheckedChangeListener
{
    protected Switch aSwitch;
    LocationManager locationManager;

    final int updateTime = 60*1000;
    final int updateDistance = 15;
    AbstractCameraHolder cameraHolder;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;
    String settingsname;

    public ExpandableChildGps(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name);
        parameterHolder = new SimpleModeParameter();
        ((SimpleModeParameter)parameterHolder).setIsSupported(true);
        this.appSettingsManager = appSettingsManager;
        this.settingsname = settingsname;
    }

    @Override
    protected void init(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandablechildboolean_on_off, this);
        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setText(Name);
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        aSwitch.setOnCheckedChangeListener(this);

    }

    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.cameraHolder = cameraUiWrapper.cameraHolder;
        final String set = appSettingsManager.getString(settingsname);
        final boolean check = Boolean.parseBoolean(set);
        aSwitch.setChecked(check);
        if (check)
            startLocationListing();
    }


    @Override
    public void onLocationChanged(Location location)
    {
        if (cameraHolder != null)
            cameraHolder.SetLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {
        //aSwitch.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {

        if (locationManager != null)
        {
            if (isChecked)
            {
                startLocationListing();
            }
            else
            {
                stopLocationListining();
            }
        }
        final String check = aSwitch.isChecked() +"";
        appSettingsManager.setString(settingsname,  check);


    }

    public void stopLocationListining() {
        if(locationManager != null)
        {
            locationManager.removeUpdates(ExpandableChildGps.this);
        }
    }

    public void startLocationListing() {
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network)
        {
            Location locnet = null;
            Location locgps = null;
            if (network)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        updateTime,
                        updateDistance,
                        ExpandableChildGps.this);
                locnet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(gps)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        updateTime,
                        updateDistance,
                        ExpandableChildGps.this);
                locgps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (locgps != null && cameraHolder != null)
                cameraHolder.SetLocation(locgps);
            else if(locnet != null && cameraHolder != null)
                cameraHolder.SetLocation(locnet);
        }
        else
        {
            if (cameraUiWrapper != null)
                cameraUiWrapper.onCameraError("Gps and Network are deactivated");
            aSwitch.setChecked(false);
        }
    }
}
