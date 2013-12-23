package com.troop.freecam;

import android.app.Activity;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.troop.freecam.manager.Drawing.DrawingOverlaySurface;
import com.troop.freecam.manager.ManualSaturationManager;
import com.troop.freecam.manager.MyTimer;
import com.troop.freecam.manager.ParametersManager;
import com.troop.freecam.manager.interfaces.ParametersChangedInterface;
import com.troop.freecam.utils.DeviceUtils;
import com.troop.menu.AFPriorityMenu;
import com.troop.menu.ColorMenu;
import com.troop.menu.DenoiseMenu;
import com.troop.menu.ExposureMenu;
import com.troop.menu.FlashMenu;
import com.troop.menu.FocusMenu;
import com.troop.menu.IppMenu;
import com.troop.menu.IsoMenu;
import com.troop.menu.MeteringMenu;
import com.troop.menu.PictureFormatMenu;
import com.troop.menu.PictureSizeMenu;
import com.troop.menu.PreviewFormatMenu;
import com.troop.menu.PreviewSizeMenu;
import com.troop.menu.SceneMenu;
import com.troop.menu.WhiteBalanceMenu;
import com.troop.menu.ZslMenu;
import com.troop.menu.switchcameramenu;

import java.io.File;

public class MainActivity extends Activity implements ParametersChangedInterface
{
	public CamPreview mPreview;
    public DrawingOverlaySurface drawSurface;
	public ImageButton shotButton;
	public Button flashButton;
    public Button focusButton;
    public Button sceneButton;
    public Button whitebalanceButton;
    public Button colorButton;
    public Button isoButton;
    public Button exposureButton;
    public Button switch3dButton;
    public Button pictureSizeButton;
    public Button previewSizeButton;
    public Button ippButton;

    //06-12-13***********
    public Button buttonAfPriority;
    public Button buttonMetering;
    public Button buttonPictureFormat;
    public Button buttonPreviewFormat;
    public CheckBox checkBoxOnScreen;
    public CheckBox checkBoxZSL;
    //*******************
	Camera.Parameters paras;
    SurfaceHolder holder;
    CameraManager camMan;
    //************************Text Views Add****************05-12-13
    TextView OnScreenBrightnessText;
    TextView OnScreenBrightnessValue;
    TextView OnScreenContrastText;
    TextView OnScreenContrastValue;
    TextView OnScreenEVText;
    TextView OnScreenEVValue;
    TextView OnScreenFlashText;
    TextView OnScreenFlashValue;
    TextView OnScreenEffectText;
    TextView OnScreenEffectValue;
    public TextView OnScreenFocusText;
    public TextView OnScreenFocusValue;
    TextView OnScreeISOText;
    TextView OnScreeISOValue;
    TextView OnScreeMeterText;
    public TextView OnScreeMeterValue;
    TextView OnScreenSaturationText;
    TextView OnScreeSaturationValue;
    TextView OnScreeSceneText;
    TextView OnScreeSceneValue;
    TextView OnScreenPictureText;
    TextView OnScreenPictureValue;
    TextView OnScreeSharpnessText;
    TextView OnScreenSharpnessValue;
    TextView OnScreenWBText;
    TextView OnScreenWBValue;

    //******************************************************

    public  ViewGroup appViewGroup;
    public SeekBar exposureSeekbar;
    public ImageButton thumbButton;

    public Boolean AFS_enable;

    public CheckBox manualExposure;
    TableRow exposureRow;

    public CheckBox manualShaprness;
    public CheckBox manualFocus;

    TableRow sharpnessRow;
    public SeekBar sharpnessSeekBar;
    TableLayout tableLayout;
    public TextView sharpnessTextView;
    public TextView exposureTextView;

    public  TextView contrastTextView;
    TableRow contrastRow;
    public CheckBox contrastcheckBox;
    public SeekBar contrastSeekBar;

    public TextView brightnessTextView;
    public SeekBar brightnessSeekBar;
    public SeekBar focusSeekBar;
    TableRow brightnessRow;
    TableRow focusRow;
    CheckBox brightnessCheckBox;

