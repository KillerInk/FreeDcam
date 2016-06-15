/*
 *
 *     Copyright (C) 2015 Ar4eR
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

package com.freedcam.ui.themesample.subfragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.freedcam.apis.basecamera.interfaces.CameraWrapperInterface;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.ui.AbstractFragment;
import com.freedcam.ui.I_Activity;
import com.freedcam.utils.AppSettingsManager;
import com.troop.freedcam.R.id;
import com.troop.freedcam.R.layout;

/**
 * Created by Ar4eR on 15.01.16.
 */
public class HorizontLineFragment extends AbstractFragment implements I_ModeParameterEvent{

    private View view;

    private ImageView lineImage;
    private ImageView upImage;
    private ImageView downImage;
    private float RotateDegree;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] mGravity;
    private float[] mGeomagnetic;
    private float roll;
    private float pitch;
    private float rolldegree;
    private float pitchdegree;
    private final float rad2deg = (float)(180.0f/Math.PI);
    private final Handler handler = new Handler();
    private Handler sensorHandler;
    private final MySensorListener msl =new MySensorListener();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        i_activity = (I_Activity)getActivity();
        view = inflater.inflate(layout.horizontline, container, false);
        lineImage = (ImageView) view.findViewById(id.horizontlevelline);
        upImage = (ImageView) view.findViewById(id.horizontlevelup);
        downImage = (ImageView) view.findViewById(id.horizontleveldown);
        upImage.setVisibility(View.GONE);
        downImage.setVisibility(View.GONE);
        HandlerThread sensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return view;
    }

    @Override
    public void onValueChanged(String val) {
        if(i_activity.getAppSettings().getString(AppSettingsManager.SETTING_HORIZONT).equals("On"))
        {
            startSensorListing();
            view.setVisibility(View.VISIBLE);
        }
        else
        {
            stopSensorListing();
            view.setVisibility(View.GONE);
        }

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public void onVisibilityChanged(boolean visible) {

    }

    public void setCameraUiWrapper(CameraWrapperInterface cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.GetParameterHandler().Horizont.addEventListner(this);
    }
    private void startSensorListing()
    {
        if (i_activity.getAppSettings().getString(AppSettingsManager.SETTING_HORIZONT).equals("On")) {
            sensorManager.registerListener(msl, accelerometer, 1000000, sensorHandler);
            sensorManager.registerListener(msl, magnetometer, 1000000, sensorHandler);
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
    }
    @Override
    public void onResume(){
        super.onResume();
        if (i_activity.getAppSettings().getString(AppSettingsManager.SETTING_HORIZONT).equals("Off") || i_activity.getAppSettings().getString(AppSettingsManager.SETTING_HORIZONT).equals(""))
            view.setVisibility(View.GONE);
        else
            startSensorListing();
    }

    private class MySensorListener implements SensorEventListener {

        static final float ALPHA = 0.2f;

        float[] lowPass(float[] input, float[] output) {
            if ( output == null ) return input;

            for ( int i=0; i<input.length; i++ ) {
                output[i] = input[i] * ALPHA + output[i] * (1.0f - ALPHA);
                //output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
        }

        public void onAccuracyChanged (Sensor sensor, int accuracy) {}

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = lowPass(event.values.clone(), mGravity);
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values.clone();
            if (mGravity != null && mGeomagnetic != null) {
                //hltheard.run();
                float[] R = new float[9];
                float[] I = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float[] orientation = new float[3];
                    SensorManager.getOrientation(R, orientation);
                    roll = orientation[1];
                    pitch = orientation[2];
                    rolldegree = roll * rad2deg;
                    pitchdegree = pitch * rad2deg;
                   // Logger.d("Sometag", String.valueOf(pitchdegree));
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (RotateDegree != rolldegree) {
                            RotateAnimation rotateAnimation = new RotateAnimation(RotateDegree, rolldegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            //rotateAnimation.setInterpolator(lineImage.getContext(), android.R.interpolator.accelerate_decelerate);
                            rotateAnimation.setFillAfter(true);
                            rotateAnimation.setDuration(400);
                            lineImage.startAnimation(rotateAnimation);
                            RotateDegree = rolldegree;
                        }
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
                    }
                });


            }
        }
    }
}
