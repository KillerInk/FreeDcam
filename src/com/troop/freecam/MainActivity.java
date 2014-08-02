package com.troop.freecam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.menu.AutoMenuControl;
import com.troop.freecam.controls.InfoScreenControl;
import com.troop.freecam.controls.menu.ManualMenuControl;
import com.troop.freecam.controls.menu.ModeMenuControl;
import com.troop.freecam.controls.menu.SettingsMenuControl;
import com.troop.freecam.interfaces.ParametersChangedInterface;
import com.troop.freecam.manager.CheckEvo3DSwitchModeManager;
import com.troop.freecam.manager.MyTimer;
import com.troop.freecam.manager.parameters.ParametersManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.surfaces.DrawingOverlaySurface;
import com.troop.freecam.utils.DeviceUtils;

import java.io.File;

public class MainActivity extends LayoutActivity implements ParametersChangedInterface
{
    public ImageButton shotButton;
    public ImageButton thumbButton;
    RelativeLayout mainlayout;
    protected TextView recordingTimerTextView;
    protected MyTimer recordTimer;
    public CheckBox checkBoxZSL;
    //*******************
    public Boolean AFS_enable;
    Button AfAssitButton;
    SettingsMenuControl settingsFragment;
    InfoScreenControl infoScreenFragment;
    AutoMenuControl autoMenuFragment;
    ManualMenuControl manualMenuControl;
    ModeMenuControl cameraModesControl;
    int currentZoom = 0;
    SensorManager sensorManager;
    Sensor sensor;
    View view;
    public SharedPreferences preferences;
    public boolean recordVideo = false;
    protected CameraManager camMan;
    public CamPreview mPreview;
    SurfaceHolder holder;

    CheckEvo3DSwitchModeManager checkEvo3DSwitchModeManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        recordVideo = preferences.getBoolean("recordVideo", false);

        mPreview = (CamPreview) findViewById(R.id.camPreview1);
        mPreview.setKeepScreenOn(true);
        holder = mPreview.getHolder();
        camMan = new CameraManager(mPreview, this, settingsManager);
        camMan.parametersManager.setParametersChanged(this);
        if (DeviceUtils.isEvo3d())
        {
            checkEvo3DSwitchModeManager = new CheckEvo3DSwitchModeManager(camMan);
            checkEvo3DSwitchModeManager.Start();
        }

        initButtons();

        infoScreenFragment = (InfoScreenControl) findViewById(R.id.infoScreenContainer);
        infoScreenFragment.SetCameraManager(camMan);

        cameraModesControl = (ModeMenuControl)findViewById(R.id.ModesConrolContainer);
        cameraModesControl.SetStuff(camMan, this);

        settingsFragment = (SettingsMenuControl)findViewById(R.id.LayoutSettings);
        settingsFragment.SetStuff(camMan, this, infoScreenFragment);

        autoMenuFragment = (AutoMenuControl)findViewById(R.id.LayoutAuto);
        autoMenuFragment.SetCameraManager(camMan, this);

        manualMenuControl = (ManualMenuControl)findViewById(R.id.Layout_Manual);
        manualMenuControl.SetStuff(camMan, this);

