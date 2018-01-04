package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;

import freed.cam.apis.basecamera.parameters.ParameterInterface;
import freed.cam.ui.themesample.cameraui.ManualButton;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

/**
 * Created by troop on 03.08.2017.
 */

public class ManualButtonToneCurve extends ManualButton
{
    public ManualButtonToneCurve(Context context, ParameterInterface parameter, int drawableImg) {
        super(context, parameter, drawableImg);
    }
}
