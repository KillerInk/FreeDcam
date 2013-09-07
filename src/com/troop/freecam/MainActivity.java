package com.troop.freecam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.troop.menu.ColorMenu;
import com.troop.menu.ExposureMenu;
import com.troop.menu.FlashMenu;
import com.troop.menu.FocusMenu;
import com.troop.menu.IsoMenu;
import com.troop.menu.PictureSizeMenu;
import com.troop.menu.PreviewSizeMenu;
import com.troop.menu.SceneMenu;
import com.troop.menu.WhiteBalanceMenu;
import com.troop.menu.switchcameramenu;

import java.util.List;

public class MainActivity extends Activity {

    //focus-mode-values=off,auto,infinity,infinity,portrait,extended
    //flash-mode-values=on,off,auto,torch
    //exposure-mode-values=manual,auto,night,backlighting,spotlight,sports,snow,beach,aperture,small-aperture
    //auto-convergence-mode-values=disable,frame,center,touch,manual
    //preview-format-values=yuv420sp,yuv420p,yuv420p
    //scene-mode-values=auto,closeup,landscape,aqua,sports,mood,night-portrait,night-indoor,fireworks,document,barcode,super-night,cine,old-film,action,beach,candlelight,night,party,portrait,snow,steadyphoto,sunset,theatre
    //supported-picture-sidebyside-size-values=4096x1536,3200x1200,2560x960,2048x768,1280x480,640x240
    //whitebalance-values=auto,daylight,cloudy-daylight,tungsten,fluorescent,incandescent,horizon,sunset,shade,twilight,warm-fluorescent
    //iso-mode-values=auto,100,200,400,800
    //exposure-mode-values=manual,auto,night,backlighting,spotlight,sports,snow,beach,aperture,small-aperture
    //focal-length=4.76
    //sharpness=100;
    //contrast=100
    //jpeg-quality=95;
    //brightness=50

