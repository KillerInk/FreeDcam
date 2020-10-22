package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;

import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.events.ShutterSpeedChangedEvent;
import freed.cam.ui.themesample.cameraui.ManualButton;

public class ManualButtonShutter extends ManualButton {
    public ManualButtonShutter(Context context, ParameterInterface parameter, int drawableImg) {
        super(context, parameter, drawableImg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShutterSpeedChanged(ShutterSpeedChangedEvent shutterSpeedChangedEvent)
    {
        if (shutterSpeedChangedEvent.type != String.class)
            return;
        if (shutterSpeedChangedEvent.key == parameter.getKey())
            valueTextView.setText(shutterSpeedChangedEvent.newValue);
    }
}
