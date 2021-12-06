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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.CameraApiManager;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.event.module.ModuleChangedEvent;
import freed.cam.event.capture.CaptureStates;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.Log;

/**
 * Created by troop on 20.06.2015.
 */
@AndroidEntryPoint
public class ShutterButton extends AppCompatButton implements ModuleChangedEvent, freed.cam.event.capture.CaptureStateChangedEvent {

    private CameraWrapperInterface cameraUiWrapper;

    private final String TAG = ShutterButton.class.getSimpleName();
    protected HandlerThread mBackgroundThread;
    protected ShutterAnimationHandler animationHandler;
    private Handler uiHandler = new Handler();

    @Inject
    public SettingsManager settingsManager;
    @Inject
    CameraApiManager cameraApiManager;

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
        Log.d(TAG, "EventBus register");
        cameraApiManager.addCaptureStateChangedEventListner(this);
        cameraApiManager.addModuleChangedEventListner(this);
        //EventBusHelper.register(this);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "EventBus unregister");
        cameraApiManager.removeCaptureStateChangedListner(this);
        cameraApiManager.removeModuleChangedEventListner(this);
        stopBackgroundThread();
    }

    private void init(Context context) {

        startBackgroundThread();
        //set background img that get then overdrawn
        setBackgroundResource(R.drawable.shutter5);

        this.setOnClickListener(v -> {
            String sf = settingsManager.get(SettingKeys.selfTimer).get();
            if (TextUtils.isEmpty(sf))
                sf = "0";
            int selftimer = Integer.parseInt(sf);
            if(selftimer > 0) {
                uiHandler.postDelayed(selftimerRunner, selftimer*1000);
                setCaptureState(CaptureStates.selftimerstart);
            }
            else
                cameraUiWrapper.getModuleHandler().startWork();
        });

    }

    private Runnable selftimerRunner =new Runnable() {
        @Override
        public void run() {
            setCaptureState(CaptureStates.selftimerstop);
            cameraUiWrapper.getModuleHandler().startWork();
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

    @Override
    public void onCaptureStateChanged(CaptureStates states) {
        setCaptureState(states);
    }
}