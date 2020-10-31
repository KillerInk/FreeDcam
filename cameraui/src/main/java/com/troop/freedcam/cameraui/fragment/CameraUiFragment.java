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
        //bind leftbar
        cameraUiFragmentBinding.cameraUiLeftBar.histogramButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.histogram));
        cameraUiFragmentBinding.cameraUiLeftBar.clippingButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.clipping));
        cameraUiFragmentBinding.cameraUiLeftBar.whitebalanceModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.wb));
        cameraUiFragmentBinding.cameraUiLeftBar.isoModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.iso));
        cameraUiFragmentBinding.cameraUiLeftBar.flashModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.flash));
        cameraUiFragmentBinding.cameraUiLeftBar.focusModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.focus));
        cameraUiFragmentBinding.cameraUiLeftBar.exposureModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.exposure));
        cameraUiFragmentBinding.cameraUiLeftBar.contshotModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.contshot));
        cameraUiFragmentBinding.cameraUiLeftBar.hdrModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.hdr));
        cameraUiFragmentBinding.cameraUiLeftBar.nightModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.night));
        cameraUiFragmentBinding.cameraUiLeftBar.pictureModeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.imageformat));

        //bind rightbar
        cameraUiFragmentBinding.cameraUiRightBar.closeButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.close));
        cameraUiFragmentBinding.cameraUiRightBar.moduleswitchButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.module));
        cameraUiFragmentBinding.cameraUiRightBar.focuspeakButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.focuspeak));
        cameraUiFragmentBinding.cameraUiRightBar.selftimerButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.selftimer));
        cameraUiFragmentBinding.cameraUiRightBar.aelockButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.aelock));
        cameraUiFragmentBinding.cameraUiRightBar.cameraSwitchButton.manualButtonBinding.setManualButtonModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.cameraswitch));

        return cameraUiFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

}