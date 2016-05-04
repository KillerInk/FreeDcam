package com.freedcam.ui.themesample.subfragments;

import com.freedcam.ui.themesample.views.uichilds.UiSettingsChild;

/**
 * Created by troop on 15.06.2015.
 */
public class Interfaces
{
    public interface I_MenuItemClick
    {
        void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment);
    }

    public interface I_CloseNotice
    {
        void onClose(String value);
    }
}
