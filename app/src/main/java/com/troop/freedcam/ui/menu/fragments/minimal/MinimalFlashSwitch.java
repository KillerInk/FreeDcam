package com.troop.freedcam.ui.menu.fragments.minimal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.troop.freedcam.ui.switches.FlashSwitchHandler;
import com.troop.freedcam.utils.ApplicationContextProvider;

/**
 * Created by George on 3/17/2015.
 */
public class MinimalFlashSwitch extends FlashSwitchHandler {
    LinearLayout HouseFlash;

    TextView Auto;
    TextView On;
    TextView Off;
    TextView Torch;

    TextView textView;
    HorizontalScrollView Sview;
    Animation in;
    Animation out;

    public MinimalFlashSwitch(View activity, AppSettingsManager appSettingsManager, Fragment fragment)
    {
        super(activity, appSettingsManager, fragment);
    }

    @Override
    protected void init()
    {
        textView = (TextView)activity.findViewById(R.id.textView_ModuleSwitch);
        textView.setOnClickListener(this);

        Off = (TextView)activity.findViewById(R.id.horTextItem7);
        On = (TextView)activity.findViewById(R.id.horTextItem6);
        Auto = (TextView)activity.findViewById(R.id.horTextItem5);
        Torch =(TextView)activity.findViewById(R.id.horTextItem8);
        HouseFlash.setVisibility(View.GONE);

        Sview = (HorizontalScrollView) activity.findViewById(R.id.horizontalScrollView);
        in = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(), R.anim.slidein);
        out = AnimationUtils.loadAnimation(ApplicationContextProvider.getContext(),R.anim.slideout);
    }

    TextView.OnClickListener AutoLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("auto", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "auto");
            textView.setText("auto");

            Auto.startAnimation(out);
            On.startAnimation(out);
            Off.startAnimation(out);
            Torch.startAnimation(out);
            Sview.setVisibility(View.GONE);

        }
    };

    TextView.OnClickListener OnLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("on", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "on");
            textView.setText("on");

            Auto.startAnimation(out);
            On.startAnimation(out);
            Off.startAnimation(out);
            Torch.startAnimation(out);
            Sview.setVisibility(View.GONE);

        }
    };

    TextView.OnClickListener OffLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("off", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "off");
            textView.setText("off");

            Auto.startAnimation(out);
            On.startAnimation(out);
            Off.startAnimation(out);
            Torch.startAnimation(out);
            Sview.setVisibility(View.GONE);

        }
    };

    TextView.OnClickListener TorchLabel = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            cameraUiWrapper.camParametersHandler.FlashMode.SetValue("torch", true);
            appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "torch");
            textView.setText("torch");
            Auto.startAnimation(out);
            On.startAnimation(out);
            Off.startAnimation(out);
            Torch.startAnimation(out);
            Sview.setVisibility(View.GONE);

        }
    };


    @Override
    public void onClick(View v)
    {
        if(Sview.getVisibility() == View.VISIBLE && Torch.getVisibility() == View.VISIBLE)
        {
            Auto.startAnimation(out);
            On.startAnimation(out);
            Off.startAnimation(out);
            Torch.startAnimation(out);

            Sview.setVisibility(View.GONE);
            return;
        }
        if(!cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(ModuleHandler.MODULE_VIDEO)){
            Sview.setVisibility(View.VISIBLE);

            Auto.setText("auto");
            //Auto.startAnimation(in);
            Auto.setVisibility(View.VISIBLE);
            On.setText("on");
            //  On.startAnimation(in);
            On.setVisibility(View.VISIBLE);
            Off.setText("off");
            //    Off.startAnimation(in);
            Off.setVisibility(View.VISIBLE);
            Torch.setText("torch");
            //   Torch.startAnimation(in);
            Torch.setVisibility(View.VISIBLE);

            for(int i = 0; i<Sview.getChildCount();i++)
            {
                Sview.getChildAt(i).startAnimation(in);
            }

            TextView it1 = (TextView)activity.findViewById(R.id.horTextItem1);
            it1.setVisibility(View.GONE);
            TextView it2 = (TextView)activity.findViewById(R.id.horTextItem2);
            it2.setVisibility(View.GONE);
            TextView it3 = (TextView)activity.findViewById(R.id.horTextItem3);
            it3.setVisibility(View.GONE);
            TextView it4 = (TextView)activity.findViewById(R.id.horTextItem4);
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
        else {
            if (textView.getText().equals("torch"))
            {
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue("off", true);
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "off");
                textView.setText("off");
            }
            else
            {
                cameraUiWrapper.camParametersHandler.FlashMode.SetValue("torch", true);
                appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, "torch");
                textView.setText("torch");
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
                if (cameraUiWrapper.camParametersHandler.FlashMode != null && cameraUiWrapper.camParametersHandler.FlashMode.IsSupported())
                {
                    textView.setVisibility(View.VISIBLE);
                    String appSet = appSettingsManager.getString(AppSettingsManager.SETTING_FLASHMODE);
                    String para = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
                    if (appSet.equals("")) {
                        appSet = cameraUiWrapper.camParametersHandler.FlashMode.GetValue();
                        appSettingsManager.setString(AppSettingsManager.SETTING_FLASHMODE, para);
                    }
                    if (!appSet.equals(para))
                        cameraUiWrapper.camParametersHandler.FlashMode.SetValue(appSet, true);


                    textView.setText(appSet);
                }
                else
                {
                    textView.setVisibility(View.GONE);
                }
            }
        });

    }
}
