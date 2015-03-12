package com.troop.freedcam.ui.menu.fragments.nubia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.switches.FlashSwitchHandler;

/**
 * Created by troop on 12.03.2015.
 */
public class NubiaFlashSwitch extends FlashSwitchHandler
{
    ScrollView HouseFlash;

    ImageView Off;
    ImageView On;
    ImageView Auto;
    ImageView Torch;
    ImageView textView;

    public NubiaFlashSwitch(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void init()
    {
        textView = (ImageView)activity.findViewById(R.id.imageViewFlash);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);
        HouseFlash = (ScrollView)activity.findViewById(R.id.scrollViewFlash);

        Off = (ImageView)activity.findViewById(R.id.btnFlash_off);
        Off.setOnClickListener(OffView);


        On = (ImageView)activity.findViewById(R.id.btnFlash_on);
        On.setOnClickListener(OnView);


        Auto = (ImageView)activity.findViewById(R.id.btnFlash_auto);
        Auto.setOnClickListener(AutoView);


        Torch =(ImageView)activity.findViewById(R.id.btnFlash_torch);
        Torch.setOnClickListener(TorchView);
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
            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_flash_on);
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

            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_flash_off);
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

            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_flash_torch);
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

            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_flash_auto);
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
                On.setVisibility(View.VISIBLE);

                Auto.setVisibility(View.VISIBLE);
                ScrollView Module = (ScrollView)activity.findViewById(R.id.scrollViewModule);
                ScrollView Night = (ScrollView)activity.findViewById(R.id.scrollViewNight);
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
                On.setVisibility(View.GONE);
                //Off.setVisibility(View.GONE);
                Auto.setVisibility(View.GONE);
                //Torch.setVisibility(View.GONE);
                ScrollView Module = (ScrollView)activity.findViewById(R.id.scrollViewModule);
                ScrollView Night = (ScrollView)activity.findViewById(R.id.scrollViewNight);
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
}
