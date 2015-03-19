package com.troop.freedcam.ui.menu.fragments.ambient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;

/**
 * Created by George on 3/17/2015.
 */
public class AmbientFlashSwitch extends FlashSwitchHandler {
    LinearLayout HouseFlash;

    ImageView Off;
    ImageView On;
    ImageView Auto;
    ImageView Torch;
    ImageView textView;

    public AmbientFlashSwitch(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void init()
    {
        textView = (ImageView)activity.findViewById(R.id.imageViewFlash);
        textView.setOnClickListener(this);

        HouseFlash = (LinearLayout)activity.findViewById(R.id.scrollViewFlash);



        Off = (ImageView)activity.findViewById(R.id.btnFlash_off);



        On = (ImageView)activity.findViewById(R.id.btnFlash_on);



        Auto = (ImageView)activity.findViewById(R.id.btnFlash_auto);



        Torch =(ImageView)activity.findViewById(R.id.btnFlash_torch);

        HouseFlash.setVisibility(View.GONE);
    }

    ImageView.OnClickListener OnView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //  LongEx.startAnimation(out);
            // HDR.startAnimation(out);
            // Movie.startAnimation(out);
            // Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("on", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "on");

            //moduleView.setBackground(activity.findViewById(R.drawable.nubia_ui_mode_pic));
            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_flash_on);
            textView.setImageBitmap(tmp);


            HouseFlash.setVisibility(View.GONE);

        }
    };

    ImageView.OnClickListener OffView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // LongEx.startAnimation(out);
            //  HDR.startAnimation(out);
            //  Movie.startAnimation(out);
            //  Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("off", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "off");

            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_flash_off);
            textView.setImageBitmap(tmp);


            HouseFlash.setVisibility(View.GONE);

        }
    };

    ImageView.OnClickListener TorchView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // LongEx.startAnimation(out);
            // HDR.startAnimation(out);
            // Movie.startAnimation(out);
            // Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("torch", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "torch");

            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_flash_torch);
            textView.setImageBitmap(tmp);


            HouseFlash.setVisibility(View.GONE);

        }
    };

    ImageView.OnClickListener AutoView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // LongEx.startAnimation(out);
            // HDR.startAnimation(out);
            //.startAnimation(out);
            // Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("auto", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "auto");

            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.ic_flash_auto);
            textView.setImageBitmap(tmp);

            HouseFlash.setVisibility(View.GONE);

        }
    };

    @Override
    public void onClick(View v)
    {
        if (!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO))
        {
            if (HouseFlash.getVisibility() == View.GONE)
            {
                On.setOnClickListener(OnView);
                Off.setOnClickListener(OffView);
                Auto.setOnClickListener(AutoView);
                Torch.setOnClickListener(TorchView);

                LinearLayout Module = (LinearLayout)activity.findViewById(R.id.scrollViewModule);
                LinearLayout Night = (LinearLayout)activity.findViewById(R.id.scrollViewNight);
                Module.setVisibility(View.GONE);
                Night.setVisibility(View.GONE);

                HouseFlash.setVisibility(View.VISIBLE);
            }
            else
            {
                HouseFlash.setVisibility(View.GONE);
            }
        }
        else
        {
            if (HouseFlash.getVisibility() == View.GONE)
            {
                LinearLayout Module = (LinearLayout)activity.findViewById(R.id.scrollViewModule);
                LinearLayout Night = (LinearLayout)activity.findViewById(R.id.scrollViewNight);
                Module.setVisibility(View.GONE);
                Night.setVisibility(View.GONE);
                HouseFlash.setVisibility(View.VISIBLE);
            }
            else
            {
                HouseFlash.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void ParametersLoaded()
    {
        if (cameraUiWrapper.camParametersHandler.FlashMode != null)
        {
            flashmode = cameraUiWrapper.camParametersHandler.FlashMode;
            flashmode.addEventListner(this);
            //flashmode.BackgroundIsSupportedChanged(true);
        }
        Log.d(TAG, "ParametersLoaded");
        activity.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.camParametersHandler.FlashMode != null && cameraUiWrapper.camParametersHandler.FlashMode.IsSupported()) {
                    textView.setVisibility(View.VISIBLE);
                    String appSet = appSettingsManager.getString(AppSettingsManager.SETTING_FLASHMODE);
                    String para = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
                    if (appSet.equals("")) {
                        appSet = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
                        appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, para);
                    }
                    if (!appSet.equals(para))
                        cameraUiWrapper.camParametersHandler.FlashMode.SetValue(appSet, true);


                    //textView.setText(appSet);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        });

    }
}
