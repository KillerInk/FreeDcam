package com.troop.freecam;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.troop.freecam.camera.CameraManager;
import com.troop.freecam.controls.AutoMenuControl;
import com.troop.freecam.controls.InfoScreenControl;
import com.troop.freecam.controls.SettingsMenuControl;
import com.troop.freecam.fragments.AutoMenuFragment;

import com.troop.freecam.fragments.InfoScreenFragment;
import com.troop.freecam.fragments.SeekbarViewFragment;
import com.troop.freecam.fragments.SettingsMenuFagment;
import com.troop.freecam.surfaces.DrawingOverlaySurface;
import com.troop.freecam.manager.MyTimer;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.interfaces.ParametersChangedInterface;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.utils.DeviceUtils;

import java.io.File;

public class MainActivity extends LayoutActivity implements ParametersChangedInterface
{


    public ImageButton shotButton;
    public ImageButton thumbButton;
    Button switchVideoPicture;
    public boolean HDRMode = false;

    RelativeLayout mainlayout;
    public CheckBox manualExposure;
    public CheckBox manualShaprness;
    public CheckBox manualFocus;
    public CheckBox contrastcheckBox;
    CheckBox brightnessCheckBox;
    public CheckBox saturationCheckBox;
    protected TextView recordingTimerTextView;
    protected MyTimer recordTimer;
    public CheckBox checkBoxZSL;
    //*******************
    public Boolean AFS_enable;
    Button AfAssitButton;
    SettingsMenuControl settingsFragment;
    InfoScreenControl infoScreenFragment;
    AutoMenuControl autoMenuFragment;
    SeekbarViewFragment seekbarViewFragment;
    int currentZoom = 0;
    SensorManager sensorManager;
    Sensor sensor;
    public Button button_stab;
    View view;


    public SharedPreferences preferences;
    public boolean recordVideo = false;
    protected CameraManager camMan;
    public CamPreview mPreview;
    //public DrawingOverlaySurface drawSurface;
    SurfaceHolder holder;

    //private final int DEFAULT_SYSTEM_UI_VISIBILITY = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    //        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        recordVideo = preferences.getBoolean("recordVideo", false);
        //drawSurface = (DrawingOverlaySurface) findViewById(R.id.view);
        mPreview = (CamPreview) findViewById(R.id.camPreview1);
        mPreview.setKeepScreenOn(true);
        holder = mPreview.getHolder();
        camMan = new CameraManager(mPreview, this, settingsManager);
        camMan.parametersManager.setParametersChanged(this);

        initButtons();

        infoScreenFragment = (InfoScreenControl) findViewById(R.id.infoScreenContainer);
        infoScreenFragment.SetCameraManager(camMan);
        //getSupportFragmentManager().beginTransaction().add(R.id.infoScreenContainer, infoScreenFragment).commit();

        settingsFragment = (SettingsMenuControl)findViewById(R.id.LayoutSettings);
        settingsFragment.SetStuff(camMan, this, infoScreenFragment);
        //settingsFragment = new SettingsMenuFagment(camMan, this, infoScreenFragment);
        //getSupportFragmentManager().beginTransaction().add(R.id.LayoutSettings, settingsFragment).commit();

        //autoMenuFragment = new AutoMenuFragment(camMan, this);
        //getSupportFragmentManager().beginTransaction().add(R.id.LayoutAuto, autoMenuFragment).commit();
        autoMenuFragment = (AutoMenuControl)findViewById(R.id.LayoutAuto);
        autoMenuFragment.SetCameraManager(camMan, this);
        seekbarViewFragment = new SeekbarViewFragment(camMan, this);
        getSupportFragmentManager().beginTransaction().add(R.id.tableVIEW, seekbarViewFragment).commit();


        mPreview.SetCameraManager(camMan);
        //drawSurface.SetCameraManager(camMan);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

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
                    Uri uri = Uri.parse("file:/" + camMan.lastPicturePath);