    public TextView saturationTextView;
    public SeekBar saturationSeekBar;
    public CheckBox saturationCheckBox;
    TableRow saturationRow;

    Button switchVideoPicture;
    Button AfAssitButton;

    Button manualLayoutButton;
    Button autoLayoutButton;
    Button settingLayoutButton;
    LinearLayout baseMenuLayout;
    LinearLayout manualMenuLayout;
    LinearLayout autoMenuLayout;
    LinearLayout settingsMenuLayout;
    public boolean hideManualMenu = true;
    public boolean hideSettingsMenu = true;
    public boolean hideAutoMenu = true;
    SharedPreferences preferences;
    CheckBox upsidedown;
    public boolean recordVideo = false;

    CheckBox crop_box;

    int currentZoom = 0;
    SensorManager sensorManager;
    Sensor sensor;

    TextView recordingTimerTextView;
    RelativeLayout mainlayout;

    MyTimer recordTimer;

    CheckBox checkboxHDR;
    boolean HDRMode = false;

    public Button button_zsl;
    public Button button_denoise;
    public Button button_stab;
    View view;

    //private final int DEFAULT_SYSTEM_UI_VISIBILITY = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    //        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        //setContentView(R.layout.activity_main);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.main, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        recordVideo = preferences.getBoolean("recordVideo", false);

        setContentView(R.layout.main);
        drawSurface = (DrawingOverlaySurface) findViewById(R.id.view);
		mPreview = (CamPreview) findViewById(R.id.camPreview1);
        mPreview.setKeepScreenOn(true);
        holder = mPreview.getHolder();
        camMan = new CameraManager(mPreview, this, preferences);
        camMan.parametersManager.setParametersChanged(this);

        mPreview.SetCameraManager(camMan);
        drawSurface.SetCameraManager(camMan);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        initButtons();
        initMenu();
        recordTimer = new MyTimer(recordingTimerTextView);
        chipsetProp();
        onScreenText();
        showtext();
        hidenavkeys();

        hideCurrentConfig();

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

