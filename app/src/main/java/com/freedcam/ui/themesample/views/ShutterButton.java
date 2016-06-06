package com.freedcam.ui.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.freedcam.apis.basecamera.camera.AbstractCameraUiWrapper;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.camera.modules.AbstractModuleHandler.I_worker;
import com.freedcam.apis.basecamera.camera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter;
import com.freedcam.apis.basecamera.camera.parameters.modes.AbstractModeParameter.I_ModeParameterEvent;
import com.freedcam.ui.themesample.handler.UserMessageHandler;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R.drawable;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, I_worker
{
    private AbstractCameraUiWrapper cameraUiWrapper;
    private AnimationDrawable shutterOpenAnimation;
    private String TAG = ShutterButton.class.getSimpleName();
    private CaptureModes currentShow = CaptureModes.image_capture_stop;
    private boolean contshot;

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
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraUiWrapper != null)
                {
                    cameraUiWrapper.moduleHandler.GetCurrentModule().DoWork();
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

    private void switchBackground(final CaptureModes showstate,final boolean animate)
    {
        post(new Runnable() {
            @Override
            public void run() {
                currentShow = showstate;
                Logger.d(TAG, "switchBackground:" + currentShow);
                switch (showstate)
                {
                    case video_recording_stop:
                        setBackgroundResource(drawable.video_recording_stop);
                        break;
                    case video_recording_start:
                        setBackgroundResource(drawable.video_recording_start);
                        break;
                    case image_capture_stop:
                        setBackgroundResource(drawable.shutteropenanimation);
                        break;
                    case image_capture_start:
                        setBackgroundResource(drawable.shuttercloseanimation);
                        break;
                    case continouse_capture_start:
                        setBackgroundResource(drawable.close_open_shutter_start_to_stop); // closed to opend shutter, set start to cancel
                        break;
                    case cont_capture_stop_while_working:
                        setBackgroundResource(drawable.alltime_open_shutter_stop_to_start); // opend shutter, set stop to start
                        break;
                    case cont_capture_stop_while_notworking:
                        setBackgroundResource(drawable.video_recording_stop); //closed shutter , set stop to start animation
                        break;
                    case continouse_capture_stop:
                        setBackgroundResource(drawable.open_close_shutter_stop_to_start);//close shutter animation and set stop to start button
                        break;
                    case continouse_capture_work_start:
                        setBackgroundResource(drawable.close_start_shutter_alltime_stop);//shows shutter open animation with stopbutton
                        break;
                    case continouse_capture_work_stop:
                        setBackgroundResource(drawable.start_close_shutter_alltime_stop);//shows shutter close animation with stopbutton
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
        post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
                {
                    switchBackground(CaptureModes.continouse_capture_stop.video_recording_stop, true);
                }
                else  if((cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_PICTURE)
                        || cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_HDR))
                        && !contshot) {
                    switchBackground(CaptureModes.image_capture_stop,true);
                }
                else if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_INTERVAL) || contshot ||cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_STACKING))
                    switchBackground(CaptureModes.continouse_capture_start,false);

            }
        });
    }

    @Override
    public void onCaptureStateChanged(CaptureModes mode)
    {
        Logger.d(TAG, "onCaptureStateChanged CurrentShow:" + currentShow);
        switchBackground(mode,true);

    }

    private I_ModeParameterEvent contshotListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            //Single","Continuous","Spd Priority Cont.
            Logger.d(TAG, "contshot:" + val);
            if (cameraUiWrapper.camParametersHandler.ContShootMode.GetValue().contains("Single")) {
                switchBackground(CaptureModes.image_capture_start, false);
                contshot = false;
            }
            else {
                switchBackground(CaptureModes.continouse_capture_start, false);
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