                    Intent i=new Intent(Intent.ACTION_VIEW);
                    if (camMan.lastPicturePath.endsWith("mp4"))
                        i.setDataAndType(uri, "video/*");
                    else
                        i.setDataAndType(uri, "image/*");
                    startActivity(i);
                }
            }
        });




        manualExposure = (CheckBox)findViewById(R.id.checkBox_exposureManual);
        manualExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (manualExposure.isChecked())
                {
                    seekbarViewFragment.exposureRow.setVisibility(View.VISIBLE);
                }
                else
                {
                    seekbarViewFragment.exposureRow.setVisibility(View.GONE);
                }
            }
        });

        manualShaprness = (CheckBox) findViewById(R.id.checkBox_sharpness);
        manualShaprness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualShaprness.isChecked())
                {
                    seekbarViewFragment.sharpnessRow.setVisibility(View.VISIBLE);
                }
                else
                {
                    seekbarViewFragment.sharpnessRow.setVisibility(View.GONE);
                }
                //sharpnessRow.invalidate();
            }
        });

        //********************ManualFocus******************************************


        manualFocus = (CheckBox)findViewById(R.id.checkBox_focus);
        manualFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualFocus.isChecked())

                    seekbarViewFragment.focusRow.setVisibility(View.VISIBLE);

                else
                    seekbarViewFragment.focusRow.setVisibility(View.GONE);
                //focusButton.setEnabled(true);
            }
        });


        //*****************************************End********************************************


        contrastcheckBox = (CheckBox)findViewById(R.id.checkBox_contrast);
        contrastcheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contrastcheckBox.isChecked())
                    seekbarViewFragment.contrastRow.setVisibility(View.VISIBLE);
                else
                    seekbarViewFragment.contrastRow.setVisibility(View.GONE);
            }
        });





        brightnessCheckBox = (CheckBox)findViewById(R.id.checkBox_brightness);
        brightnessCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brightnessCheckBox.isChecked())
                    seekbarViewFragment.brightnessRow.setVisibility(View.VISIBLE);
                else
                    seekbarViewFragment.brightnessRow.setVisibility(View.GONE);
            }
        });


        saturationCheckBox = (CheckBox) findViewById(R.id.checkBox_saturation);
        saturationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saturationCheckBox.isChecked())
                    seekbarViewFragment.saturationRow.setVisibility(View.VISIBLE);
                else
                    seekbarViewFragment.saturationRow.setVisibility(View.GONE);
            }
        });

        switchVideoPicture = (Button)findViewById(R.id.button_switchVideoPicture);
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
        });

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
            thumbButton.setImageBitmap(null);
            if(recordVideo == false)
            {
                if (HDRMode == false)
                    camMan.StartTakePicture();
                else
                    camMan.HdrRender.TakeHDRPictures(true);


            }
            else
            {
                if (camMan.IsRecording == false)
                {
                    camMan.StartRecording();
                    recordTimer.Start();
                    shotButton.setBackgroundResource(R.drawable.icon_stop_thanos_blast);
                    mainlayout.addView(recordingTimerTextView);
                    //OnScreenPictureText.setText("Video Size:");
                    //OnScreenPictureValue.setText(camMan.parametersManager.getParameters().get("video-size"));
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
    };

    public void setSwitchVideoPictureBackground()
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
    }

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

    public void videoui()
    {


    }

    public void tabletScaling()
    {
        //Will Scale Entire UI For Tablet Mode Current Tabs Nexus 7 / Nexus 10
    }

    public void chipsetProp()
    {
        try {
            String s = Build.MODEL;

            if(!DeviceUtils.isG2())
                manualFocus.setVisibility(View.GONE);

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
                camMan.StartTakePicture();
            }
            else
            {
                super.dispatchKeyEvent(event);
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
        sensorManager.registerListener(camMan, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        infoScreenFragment.hideCurrentConfig();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(camMan);
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
    public void parametersHasChanged(boolean restarted)
    {
        try{
            seekbarViewFragment.UpdateValues(restarted);
            autoMenuFragment.UpdateUI(restarted);
            settingsFragment.UpdateUI(restarted);


            //Crosshair appairing
            /*if (camMan.parametersManager.getParameters().getFocusMode().equals("auto"))
            {
                drawSurface.drawingRectHelper.Enabled = true;
            }
            else
            {
                drawSurface.drawingRectHelper.Enabled = false;
            }*/
            infoScreenFragment.showtext();

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
	




