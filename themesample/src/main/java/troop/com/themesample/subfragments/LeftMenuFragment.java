package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItemBayerFormat;
import troop.com.themesample.views.menu.MenuItemSDSave;
import troop.com.themesample.views.uichilds.UiSettingsChild;
import troop.com.themesample.views.menu.MenuItemTheme;

/**
 * Created by troop on 15.06.2015.
 */
public class LeftMenuFragment extends AbstractFragment  implements Interfaces.I_MenuItemClick
{
    MenuItemTheme themeItem;
    MenuItemBayerFormat bayerFormatItem;
    troop.com.themesample.views.menu.MenuItem pictureSize;
    MenuItemSDSave sdSave;

    Interfaces.I_MenuItemClick onMenuItemClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.leftmenufragment, container, false);
        setWrapper();
        return view;
    }

    private void setWrapper()
    {
        themeItem = (MenuItemTheme)view.findViewById(R.id.MenuItemTheme);
        themeItem.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_Theme);
        themeItem.SetParameter(wrapper.camParametersHandler.ThemeList);
        themeItem.SetMenuItemListner(this);

        bayerFormatItem = (MenuItemBayerFormat)view.findViewById(R.id.MenuItemBayerFormat);
        bayerFormatItem.SetStuff(i_activity,appSettingsManager,"");
        bayerFormatItem.SetParameter(wrapper.camParametersHandler.PictureFormat);
        bayerFormatItem.SetMenuItemListner(this);

        pictureSize = (troop.com.themesample.views.menu.MenuItem)view.findViewById(R.id.MenuItemPicSize);
        pictureSize.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_PICTURESIZE);
        pictureSize.SetParameter(wrapper.camParametersHandler.PictureSize);
        pictureSize.SetMenuItemListner(this);

        sdSave = (MenuItemSDSave)view.findViewById(R.id.MenuItemSDSave);
        sdSave.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_EXTERNALSD);
        sdSave.SetCameraUiWrapper(wrapper);
        sdSave.SetMenuItemListner(this);
    }

    public void SetMenuItemClickListner(Interfaces.I_MenuItemClick menuItemClick)
    {
        this.onMenuItemClick = menuItemClick;
    }



    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onMenuItemClick(item, true);
    }
}
