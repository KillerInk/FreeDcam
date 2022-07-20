package freed.cam.ui.themenextgen.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.troop.freedcam.R;

import freed.cam.ui.themenextgen.adapter.SettingTabPagerAdapter;

public class NextGenCameraSettingFragment extends Fragment
{

    ViewPager settings_holder;
    SettingTabPagerAdapter settingTabPagerAdapter;
    TabLayout tabLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nextgen_setting_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        settings_holder = view.findViewById(R.id.setting_fragment_holder);
        settings_holder.setOffscreenPageLimit(3);
        tabLayout = view.findViewById(R.id.tab_layout);
        settingTabPagerAdapter = new SettingTabPagerAdapter(getParentFragmentManager());
        settings_holder.setAdapter(settingTabPagerAdapter);
        tabLayout.setupWithViewPager(settings_holder);

    }
}
