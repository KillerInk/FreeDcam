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
import com.troop.freedcam.cameraui.databinding.CameraUiFragmentBinding;
import com.troop.freedcam.cameraui.viewmodels.CameraUiViewModel;

public class CameraUiFragment extends Fragment {

    private CameraUiViewModel mViewModel;
    private CameraUiFragmentBinding cameraUiFragmentBinding;

    public static CameraUiFragment newInstance() {
        return new CameraUiFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        cameraUiFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.camera_ui_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(CameraUiViewModel.class);
        cameraUiFragmentBinding.setCameraUiViewModel(mViewModel);
        //bind manuals
        cameraUiFragmentBinding.cameraUiManuals.manualBurst.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.burst));
        cameraUiFragmentBinding.cameraUiManuals.manualContrast.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.contrast));
        cameraUiFragmentBinding.cameraUiManuals.manualExposurecompensation.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.ev));
        cameraUiFragmentBinding.cameraUiManuals.manualFnum.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.fnum));
        cameraUiFragmentBinding.cameraUiManuals.manualFx.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.fx));
        cameraUiFragmentBinding.cameraUiManuals.manualIso.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.iso));
        cameraUiFragmentBinding.cameraUiManuals.manualSaturation.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.saturation));
        cameraUiFragmentBinding.cameraUiManuals.manualSharpness.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.sharpness));
        cameraUiFragmentBinding.cameraUiManuals.manualFocus.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.focus));
        cameraUiFragmentBinding.cameraUiManuals.manualShift.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.shift));
        cameraUiFragmentBinding.cameraUiManuals.manualShutter.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.shutter));
        cameraUiFragmentBinding.cameraUiManuals.manualTonecurve.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.tonecurve));
        cameraUiFragmentBinding.cameraUiManuals.manualWhitebalance.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.wb));
        cameraUiFragmentBinding.cameraUiManuals.manualZoom.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.zoom));
        //
        return cameraUiFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}