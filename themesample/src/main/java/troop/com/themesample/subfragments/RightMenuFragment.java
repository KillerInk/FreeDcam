package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;

import troop.com.themesample.R;
import troop.com.themesample.views.UiSettingsChild;
import troop.com.themesample.views.menu.MenuItem;
import troop.com.themesample.views.menu.MenuItemTheme;

/**
 * Created by troop on 15.06.2015.
 */
public class RightMenuFragment extends AbstractFragment implements Interfaces.I_MenuItemClick
{

    Interfaces.I_MenuItemClick onMenuItemClick;
    MenuItem scene;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.rightmenufragment, container, false);
        setWrapper();
        return view;
    }

    private void setWrapper()
    {
        scene = (MenuItem)view.findViewById(R.id.MenuItemScene);
        scene.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_Theme);
        scene.SetParameter(wrapper.camParametersHandler.SceneMode);
        scene.SetMenuItemListner(this);
    }

    public void SetMenuItemClickListner(Interfaces.I_MenuItemClick menuItemClick)
    {
        this.onMenuItemClick = menuItemClick;
    }



    @Override
    public void onMenuItemClick(UiSettingsChild item, boolean fromLeftFragment) {
        onMenuItemClick.onMenuItemClick(item, false);
    }
}
