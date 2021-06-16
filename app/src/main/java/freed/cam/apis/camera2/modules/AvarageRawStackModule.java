package freed.cam.apis.camera2.modules;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import freed.cam.apis.basecamera.CameraWrapperInterface;
import freed.cam.apis.camera2.Camera2;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class AvarageRawStackModule extends RawStackPipe {

    public AvarageRawStackModule(Camera2 cameraUiWrapper, Handler mBackgroundHandler, Handler mainHandler) {
        super(cameraUiWrapper, mBackgroundHandler, mainHandler);
        name = ShortName();
    }

    @Override
    public String LongName() {
        return "Average";
    }

    @Override
    public String ShortName() {
        return "AVG";
    }

    @Override
    protected void internalTakePicture() {
        if (settingsManager.get(SettingKeys.forceRawToDng).get()) {
            if (settingsManager.get(SettingKeys.support12bitRaw).get())
                continouseRawCapture.startStackAvarage(BurstCounter.getBurstCount(), 2);
            else
                continouseRawCapture.startStackAvarage(BurstCounter.getBurstCount(), 4);
        }
        else
            continouseRawCapture.startStackAvarage(BurstCounter.getBurstCount(), 0);
    }
}
