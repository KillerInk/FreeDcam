package freed.cam.ui.themesample.cameraui.childs;

import android.content.Context;

import com.troop.freedcam.camera.basecamera.parameters.AbstractParameter;
import com.troop.freedcam.camera.basecamera.parameters.ParameterInterface;
import freed.cam.events.ValueChangedEvent;
import freed.cam.ui.themesample.cameraui.ManualButton;
import com.troop.freedcam.utils.Log;

/**
 * Created by troop on 03.08.2017.
 */

public class ManualButtonToneCurve extends ManualButton
{

    private final String TAG = ManualButtonToneCurve.class.getSimpleName();

    public ManualButtonToneCurve(Context context, ParameterInterface parameter, int drawableImg) {
        super(context, parameter, drawableImg);
    }

    @Override
    public void onViewStateChanged(ValueChangedEvent<AbstractParameter.ViewState> viewStateValueChangedEvent) {
        super.onViewStateChanged(viewStateValueChangedEvent);
        if (viewStateValueChangedEvent.key == parameter.getKey())
            Log.d(TAG, "onViewStateChanged " + viewStateValueChangedEvent.newValue.toString());
    }
}
