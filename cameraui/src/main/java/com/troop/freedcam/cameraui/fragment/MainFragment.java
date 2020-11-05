package com.troop.freedcam.cameraui.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.troop.freedcam.camera.featuredetector.Camera1FeatureDetectorTask;
import com.troop.freedcam.camera.featuredetector.Camera2FeatureDetectorTask;
import com.troop.freedcam.cameraui.BuildConfig;
import com.troop.freedcam.cameraui.R;
import com.troop.freedcam.cameraui.adapter.DisableTouchViewPagerAdapter;
import com.troop.freedcam.cameraui.databinding.MainFragmentBinding;
import com.troop.freedcam.cameraui.service.OrientationManager;
import com.troop.freedcam.cameraui.viewmodels.MainViewModel;
import com.troop.freedcam.settings.SettingsManager;
import com.troop.freedcam.utils.Log;
import com.troop.freedcam.utils.PermissionManager;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();
    private MainViewModel mViewModel;
    private MainFragmentBinding mainFragmentBinding;
    private CameraFragmentManager cameraFragmentManager;
    private PermissionManager permissionManager;
    private OrientationManager orientationManager;


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (!SettingsManager.getInstance().isInit()) {
            SettingsManager.getInstance().init();
        }
        permissionManager = new PermissionManager(getActivity());
        orientationManager = new OrientationManager();
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        mainFragmentBinding.setMainViewModel(mViewModel);
        DisableTouchViewPagerAdapter disableTouchViewPagerAdapter = new DisableTouchViewPagerAdapter(getFragmentManager());
        mainFragmentBinding.disableTouchViewPager.setAdapter(disableTouchViewPagerAdapter);
        mainFragmentBinding.disableTouchViewPager.setCurrentItem(1);
        cameraFragmentManager = new CameraFragmentManager(getChildFragmentManager(),R.id.cameraFragmentContainer,getContext(),permissionManager);
        return mainFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");
        orientationManager.Start();
        if (permissionManager.isPermissionGranted(PermissionManager.Permissions.SdCard_Camera)) {
            Log.d(TAG, "sd and camera permissiong granted");
            if (!SettingsManager.getInstance().getAreFeaturesDetected() || SettingsManager.getInstance().appVersionHasChanged(BuildConfig.VERSION_CODE)) {
                runFeatureDetector();
            } else {
                Log.d(TAG, "camerfragmentmanager resume");
                cameraFragmentManager.onResume();
            }
        }
        else {
            Log.d(TAG, "request perrmissions for sd and camera");
            permissionManager.requestPermission(PermissionManager.Permissions.SdCard_Camera);
        }
    }

    private void runFeatureDetector() {
        new Thread(() -> {
            Log.d(TAG, "StartFeatureDetector");
            SettingsManager.getInstance().setCamApi(SettingsManager.API_SONY);
            Camera2FeatureDetectorTask task = null;
            Camera1FeatureDetectorTask task1 = null;
            if (Build.VERSION.SDK_INT >= 21) {
                task = new Camera2FeatureDetectorTask();
                task.detect();
            }
            task1 = new Camera1FeatureDetectorTask();
            task1.detect();
            if (SettingsManager.getInstance().hasCamera2Features()) {
                if (task.hwlvl == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
                    SettingsManager.getInstance().setCamApi(SettingsManager.API_1);
                else
                    SettingsManager.getInstance().setCamApi(SettingsManager.API_2);
            }
            SettingsManager.getInstance().setAppVersion(BuildConfig.VERSION_CODE);
            SettingsManager.getInstance().setAreFeaturesDetected(true);
            SettingsManager.getInstance().save();
            new Handler(Looper.getMainLooper()).post(() -> cameraFragmentManager.onResume());
        }).start();
    }

    @Override
    public void onPause() {
        super.onPause();
        orientationManager.Stop();
        cameraFragmentManager.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraFragmentManager.destroy();
        SettingsManager.getInstance().release();
    }
}