        mPreview.SetCameraManager(camMan);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        recordTimer = new MyTimer(recordingTimerTextView);
        chipsetProp();
        hidenavkeys();

	}

    private void initButtons()
    {
        shotButton = (ImageButton) findViewById(R.id.imageButton1);
        shotButton.setOnClickListener(shotListner);

        thumbButton = (ImageButton)findViewById(R.id.imageButton_thumb);
        thumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (camMan.lastPicturePath != null)
                {
                    Uri uri = Uri.fromFile(new File(camMan.lastPicturePath));

                    Intent i=new Intent(Intent.ACTION_VIEW);
                    if (camMan.lastPicturePath.endsWith("mp4"))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    startActivity(i);
                }
            }
        });

        /*switchVideoPicture = (Button)findViewById(R.id.button_switchVideoPicture);
        setSwitchVideoPictureBackground();
        switchVideoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordVideo) {
                    recordVideo = false;

                    preferences.edit().putBoolean("recordVideo", false).commit();
                } else {
                    recordVideo = true;

                    preferences.edit().putBoolean("recordVideo", true).commit();
                }
                setSwitchVideoPictureBackground();
            }
        });*/

        recordingTimerTextView = (TextView)findViewById(R.id.textView_timerRecording);
        mainlayout = (RelativeLayout)findViewById(R.id.mainRelativLayout);
        mainlayout.removeView(recordingTimerTextView);

        //06-12-13********************
        /*Hfr Menu****
        ippButton = (Button)findViewById(R.id.button_ipp);
        ippButton.setOnClickListener(new IppMenu(camMan, this));
        */
        //*****************************
    }

    View.OnClickListener shotListner = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            doaction();
        }
    };

        private void doaction()
        {
            thumbButton.setImageBitmap(null);
            //Picture Mode
            if(camMan.Settings.CameraMode.get() == SettingsManager.Preferences.MODE_PIC && !camMan.IsWorking)
            {
                if(!camMan.autoFocusManager.focusing)
                    camMan.StartTakePicture();
                else
                    camMan.autoFocusManager.takePicture = true;
            }
            //HDR Mode
            else if(camMan.Settings.CameraMode.get() == SettingsManager.Preferences.MODE_HDR)
            {
                camMan.HdrRender.TakeHDRPictures(true);
            }
            //VideoMode
            else if (camMan.Settings.CameraMode.get() == SettingsManager.Preferences.MODE_VIDEO)
            {
                if (camMan.IsRecording == false)
                {
                    camMan.StartRecording();
                    recordTimer.Start();
                    shotButton.setBackgroundResource(R.drawable.icon_stop_thanos_blast);
                    mainlayout.addView(recordingTimerTextView);
                }
                else
                {
                    camMan.StopRecording();
                    recordTimer.Stop();
                    shotButton.setBackgroundResource(R.drawable.icon_record_thanos_blast);
                    thumbButton.setImageBitmap(ThumbnailUtils.createVideoThumbnail(camMan.lastPicturePath, MediaStore.Images.Thumbnails.MINI_KIND));
                    mainlayout.removeView(recordingTimerTextView);
                }
            }
        }


    /*public void setSwitchVideoPictureBackground()
    {
        if (recordVideo)
        {
            switchVideoPicture.setBackgroundResource(R.drawable.icon_video_mode);
            shotButton.setBackgroundResource(R.drawable.icon_record_thanos_blast);
        }
        else
        {
            switchVideoPicture.setBackgroundResource(R.drawable.icon_picture_mode);
            shotButton.setBackgroundResource(R.drawable.icon_shutter_thanos_blast);
        }
    }*/

   /* @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        //Check the event and do magic here, such as...
        if (event.getAction() == MotionEvent.ACTION_DOWN) {


        }

        //Be careful not to override the return unless necessary
        return super.dispatchTouchEvent(event);
    } */

    /*public void videoui()
    {


    }

    public void tabletScaling()
    {
        //Will Scale Entire UI For Tablet Mode Current Tabs Nexus 7 / Nexus 10
    }*/

    public void chipsetProp()
    {
        try {
            String s = Build.MODEL;

            //if(!DeviceUtils.isG2())
                //manualFocus.setVisibility(View.GONE);

            if (!DeviceUtils.isQualcomm())
            {
                checkBoxZSL.setEnabled(false);
                //autoMenuFragment.buttonMetering.setEnabled(false);
            }

            if(!s.equals("LG-P720") || !s.equals("LG-P725"))
                settingsFragment.upsidedown.setVisibility(View.GONE);

            if (!DeviceUtils.isOmap())
            {
                //settingsFragment.ippButton.setEnabled(false);
                //autoMenuFragment.exposureButton.setEnabled(false);
            }
        }
        catch (NullPointerException ex)
        {


        }
    }

    public void hidenavkeys()
    {
        try {

            view.setSystemUiVisibility(2);
        }
        catch (NullPointerException ex)
        {

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int key = event.getKeyCode();

        if(key == KeyEvent.KEYCODE_VOLUME_UP)
        {
            camMan.zoomManager.setZoom(1);
        }
        else if(key == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            camMan.zoomManager.setZoom(-1);
        }
        else if(key == KeyEvent.KEYCODE_3D_MODE ||key == KeyEvent.KEYCODE_POWER )
        {
            doaction();
        }
        else
        {
            super.dispatchKeyEvent(event);
        }
        if(DeviceUtils.isEvo3d())
        {
            if (key == 27)
                doaction();
            if (key == 80 && !camMan.autoFocusManager.focusing && !camMan.autoFocusManager.hasFocus )
                camMan.autoFocusManager.StartFocus();

        }

        return true;
    }


    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    View.OnClickListener AFSListner = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (AFS_enable != true)
            {
                AFS_enable = true;
            }
            else
            {
                AFS_enable = false;
            }

        }
    };
	


    public void SwitchCropButton()
    {
        if(!camMan.Settings.Cameras.GetCamera().equals(SettingsManager.Preferences.MODE_3D))
        {
            settingsFragment.crop_box.setVisibility(View.GONE);
        }
        else
        {
            try
            {
                settingsFragment.crop_box.setVisibility(View.VISIBLE);
            }
            catch (Exception ex)
            {
                Log.d("MainActivity", "CropBox is already added");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(camMan, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        infoScreenFragment.hideCurrentConfig();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(camMan);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("result");
                camMan.onPictureSaved(new File(result));
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    //TODO throw event with a value wich parameter has changed
    ///this updates the complete ui. its called everytime the camera parameters has changed
    @Override
    public void parametersHasChanged(boolean restarted, ParametersManager.enumParameters paras)
    {
        try{
            autoMenuFragment.UpdateUI(restarted, paras);
            settingsFragment.UpdateUI(restarted, paras);
            manualMenuControl.UpdateUI(restarted);
            infoScreenFragment.showtext();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
	




