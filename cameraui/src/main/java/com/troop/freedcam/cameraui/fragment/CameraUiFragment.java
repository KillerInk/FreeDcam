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
import com.troop.freedcam.eventbus.EventBusHelper;

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
        cameraUiFragmentBinding.cameraUiManuals.setVisibilityEnableModel(mViewModel.getManualControlsHolderModel());
        cameraUiFragmentBinding.cameraUiRightBar.setShutterButtonModel(mViewModel.getShutterButtonModel());
        //bind manuals
        cameraUiFragmentBinding.cameraUiManuals.manualBurst.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.burst));
        cameraUiFragmentBinding.cameraUiManuals.manualContrast.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.contrast));
        cameraUiFragmentBinding.cameraUiManuals.manualExposurecompensation.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.ev));
        cameraUiFragmentBinding.cameraUiManuals.manualFnum.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.fnum));
        cameraUiFragmentBinding.cameraUiManuals.manualFx.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.fx));
        cameraUiFragmentBinding.cameraUiManuals.manualIso.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.iso));
        cameraUiFragmentBinding.cameraUiManuals.manualSaturation.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.saturation));
        cameraUiFragmentBinding.cameraUiManuals.manualSharpness.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.sharpness));
        cameraUiFragmentBinding.cameraUiManuals.manualFocus.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.focus));
        cameraUiFragmentBinding.cameraUiManuals.manualShift.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.shift));
        cameraUiFragmentBinding.cameraUiManuals.manualShutter.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.shutter));
        cameraUiFragmentBinding.cameraUiManuals.manualTonecurve.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.tonecurve));
        cameraUiFragmentBinding.cameraUiManuals.manualWhitebalance.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.wb));
        cameraUiFragmentBinding.cameraUiManuals.manualZoom.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.ManualButtons.zoom));
        //bind leftbar
        cameraUiFragmentBinding.cameraUiLeftBar.histogramButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.histogram));
        cameraUiFragmentBinding.cameraUiLeftBar.clippingButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.clipping));
        cameraUiFragmentBinding.cameraUiLeftBar.whitebalanceModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.wb));
        cameraUiFragmentBinding.cameraUiLeftBar.isoModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.iso));
        cameraUiFragmentBinding.cameraUiLeftBar.flashModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.flash));
        cameraUiFragmentBinding.cameraUiLeftBar.focusModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.focus));
        cameraUiFragmentBinding.cameraUiLeftBar.aepriotiryModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.aepriority));
        cameraUiFragmentBinding.cameraUiLeftBar.exposureModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.exposure));
        cameraUiFragmentBinding.cameraUiLeftBar.contshotModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.contshot));
        cameraUiFragmentBinding.cameraUiLeftBar.hdrModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.hdr));
        cameraUiFragmentBinding.cameraUiLeftBar.nightModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.night));
        cameraUiFragmentBinding.cameraUiLeftBar.pictureModeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.LeftbarButtons.imageformat));

        //bind rightbar
        cameraUiFragmentBinding.cameraUiRightBar.closeButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.close));
        cameraUiFragmentBinding.cameraUiRightBar.moduleswitchButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.module));
        cameraUiFragmentBinding.cameraUiRightBar.focuspeakButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.focuspeak));
        cameraUiFragmentBinding.cameraUiRightBar.selftimerButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.selftimer));
        cameraUiFragmentBinding.cameraUiRightBar.aelockButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.aelock));
        cameraUiFragmentBinding.cameraUiRightBar.cameraSwitchButton.manualButtonBinding.setVisibilityEnableModel(mViewModel.getManualButtonModel(CameraUiViewModel.RightbarButtons.cameraswitch));

        return cameraUiFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBusHelper.register(mViewModel);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBusHelper.unregister(mViewModel);
    }
}