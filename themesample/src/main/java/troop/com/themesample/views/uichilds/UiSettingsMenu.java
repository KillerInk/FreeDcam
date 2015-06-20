package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 14.06.2015.
 */
public class UiSettingsMenu extends UiSettingsChild {
    public UiSettingsMenu(Context context) {
        super(context);
    }

    public UiSettingsMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        super.inflateTheme(inflater);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter) {
        super.SetParameter(parameter);
    }

    @Override
    public void setTextToTextBox(AbstractModeParameter parameter) {

    }


    @Override
    public void onValueChanged(String val) {

    }

    @Override
    public void onIsSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onIsSetSupportedChanged(boolean isSupported) {

    }

    @Override
    public void onValuesChanged(String[] values) {

    }

    @Override
    public String ModuleChanged(String module) {
        return null;
    }

    @Override
    public void ParametersLoaded() {

    }
}
