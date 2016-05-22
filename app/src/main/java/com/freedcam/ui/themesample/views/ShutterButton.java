package com.freedcam.ui.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.ui.themesample.handler.UserMessageHandler;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    private AbstractCameraUiWrapper cameraUiWrapper;
    private AnimationDrawable shutterOpenAnimation;
    private String TAG = ShutterButton.class.getSimpleName();
    private Showstate currentShow = Showstate.image_capture_stopped;
    private boolean contshot = false;

    enum Showstate
    {
        video_recording_stopped,
        video_recording_started,
        image_capture_stopped,
        image_capture_started,
        continouse_capture_cancel_whilework,
        continouse_capture_cancel_nowork,
        continouse_capture_start,
        continouse_capture_stop_fromcancel,
        continouse_capture_stop,
        continouse_capture_open,
        continouse_capture_close,


    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShutterButton(Context context) {
        super(context);
        init();
    }

    private void init()
    {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraUiWrapper != null)
                {
                    if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_INTERVAL) ||contshot || cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_STACKING))
                    {
                        Logger.d(TAG, "is contshoot, interval or stack");
                        if (!cameraUiWrapper.moduleHandler.GetCurrentModule().DoWork())
                        {
                            Logger.d(TAG, "module has work running");
                            if (cameraUiWrapper.moduleHandler.GetCurrentModule().IsWorking())
                            {
                                Logger.d(TAG, "module is working, set cancel");
                                switchBackground(Showstate.continouse_capture_cancel_whilework, true);
                            }
                            else {
                                Logger.d(TAG, "module is not working, set nowork");
                                switchBackground(Showstate.continouse_capture_cancel_nowork, true);
                            }
                        }
                        else
                        {
                            Logger.d(TAG, "module has no work running");
                            switchBackground(Showstate.continouse_capture_start, false);
                        }
                    }
                    else
                    {
                        Logger.d(TAG, "normal DoWork");
                        cameraUiWrapper.moduleHandler.GetCurrentModule().DoWork();
                    }
                }
            }
        });
    }



    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, UserMessageHandler messageHandler)
    {
        if (this.cameraUiWrapper == cameraUiWrapper || cameraUiWrapper.moduleHandler == null || cameraUiWrapper.moduleHandler.moduleEventHandler == null)
            return;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.moduleHandler.SetWorkListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        if (cameraUiWrapper.camParametersHandler.ContShootMode != null)
            cameraUiWrapper.camParametersHandler.ContShootMode.addEventListner(contshotListner);

        ModuleChanged("");
        Logger.d(TAG, "Set cameraUiWrapper to ShutterButton");
    }

    private void switchBackground(final Showstate showstate,final boolean animate)
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                currentShow = showstate;
                Logger.d(TAG, "switchBackground:" + currentShow);
                switch (showstate)
                {
                    case video_recording_stopped:
                        setBackgroundResource(R.drawable.video_recording_stop);
                        break;
                    case video_recording_started:
                        setBackgroundResource(R.drawable.video_recording_start);
                        break;
                    case image_capture_stopped:
                        setBackgroundResource(R.drawable.shutteropenanimation);
                        break;
                    case image_capture_started:
                        setBackgroundResource(R.drawable.shuttercloseanimation);
                        break;
                    case continouse_capture_start:
                        setBackgroundResource(R.drawable.contshot_start);
                        break;
                    case continouse_capture_cancel_whilework:
                        setBackgroundResource(R.drawable.contshot_cancel_whilework);
                        break;
                    case continouse_capture_cancel_nowork:
                        setBackgroundResource(R.drawable.video_recording_stop);
                        break;
                    case continouse_capture_stop_fromcancel:
                        setBackgroundResource(R.drawable.contshot_stop_normal);
                        break;
                    case continouse_capture_stop:
                        setBackgroundResource(R.drawable.contshot_stop_normal);
                        break;
                    case continouse_capture_open:
                        setBackgroundResource(R.drawable.contshot_cancel_shown_open);
                        break;
                    case continouse_capture_close:
                        setBackgroundResource(R.drawable.contshot_cancel_shown_close);
                        break;


                }
                shutterOpenAnimation = (AnimationDrawable) getBackground();
                if (animate) {
                    if (shutterOpenAnimation.isRunning()) {
                        shutterOpenAnimation.stop();
                    }
                    shutterOpenAnimation.setOneShot(true);
                    shutterOpenAnimation.start();
                }
            }
        });

    }

    @Override
    public void ModuleChanged(String module) {

        Logger.d(TAG, "Module Changed");
        if (cameraUiWrapper.camParametersHandler.ContShootMode != null && cameraUiWrapper.camParametersHandler.ContShootMode.IsSupported())
        {
            contshotListner.onValueChanged(cameraUiWrapper.camParametersHandler.ContShootMode.GetValue());

        }
        this.post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
                {
                    switchBackground(Showstate.video_recording_stopped, true);
                }
                else  if((cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_PICTURE)
                        || cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_HDR))
                        && !contshot) {
                    switchBackground(Showstate.image_capture_stopped,true);
                }
                else if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_INTERVAL) || contshot ||cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_STACKING))
                    switchBackground(Showstate.continouse_capture_start,false);

            }
        });
    }

    @Override
    public void onWorkStarted()
    {
        Logger.d(TAG, "onWorkStarted CurrentShow:" + currentShow);

        switch (currentShow)
        {
            case video_recording_stopped:
                switchBackground(Showstate.video_recording_started,true);
                break;
            case image_capture_stopped:
                switchBackground(Showstate.image_capture_started,true);
                break;
            case continouse_capture_start:
                switchBackground(Showstate.continouse_capture_start,true);
                break;
            case continouse_capture_close:
                switchBackground(Showstate.continouse_capture_open,true);
                break;
        }
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        Logger.d(TAG, "onWorkFinished CurrentShow:" + currentShow);
        this.post(new Runnable() {
            @Override
            public void run() {

                switch (currentShow)
                {
                    case video_recording_started:
                        switchBackground(Showstate.video_recording_stopped,true);
                        break;
                    case image_capture_started:
                        switchBackground(Showstate.image_capture_stopped,true);
                        break;
                    case continouse_capture_open:
                        switchBackground(Showstate.continouse_capture_close,true);
                        break;
                    case continouse_capture_start:
                        switchBackground(Showstate.continouse_capture_close,true);
                        break;
                    case continouse_capture_stop_fromcancel:
                        switchBackground(Showstate.continouse_capture_stop_fromcancel,true);
                        break;
                    case continouse_capture_stop:
                        switchBackground(Showstate.continouse_capture_stop, true);
                        break;
                    case continouse_capture_cancel_whilework:
                        switchBackground(Showstate.continouse_capture_stop, true);
                        break;

                }


            }
        });

        /*if (!appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION).equals("off")) {
            currentShow =Showstate.continouse_capture_close;
        }*/

    }

    private AbstractModeParameter.I_ModeParameterEvent contshotListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            //Single","Continuous","Spd Priority Cont.
            Logger.d(TAG, "contshot:" + val);
            if (cameraUiWrapper.camParametersHandler.ContShootMode.GetValue().contains("Single")) {
                switchBackground(Showstate.image_capture_started, false);
                contshot = false;
            }
            else {
                switchBackground(Showstate.continouse_capture_start, false);
                contshot = true;
            }
        }

        @Override
        public void onIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };
}