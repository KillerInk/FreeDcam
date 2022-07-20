package freed.cam.ui.themenextgen.adapter.customclicks;

import android.widget.CompoundButton;

import freed.cam.apis.basecamera.CameraThreadHandler;

public class AspectRatioCheckedChanged implements CompoundButton.OnCheckedChangeListener{
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CameraThreadHandler.restartPreviewAsync();
    }
}
