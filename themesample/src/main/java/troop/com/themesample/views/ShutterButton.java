package troop.com.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;
import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;
import com.troop.freedcam.sonyapi.parameters.ParameterHandlerSony;
import com.troop.freedcam.ui.AppSettingsManager;

import java.util.Date;

import troop.com.themesample.R;

import troop.com.themesample.handler.UserMessageHandler;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    AbstractCameraUiWrapper cameraUiWrapper;
    AnimationDrawable shutterOpenAnimation;

    AnimationDrawable repeatT;

    AppSettingsManager appSettingsManager;
    String TAG = ShutterButton.class.getSimpleName();
    Showstate currentShow = Showstate.image_capture_stopped;
    boolean contshot = false;
    Handler handlerLoop;


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

                    if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_INTERVAL) ||contshot)
                    {
                        if (!cameraUiWrapper.moduleHandler.GetCurrentModule().DoWork())
                        {
                            if (cameraUiWrapper.moduleHandler.GetCurrentModule().IsWorking())
                                switchBackground(Showstate.continouse_capture_cancel_whilework, true);
                            else
                                switchBackground(Showstate.continouse_capture_cancel_nowork,true);
                        }
                        else
                            switchBackground(Showstate.continouse_capture_start,false);
                    }
                    else
                        cameraUiWrapper.moduleHandler.GetCurrentModule().DoWork();
                }
            }
        });
    }



    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper, AppSettingsManager appSettingsManager, UserMessageHandler messageHandler)
    {
        if (this.cameraUiWrapper == cameraUiWrapper)
            return;
        this.cameraUiWrapper = cameraUiWrapper;
        this.appSettingsManager = appSettingsManager;
        cameraUiWrapper.moduleHandler.SetWorkListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
        if (cameraUiWrapper.camParametersHandler.ContShootMode != null)
            cameraUiWrapper.camParametersHandler.ContShootMode.addEventListner(contshotListner);

        ModuleChanged("");
        Log.d(TAG,"Set wrapper to ShutterButton");
    }

    private void switchBackground(Showstate showstate, boolean animate)

        if (appSettingsManager.GetCurrentModule().equals(AbstractModuleHandler.MODULE_VIDEO))
    {
        currentShow = showstate;
        Log.d(TAG, "switchBackground:" + currentShow);
        switch (showstate)
        {
            case video_recording_stopped:
                setBackgroundResource(R.drawable.video_recording_start);
                break;
            case video_recording_started:
                setBackgroundResource(R.drawable.video_recording_stop);
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
        handlerLoop = new Handler();
    }

    private void startLooperThread(int delay)
    {
        if (isWorking)
            handlerLoop.postDelayed(runner, delay);
            //handlerLoop.post(runner);
    }

    Runnable runner = new Runnable() {
        @Override
        public void run()
        {
            if (cameraUiWrapper == null)
                return;
            animatE();

            startLooperThread(1000);

        }
    };


    @Override
    public String ModuleChanged(String module) {
        if (appSettingsManager.GetCurrentModule().equals(AbstractModuleHandler.MODULE_VIDEO))
        {

            setBackgroundResource(R.drawable.video_recording);
           // repeatT = (AnimationDrawable) getBackground();
        }
        else
        {

            setBackgroundResource(R.drawable.shuttercloseanimation);
            //getBackground().setAlpha(alpha);
            shutterOpenAnimation = (AnimationDrawable) getBackground();
        }

        Log.d(TAG, "Module Changed");
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
                else if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_INTERVAL) || contshot)
                    switchBackground(Showstate.continouse_capture_start,false);

            }
        });
        return null;

    }

    int workerCounter = 0;
    int finishcounter = 0;
    @Override
    public void onWorkStarted()
    {
        Log.d(TAG, "onWorkStarted CurrentShow:" + currentShow);
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


        workerCounter++;
        finishcounter = 0;
    }

    @Override
    public void onWorkFinished(boolean finished)
        Log.d(TAG, "workstarted " + workerCounter + " worfinshed " + finishcounter++);
        Log.d(TAG, "onWorkFinished CurrentShow:" + currentShow);
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

    private void registerAnimation(final Runnable cb){

        final CustomAnimationDrawableNew aniDrawable = new CustomAnimationDrawableNew((AnimationDrawable) getBackground());

        setBackgroundDrawable(aniDrawable);
        aniDrawable.setDither(true);
        aniDrawable.setOneShot(false);

        aniDrawable.setOnFinishCallback(cb);


        if(!aniDrawable.isRunning()){
            aniDrawable.start();
        }
    }

    private void doAnimP() {

            }
        });
        /*if (!appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION).equals("off")) {
            currentShow =Showstate.continouse_capture_close;
        }*/
    }

    AbstractModeParameter.I_ModeParameterEvent contshotListner = new AbstractModeParameter.I_ModeParameterEvent() {
        @Override
        public void onValueChanged(String val)
        {
            //Single","Continuous","Spd Priority Cont.
            Log.d(TAG, "contshot:" + val);
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

                    //animatE();
                    isWorking = false;
        }

        @Override
        public void onValuesChanged(String[] values) {

        }
    };
}
