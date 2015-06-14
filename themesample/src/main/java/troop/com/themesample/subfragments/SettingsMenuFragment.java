package troop.com.themesample.subfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.troop.freedcam.i_camera.AbstractCameraUiWrapper;
import com.troop.freedcam.ui.AbstractFragment;
import com.troop.freedcam.ui.AppSettingsManager;
import com.troop.freedcam.ui.I_Activity;

import troop.com.themesample.R;
import troop.com.themesample.views.menu.MenuItemTheme;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsMenuFragment extends AbstractFragment
{

    AbstractCameraUiWrapper wrapper;
    AppSettingsManager appSettingsManager;
    I_Activity i_activity;
    View view;
    TextView closeTab;
    LinearLayout left_Holder;
    LinearLayout right_Holder;

    MenuItemTheme themeItem;

    View.OnClickListener onSettingsClickListner;

    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity, View.OnClickListener onSettingsClickListner)
    {
        SetStuff(appSettingsManager, i_activity);
        this.onSettingsClickListner = onSettingsClickListner;
    }

    @Override
    public void SetCameraUIWrapper(AbstractCameraUiWrapper wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity)
    {
        this.appSettingsManager = appSettingsManager;
        this.i_activity = i_activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.settingsmenufragment, container, false);
        this.closeTab = (TextView)view.findViewById(R.id.textView_Close);
        closeTab.setOnClickListener(onSettingsClickListner);
        right_Holder = (LinearLayout)view.findViewById(R.id.right_holder);
        left_Holder = (LinearLayout)view.findViewById(R.id.left_holder);
        themeItem = (MenuItemTheme)view.findViewById(R.id.MenuItemTheme);
        themeItem.SetStuff(i_activity,appSettingsManager, AppSettingsManager.SETTING_Theme);
        setWrapper();
        return view;
    }

    private void setWrapper()
    {
        themeItem.SetParameter(wrapper.camParametersHandler.ThemeList);
    }

    View.OnClickListener onLeftViewClick = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {

        }
    };
}
