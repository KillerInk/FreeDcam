package freed.cam.ui.themenextgen.adapter.customclicks;

import freed.cam.ActivityFreeDcamMain;
import freed.cam.ui.themenextgen.view.button.NextGenSettingButton;

public class RunFeatureDetectorClick implements NextGenSettingButton.NextGenSettingButtonClick {

    @Override
    public void onSettingButtonClick() {
        ActivityFreeDcamMain.cameraApiManager().runFeatureDetector();
    }
}
