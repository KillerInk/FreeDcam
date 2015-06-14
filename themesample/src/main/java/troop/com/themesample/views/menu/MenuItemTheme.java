package troop.com.themesample.views.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.troop.freedcam.i_camera.parameters.AbstractModeParameter;

/**
 * Created by troop on 14.06.2015.
 */
public class MenuItemTheme extends MenuItem {
    public MenuItemTheme(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MenuItemTheme(Context context) {
        super(context);
    }

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    @Override
    protected void inflateTheme(LayoutInflater inflater) {
        super.inflateTheme(inflater);
    }

    @Override
    public void SetParameter(AbstractModeParameter parameter)
    {
        this.parameter = parameter;
        String s = appSettingsManager.GetTheme();
        if (s.equals("")) {
            s = "Classic";
            appSettingsManager.setString(settingsname,s);
        }
        valueText.setText(s);
    }
}
