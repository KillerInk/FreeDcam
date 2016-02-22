package troop.com.themesample.subfragments;

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

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;

/**
 * Created by Ar4eR on 15.01.16.
 */
public class HorizontLineFragment extends AbstractFragment implements AbstractModeParameter.I_ModeParameterEvent{

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
    float rolldegree;
    float pitchdegree;
    final float rad2deg = (float)(180.0f/Math.PI);
    final Handler handler = new Handler();
    private HandlerThread sensorThread;
    private Handler sensorHandler;
    private MySensorListener msl =new MySensorListener();



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
        sensorThread = new HandlerThread("Sensor thread", Thread.MAX_PRIORITY);
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (appSettingsManager.getString(AppSettingsManager.SETTING_HORIZONT).equals("Off") || appSettingsManager.getString(AppSettingsManager.SETTING_HORIZONT).equals(""))
            view.setVisibility(View.GONE);
        else
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
    public void onVisibilityChanged(boolean visible) {

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
            sensorManager.registerListener(msl, accelerometer, 1000000, sensorHandler);
            sensorManager.registerListener(msl, magnetometer, 1000000, sensorHandler);
        }
    }

    public void stopSensorListing()
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
        startSensorListing();
    }

    private class MySensorListener implements SensorEventListener {

        static final float ALPHA = 0.2f;

        protected float[] lowPass( float[] input, float[] output ) {
            if ( output == null ) return input;

            for ( int i=0; i<input.length; i++ ) {
                output[i] = (input[i] * ALPHA) + (output[i] * (1.0f - ALPHA));
                //output[i] = output[i] + ALPHA * (input[i] - output[i]);
            }
            return output;
        }

        public void onAccuracyChanged (Sensor sensor, int accuracy) {}

        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                mGravity = lowPass(event.values.clone(),mGravity);
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                mGeomagnetic = event.values.clone();
            if (mGravity != null && mGeomagnetic != null) {
                //hltheard.run();
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = sensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    sensorManager.getOrientation(R, orientation);
                    roll = orientation[1];
                    pitch = orientation[2];
                    rolldegree = roll * rad2deg;
                    pitchdegree = pitch * rad2deg;
                   // Log.d("Sometag", String.valueOf(pitchdegree));
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
