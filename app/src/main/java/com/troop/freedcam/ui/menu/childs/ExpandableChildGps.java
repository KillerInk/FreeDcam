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
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.ExpandableGroup;

/**
 * Created by troop on 09.02.2015.
 */
public class ExpandableChildGps extends ExpandableChild implements LocationListener
{
    protected Switch aSwitch;
    LocationManager locationManager;

    final int updateTime = 60*1000;
    final int updateDistance = 15;
    AbstractCameraHolder cameraHolder;

    public ExpandableChildGps(Context context, ExpandableGroup group, String name, AppSettingsManager appSettingsManager, String settingsname) {
        super(context, group, name, appSettingsManager, settingsname);
        parameterHolder = new SimpleModeParameter();
        ((SimpleModeParameter)parameterHolder).setIsSupported(true);
    }

    @Override
    protected void init(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandablechildboolean, this);
        aSwitch = (Switch)findViewById(R.id.switch1);
        aSwitch.setText(Name);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (locationManager != null)
                {
                    if (isChecked)
                    {
                        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        //boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        if (gps)
                        {
                            Location loc = null;
                            /*if (network)
                            {
                                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                                        updateTime,
                                        updateDistance,
                                        ExpandableChildGps.this);
                                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            }*/
                            if(gps && loc == null)
                            {
                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        updateTime,
                                        updateDistance,
                                        ExpandableChildGps.this);
                                loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            }
                            if (loc != null)
                                cameraHolder.SetLocation(loc);
                        }
                        else
                            aSwitch.setChecked(false);
                    }
                    else
                    {
                        if(locationManager != null){
                            locationManager.removeUpdates(ExpandableChildGps.this);
                        }
                    }
                }
                appSettingsManager.setString(settingsname, isChecked +"");

            }
        });
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void SetCameraHolder(AbstractCameraHolder cameraHolder)
    {
        this.cameraHolder = cameraHolder;
    }


    @Override
    public void onLocationChanged(Location location)
    {
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
}