    //s3d-prv-frame-layout-values=none;
    // zoom=0;
    // max-num-detected-faces-hw=35;
    // sensor-orientation=0;
    // whitebalance=auto;
    // preview-format-values=yuv420sp,yuv420p,yuv422i-yuyv,yuv420p;
    // auto-convergence-mode-values=;
    // jpeg-thumbnail-quality=60;
    // preview-format=yuv420sp;
    // exposure-mode-values=manual,auto,night,backlighting,spotlight,sports,snow,beach,aperture,small-aperture;
    // exif-make=LG Electronics;
    // iso=auto;
    // flash-mode-values=on,off,auto,torch;
    // sensor-orientation-values=;
    // supported-manual-convergence-min=-100;
    // supported-preview-sidebyside-size-values=;
    // preview-frame-rate=30;camera-name=IMX072;
    // jpeg-thumbnail-width=160;
    // scene-mode-values=auto,closeup,landscape,aqua,sports,mood,night-portrait,night-indoor,fireworks,document,barcode,super-night,cine,old-film,action,beach,candlelight,night,party,portrait,snow,steadyphoto,sunset,theatre;
    // exif-model=LG-P920;
    // preview-fps-range-values=(15000,30000);
    // gbce=true;
    // preview-size-values=1920x1080,1280x720,960x720,800x480,720x576,720x480,640x480,320x240,352x288,240x160,176x144;
    // manual-exposure-right=1;
    // vnf-supported=true;
    // supported-picture-sidebyside-size-values=;
    // preview-fps-range=15000,30000;
    // auto-whitebalance-lock=false;
    // min-exposure-compensation=-30;
    // antibanding=auto;
    // supported-manual-gain-iso-max=800;
    // max-num-focus-areas=20;
    // supported-manual-gain-iso-min=100;
    // vertical-view-angle=42.5;
    // video-stabilization-supported=true;
    // iso-mode-values=auto,100,200,400,800;
    // manual-gain-iso=100;
    // s3d-cap-frame-layout=none;
    // supported-manual-gain-iso-step=100;
    // glbce=false;
    // supported-manual-exposure-step=1;
    // picture-format-values=jpeg;
    // supported-preview-topbottom-size-values=;
    // glbce-supported=true;
    // exposure-compensation-step=0.1;manual-convergence=0;
    // picture-size=2592x1944;
    // saturation=100;
    // whitebalance-values=auto,daylight,cloudy-daylight,tungsten,fluorescent,incandescent,horizon,sunset,shade,twilight,warm-fluorescent;
    // picture-format=jpeg;
    // supported-picture-subsampled-size-values=;
    // current-iso=100;
    // ipp=ldc-nsf;
    // raw-height=1960;
    // recording-hint=;
    // video-stabilization=false;
    // ipp-values=off,ldc,nsf,ldc-nsf;
    // zoom-supported=true;
    // sharpness=100;
    // contrast=100;
    // scene-mode=auto;
    // jpeg-quality=100;
    // supported-manual-exposure-min=1;
    // manual-gain-iso-right=100;
    // preview-size=640x480;
    // focal-length=4.76;
    // mode-values=high-quality,video-mode,high-performance,high-quality-zsl,exposure-bracketing,temporal-bracketing;
    // vnf=false;
    // preview-frame-rate-values=30,15;max-num-metering-areas=20;
    // s3d-prv-frame-layout=none;
    // manual-exposure=1;
    // focus-mode-values=off,continuous-video,continuous-picture,auto,macro,infinity,infinity,portrait,extended,face-priority;
    // jpeg-thumbnail-size-values=640x480,160x120,200x120,320x240,512x384,352x144,176x144,96x96,0x0;
    // supported-manual-exposure-max=125;
    // zoom-ratios=100,104,107,111,115,119,123,127,132,137,141,146,152,157,162,168,174,180,187,193,200,207,214,222,230,238,246,255,264,273,283,293,303,314,325,336,348,361,373,386,400;
    // gbce-supported=true;
    // exposure=auto;
    // picture-size-values=2592x1944,2592x1728,2592x1458,2240x1344,2048x1536,1920x1080,1600x1200,1280x1024,1280x960,1280x768,1280x720,1024x768,640x480,320x240;
    // s3d-cap-frame-layout-values=none;auto-convergence-mode=frame;
    // supported-manual-convergence-max=100;
    // horizontal-view-angle=54.8;
    // supported-manual-convergence-step=1;
    // brightness=50;
    // jpeg-thumbnail-height=120;
    // smooth-zoom-supported=true;
    // raw-width=2608;
    // focus-mode=off;
    // supported-preview-subsampled-size-values=;
    // mechanical-misalignment-correction-values=;
    // auto-whitebalance-lock-supported=true;
    // video-frame-format=OMX_TI_COLOR_FormatYUV420PackedSemiPlanar;
    // max-num-detected-faces-sw=0;
    // supported-picture-topbottom-size-values=;max-exposure-compensation=30;
    // video-snapshot-supported=true;exposure-compensation=0;
    // flash-mode=off;
    // auto-exposure-lock=false;
    // effect-values=none,negative,solarize,sepia,mono,natural,vivid,color-swap,blackwhite,whiteboard,blackboard,aqua,posterize;
    // max-zoom=40;
    // effect=none;
    // focus-distances=Infinity,Infinity,Infinity;
    // auto-exposure-lock-supported=true;
    // antibanding-values=off,auto,50hz,60hz


	public CamPreview mPreview;
	private ImageButton shotButton;
	String imagepath;
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
	Camera.Parameters paras;
    SurfaceHolder holder;
    CameraManager camMan;
    public TextView flashText;
    public TextView focusText;
    public TextView sceneText;
    public TextView whitebalanceText;
    public TextView colorText;
    public  ViewGroup appViewGroup;
    public SeekBar exposureSeekbar;
    public ImageButton thumbButton;
    CheckBox manualExposure;
    TableRow exposureRow;
    CheckBox manualShaprness;
    TableRow sharpnessRow;
    public SeekBar sharpnessSeekBar;
    TableLayout tableLayout;
    public TextView sharpnessTextView;
    public TextView exposureTextView;

