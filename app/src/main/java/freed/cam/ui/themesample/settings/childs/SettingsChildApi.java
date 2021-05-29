package freed.cam.ui.themesample.settings.childs;

import android.content.Context;
import android.util.AttributeSet;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.events.SwichCameraFragmentEvent;
import freed.cam.events.ValueChangedEvent;
import freed.settings.SettingsManager;

@AndroidEntryPoint
public class SettingsChildApi extends  SettingsChildMenu {

    @Inject
    SettingsManager settingsManager;

    public SettingsChildApi(Context context) {
        super(context);
    }

    public SettingsChildApi(Context context, int headerid, int descriptionid) {
        super(context, headerid, descriptionid);
    }

    public SettingsChildApi(Context context, AbstractParameter parameter) {
        super(context, parameter);
    }

    public SettingsChildApi(Context context, AbstractParameter parameter, int headerid, int descriptionid) {
        super(context, parameter, headerid, descriptionid);
    }

    public SettingsChildApi(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStringValueChanged(SwichCameraFragmentEvent value) {
        //onStringValueChanged(settingsManager.getCamApi());
    }

    @Override
    public void onStringValueChanged(ValueChangedEvent<String> value) {

    }
}
