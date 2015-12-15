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
    final int alpha = 150;
    boolean isVideo = false;

    public ShutterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShutterButton(Context context) {
        super(context);
        //init();
        //start handler
        Toast.makeText(context, "STARTING", Toast.LENGTH_SHORT).show();
        //set delay to start
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


        if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
        {

            isVideo = true;
            setBackgroundResource(R.drawable.video_recording_start);
            shutterOpenAnimation = (AnimationDrawable) getBackground();

        }
        else
        {

            isVideo = false;
            setBackgroundResource(R.drawable.shuttercloseanimation);
            //getBackground().setAlpha(alpha);
            shutterOpenAnimation = (AnimationDrawable) getBackground();
        }


    }


    @Override
    public String ModuleChanged(String module) {

        if (cameraUiWrapper.moduleHandler.GetCurrentModuleName().equals(AbstractModuleHandler.MODULE_VIDEO))
        {

            isVideo = true;
            setBackgroundResource(R.drawable.video_recording_start);
            shutterOpenAnimation = (AnimationDrawable) getBackground();

        }
        else {

            isVideo = false;
            setBackgroundResource(R.drawable.shuttercloseanimation);
            //getBackground().setAlpha(alpha);
            shutterOpenAnimation = (AnimationDrawable) getBackground();
        }
            return null;

    }


    int workerCounter = 0;
    int finishcounter = 0;
    @Override
    public void onWorkStarted()
    {
        if(isVideo)
        {
            doAnim();
        }
        else
        {
            doAnimP2();
        }
                workerCounter++;
                finishcounter = 0;

    }

    private void doAnim()
    {

        setBackgroundResource(R.drawable.video_recording_start);


        shutterOpenAnimation = (AnimationDrawable) getBackground();
        if (shutterOpenAnimation .isRunning()) {
            shutterOpenAnimation .stop();
    }
        shutterOpenAnimation .start();

    }

    private void doAnimP()
    {
        setBackgroundResource(R.drawable.shutteropenanimation);
        //getBackground().setAlpha(alpha);
        shutterOpenAnimation = (AnimationDrawable) getBackground();

        if (shutterOpenAnimation .isRunning()) {
            shutterOpenAnimation .stop();
        }
        shutterOpenAnimation.setOneShot(true);
        shutterOpenAnimation .start();

     //   shutterOpenAnimation.stop();

     //   shutterOpenAnimation.start();
    }

    private void doAnimP2()
    {
        setBackgroundResource(R.drawable.shuttercloseanimation);
        //getBackground().setAlpha(alpha);
        shutterOpenAnimation = (AnimationDrawable) getBackground();

        if (shutterOpenAnimation .isRunning()) {
            shutterOpenAnimation .stop();
        }
        shutterOpenAnimation.setOneShot(true);
        shutterOpenAnimation .start();

        //   shutterOpenAnimation.stop();

        //   shutterOpenAnimation.start();

      //  setBackgroundResource(R.drawable.shuttercloseanimation);
        // getBackground().setAlpha(alpha);
       // shutterOpenAnimation = (AnimationDrawable) getBackground();
      //  shutterOpenAnimation.stop();
       // shutterOpenAnimation.setOneShot(true);
      //  shutterOpenAnimation.start();
    }
    private void doAnimP3()
    {
        setBackgroundResource(R.drawable.video_recording_stop);
        shutterOpenAnimation = (AnimationDrawable) getBackground();
        if (shutterOpenAnimation .isRunning())
        {
            shutterOpenAnimation .stop();
        }
        shutterOpenAnimation .start();
    }

    @Override
    public void onWorkFinished(boolean finished) {
        Log.d(TAG, "workstarted " + workerCounter + " worfinshed " + finishcounter++);
        this.post(new Runnable() {
            @Override
            public void run() {
                if (isVideo) {
                    doAnimP3();
                }
                else
                {
                    doAnimP();
                }
            }
        });

        if (!appSettingsManager.getString(AppSettingsManager.SETTING_INTERVAL_DURATION).equals("off"))
            intervalHandler.DoNextInterval();

    }
}