    public  TextView contrastTextView;
    TableRow contrastRow;
    CheckBox contrastRadioButton;
    public SeekBar contrastSeekBar;

    public TextView brightnessTextView;
    public SeekBar brightnessSeekBar;
    TableRow brightnessRow;
    CheckBox brightnessCheckBox;


    int currentZoom = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appViewGroup = (ViewGroup) inflater.inflate(R.layout.activity_main, null);




        setContentView(R.layout.activity_main);

		mPreview = (CamPreview) findViewById(R.id.camPreview1);
        mPreview.setKeepScreenOn(true);
        holder = mPreview.getHolder();
        camMan = new CameraManager(mPreview, this);

        mPreview.SetCameraManager(camMan);

        initButtons();

	}

    private void initButtons()
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



        //exposureSeekbar.setVisibility(View.INVISIBLE);
        switch3dButton = (Button) findViewById(R.id.button_switch3d);
        switch3dButton.setOnClickListener(new switchcameramenu(camMan, this));
        thumbButton = (ImageButton)findViewById(R.id.imageButton_thumb);
        thumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://" + camMan.lastPicturePath);
                Intent i=new Intent(Intent.ACTION_VIEW, uri);
                i.setType("image/*");
                startActivity(i);
            }
        });

        tableLayout = (TableLayout) findViewById(R.id.tableVIEW);

        exposureTextView = (TextView) findViewById(R.id.textViewexposure);
        exposureSeekbar  = (SeekBar) findViewById(R.id.seekBar_exposure);
        exposureSeekbar.setOnSeekBarChangeListener(camMan.manualExposureManager);
        exposureRow = (TableRow) findViewById(R.id.tableRowExposure);
        tableLayout.removeView(exposureRow);
        manualExposure = (CheckBox)findViewById(R.id.checkBox_exposureManual);
        manualExposure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (manualExposure.isChecked())
                {
                    tableLayout.addView(exposureRow);
                }
                else
                {
                    tableLayout.removeView(exposureRow);
                }
            }
        });


        sharpnessTextView = (TextView)findViewById(R.id.textView_sharpness);
        sharpnessSeekBar = (SeekBar)findViewById(R.id.seekBar_sharpness);
        sharpnessSeekBar.setOnSeekBarChangeListener(camMan.manualSharpnessManager);
        sharpnessRow = (TableRow) findViewById(R.id.tableRowSharpness);
        tableLayout.removeView(sharpnessRow);
        manualShaprness = (CheckBox) findViewById(R.id.checkBox_sharpness);
        manualShaprness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualShaprness.isChecked())
                {
                    tableLayout.addView(sharpnessRow);
                    //manualShaprness.setVisibility(View.VISIBLE);

                    //sharpnessRow.bringToFront();
                }
                else
                {
                    tableLayout.removeView(sharpnessRow);
                }
                //sharpnessRow.invalidate();
            }
        });

        contrastRow = (TableRow)findViewById(R.id.tableRowContrast);

        contrastSeekBar = (SeekBar) findViewById(R.id.seekBar_contrast);
        contrastSeekBar.setOnSeekBarChangeListener(camMan.manualContrastManager);
        contrastTextView = (TextView) findViewById(R.id.textView_contrast);
        contrastRadioButton = (CheckBox)findViewById(R.id.radioButton_contrast);
        contrastRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contrastRadioButton.isChecked())
                    tableLayout.addView(contrastRow);
                else
                    tableLayout.removeView(contrastRow);
            }
        });
        tableLayout.removeView(contrastRow);


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
            else if(key == KeyEvent.KEYCODE_3D_MODE)
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
	
	View.OnClickListener shotListner = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			camMan.TakePicture();
		}
	};



}
	




