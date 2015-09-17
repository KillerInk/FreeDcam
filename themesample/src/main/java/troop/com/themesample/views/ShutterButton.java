package troop.com.themesample.views;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
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
            public void onClick(View v)
            {
                if (cameraUiWrapper != null) {
                    cameraUiWrapper.DoWork();

                }
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
        setBackgroundResource(R.drawable.shuttercloseanimation);
        shutterOpenAnimation = (AnimationDrawable) getBackground();
        shutterOpenAnimation.stop();
        shutterOpenAnimation.setOneShot(true);
        shutterOpenAnimation.start();

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
