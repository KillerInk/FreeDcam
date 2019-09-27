package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.events.IsoChangedEvent;
import freed.cam.ui.themesample.cameraui.ManualButton;

public class ManualButtonIso extends ManualButton {
    public ManualButtonIso(Context context, ParameterInterface parameter, int drawableImg) {
        super(context, parameter, drawableImg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIsoChangedEvent(IsoChangedEvent isoChangedEvent)
    {
        if (isoChangedEvent.type != String.class)
            return;
        if (isoChangedEvent.key == parameter.getKey())
            valueTextView.setText(isoChangedEvent.newValue);
    }
}
