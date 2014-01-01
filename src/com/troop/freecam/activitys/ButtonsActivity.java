package com.troop.freecam.activitys;

import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.troop.freecam.MainActivity;
import com.troop.freecam.R;
import com.troop.freecam.controls.ExtendedButton;
import com.troop.freecam.manager.ManualSaturationManager;
import com.troop.freecam.manager.MyTimer;
import com.troop.freecam.manager.ParametersManager;
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
import com.troop.menu.VideoSizesMenu;
import com.troop.menu.WhiteBalanceMenu;
import com.troop.menu.ZslMenu;
import com.troop.menu.switchcameramenu;

/**
 * Created by troop on 30.12.13.
 */
public class ButtonsActivity extends BaseActivity
{
    public Button sceneButton;
    public Button whitebalanceButton;
    public Button colorButton;
    public Button isoButton;
    public Button exposureButton;
    public ExtendedButton buttonAfPriority;
    public Button buttonMetering;
    public ExtendedButton buttonPreviewFormat;
    public ImageButton shotButton;
    public ImageButton thumbButton;
    Button switchVideoPicture;
    CheckBox checkboxHDR;
    boolean HDRMode = false;
    TableLayout tableLayout;
    RelativeLayout mainlayout;
    public CheckBox manualExposure;
    TableRow exposureRow;
    public SeekBar exposureSeekbar;
    public CheckBox manualShaprness;
    public CheckBox manualFocus;
    TableRow sharpnessRow;
    public SeekBar sharpnessSeekBar;
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
    protected TextView recordingTimerTextView;
    protected MyTimer recordTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initButtons();
    }

    private void initButtons()
    {
        shotButton = (ImageButton) findViewById(R.id.imageButton1);
        shotButton.setOnClickListener(shotListner);

        sceneButton = (Button) findViewById(R.id.buttonScene);
        sceneButton.setOnClickListener(new SceneMenu(camMan, (MainActivity)this));

        whitebalanceButton = (Button) findViewById(R.id.buttonwhiteBalance);
        whitebalanceButton.setOnClickListener(new WhiteBalanceMenu(camMan, (MainActivity)this));

        colorButton = (Button) findViewById(R.id.buttoncolor);
        colorButton.setOnClickListener(new ColorMenu(camMan, (MainActivity)this));

        isoButton = (Button) findViewById(R.id.buttoniso);
        isoButton.setOnClickListener(new IsoMenu(camMan, (MainActivity)this));

        exposureButton = (Button) findViewById(R.id.button_exposure);
        exposureButton.setOnClickListener(new ExposureMenu(camMan, (MainActivity)this));

        //06-12-13*************************************************************
        buttonAfPriority = (ExtendedButton)findViewById(R.id.buttonAFPriority);
        buttonAfPriority.setOnClickListener(new AFPriorityMenu(camMan,(MainActivity)this));

        buttonMetering = (Button)findViewById(R.id.buttonMetering);
        buttonMetering.setOnClickListener(new MeteringMenu(camMan,(MainActivity)this));

        buttonPreviewFormat = (ExtendedButton)findViewById(R.id.buttonPreviewFormat);
        buttonPreviewFormat.setOnClickListener(new PreviewFormatMenu(camMan,(MainActivity)this));
        //**********************************************************************

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
                //focusButton.setEnabled(true);
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

        checkboxHDR = (CheckBox)findViewById(R.id.checkBox_hdr);
        checkboxHDR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HDRMode = checkboxHDR.isChecked();
            }
        });



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
}
