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

package freed.cam.ui.themesample.cameraui;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.troop.freedcam.R;

import freed.cam.apis.KEYS;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.apis.basecamera.parameters.modes.AbstractModeParameter;
import freed.cam.ui.themesample.handler.UserMessageHandler;
import freed.utils.Logger;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements ModuleChangedEvent, ModuleHandlerAbstract.CaptureStateChanged
{
    private CameraWrapperInterface cameraUiWrapper;
    private AnimationDrawable shutterOpenAnimation;
    private final String TAG = ShutterButton.class.getSimpleName();
    private CaptureStates currentShow = CaptureStates.image_capture_stop;
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



    public void SetCameraUIWrapper(CameraWrapperInterface cameraUiWrapper, UserMessageHandler messageHandler)
    {
        if (this.cameraUiWrapper == cameraUiWrapper || cameraUiWrapper.GetModuleHandler() == null)
            return;
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.GetModuleHandler().SetWorkListner(this);
        cameraUiWrapper.GetModuleHandler().addListner(this);
        if (cameraUiWrapper.GetParameterHandler().ContShootMode != null)
            cameraUiWrapper.GetParameterHandler().ContShootMode.addEventListner(this.contshotListner);

        this.onModuleChanged("");
        Logger.d(this.TAG, "Set cameraUiWrapper to ShutterButton");
    }

    private void switchBackground(final CaptureStates showstate, final boolean animate)
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
    public void onModuleChanged(String module) {

        Logger.d(this.TAG, "Module Changed");
        if (this.cameraUiWrapper.GetParameterHandler().ContShootMode != null && this.cameraUiWrapper.GetParameterHandler().ContShootMode.IsSupported())
        {
            this.contshotListner.onParameterValueChanged(this.cameraUiWrapper.GetParameterHandler().ContShootMode.GetValue());

        }
        post(new Runnable() {
            @Override
            public void run() {
                if (cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_VIDEO))
                {
                    switchBackground(CaptureStates.video_recording_stop, true);
                }
                else  if((cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_PICTURE)
                        || cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_HDR)
                        || cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_AFBRACKET))
                        && !contshot) {
                    switchBackground(CaptureStates.image_capture_stop,true);
                }
                else if (cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_INTERVAL)
                        || contshot || cameraUiWrapper.GetModuleHandler().GetCurrentModuleName().equals(KEYS.MODULE_STACKING))
                    switchBackground(CaptureStates.continouse_capture_start,false);

            }
        });
    }

    @Override
    public void onCaptureStateChanged(CaptureStates mode)
    {
        Logger.d(this.TAG, "onCaptureStateChanged CurrentShow:" + this.currentShow);
        this.switchBackground(mode,true);

    }

    private final AbstractModeParameter.I_ModeParameterEvent contshotListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onParameterValueChanged(String val)
        {
            //Single","Continuous","Spd Priority Cont.
            Logger.d(ShutterButton.this.TAG, "contshot:" + val);
            if (ShutterButton.this.cameraUiWrapper.GetParameterHandler().ContShootMode.GetValue().contains("Single")) {
                ShutterButton.this.switchBackground(CaptureStates.image_capture_start, false);
                ShutterButton.this.contshot = false;
            }
            else {
                ShutterButton.this.switchBackground(CaptureStates.continouse_capture_start, false);
                ShutterButton.this.contshot = true;
            }
        }

        @Override
        public void onParameterIsSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterIsSetSupportedChanged(boolean isSupported) {

        }

        @Override
        public void onParameterValuesChanged(String[] values) {

        }

        @Override
        public void onVisibilityChanged(boolean visible) {

        }
    };
}