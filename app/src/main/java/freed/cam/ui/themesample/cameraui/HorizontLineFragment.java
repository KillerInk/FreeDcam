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

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;
import com.troop.freedcam.R.string;

import org.greenrobot.eventbus.Subscribe;

import freed.ActivityInterface;
import freed.FreedApplication;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterEvents;
import freed.cam.events.EventBusHelper;
import freed.cam.events.ValueChangedEvent;
import freed.cam.ui.themesample.AbstractFragment;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by Ar4eR on 15.01.16.
 */
public class HorizontLineFragment extends AbstractFragment implements ParameterEvents {

    private View view;

    private ImageView lineImage;
    private ImageView upImage;
    private ImageView downImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float pitchdegree;
    private final Handler handler = new Handler();
    private Handler sensorHandler;
    private final MySensorListener msl =new MySensorListener();
    private CompassDrawer compassDrawer;
    private String TAG = HorizontLineFragment.class.getSimpleName();
    float[] orientation = new float[3];
    float yaw;
    float[] R = new float[9];
    float[] I = new float[9];



    @Subscribe
    public void onHorizontModeChanged(ValueChangedEvent<String> valueChangedEvent)
    {
        if (valueChangedEvent.key == SettingKeys.HorizontLvl)
        {
            onStringValueChanged(valueChangedEvent.newValue);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        fragment_activityInterface = (ActivityInterface)getActivity();
        view = inflater.inflate(layout.cameraui_horizontline, container, false);
        lineImage = view.findViewById(id.horizontlevelline);
        upImage = view.findViewById(id.horizontlevelup);
        downImage = view.findViewById(id.horizontleveldown);
        upImage.setVisibility(View.GONE);
        downImage.setVisibility(View.GONE);
        HandlerThread sensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        compassDrawer = view.findViewById(id.view_compass);
        compassDrawer.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewStateChanged(AbstractParameter.ViewState value) {

    }

    @Override
    public void onIntValueChanged(int current) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onStringValueChanged(String value) {
        if(SettingsManager.getGlobal(SettingKeys.HorizontLvl).get() != null && SettingsManager.getGlobal(SettingKeys.HorizontLvl).get().equals(FreedApplication.getStringFromRessources(string.on)))
        {
            startSensorListing();
            view.setVisibility(View.VISIBLE);
            view.bringToFront();
        }
        else
        {
            stopSensorListing();
            view.setVisibility(View.GONE);
        }
    }

    public void setCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        //cameraUiWrapper.getParameterHandler().get(SettingKeys.HorizontLvl).addEventListner(this);
        onStringValueChanged(cameraUiWrapper.getParameterHandler().get(SettingKeys.HorizontLvl).GetStringValue());
    }

    private void startSensorListing()
    {
        if (SettingsManager.getGlobal(SettingKeys.HorizontLvl).get().equals(FreedApplication.getStringFromRessources(string.on))) {
            sensorManager.registerListener(msl, accelerometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW, sensorHandler);
            sensorManager.registerListener(msl, magnetometer, SensorManager.SENSOR_STATUS_ACCURACY_LOW, sensorHandler);
        }
    }

    private void stopSensorListing()
    {
        if (sensorManager != null)
            sensorManager.unregisterListener(msl);

    }
    @Override
    public void onPause(){
        super.onPause();
        stopSensorListing();
        EventBusHelper.unregister(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        EventBusHelper.register(this);
        try {
            if (SettingsManager.getGlobal(SettingKeys.HorizontLvl).get() != null && SettingsManager.getGlobal(SettingKeys.HorizontLvl).get().equals(FreedApplication.getStringFromRessources(string.off))
                    || TextUtils.isEmpty(SettingsManager.get(SettingKeys.HorizontLvl).get()))
                view.setVisibility(View.GONE);
            else
                startSensorListing();
        }
        catch (NullPointerException ex)
        {
            Log.WriteEx(ex);
        }

    }

    private class MySensorListener implements SensorEventListener {

        static final float ALPHA = 0.2f;

        float[] lowPass(float[] input, float[] output) {
            if ( output == null ) return input;

            for ( int i=0; i<input.length; i++ ) {
                output[i] = input[i] * ALPHA + output[i] * (1.0f - ALPHA);
            }
            return output;
        }

        public void onAccuracyChanged (Sensor sensor, int accuracy) {}

        public void onSensorChanged(final SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity =lowPass( event.values.clone(), mGravity);
            else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = lowPass(event.values.clone(), mGeomagnetic);
            if (mGravity != null && mGeomagnetic != null) {
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y, I);
                    SensorManager.getOrientation(I, orientation);
                    float rolldegree = get360Degrees(orientation[1]);
                    pitchdegree = (float) Math.toDegrees(orientation[2]);
                    yaw = get360Degrees(orientation[0]);
                    updateUi(pitchdegree, rolldegree, -yaw);
                }
            }
        }
    }

    private float get360Degrees(float input)
    {
        return ((float) Math.toDegrees(input) +360) %360;
    }

    private void updateUi(final float pitch,final  float roll,final float yaw)
    {
        handler.post(() -> {
            //compassDrawer.SetPosition(yaw);
                lineImage.setRotation(roll);
            if (pitchdegree > -89) {
                if(upImage.getVisibility() != View.VISIBLE)
                    upImage.setVisibility(View.VISIBLE);
                downImage.setVisibility(View.GONE);
            }
            else if (pitchdegree < -91) {
                upImage.setVisibility(View.GONE);
                if(downImage.getVisibility() != View.VISIBLE)
                    downImage.setVisibility(View.VISIBLE);
            }
            else if (pitchdegree >= -91 && pitchdegree <= -89) {
                upImage.setVisibility(View.GONE);
                downImage.setVisibility(View.GONE);
            }
        });
    }
}
