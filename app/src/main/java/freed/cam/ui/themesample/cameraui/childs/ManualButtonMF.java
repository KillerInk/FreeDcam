package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;

import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import com.troop.freedcam.eventbus.events.FocusPositionChangedEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.ui.themesample.cameraui.ManualButton;

/**
 * Created by KillerInk on 23.01.2018.
 */

public class ManualButtonMF extends ManualButton {
    public ManualButtonMF(Context context, ParameterInterface parameter, int drawableImg) {
        super(context, parameter, drawableImg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFocusPositonChanged(FocusPositionChangedEvent focusPositionChangedEvent)
    {
        if (focusPositionChangedEvent.type != String.class)
            return;
        if (focusPositionChangedEvent.key == parameter.getKey())
            valueTextView.setText(focusPositionChangedEvent.newValue);
    }
}
