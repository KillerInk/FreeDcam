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
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.troop.freedcam.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.modules.ModuleChangedEvent;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;
import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract.CaptureStates;
import freed.cam.events.CaptureStateChangedEvent;
import freed.cam.events.EventBusHelper;
import freed.cam.events.ModuleHasChangedEvent;
import freed.cam.events.StartWorkEvent;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends AppCompatButton implements ModuleChangedEvent {

    @Subscribe
    public void onCaptureStateChanged(CaptureStateChangedEvent stateChangedEvent) {
        CaptureStates mode = stateChangedEvent.captureState;
        setCaptureState(mode);
    }

    @Subscribe
    public void onModuleHasChangedEvent(ModuleHasChangedEvent event)
    {
        onModuleChanged(event.NewModuleName);
    }

    private CameraWrapperInterface cameraUiWrapper;

    private final String TAG = ShutterButton.class.getSimpleName();
    protected HandlerThread mBackgroundThread;
    protected ShutterAnimationHandler animationHandler;
    private Handler uiHandler = new Handler();

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        if (mBackgroundThread ==  null || !mBackgroundThread.isAlive()) {
            mBackgroundThread = new HandlerThread("ShutterDraw");
            mBackgroundThread.start();
            animationHandler = new ShutterAnimationHandler(mBackgroundThread.getLooper(), getResources(), this);
        }
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread()
    {
        Log.d(TAG, "stopShutterDraw");
        if (mBackgroundThread == null)
            return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBackgroundThread.quitSafely();
        } else
            mBackgroundThread.quit();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            animationHandler = null;
        } catch (InterruptedException e) {
            Log.WriteEx(e);
        }
    }

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ShutterButton(Context context) {
        super(context);
        this.init(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startBackgroundThread();
        EventBusHelper.register(this);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBusHelper.unregister(this);
        stopBackgroundThread();
    }

    private void init(Context context) {

        startBackgroundThread();
        //set background img that get then overdrawn
        setBackgroundResource(R.drawable.shutter5);

        this.setOnClickListener(v -> {
            String sf = SettingsManager.get(SettingKeys.selfTimer).get();
            if (TextUtils.isEmpty(sf))
                sf = "0";
            int selftimer = Integer.parseInt(sf);
            if(selftimer > 0) {
                uiHandler.postDelayed(selftimerRunner, selftimer*1000);
                setCaptureState(CaptureStates.selftimerstart);
            }
            else
                EventBusHelper.post(new StartWorkEvent());
        });

    }

    private Runnable selftimerRunner =new Runnable() {
        @Override
        public void run() {
            setCaptureState(CaptureStates.selftimerstop);
            EventBusHelper.post(new StartWorkEvent());
        }
    };


    public void SetCameraUIWrapper(CameraWrapperInterface cameraUiWrapper) {
        if (cameraUiWrapper.getModuleHandler() == null)
            return;
        this.cameraUiWrapper = cameraUiWrapper;
        if(cameraUiWrapper.getModuleHandler().getCurrentModule() != null) {
            setCaptureState(cameraUiWrapper.getModuleHandler().getCurrentModule().getCurrentCaptureState());

        }
        Log.d(this.TAG, "Set cameraUiWrapper to ShutterButton");
    }

    private void setCaptureState(CaptureStates mode) {
        if(mode == null) {
            Log.d(TAG, "onCaptureStateChanged: Capture State is null");
            return;
        }
        //first start shutter animation
        animationHandler.setCaptureState(mode);
        Log.d(TAG, "switchBackground:" + mode);
        if (!animationHandler.isRunning())
            animationHandler.startDrawing();

        //set specfic mode overides like drawtimer,
        //setting it that way shutter is already abit opend till the green timer gets visible
        switch (mode) {
            case video_recording_stop:
                animationHandler.drawTimer(false);
                animationHandler.stopShutterTimer();
                break;
            case video_recording_start:
                animationHandler.drawTimer(true);
                break;
            case image_capture_stop:
                animationHandler.drawTimer(false);
                animationHandler.stopShutterTimer();
                break;
            case image_capture_start:
                animationHandler.drawTimer(true);
                break;
            case continouse_capture_start:
                break;
            case continouse_capture_stop:
                break;
            case continouse_capture_work_start:
                animationHandler.drawTimer(true);
                break;
            case continouse_capture_work_stop:
                animationHandler.drawTimer(false);
                break;
            case cont_capture_stop_while_working:
                break;
            case cont_capture_stop_while_notworking:
                animationHandler.drawTimer(false);
                animationHandler.stopShutterTimer();
                break;
            case selftimerstart:
                animationHandler.drawTimer(true);
                break;
            case selftimerstop:
                animationHandler.drawTimer(false);
                animationHandler.stopShutterTimer();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        animationHandler.onDraw(canvas);
    }

    @Override
    public void onModuleChanged(String module) {
        if (cameraUiWrapper == null)
            return;
        if(cameraUiWrapper.getModuleHandler().getCurrentModule() != null) {
            setCaptureState(cameraUiWrapper.getModuleHandler().getCurrentModule().getCurrentCaptureState());
        }
    }
}