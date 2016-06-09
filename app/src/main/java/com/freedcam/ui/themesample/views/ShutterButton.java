/*
 *
 *     Copyright (C) 2015 Ingo Fuchs
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * /
 */

package com.freedcam.ui.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.freedcam.apis.KEYS;
import com.freedcam.apis.basecamera.interfaces.I_CameraUiWrapper;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler;
import com.freedcam.apis.basecamera.modules.AbstractModuleHandler.CaptureModes;
import com.freedcam.apis.basecamera.modules.I_ModuleEvent;
import com.freedcam.apis.basecamera.parameters.modes.AbstractModeParameter;
import com.freedcam.ui.themesample.handler.UserMessageHandler;
import com.freedcam.utils.Logger;
import com.troop.freedcam.R;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    private I_CameraUiWrapper cameraUiWrapper;
    private AnimationDrawable shutterOpenAnimation;
    private final String TAG = ShutterButton.class.getSimpleName();
    private AbstractModuleHandler.CaptureModes currentShow = AbstractModuleHandler.CaptureModes.image_capture_stop;
    private boolean contshot;

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public ShutterButton(Context context) {
        super(context);
        this.init();
    }

    private void init()
    {
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ShutterButton.this.cameraUiWrapper != null)
                {
                    ShutterButton.this.cameraUiWrapper.GetModuleHandler().GetCurrentModule().DoWork();
                }
            }
        });
    }



    public void SetCameraUIWrapper(I_CameraUiWrapper cameraUiWrapper, UserMessageHandler messageHandler)
    {
        if (this.cameraUiWrapper == cameraUiWrapper || cameraUiWrapper.GetModuleHandler() == null || cameraUiWrapper.GetModuleHandler().moduleEventHandler == null)
            return;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.GetModuleHandler().SetWorkListner(this);
        cameraUiWrapper.GetModuleHandler().moduleEventHandler.addListner(this);
        if (cameraUiWrapper.GetParameterHandler().ContShootMode != null)
            cameraUiWrapper.GetParameterHandler().ContShootMode.addEventListner(this.contshotListner);

        this.ModuleChanged("");
        Logger.d(this.TAG, "Set cameraUiWrapper to ShutterButton");
    }

    private void switchBackground(final AbstractModuleHandler.CaptureModes showstate,final boolean animate)
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                ShutterButton.this.currentShow = showstate;
                Logger.d(ShutterButton.this.TAG, "switchBackground:" + ShutterButton.this.currentShow);
                switch (showstate)
                {
                    case video_recording_stop:
                        ShutterButton.this.setBackgroundResource(R.drawable.video_recording_stop);
                        break;
                    case video_recording_start:
                        ShutterButton.this.setBackgroundResource(R.drawable.video_recording_start);
                        break;
                    case image_capture_stop:
                        ShutterButton.this.setBackgroundResource(R.drawable.shutteropenanimation);
                        break;
                    case image_capture_start:
                        ShutterButton.this.setBackgroundResource(R.drawable.shuttercloseanimation);
                        break;
                    case continouse_capture_start:
                        ShutterButton.this.setBackgroundResource(R.drawable.close_open_shutter_start_to_stop); // closed to opend shutter, set start to cancel
                        break;
                    case cont_capture_stop_while_working:
                        ShutterButton.this.setBackgroundResource(R.drawable.alltime_open_shutter_stop_to_start); // opend shutter, set stop to start
                        break;
                    case cont_capture_stop_while_notworking:
                        ShutterButton.this.setBackgroundResource(R.drawable.video_recording_stop); //closed shutter , set stop to start animation
                        break;
                    case continouse_capture_stop:
                        ShutterButton.this.setBackgroundResource(R.drawable.open_close_shutter_stop_to_start);//close shutter animation and set stop to start button
                        break;
                    case continouse_capture_work_start:
                        ShutterButton.this.setBackgroundResource(R.drawable.close_start_shutter_alltime_stop);//shows shutter open animation with stopbutton
                        break;
                    case continouse_capture_work_stop:
                        ShutterButton.this.setBackgroundResource(R.drawable.start_close_shutter_alltime_stop);//shows shutter close animation with stopbutton
                        break;


                }
                ShutterButton.this.shutterOpenAnimation = (AnimationDrawable) ShutterButton.this.getBackground();
                if (animate) {
                    if (ShutterButton.this.shutterOpenAnimation.isRunning()) {
                        ShutterButton.this.shutterOpenAnimation.stop();
                    }
                    ShutterButton.this.shutterOpenAnimation.setOneShot(true);
                    ShutterButton.this.shutterOpenAnimation.start();
                }
            }
        });

    }

    @Override
    public void ModuleChanged(String module) {

        Logger.d(this.TAG, "Module Changed");
        if (this.cameraUiWrapper.GetParameterHandler().ContShootMode != null && this.cameraUiWrapper.GetParameterHandler().ContShootMode.IsSupported())
        {
            this.contshotListner.onValueChanged(this.cameraUiWrapper.GetParameterHandler().ContShootMode.GetValue());

        }
        post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
                {
                    switchBackground(CaptureModes.video_recording_stop, true);
                }
                else  if((cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_PICTURE)
                        || cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_HDR))
                        && !contshot) {
                    switchBackground(CaptureModes.image_capture_stop,true);
                }
                else if (cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_INTERVAL)
                        || contshot || cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_STACKING))
                    switchBackground(CaptureModes.continouse_capture_start,false);

            }
        });
    }

    @Override
    public void onCaptureStateChanged(AbstractModuleHandler.CaptureModes mode)
    {
        Logger.d(this.TAG, "onCaptureStateChanged CurrentShow:" + this.currentShow);
        this.switchBackground(mode,true);

    }

    private final AbstractModeParameter.I_ModeParameterEvent contshotListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            //Single","Continuous","Spd Priority Cont.
            Logger.d(ShutterButton.this.TAG, "contshot:" + val);
            if (ShutterButton.this.cameraUiWrapper.GetParameterHandler().ContShootMode.GetValue().contains("Single")) {
                ShutterButton.this.switchBackground(AbstractModuleHandler.CaptureModes.image_capture_start, false);
                ShutterButton.this.contshot = false;
            }
            else {
                ShutterButton.this.switchBackground(AbstractModuleHandler.CaptureModes.continouse_capture_start, false);
                ShutterButton.this.contshot = true;
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