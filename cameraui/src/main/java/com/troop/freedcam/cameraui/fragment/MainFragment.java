package com.troop.freedcam.cameraui.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.adapter.DisableTouchViewPagerAdapter;
import com.troop.freedcam.cameraui.databinding.MainFragmentBinding;
import com.troop.freedcam.cameraui.viewmodels.MainViewModel;

public class MainFragment extends Fragment {

    private MainViewModel mViewModel;
    private MainFragmentBinding mainFragmentBinding;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mainFragmentBinding.setMainViewModel(mViewModel);
        DisableTouchViewPagerAdapter disableTouchViewPagerAdapter = new DisableTouchViewPagerAdapter(getFragmentManager());
        mainFragmentBinding.disableTouchViewPager.setAdapter(disableTouchViewPagerAdapter);
        mainFragmentBinding.disableTouchViewPager.setCurrentItem(1);

        return mainFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

}