package freed.cam.apis.basecamera.parameters.manual;

import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingKeys;

public class ZebraManualLowParameter extends ZebraManualParameter {
    public ZebraManualLowParameter(SettingKeys.Key key, PreviewController previewController) {
        super(key, previewController);
    }

    @Override
    protected void setValue(int valueToSet, boolean setToCamera) {
        super.setValue(valueToSet, setToCamera);
        String val = stringvalues[valueToSet];
        settingMode.set(val);
        float t = Float.parseFloat(val) * 0.0001f;
        previewController.setZebraLow(t);
    }
}
