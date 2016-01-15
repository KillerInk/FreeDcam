package troop.com.themesample.subfragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;

/**
 * Created by Ar4eR on 15.01.16.
 */
public class HorizontLineFragment extends AbstractFragment implements AbstractModeParameter.I_ModeParameterEvent, SensorEventListener{

    View view;
    AbstractCameraUiWrapper cameraUiWrapper;
    AppSettingsManager appSettingsManager;

    private ImageView lineImage;
    private ImageView upImage;
    private ImageView downImage;
    private float RotateDegree = 0f;
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;
    float roll;
    float pitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        this.view = inflater.inflate(R.layout.horizontline, container, false);
        lineImage = (ImageView)view.findViewById(R.id.horizontlevelline);
        upImage = (ImageView)view.findViewById(R.id.horizontlevelup);
        downImage = (ImageView)view.findViewById(R.id.horizontleveldown);
        upImage.setVisibility(View.GONE);
        downImage.setVisibility(View.GONE);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_HORIZONT).equals("Off"))
            view.setVisibility(View.GONE);
        startSensorListing();
        return view;
    }

    @Override
    public void onValueChanged(String val) {
        if(appSettingsManager.getString(AppSettingsManager.SETTING_HORIZONT).equals("On"))
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
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values.clone();
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = sensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                sensorManager.getOrientation(R, orientation);
                roll = orientation[1];
                pitch = orientation[2];
            }
            final float rad2deg = (float)(180.0f/Math.PI);
            float rolldegree = roll * rad2deg;
            float pitchdegree = pitch * rad2deg;
            Log.d("Sometag", String.valueOf(pitchdegree));
            if (RotateDegree != rolldegree) {
                RotateAnimation rotateAnimation = new RotateAnimation(RotateDegree, rolldegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setFillAfter(true);
                lineImage.startAnimation(rotateAnimation);
                RotateDegree = rolldegree;
            }
            if (pitchdegree > -89.5)
            {
                upImage.setVisibility(View.VISIBLE);
                downImage.setVisibility(View.GONE);
            }
            if (pitchdegree < -90.5)
            {
                upImage.setVisibility(View.GONE);
                downImage.setVisibility(View.VISIBLE);
            }
            if (pitchdegree >= -90.5 && pitchdegree <= -89.5)
            {
                upImage.setVisibility(View.GONE);
                downImage.setVisibility(View.GONE);

            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setCameraUiWrapper(AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        cameraUiWrapper.camParametersHandler.Horizont.addEventListner(this);
    }
    public void startSensorListing()
    {
        if (appSettingsManager.getString(AppSettingsManager.SETTING_HORIZONT).equals("On")) {
            sensorManager.registerListener(this, accelerometer, 1000000);
            sensorManager.registerListener(this, magnetometer, 1000000);
        }
    }

    public void stopSensorListing()
    {
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }
    @Override
    public void onPause(){
        super.onPause();
        stopSensorListing();
    }
    @Override
    public void onResume(){
        super.onResume();
        startSensorListing();
    }

}
