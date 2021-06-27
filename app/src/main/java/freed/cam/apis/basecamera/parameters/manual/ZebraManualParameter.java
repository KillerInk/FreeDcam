package freed.cam.apis.basecamera.parameters.manual;

import freed.cam.apis.basecamera.parameters.AbstractParameter;
import freed.cam.previewpostprocessing.PreviewController;
import freed.settings.SettingKeys;

public class ZebraManualParameter extends AbstractParameter {

    protected PreviewController previewController;

    public ZebraManualParameter(SettingKeys.Key key, PreviewController previewController) {
        super(key);
        this.previewController = previewController;
    }
}
