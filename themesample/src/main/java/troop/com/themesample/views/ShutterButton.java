package troop.com.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;


import java.sql.Time;

import troop.com.themesample.R;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    long Countduration = 0;

    //intervalmeter start ///////////////

    static int counter = 0;
    boolean running = false;
    int interval_millis = 15000;
    int interval_duration = 15000;
    int delay = 1000;
    Handler handler = new Handler();

    //end//////////////////////////////


    AbstractCameraUiWrapper cameraUiWrapper;
    AnimationDrawable shutterOpenAnimation;


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
        handler.postDelayed(runnable, delay);
    }


    // handle interval
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            init();
            running = true;
            handler.postDelayed(this, interval_millis);
        }
    };

    private void init()
    {
        setBackgroundResource(R.drawable.shuttercloseanimation);
        shutterOpenAnimation = (AnimationDrawable) getBackground();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraUiWrapper != null)
                    cameraUiWrapper.DoWork();

                //need to workout how to calculate duration in time when its over end handler
                handler.removeCallbacks(runnable);
            }
        });
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
