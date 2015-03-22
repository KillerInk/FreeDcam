package com.troop.freedcam.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by George on 3/21/2015.
 */
public class SensorsUtil {

    private SensorManager sensorManager;
    private SensorEventListener eventListener;

    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;


    public void init()
    {
        sensorManager = (SensorManager)ApplicationContextProvider.getContext().getSystemService(Context.SENSOR_SERVICE);

        mAccel = 0.000f;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
    }



    public void setUp()
    {


        eventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float)Math.sqrt((double)(x*x + y*y  + z*z));
                float delta = mAccelCurrent - mAccelLast;

                mAccel = mAccel*0.9f+delta;


            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    public float getMotion()
    {
        return mAccel;
    }

    public void start()
    {
        sensorManager.registerListener(eventListener,sensorManager.getDefaultSensor(SensorManager.SENSOR_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI);
    }

    public void stop()
    {
        sensorManager.unregisterListener(eventListener);

    }
}
