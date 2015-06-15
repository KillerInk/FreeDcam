package troop.com.themesample.subfragments;

import troop.com.themesample.views.UiSettingsChild;
import troop.com.themesample.views.menu.MenuItem;

/**
 * Created by troop on 15.06.2015.
 */
public class Interfaces
{
    public interface I_MenuItemClick
    {
        void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment);
    }

    protected interface I_CloseNotice
    {
        void onClose(String value);
    }
}
