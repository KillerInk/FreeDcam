package com.troop.freedcam.ui.menu.fragments.minimal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.R;
import com.troop.freedcam.camera.modules.ModuleHandler;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.switches.ModuleSwitchHandler;
import com.troop.freedcam.utils.ApplicationContextProvider;

/**
 * Created by George on 3/17/2015.
 */
public class MinimalModeSwitch extends ModuleSwitchHandler {

    TextView HDR;
    TextView Picture;
    TextView Movie;
    TextView LongEx;
    HorizontalScrollView Sviewx;
    Animation in;
    Animation out;


    TextView moduleView;

    LinearLayout ModeHouse;
    public MinimalModeSwitch(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void init()
    {
        moduleView = (TextView)activity.findViewById(R.id.textView_ModuleSwitch);
        moduleView.setOnClickListener(this);

        ModeHouse = (LinearLayout)activity.findViewById(R.id.scrollViewModule);
        ModeHouse.setVisibility(View.GONE);

        Picture = (TextView)activity.findViewById(R.id.horTextItem1);
        Picture.setOnClickListener(PicLabel);


        Movie = (TextView)activity.findViewById(R.id.horTextItem2);
        Movie.setOnClickListener(MovieLabel);


        HDR = (TextView)activity.findViewById(R.id.horTextItem3);
        HDR.setOnClickListener(HDRLabel);


        LongEx =(TextView)activity.findViewById(R.id.horTextItem4);
        LongEx.setOnClickListener(LowExLabel);

        Sviewx = (HorizontalScrollView) activity.findViewById(R.id.horizontalScrollView);

        in = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(), R.anim.slidein);
        out = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(),R.anim.slideout);
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

            moduleView.setText("Pic");
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

            moduleView.setText("HDR");
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

            moduleView.setText("Mov");
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

            moduleView.setText("LoEx");
            Sviewx.setVisibility(View.GONE);

        }
    };



    @Override
    public void onClick(View v) {

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
        moduleView.setText(moduleHandler.GetCurrentModule().ShortName());
        moduleView.setVisibility(View.VISIBLE);

    }
}
