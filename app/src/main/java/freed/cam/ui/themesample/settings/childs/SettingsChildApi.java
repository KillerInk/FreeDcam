package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.util.AttributeSet;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.cam.events.ValueChangedEvent;
import freed.settings.SettingsManager;

public class SettingsChildApi extends  SettingsChildMenu {
    public SettingsChildApi(Context context) {
        super(context);
    }

    public SettingsChildApi(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
    }

    public SettingsChildApi(Context context, ParameterInterface parameter) {
        super(context, parameter);
    }

    public SettingsChildApi(Context context, ParameterInterface parameter, int headerid, int descriptionid) {
        super(context, parameter, headerid, descriptionid);
    }

    public SettingsChildApi(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringValueChanged(SwichCameraFragmentEvent value) {
        onStringValueChanged(SettingsManager.getInstance().getCamApi());
    }

    @Override
    public void onStringValueChanged(ValueChangedEvent<String> value) {

    }
}
