package troop.com.themesample.views.uichilds;

import android.content.Context;
import android.util.AttributeSet;

import com.troop.freedcam.utils.StringUtils;

import troop.com.themesample.subfragments.Interfaces;

/**
 * Created by troop on 09.09.2015.
 */
public class UiSettingsFocusPeak extends UiSettingsChild implements Interfaces.I_MenuItemClick {
    public UiSettingsFocusPeak(Context context) {
        super(context);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UiSettingsFocusPeak(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void SetMenuItemListner(Interfaces.I_MenuItemClick menuItemClick) {
        super.SetMenuItemListner(this);
    }

    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment)
    {
        if (parameter.GetValue().equals(StringUtils.ON))
            parameter.SetValue(StringUtils.OFF,false);
        else
            parameter.SetValue(StringUtils.ON,false);
    }
}
