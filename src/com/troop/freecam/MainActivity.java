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
import com.troop.freecam.controls.menu.SettingsMenuControl;
import com.troop.freecam.interfaces.ParametersChangedInterface;
import com.troop.freecam.manager.CheckEvo3DSwitchModeManager;
import com.troop.freecam.manager.MyTimer;
import com.troop.freecam.manager.parameters.ParametersManager;
import com.troop.freecam.manager.SettingsManager;
import com.troop.freecam.surfaces.CamPreview;
import com.troop.freecam.surfaces.DrawingOverlaySurface;
import com.troop.freecam.utils.DeviceUtils;
import com.troop.freecam.utils.EncodeTiff;
import com.troop.menu.PictureFormatMenu;

import java.io.File;

public class MainActivity extends LayoutActivity implements ParametersChangedInterface
{
    PictureFormatMenu pictureFormatMenu;

    public ImageButton shotButton;
    public ImageButton thumbButton;
    Button switchVideoPicture;
    public boolean HDRMode = false;

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
    //SeekbarViewControl seekbarViewFragment;
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

    CheckEvo3DSwitchModeManager checkEvo3DSwitchModeManager;

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
        if (DeviceUtils.isEvo3d())
        {
            checkEvo3DSwitchModeManager = new CheckEvo3DSwitchModeManager(camMan);
            checkEvo3DSwitchModeManager.Start();
        }

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
        //seekbarViewFragment = (SeekbarViewControl)findViewById(R.id.tableVIEW);
        //seekbarViewFragment.SetCameraManger(camMan, this);
        manualMenuControl = (ManualMenuControl)findViewById(R.id.Layout_Manual);
        manualMenuControl.SetStuff(camMan, this);


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
        EncodeTiff.setContext(getApplicationContext());

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
            doaction();
        }
    };

    public void doRJ()
    {

    }

        private void doaction()
        {

            thumbButton.setImageBitmap(null);
            if(recordVideo == false && !camMan.IsWorking)
            {
                if (HDRMode == false)
                {
                    if(!camMan.autoFocusManager.focusing)
                    {
                        //camMan.StartRawTakePicture("jpeg");
                        camMan.StartTakePicture();

                    }
                    else
                    {
                       // camMan.StartRawTakePicture("jpeg");
                        camMan.autoFocusManager.takePicture = true;

                    }
                }
                else
                    camMan.HdrRender.TakeHDRPictures(true);


            }
            else
            if (recordVideo == true)
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
        if(DeviceUtils.isEvo3d() || DeviceUtils.isZTEADV())
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
            //seekbarViewFragment.UpdateValues(restarted);
            autoMenuFragment.UpdateUI(restarted, paras);
            settingsFragment.UpdateUI(restarted, paras);
            manualMenuControl.UpdateUI(restarted);



            //Crosshair appairing
            /*if (camMan.parametersManager.getParameters().getFocusMode().equals("auto") || camMan.parametersManager.getParameters().getFocusMode().equals(Camera.Parameters.FOCUS_MODE_MACRO)
                    || camMan.parametersManager.getParameters().getFocusMode().equals("extended"))
            {
                drawSurface.drawingRectHelper.Enabled = true;
            }
            else
            {
                drawSurface.drawingRectHelper.Enabled = false;
                drawSurface.drawingRectHelper.drawRectangle = false;
                drawSurface.drawingRectHelper.Draw();
            }*/
            infoScreenFragment.showtext();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

}
	




