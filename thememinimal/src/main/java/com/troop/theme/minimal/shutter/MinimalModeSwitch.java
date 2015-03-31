package com.troop.theme.minimal.shutter;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.menu.themes.classic.shutter.ModuleSwitchHandler;
import com.troop.freedcam.utils.ApplicationContextProvider;
import com.troop.theme.minimal.R;


/**
 * Created by George on 3/17/2015.
 */
public class MinimalModeSwitch extends ModuleSwitchHandler {

    TextView ModuleText;

    TextView HDR;
    TextView Picture;
    TextView Movie;
    TextView LongEx;
    HorizontalScrollView Sviewx;
    Animation in;
    Animation out;


    public MinimalModeSwitch(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void init()
    {
        ModuleText = (TextView)activity.findViewById(R.id.minimal_textView_ModuleSwitch);
        ModuleText.setOnClickListener(this);

        Picture = (TextView)activity.findViewById(R.id.horTextItem1);
        Movie = (TextView)activity.findViewById(R.id.horTextItem2);
        HDR = (TextView)activity.findViewById(R.id.horTextItem3);
        LongEx =(TextView)activity.findViewById(R.id.horTextItem4);



        Sviewx = (HorizontalScrollView) activity.findViewById(R.id.horizontalScrollView);

        in = AnimationUtils.loadAnimation(activity.getContext(), R.anim.slidein);
        out = AnimationUtils.loadAnimation(activity.getContext(),R.anim.slideout);



    }

    TextView.OnClickListener PicLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appSettingsManager.SetCurrentModule("module_picture");
            moduleHandler.SetModule("module_picture");
            LongEx.startAnimation(out);
            HDR.startAnimation(out);
            Movie.startAnimation(out);
            Picture.startAnimation(out);

            ModuleText.setText("Pic");
            Sviewx.setVisibility(View.GONE);

        }
    };

    TextView.OnClickListener HDRLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appSettingsManager.SetCurrentModule("module_hdr");
            moduleHandler.SetModule("module_hdr");
            LongEx.startAnimation(out);
            HDR.startAnimation(out);
            Movie.startAnimation(out);
            Picture.startAnimation(out);

            ModuleText.setText("HDR");
            Sviewx.setVisibility(View.GONE);

        }
    };

    TextView.OnClickListener MovieLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appSettingsManager.SetCurrentModule("module_video");
            moduleHandler.SetModule("module_video");
            LongEx.startAnimation(out);
            HDR.startAnimation(out);
            Movie.startAnimation(out);
            Picture.startAnimation(out);

            ModuleText.setText("Mov");
            Sviewx.setVisibility(View.GONE);

        }
    };

    TextView.OnClickListener LowExLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            appSettingsManager.SetCurrentModule("module_longexposure");
            moduleHandler.SetModule("module_longexposure");
            LongEx.startAnimation(out);
            HDR.startAnimation(out);
            Movie.startAnimation(out);
            Picture.startAnimation(out);

            ModuleText.setText("LoEx");
            Sviewx.setVisibility(View.GONE);

        }
    };



    @Override
    public void onClick(View v) {
        Picture.setOnClickListener(PicLabel);

        Movie.setOnClickListener(MovieLabel);
        HDR.setOnClickListener(HDRLabel);
        LongEx.setOnClickListener(LowExLabel);

        if(Sviewx.getVisibility() == View.VISIBLE && HDR.getVisibility() == View.VISIBLE)
        {
            Picture.startAnimation(out);
            Movie.startAnimation(out);
            HDR.startAnimation(out);
            LongEx.startAnimation(out);

            Sviewx.setVisibility(View.GONE);
            return;
        }

        Sviewx.setVisibility(View.VISIBLE);



        Picture.setVisibility(View.VISIBLE);
        Picture.startAnimation(in);
        Picture.setText("Picture");
        Movie.setText("Movie");
        Movie.startAnimation(in);
        Movie.setVisibility(View.VISIBLE);
        HDR.setText("HDR");
        HDR.startAnimation(in);
        HDR.setVisibility(View.VISIBLE);
        LongEx.setText("Long Exposure");
        LongEx.startAnimation(in);
        LongEx.setVisibility(View.VISIBLE);

        TextView it1 = (TextView) activity.findViewById(R.id.horTextItem5);
        it1.setVisibility(View.GONE);

        TextView it2 = (TextView) activity.findViewById(R.id.horTextItem6);
        it2.setVisibility(View.GONE);

        TextView it3 = (TextView) activity.findViewById(R.id.horTextItem7);
        it3.setVisibility(View.GONE);

        TextView it4 = (TextView) activity.findViewById(R.id.horTextItem8);
        it4.setVisibility(View.GONE);

        /////////////////////////////////////////////////////////
        TextView it5 = (TextView) activity.findViewById(R.id.nubia1);
        it5.setVisibility(View.GONE);

        TextView it6 = (TextView) activity.findViewById(R.id.nubia2);
        it6.setVisibility(View.GONE);

        TextView it7 = (TextView) activity.findViewById(R.id.nubia3);
        it7.setVisibility(View.GONE);
        /////////////////////////////////////////////////////////
    }

    @Override
    public void ParametersLoaded()
    {
        moduleHandler.SetModule(appSettingsManager.GetCurrentModule());
        ModuleText.setText(moduleHandler.GetCurrentModule().ShortName());
        ModuleText.setVisibility(View.VISIBLE);

    }
}
