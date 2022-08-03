package freed.cam.apis.camera2.parameters.modes;

import com.troop.freedcam.R;

import freed.FreedApplication;
import freed.cam.ActivityFreeDcamMain;
import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.basecamera.parameters.modes.FocusPeakMode;
import freed.cam.histogram.HistogramController;
import freed.cam.previewpostprocessing.PreviewPostProcessingModes;
import freed.settings.SettingKeys;
import freed.settings.mode.ApiBooleanSettingMode;
import freed.settings.mode.SettingMode;

public class HistogramQcom extends FocusPeakMode {

    private HistogramController histogramController;
    public HistogramQcom(CameraWrapperInterface cameraUiWrapper, SettingKeys.Key<ApiBooleanSettingMode> settingMode) {
        super(cameraUiWrapper, settingMode);
        this.settingMode = SettingKeys.HISTOGRAM_STATS_QCOM;
        this.histogramController = ActivityFreeDcamMain.histogramController();
    }

    @Override
    public void setStringValue(String valueToSet, boolean setToCamera)
    {
        currentString = valueToSet;
        boolean toset = valueToSet.equals(FreedApplication.getStringFromRessources(R.string.on_));
        histogramController.enableCameraHistogram(toset);
        settingsManager.get(settingMode).set(toset);
        fireStringValueChanged(valueToSet);

    }

    @Override
    public void set(boolean bool) {
        histogramController.enableCameraHistogram(bool);
        settingsManager.get(settingMode).set(bool);
        fireStringValueChanged(getStringValue());
    }

    @Override
    public ViewState getViewState() {
        return ViewState.Visible;
    }
}