    public void initMenu()
    {

        baseMenuLayout = (LinearLayout)findViewById(R.id.baseMenuLayout);
        autoMenuLayout = (LinearLayout)findViewById(R.id.LayoutAuto);
        manualMenuLayout = (LinearLayout)findViewById(R.id.Layout_Manual);
        settingsMenuLayout = (LinearLayout)findViewById(R.id.LayoutSettings);


        manualLayoutButton = (Button)findViewById(R.id.buttonManualMode);
        manualLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (hideManualMenu == false)
                {
                    hideManualMenu = true;
                    manualMenuLayout.setVisibility(View.GONE);
                }
                else
                {
                    hideManualMenu = false;
                    //if (baseMenuLayout.findViewById(R.id.Layout_Manual) == null)
                        manualMenuLayout.setVisibility(View.VISIBLE);
                    if (hideAutoMenu == false)
                    {
                        hideAutoMenu = true;
                        autoMenuLayout.setVisibility(View.GONE);
                    }
                    if (hideSettingsMenu == false)
                    {
                        hideSettingsMenu = true;
                        settingsMenuLayout.setVisibility(View.GONE);
                    }
                }

            }
        });

        autoLayoutButton = (Button)findViewById(R.id.buttonAutoMode);
        autoLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (hideAutoMenu == false)
                {
                    hideAutoMenu = true;

                    autoMenuLayout.setVisibility(View.GONE);
                }
                else
                {
                    hideAutoMenu = false;
                    //if (baseMenuLayout.findViewById(R.id.LayoutAuto) == null)
                        autoMenuLayout.setVisibility(View.VISIBLE);

                    if (hideSettingsMenu == false)
                    {
                        hideSettingsMenu = true;
                        settingsMenuLayout.setVisibility(View.GONE);
                    }
                    if (hideManualMenu == false)
                    {
                        hideManualMenu = true;
                        manualMenuLayout.setVisibility(View.GONE);
                    }

                }

            }
        });
        settingLayoutButton = (Button)findViewById(R.id.buttonSettingsMode);
        settingLayoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (hideSettingsMenu == false)
                {
                    hideSettingsMenu = true;
                    settingsMenuLayout.setVisibility(View.GONE);
                }
                else
                {
                    hideSettingsMenu = false;
                    //if (baseMenuLayout.findViewById(R.id.LayoutSettings) == null)
                        settingsMenuLayout.setVisibility(View.VISIBLE);
                    if (hideAutoMenu == false)
                    {
                        hideAutoMenu = true;
                        autoMenuLayout.setVisibility(View.GONE);
                    }
                    if (hideManualMenu == false)
                    {
                        hideManualMenu = true;
                        manualMenuLayout.setVisibility(View.GONE);
                    }

                }

            }
        });

        autoMenuLayout.setVisibility(View.GONE);
        manualMenuLayout.setVisibility(View.GONE);
        settingsMenuLayout.setVisibility(View.GONE);

        if(!preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D).equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            crop_box.setVisibility(View.GONE);
        }
        else
        {
            crop_box.setChecked(true);
        }
    }


    public void initButtons()
    {
        flashButton = (Button) findViewById(R.id.button_flash);
        flashButton.setOnClickListener(new FlashMenu(camMan, this));
        shotButton = (ImageButton) findViewById(R.id.imageButton1);
        shotButton.setOnClickListener(shotListner);

        focusButton = (Button) findViewById(R.id.button_focus);
        focusButton.setOnClickListener(new FocusMenu(camMan, this));
        sceneButton = (Button) findViewById(R.id.buttonScene);
        sceneButton.setOnClickListener(new SceneMenu(camMan, this));
        whitebalanceButton = (Button) findViewById(R.id.buttonwhiteBalance);
        whitebalanceButton.setOnClickListener(new WhiteBalanceMenu(camMan, this));
        colorButton = (Button) findViewById(R.id.buttoncolor);
        colorButton.setOnClickListener(new ColorMenu(camMan, this));
        isoButton = (Button) findViewById(R.id.buttoniso);
        isoButton.setOnClickListener(new IsoMenu(camMan, this));
        exposureButton = (Button) findViewById(R.id.button_exposure);
        exposureButton.setOnClickListener(new ExposureMenu(camMan, this));
        pictureSizeButton = (Button) findViewById(R.id.button_pictureSize);
        pictureSizeButton.setOnClickListener(new PictureSizeMenu(camMan, this));
        previewSizeButton = (Button)findViewById(R.id.button_previewsize);
        previewSizeButton.setOnClickListener(new PreviewSizeMenu(camMan,this));

        //06-12-13*************************************************************
        buttonAfPriority = (Button)findViewById(R.id.buttonAFPriority);
        buttonAfPriority.setOnClickListener(new AFPriorityMenu(camMan,this));


        buttonMetering = (Button)findViewById(R.id.buttonMetering);
        buttonMetering.setOnClickListener(new MeteringMenu(camMan,this));


        buttonPictureFormat = (Button)findViewById(R.id.button_pictureFormat);
        buttonPictureFormat.setOnClickListener(new PictureFormatMenu(camMan,this));

        buttonPreviewFormat = (Button)findViewById(R.id.buttonPreviewFormat);
        buttonPreviewFormat.setOnClickListener(new PreviewFormatMenu(camMan,this));
        //**********************************************************************



        ippButton = (Button)findViewById(R.id.button_ipp);
        ippButton.setOnClickListener(new IppMenu(camMan, this));

        crop_box = (CheckBox)findViewById(R.id.checkBox_crop);
        crop_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (crop_box.isChecked())
                {
                    camMan.crop = true;
                    preferences.edit().putBoolean("crop", true).commit();
                }
                else
                {
                    camMan.crop = false;
                    preferences.edit().putBoolean("crop", false).commit();
                }
            }
        });


        //exposureSeekbar.setVisibility(View.INVISIBLE);
        switch3dButton = (Button) findViewById(R.id.button_switch3d);
        switch3dButton.setOnClickListener(new switchcameramenu(camMan, this));
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

        tableLayout = (TableLayout) findViewById(R.id.tableVIEW);


        exposureTextView = (TextView) findViewById(R.id.textViewexposure);
        exposureSeekbar  = (SeekBar) findViewById(R.id.seekBar_exposure);
        exposureSeekbar.setProgress(30);
        exposureSeekbar.setOnSeekBarChangeListener(camMan.manualExposureManager);
        exposureRow = (TableRow) findViewById(R.id.tableRowExposure);
        exposureRow.setVisibility(View.GONE);
        manualExposure = (CheckBox)findViewById(R.id.checkBox_exposureManual);
        manualExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (manualExposure.isChecked())
                {
                    exposureRow.setVisibility(View.VISIBLE);
                }
                else
                {
                    exposureRow.setVisibility(View.GONE);
                }
            }
        });


        sharpnessTextView = (TextView)findViewById(R.id.textView_sharpness);
        sharpnessSeekBar = (SeekBar)findViewById(R.id.seekBar_sharpness);
        sharpnessSeekBar.setProgress(100);
        sharpnessSeekBar.setOnSeekBarChangeListener(camMan.manualSharpnessManager);
        sharpnessRow = (TableRow) findViewById(R.id.tableRowSharpness);
        sharpnessRow.setVisibility(View.GONE);

        manualShaprness = (CheckBox) findViewById(R.id.checkBox_sharpness);
        manualShaprness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualShaprness.isChecked())
                {
                    sharpnessRow.setVisibility(View.VISIBLE);
                }
                else
                {
                    sharpnessRow.setVisibility(View.GONE);
                }
                //sharpnessRow.invalidate();
            }
        });

        //********************ManualFocus******************************************
        focusRow = (TableRow)findViewById(R.id.tableRowFocus);
        focusSeekBar = (SeekBar)findViewById(R.id.seekBarFocus);
        focusSeekBar.setMax(60);
        manualFocus = (CheckBox)findViewById(R.id.checkBox_focus);
        brightnessTextView = (TextView)(findViewById(R.id.textViewFocus));
        focusSeekBar.setOnSeekBarChangeListener(camMan.manualFocus);

        manualFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualFocus.isChecked())

                    focusRow.setVisibility(View.VISIBLE);

                else
                    focusRow.setVisibility(View.GONE);
                focusButton.setEnabled(true);
            }
        });
        focusRow.setVisibility(View.GONE);

        //*****************************************End********************************************

        contrastRow = (TableRow)findViewById(R.id.tableRowContrast);

        contrastSeekBar = (SeekBar) findViewById(R.id.seekBar_contrast);
        contrastSeekBar.setProgress(100);
        contrastSeekBar.setOnSeekBarChangeListener(camMan.manualContrastManager);

        contrastTextView = (TextView) findViewById(R.id.textView_contrast);
        contrastcheckBox = (CheckBox)findViewById(R.id.checkBox_contrast);
        contrastcheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contrastcheckBox.isChecked())
                    contrastRow.setVisibility(View.VISIBLE);
                else
                    contrastRow.setVisibility(View.GONE);
            }
        });
        contrastRow.setVisibility(View.GONE);


        brightnessRow = (TableRow)findViewById(R.id.tableRowBrightness);
        brightnessSeekBar = (SeekBar)findViewById(R.id.seekBar_brightness);
        brightnessCheckBox = (CheckBox)findViewById(R.id.checkBox_brightness);
        brightnessTextView = (TextView)(findViewById(R.id.textView_brightness));
        brightnessSeekBar.setOnSeekBarChangeListener(camMan.manualBrightnessManager);

        brightnessCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brightnessCheckBox.isChecked())
                    tableLayout.addView(brightnessRow);
                else
                    tableLayout.removeView(brightnessRow);
            }
        });
        tableLayout.removeView(brightnessRow);

        saturationCheckBox = (CheckBox) findViewById(R.id.checkBox_saturation);
        saturationRow = (TableRow)findViewById(R.id.tableRowsaturation);
        saturationTextView = (TextView)findViewById(R.id.textViewSaturation);
        saturationSeekBar = (SeekBar)findViewById(R.id.seekBarSaturation);
        saturationSeekBar.setProgress(100);
        saturationSeekBar.setOnSeekBarChangeListener(new ManualSaturationManager(camMan));
        saturationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saturationCheckBox.isChecked())
                    tableLayout.addView(saturationRow);
                else
                    tableLayout.removeView(saturationRow);
            }
        });
        tableLayout.removeView(saturationRow);

        upsidedown = (CheckBox) findViewById(R.id.button_fixupsidedown);
        boolean upsidedownfix = preferences.getBoolean("upsidedown", false);
        if (upsidedownfix == true)
            upsidedown.setChecked(true);
        upsidedown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (upsidedown.isChecked())
                {
                    preferences.edit().putBoolean("upsidedown", true).commit();
                    camMan.Stop();
                    camMan.Start();

                    camMan.Restart(true);
                }
                else
                {
                    preferences.edit().putBoolean("upsidedown", false).commit();
                    camMan.Stop();
                    camMan.Start();
                    camMan.Restart(true);
                }

            }
        });

        switchVideoPicture = (Button)findViewById(R.id.button_switchVideoPicture);
        setSwitchVideoPictureBackground();
        switchVideoPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recordVideo)
                {
                    recordVideo = false;

                    preferences.edit().putBoolean("recordVideo", false).commit();
                }
                else
                {
                    recordVideo = true;

                    preferences.edit().putBoolean("recordVideo", true).commit();
                }
                setSwitchVideoPictureBackground();
            }
        });

        recordingTimerTextView = (TextView)findViewById(R.id.textView_timerRecording);
        mainlayout = (RelativeLayout)findViewById(R.id.mainRelativLayout);
        mainlayout.removeView(recordingTimerTextView);

        checkboxHDR = (CheckBox)findViewById(R.id.checkBox_hdr);
        checkboxHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HDRMode = checkboxHDR.isChecked();
            }
        });



        //06-12-13********************


        checkBoxOnScreen = (CheckBox)findViewById(R.id.checkBoxOnscreen);
        checkBoxOnScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (checkBoxOnScreen.isChecked())
                {
                    showCurrentConfig();
                }
                else
                {
                    hideCurrentConfig();
                }
            }
        });


        //07-12-13
       // button_stab = (Button)findViewById(R.id.button_ipp);
       // button_stab.setOnClickListener(new IppMenu(camMan, this));

        button_denoise = (Button)findViewById(R.id.button_denoise);
        button_denoise.setOnClickListener(new DenoiseMenu(camMan, this));

        button_zsl = (Button)findViewById(R.id.buttonZsl);
        button_zsl.setOnClickListener(new ZslMenu(camMan, this));

        /*Hfr Menu****
        ippButton = (Button)findViewById(R.id.button_ipp);
        ippButton.setOnClickListener(new IppMenu(camMan, this));
        */





        //*****************************


    }

    public void onScreenText()
    {
        try {


            OnScreenBrightnessText = (TextView) findViewById(R.id.textViewBrightnessText);
            OnScreenBrightnessValue = (TextView) findViewById(R.id.textViewBrightnessValue);
            OnScreenContrastText = (TextView) findViewById(R.id.textViewContrastText);
            OnScreenContrastValue = (TextView) findViewById(R.id.textViewContrastValue);
            OnScreenEVText = (TextView) findViewById(R.id.textViewEVText);
            OnScreenEVValue = (TextView) findViewById(R.id.textViewEvValue);
            OnScreenFlashText = (TextView) findViewById(R.id.textViewFlashtext);
            OnScreenFlashValue = (TextView) findViewById(R.id.textViewFlashValue);
            OnScreenEffectText = (TextView) findViewById(R.id.textViewEffetText);
            OnScreenEffectValue = (TextView) findViewById(R.id.textViewEffectValue);
            OnScreenFocusText = (TextView) findViewById(R.id.textViewFocusText);
            OnScreenFocusValue = (TextView) findViewById(R.id.textViewFocusValue);
            OnScreeISOText = (TextView) findViewById(R.id.textViewISOText);
            OnScreeISOValue = (TextView) findViewById(R.id.textViewISOValue);
            OnScreeMeterText = (TextView) findViewById(R.id.textViewMeterText);
            OnScreeMeterValue = (TextView) findViewById(R.id.textViewMeterValue);
            OnScreenSaturationText = (TextView) findViewById(R.id.textViewSatuText);
            OnScreeSaturationValue = (TextView) findViewById(R.id.textViewSatuValue);
            OnScreeSceneText = (TextView) findViewById(R.id.textViewSceneText);
            OnScreeSceneValue = (TextView) findViewById(R.id.textViewSceneValue);
            OnScreenPictureText = (TextView) findViewById(R.id.textViewPictureText);
            OnScreenPictureValue = (TextView) findViewById(R.id.textViewPictureValue);
            OnScreeSharpnessText = (TextView) findViewById(R.id.textViewSharpText);
            OnScreenSharpnessValue = (TextView) findViewById(R.id.textViewSharpValue);
            OnScreenWBText = (TextView) findViewById(R.id.textViewWBText);
            OnScreenWBValue = (TextView) findViewById(R.id.textViewWBValue);


        }
        catch (NullPointerException ex)
        {


        }
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
                checkBoxZSL.setEnabled(false);
                buttonMetering.setEnabled(false);

            if(!s.equals("LG-P720") || !s.equals("LG-P725"))
                upsidedown.setVisibility(View.GONE);

            if (!DeviceUtils.isOmap())
                ippButton.setEnabled(false);
                exposureButton.setEnabled(false);
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

    public void hideCurrentConfig ()
    {
        OnScreenBrightnessText.setVisibility(View.INVISIBLE);
        OnScreenBrightnessValue.setVisibility(View.INVISIBLE);
        OnScreenContrastText.setVisibility(View.INVISIBLE);
        OnScreenContrastValue.setVisibility(View.INVISIBLE);
        OnScreenEVText.setVisibility(View.INVISIBLE);
        OnScreenEVValue.setVisibility(View.INVISIBLE);
        OnScreenFlashText.setVisibility(View.INVISIBLE);
        OnScreenFlashValue.setVisibility(View.INVISIBLE);
        OnScreenEffectText.setVisibility(View.INVISIBLE);
        OnScreenEffectValue.setVisibility(View.INVISIBLE);
        OnScreenFocusText.setVisibility(View.INVISIBLE);
        OnScreenFocusValue.setVisibility(View.INVISIBLE);
        OnScreeISOText.setVisibility(View.INVISIBLE);
        OnScreeISOValue.setVisibility(View.INVISIBLE);
        OnScreeMeterText.setVisibility(View.INVISIBLE);
        OnScreeMeterValue.setVisibility(View.INVISIBLE);
        OnScreenSaturationText.setVisibility(View.INVISIBLE);
        OnScreeSaturationValue.setVisibility(View.INVISIBLE);
        OnScreeSceneText.setVisibility(View.INVISIBLE);
        OnScreeSceneValue.setVisibility(View.INVISIBLE);
        OnScreenPictureText.setVisibility(View.INVISIBLE);
        OnScreenPictureValue.setVisibility(View.INVISIBLE);
        OnScreeSharpnessText.setVisibility(View.INVISIBLE);
        OnScreenSharpnessValue.setVisibility(View.INVISIBLE);
        OnScreenWBText.setVisibility(View.INVISIBLE);
        OnScreenWBValue.setVisibility(View.INVISIBLE);

    }


    public void showCurrentConfig ()
    {
        OnScreenBrightnessText.setVisibility(View.VISIBLE);
        OnScreenBrightnessValue.setVisibility(View.VISIBLE);
        OnScreenContrastText.setVisibility(View.VISIBLE);
        OnScreenContrastValue.setVisibility(View.VISIBLE);
        OnScreenEVText.setVisibility(View.VISIBLE);
        OnScreenEVValue.setVisibility(View.VISIBLE);
        OnScreenFlashText.setVisibility(View.VISIBLE);
        OnScreenFlashValue.setVisibility(View.VISIBLE);
        OnScreenEffectText.setVisibility(View.VISIBLE);
        OnScreenEffectValue.setVisibility(View.VISIBLE);
        OnScreenFocusText.setVisibility(View.VISIBLE);
        OnScreenFocusValue.setVisibility(View.VISIBLE);
        OnScreeISOText.setVisibility(View.VISIBLE);
        OnScreeISOValue.setVisibility(View.VISIBLE);
        OnScreeMeterText.setVisibility(View.VISIBLE);
        OnScreeMeterValue.setVisibility(View.VISIBLE);
        OnScreenSaturationText.setVisibility(View.VISIBLE);
        OnScreeSaturationValue.setVisibility(View.VISIBLE);
        OnScreeSceneText.setVisibility(View.VISIBLE);
        OnScreeSceneValue.setVisibility(View.VISIBLE);
        OnScreenPictureText.setVisibility(View.VISIBLE);
        OnScreenPictureValue.setVisibility(View.VISIBLE);
        OnScreeSharpnessText.setVisibility(View.VISIBLE);
        OnScreenSharpnessValue.setVisibility(View.VISIBLE);
        OnScreenWBText.setVisibility(View.VISIBLE);
        OnScreenWBValue.setVisibility(View.VISIBLE);

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
                    showtext();

            }
            else
            {
                if (camMan.IsRecording == false)
                {
                    camMan.StartRecording();
                    recordTimer.Start();
                    shotButton.setBackgroundResource(R.drawable.icon_stop_thanos_blast);
                    mainlayout.addView(recordingTimerTextView);
                    OnScreenPictureText.setText("Video Size:");
                    OnScreenPictureValue.setText(camMan.parametersManager.getParameters().get("video-size"));
                }
                else
                {

                    camMan.StopRecording();
                    recordTimer.Stop();
                    shotButton.setBackgroundResource(R.drawable.icon_record_thanos_blast);
                    thumbButton.setImageBitmap(ThumbnailUtils.createVideoThumbnail(camMan.lastPicturePath,MediaStore.Images.Thumbnails.MINI_KIND));
                    mainlayout.removeView(recordingTimerTextView);
                }
            }
		}
	};

    public void SwitchCropButton()
    {
        if(!preferences.getString(ParametersManager.SwitchCamera, ParametersManager.SwitchCamera_MODE_2D).equals(ParametersManager.SwitchCamera_MODE_3D))
        {
            settingsMenuLayout.removeView(crop_box);
        }
        else
        {
            try
            {
            settingsMenuLayout.addView(crop_box);
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

    ///this updates the complete ui. its called everytime the camera parameters are changed
    @Override
    public void parametersHasChanged(boolean restarted)
    {
        try{
           if (camMan.parametersManager.getSupportSharpness())
                sharpnessTextView.setText("Sharpness: " + camMan.parametersManager.getParameters().getInt("sharpness"));
            //if (!parameters.get("exposure").equals("manual"))
            exposureTextView.setText("Exposure: " + camMan.parametersManager.getParameters().getExposureCompensation());
            //else
            //activity.exposureTextView.setText("Exposure: " + parameters.getInt("manual-exposure"));

            contrastTextView.setText("Contrast: " + camMan.parametersManager.getParameters().get("contrast"));
            saturationTextView.setText("Saturation: " + camMan.parametersManager.getParameters().get("saturation"));
            brightnessTextView.setText("Brightness: " + camMan.parametersManager.getParameters().get("brightness"));
            buttonPreviewFormat.setText(camMan.parametersManager.getParameters().get("preview-format"));
            sceneButton.setText(camMan.parametersManager.getParameters().getSceneMode());
            previewSizeButton.setText(camMan.parametersManager.getParameters().getPreviewSize().width + "x" + camMan.parametersManager.getParameters().getPreviewSize().height);
            String size1 = String.valueOf(camMan.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(camMan.parametersManager.getParameters().getPictureSize().height);
            pictureSizeButton.setText(size1);

            if(DeviceUtils.isQualcomm())
                button_zsl.setText(camMan.parametersManager.getParameters().get("zsl"));
            if(DeviceUtils.isOmap())
                button_zsl.setText(camMan.parametersManager.getParameters().get("mode"));


            ippButton.setText(camMan.parametersManager.getParameters().get("ipp"));

            camMan.manualExposureManager.SetMinMax(camMan.parametersManager.getParameters().getMinExposureCompensation(), camMan.parametersManager.getParameters().getMaxExposureCompensation());
            camMan.manualExposureManager.ExternalSet = true;
            camMan.manualExposureManager.SetCurrentValue(camMan.parametersManager.getParameters().getExposureCompensation());
            if (camMan.parametersManager.getSupportSharpness())
            {
                sharpnessSeekBar.setMax(180);
                sharpnessSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("sharpness"));
            }
            if (camMan.parametersManager.getSupportContrast())
            {
                contrastSeekBar.setMax(180);
                camMan.manualContrastManager.ExternalSet = true;
                contrastSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("contrast"));

            }
            if (camMan.parametersManager.getSupportBrightness())
            {
                brightnessSeekBar.setMax(100);
                brightnessSeekBar.setProgress(camMan.parametersManager.getParameters().getInt("brightness"));
            }
            if (camMan.parametersManager.getSupportSaturation())
            {
                saturationSeekBar.setMax(180);
            }
            if (camMan.parametersManager.is3DMode())
                crop_box.setVisibility(View.VISIBLE);
            else
                crop_box.setVisibility(View.GONE);
            crop_box.setChecked(camMan.parametersManager.doCropping());
            if (!camMan.parametersManager.getSupportFlash())
                settingsMenuLayout.removeView(flashButton);
            else
                flashButton.setText(camMan.parametersManager.getParameters().getFlashMode());
            showtext();

            if (!camMan.parametersManager.getSupportAfpPriority())
                buttonAfPriority.setVisibility(View.GONE);
            if (!camMan.parametersManager.getSupportAutoExposure())
            {
                buttonMetering.setVisibility(View.GONE);
            }
        }
        catch (NullPointerException ex)
        {

        }




    }
    public void showtext()
    {

        try
        {
            OnScreenBrightnessValue.setText(camMan.parametersManager.getParameters().get("brightness"));
            OnScreenContrastValue.setText(camMan.parametersManager.getParameters().get("contrast"));
            OnScreenSharpnessValue.setText(camMan.parametersManager.getParameters().get("saturation"));
            OnScreeSaturationValue.setText(camMan.parametersManager.getParameters().get("sharpness"));
            OnScreenEVValue.setText(camMan.parametersManager.getParameters().get("exposure-compensation"));
            OnScreenEffectValue.setText(camMan.parametersManager.getParameters().get("effect"));
            OnScreeISOValue.setText(camMan.parametersManager.getParameters().get("iso"));
            OnScreenFlashValue.setText(camMan.parametersManager.getParameters().get("flash-mode"));
            OnScreenFocusValue.setText(camMan.parametersManager.getParameters().get("focus-mode"));
            String size1 = String.valueOf(camMan.parametersManager.getParameters().getPictureSize().width) + "x" + String.valueOf(camMan.parametersManager.getParameters().getPictureSize().height);
            OnScreenPictureValue.setText(size1);
            OnScreeSceneValue.setText(camMan.parametersManager.getParameters().get("scene-mode"));
            OnScreenWBValue.setText(camMan.parametersManager.getParameters().get("whitebalance"));
            if (DeviceUtils.isOmap())
                OnScreeMeterValue.setText(camMan.parametersManager.getParameters().get("auto-exposure"));
        }
        catch (Exception ex)
        {

        }

    }
}
	




