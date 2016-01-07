package troop.com.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;
import troop.com.themesample.handler.IntervalHandler;
import troop.com.themesample.handler.UserMessageHandler;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    AbstractCameraUiWrapper cameraUiWrapper;
    AnimationDrawable shutterOpenAnimation;
    IntervalHandler intervalHandler;
    AppSettingsManager appSettingsManager;
    String TAG = ShutterButton.class.getSimpleName();
    boolean isVideo = false;
    Showstate currentShow = Showstate.image_capture_stopped;

    enum Showstate
    {
        video_recording_stopped,
        video_recording_started,
        image_capture_stopped,
        image_capture_started,
        continouse_capture_stopped_running,
        continouse_capture_started_running,
        continouse_capture_stopped_stop,
        continouse_capture_started_stop,
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
                if (cameraUiWrapper != null) {
                    String s = appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION);
                    if (s.equals("")) {
                        s = "off";
                        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL, "1 sec");
                        appSettingsManager.setString(AppSettingsManager.SETTING_INTERVAL_DURATION, s);
                        appSettingsManager.setString(AppSettingsManager.SETTING_TIMER, "0 sec");
                    }
                    if (!s.equals("off")) {
                        if (!intervalHandler.IsWorking()) {
                            intervalHandler.StartInterval();
                        } else
                            intervalHandler.CancelInterval();
                    } else
                        intervalHandler.StartShutterTime();
                }
            }
        });
    }



    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, UserMessageHandler messageHandler)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        cameraUiWrapper.moduleHandler.SetWorkListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        intervalHandler = new IntervalHandler(appSettingsManager, cameraUiWrapper, messageHandler);
        if (cameraUiWrapper.camParametersHandler.ContShootMode != null)
            cameraUiWrapper.camParametersHandler.ContShootMode.addEventListner(contshotListner);

        ModuleChanged("");
    }

    private void switchBackground(Showstate showstate)
    {
        currentShow = showstate;
        switch (showstate)
        {
            case video_recording_stopped:
                setBackgroundResource(R.drawable.video_recording_start);
                break;
            case video_recording_started:
                setBackgroundResource(R.drawable.video_recording_stop);
                break;
            case image_capture_stopped:
                setBackgroundResource(R.drawable.shuttercloseanimation);
                break;
            case image_capture_started:
                setBackgroundResource(R.drawable.shutteropenanimation);
                break;
            case continouse_capture_started_running:
                break;
            case continouse_capture_started_stop:
                break;
            case continouse_capture_stopped_running:
                break;
            case continouse_capture_stopped_stop:
                break;


        }
        shutterOpenAnimation = (AnimationDrawable) getBackground();
        if (shutterOpenAnimation .isRunning()) {
            shutterOpenAnimation .stop();
        }
        shutterOpenAnimation.setOneShot(true);
        shutterOpenAnimation .start();
    }

    @Override
    public String ModuleChanged(String module) {

        if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
        {
            switchBackground(Showstate.video_recording_stopped);
        }
        else {
            switchBackground(Showstate.image_capture_stopped);
        }
            return null;

    }

    int workerCounter = 0;
    int finishcounter = 0;
    @Override
    public void onWorkStarted()
    {
        if (currentShow == Showstate.video_recording_stopped)
            currentShow = Showstate.video_recording_started;
        else
            currentShow = Showstate.image_capture_started;
        switchBackground(currentShow);
        workerCounter++;
        finishcounter = 0;
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        Log.d(TAG, "workstarted " + workerCounter + " worfinshed " + finishcounter++);
        this.post(new Runnable() {
            @Override
            public void run() {
                if (currentShow == Showstate.video_recording_started)
                    switchBackground(Showstate.video_recording_stopped);
                else
                    switchBackground(Showstate.image_capture_stopped);
            }
        });

        if (!appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION).equals("off"))
            intervalHandler.DoNextInterval();

    }

    AbstractModeParameter.I_ModeParameterEvent contshotListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val) {

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
    };
}
