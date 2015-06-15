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
import troop.com.themesample.views.menu.MenuItem;
import troop.com.themesample.views.menu.MenuItemTheme;

/**
 * Created by troop on 14.06.2015.
 */
public class SettingsMenuFragment extends AbstractFragment implements Interfaces.I_CloseNotice, Interfaces.I_MenuItemClick
{
    TextView closeTab;
    LinearLayout left_Holder;
    LinearLayout right_Holder;
    LeftMenuFragment leftMenuFragment;
    RightMenuFragment rightMenuFragment;
    ValuesMenuFragment valuesMenuFragment;

    View.OnClickListener onSettingsClickListner;

    final int VALUE_MENU_CLOSED = 0;
    final int VALUE_MENU_RIGHT_OPEN = 1;
    final int VALUE_MENU_LEFT_OPEN = 2;
    int value_menu_status = VALUE_MENU_CLOSED;

    MenuItem currentOpendItem;

    public void SetStuff(AppSettingsManager appSettingsManager, I_Activity i_activity, View.OnClickListener onSettingsClickListner)
    {
        SetStuff(appSettingsManager, i_activity);

        this.onSettingsClickListner = onSettingsClickListner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.view = inflater.inflate(R.layout.settingsmenufragment, container, false);
        this.closeTab = (TextView)view.findViewById(R.id.textView_Close);
        closeTab.setOnClickListener(onSettingsClickListner);
        right_Holder = (LinearLayout)view.findViewById(R.id.right_holder);
        left_Holder = (LinearLayout)view.findViewById(R.id.left_holder);
        setWrapper();
        return view;
    }

    private void setWrapper()
    {
        loadLeftFragment();
        loadRightFragment();
        value_menu_status = VALUE_MENU_CLOSED;
    }

    private void loadLeftFragment()
    {
        leftMenuFragment = new LeftMenuFragment();
        leftMenuFragment.SetStuff(appSettingsManager, i_activity);
        leftMenuFragment.SetCameraUIWrapper(wrapper);
        leftMenuFragment.SetMenuItemClickListner(this);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.left_holder, leftMenuFragment);
        transaction.commit();
    }

    private void loadRightFragment()
    {
        rightMenuFragment = new RightMenuFragment();
        rightMenuFragment.SetStuff(appSettingsManager, i_activity);
        rightMenuFragment.SetCameraUIWrapper(wrapper);
        rightMenuFragment.SetMenuItemClickListner(this);
        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
        transaction.replace(R.id.right_holder, rightMenuFragment);
        transaction.commit();
    }


    @Override
    public void onClose(String value)
    {
        currentOpendItem.SetValue(value);
        if (currentOpendItem instanceof MenuItemTheme)
            return;
        closeValueMenu();
    }

    private void closeValueMenu()
    {
        if (value_menu_status == VALUE_MENU_LEFT_OPEN) {
            loadLeftFragment();
        } else if (value_menu_status == VALUE_MENU_RIGHT_OPEN) {
            loadRightFragment();
        }
        currentOpendItem = null;
        value_menu_status = VALUE_MENU_CLOSED;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onMenuItemClick(MenuItem item, boolean fromLeftFragment)
    {
        if (currentOpendItem == item)
        {
            closeValueMenu();
            return;
        }
        currentOpendItem = item;

        valuesMenuFragment = new ValuesMenuFragment();
        valuesMenuFragment.SetMenuItem(item.GetValues(), this);

        if (fromLeftFragment)
        {
            value_menu_status = VALUE_MENU_RIGHT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
            transaction.replace(R.id.right_holder, valuesMenuFragment);
            transaction.commit();
        }
        else
        {
            value_menu_status = VALUE_MENU_LEFT_OPEN;
            android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter, R.anim.exit);
            transaction.replace(R.id.left_holder, valuesMenuFragment);
            transaction.commit();
        }
    }
}
