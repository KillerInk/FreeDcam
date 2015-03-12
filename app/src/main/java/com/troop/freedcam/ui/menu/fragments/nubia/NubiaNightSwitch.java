package com.troop.freedcam.ui.menu.fragments.nubia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;

/**
 * Created by troop on 12.03.2015.
 */
public class NubiaNightSwitch extends NightModeSwitchHandler
{
    ImageView textView;
    ScrollView HouseNight;
    ImageView On;
    ImageView Off;
    ImageView Tripod;

    public NubiaNightSwitch(View activity, AppSettingsManager appSettingsManager)
    {
        super(activity, appSettingsManager);
    }

    @Override
    protected void init()
    {
        textView = (ImageView)activity.findViewById(R.id.imageViewNight);

        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);

        HouseNight = (ScrollView)activity.findViewById(R.id.scrollViewNight);

        Off = (ImageView)activity.findViewById(R.id.btnNight_off);
        Off.setOnClickListener(OffView);


        On = (ImageView)activity.findViewById(R.id.btnNight_on);
        On.setOnClickListener(OnView);


        Tripod = (ImageView)activity.findViewById(R.id.btnNight_torch);
        Tripod.setOnClickListener(TripodView);
    }

    ImageView.OnClickListener OnView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //  LongEx.startAnimation(out);
            // HDR.startAnimation(out);
            // Movie.startAnimation(out);
            // Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.NightMode.SetValue("on", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, "on");


            //moduleView.setBackground(activity.findViewById(R.drawable.nubia_ui_mode_pic));
            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_night_on);
            textView.setImageBitmap(tmp);


            HouseNight.setVisibility(View.GONE);

        }
    };

    ImageView.OnClickListener OffView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // LongEx.startAnimation(out);
            //  HDR.startAnimation(out);
            //  Movie.startAnimation(out);
            //  Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.NightMode.SetValue("off", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, "off");


            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_night_off);
            textView.setImageBitmap(tmp);


            HouseNight.setVisibility(View.GONE);

        }
    };

    ImageView.OnClickListener TripodView = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // LongEx.startAnimation(out);
            // HDR.startAnimation(out);
            // Movie.startAnimation(out);
            // Picture.startAnimation(out);
            cameraUiWrapper.camParametersHandler.NightMode.SetValue("tripod", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, "tripod");


            Bitmap tmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.material_ui_night_tripod);
            textView.setImageBitmap(tmp);


            HouseNight.setVisibility(View.GONE);

        }
    };

    @Override
    public void onClick(View v)
    {
        if(HouseNight.getVisibility() == View.GONE)
        {
            ScrollView Flash = (ScrollView)activity.findViewById(R.id.scrollViewFlash);
            ScrollView Module = (ScrollView)activity.findViewById(R.id.scrollViewModule);
            Flash.setVisibility(View.GONE);
            Module.setVisibility(View.GONE);

            HouseNight.setVisibility(View.VISIBLE);
        }
        else
        {
            HouseNight.setVisibility(View.GONE);
        }
    }
}
