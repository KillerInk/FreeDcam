package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.events.FocusPositionChangedEvent;
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
