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
    private float RotateDegree = 0f;
    private SensorManager sensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;
    float roll;
    //float pitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,null);
        this.view = inflater.inflate(R.layout.horizontline, container, false);
        lineImage = (ImageView)view.findViewById(R.id.horizontlevelline);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_HORIZONT).equals("On"))
            startSensorListing();
        else
            view.setVisibility(View.GONE);
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
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = sensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                sensorManager.getOrientation(R, orientation);
                roll = orientation[1];
                //pitch = orientation[0];
            }
            float rolldegree = Math.round(roll * 57.2957795);
            //float pitchdegree = Math.round(pitch * 57.2957795);
            if (RotateDegree != rolldegree) {
                RotateAnimation rotateAnimation = new RotateAnimation(RotateDegree, rolldegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setFillAfter(true);
                lineImage.startAnimation(rotateAnimation);
                RotateDegree = rolldegree;
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopSensorListing()
    {
        if (sensorManager != null)
            sensorManager.unregisterListener(this);
    }

}
