package com.troop.freedcam.ui.menu.fragments.minimal;

import android.support.v4.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.switches.NightModeSwitchHandler;
import com.troop.freedcam.utils.ApplicationContextProvider;

/**
 * Created by George on 3/17/2015.
 */
public class MinimalNightSwitch extends NightModeSwitchHandler {
    TextView textView;
    LinearLayout HouseNight;
    TextView Off;
    TextView On;
    TextView Tripod;
    HorizontalScrollView Sviewq;
    Animation in;
    Animation out;

    public MinimalNightSwitch(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        super(activity, appSettingsManager,fragment);
    }

    @Override
    protected void init()
    {
        textView = (TextView)activity.findViewById(R.id.minimal_textView_nightmode);
        textView.setOnClickListener(this);
        textView.setVisibility(View.GONE);



        Tripod = (TextView)activity.findViewById(R.id.nubia3);





        On = (TextView)activity.findViewById(R.id.nubia2);




        Off = (TextView)activity.findViewById(R.id.nubia1);


        Sviewq = (HorizontalScrollView) activity.findViewById(R.id.horizontalScrollView);
        in = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(), R.anim.slidein);
        out = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(), R.anim.slideout);

    }

    private TextView.OnClickListener TripodLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.NightMode.SetValue("tripod", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, "tripod");
            textView.setText("tripod");

            On.startAnimation(out);
            Off.startAnimation(out);
            Tripod.startAnimation(out);
            Sviewq.setVisibility(View.GONE);

        }
    };

    private TextView.OnClickListener OnLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.NightMode.SetValue("on", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, "on");
            textView.setText("on");

            On.startAnimation(out);
            Off.startAnimation(out);
            Tripod.startAnimation(out);
            Sviewq.setVisibility(View.GONE);

        }
    };

    private TextView.OnClickListener OffLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.NightMode.SetValue("off", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, "off");
            textView.setText("off");

            On.startAnimation(out);
            Off.startAnimation(out);
            Tripod.startAnimation(out);
            Sviewq.setVisibility(View.GONE);

        }
    };

    @Override
    public void onClick(View v)
    {
        Tripod.setOnClickListener(TripodLabel);
        On.setOnClickListener(OnLabel);
        Off.setOnClickListener(OffLabel);


        if(Sviewq.getVisibility() == View.VISIBLE && Tripod.getVisibility() == View.VISIBLE)
        {

            On.startAnimation(out);
            Off.startAnimation(out);
            Tripod.startAnimation(out);
            Sviewq.setVisibility(View.GONE);
            return;
        }

        Sviewq.setVisibility(View.VISIBLE);

        On.setText("on");
        On.setVisibility(View.VISIBLE);

        Tripod.setText("tripod");
        Tripod.setVisibility(View.VISIBLE);

        Off.setText("off");
        Off.setVisibility(View.VISIBLE);
        for(int i = 0; i<Sviewq.getChildCount();i++)
        {
            Sviewq.getChildAt(i).startAnimation(in);
        }
        hider();
    }
    private void hider()
    {
        TextView it1 = (TextView)activity.findViewById(R.id.horTextItem1);
        it1.setVisibility(View.GONE);
        TextView it2 = (TextView)activity.findViewById(R.id.horTextItem2);
        it2.setVisibility(View.GONE);
        TextView it3 = (TextView)activity.findViewById(R.id.horTextItem3);
        it3.setVisibility(View.GONE);
        TextView it4 = (TextView)activity.findViewById(R.id.horTextItem4);
        it4.setVisibility(View.GONE);
        TextView it1x = (TextView) activity.findViewById(R.id.horTextItem5);
        it1x.setVisibility(View.GONE);

        TextView it2x = (TextView) activity.findViewById(R.id.horTextItem6);
        it2x.setVisibility(View.GONE);

        TextView it3x = (TextView) activity.findViewById(R.id.horTextItem7);
        it3x.setVisibility(View.GONE);

        TextView it4x = (TextView) activity.findViewById(R.id.horTextItem8);
        it4x.setVisibility(View.GONE);
    }

    @Override
    public void ParametersLoaded()
    {
        activity.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.camParametersHandler.NightMode != null && cameraUiWrapper.camParametersHandler.NightMode.IsSupported()) {
                    textView.setVisibility(View.VISIBLE);
                    String appSet = appSettingsManager.getString(AppSettingsManager.SETTING_NIGHTEMODE);
                    String para = cameraUiWrapper.camParametersHandler.NightMode.GetValue();
                   // if (para == null || para.equals(""))
                    //    para = "off";
                    if (appSet.equals("")) {
                        appSet = cameraUiWrapper.camParametersHandler.NightMode.GetValue();
                        appSettingsManager.setString(AppSettingsManager.SETTING_NIGHTEMODE, para);
                    }
//                    if (!appSet.equals(para))
  //                      cameraUiWrapper.camParametersHandler.NightMode.SetValue(appSet, true);
                    textView.setText(appSet);
                } else {
                    textView.setVisibility(View.GONE);
                }
            }
        });

    }
}