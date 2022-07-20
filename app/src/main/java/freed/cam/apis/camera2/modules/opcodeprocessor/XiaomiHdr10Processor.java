package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.xiaomi.CaptureRequestXiaomi;
import freed.FreedApplication;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.N)
public class XiaomiHdr10Processor extends BaseOpcodeProcessor {

    private final SettingsManager settingsManager;
    public XiaomiHdr10Processor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
        settingsManager = FreedApplication.settingsManager();
    }

    @Override
    public void applyOpCodeToSession() {
        if(settingsManager.get(SettingKeys.XIAOMI_PRO_VIDEO_LOG).isSupported())
            captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.PRO_VIDEO_LOG_ENABLED, (byte) 1, false);
    }

    @Override
    public void prepareRecording() {

    }

    @Override
    public void startRecording() {

    }

    @Override
    public void stopRecording() {

    }
}
