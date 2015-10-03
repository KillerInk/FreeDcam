package troop.com.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;

import troop.com.themesample.R;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    Boolean isInterval = false;
    Boolean isCountdown = false;
    long delay = 1;
    long duration = 30;
    long Countduration = 0;
    long interval = 1;
    long LONG = 1;

    AbstractCameraUiWrapper cameraUiWrapper;
    AnimationDrawable shutterOpenAnimation;


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
        setBackgroundResource(R.drawable.shuttercloseanimation);
        shutterOpenAnimation = (AnimationDrawable) getBackground();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCountdown) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (cameraUiWrapper != null)
                                cameraUiWrapper.DoWork();
                        }
                    }, getCountDown());

                } else if (isInterval) {
                    if (Countduration < duration)
                    {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                /////////////////////////////////////
                                         // Set Exposure Here
                                /////////////////////////////////////
                                if (cameraUiWrapper != null)
                                    cameraUiWrapper.DoWork();
                                try {
                                    Thread.sleep(getInterval());
                                    Countduration++;
                                }
                                catch (InterruptedException ex)
                                {

                                }
                            }
                        }, getDelay());
                    }

                }
                else
                {
                    if (cameraUiWrapper != null)
                        cameraUiWrapper.DoWork();
                }

            }
        });
    }

    private long getCountDown()
    {
        //20 seconds
        return 20000;
    }

    private long getDelay()
    {
        // time that shutter actuall send first callback after shutter button pressed
        //20 seconds
        return 20000;
    }

    private long getInterval()
    {
        // the time between each shot
        //5 seconds
        return 5000;
    }

    private long getDuration()
    {
        //for how long must the intervalmeter run ie 2hours or infinite till manually stopped
        //20 seconds
        return 20000;
    }

    private long getLong()
    {
        //20 seconds
        //how long each image is to be exposed should control expo time
        return 20000;
    }


    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
        cameraUiWrapper.moduleHandler.SetWorkListner(this);
        cameraUiWrapper.moduleHandler.moduleEventHandler.addListner(this);
    }


    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void onWorkStarted()
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                setBackgroundResource(R.drawable.shuttercloseanimation);
                shutterOpenAnimation = (AnimationDrawable) getBackground();
                shutterOpenAnimation.stop();
                shutterOpenAnimation.setOneShot(true);
                shutterOpenAnimation.start();
            }
        });


        /*RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

//Setup anim with desired properties
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE); //Repeat animation indefinitely
        anim.setDuration(5000);
        this.startAnimation(anim);*/
    }

    @Override
    public void onWorkFinished(boolean finished)
    {
        this.post(new Runnable() {
            @Override
            public void run() {
                setBackgroundResource(R.drawable.shutteropenanimation);
                shutterOpenAnimation = (AnimationDrawable) getBackground();
                shutterOpenAnimation.stop();
                shutterOpenAnimation.setOneShot(true);
                shutterOpenAnimation.start();
            }
        });

    }
}
