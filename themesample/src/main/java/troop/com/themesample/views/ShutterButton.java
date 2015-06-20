package troop.com.themesample.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.i_camera.modules.AbstractModuleHandler;
import com.troop.freedcam.i_camera.modules.I_ModuleEvent;

/**
 * Created by troop on 20.06.2015.
 */
public class ShutterButton extends Button implements I_ModuleEvent, AbstractModuleHandler.I_worker
{
    AbstractCameraUiWrapper cameraUiWrapper;

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
            public void onClick(View v)
            {
                if (cameraUiWrapper != null)
                    cameraUiWrapper.DoWork();
            }
        });
    }


    public void SetCameraUIWrapper(AbstractCameraUiWrapper cameraUiWrapper)
    {
        this.cameraUiWrapper = cameraUiWrapper;
    }


    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void onWorkStarted() {

    }

    @Override
    public void onWorkFinished(boolean finished) {

    }
}
