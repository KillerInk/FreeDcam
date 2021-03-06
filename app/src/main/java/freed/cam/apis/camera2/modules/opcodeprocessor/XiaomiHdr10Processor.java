package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.xiaomi.CaptureRequestXiaomi;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.N)
public class XiaomiHdr10Processor extends BaseOpcodeProcessor {

    public XiaomiHdr10Processor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
    }

    @Override
    public void applyOpCodeToSession() {
        if(SettingsManager.get(SettingKeys.XIAOMI_PRO_VIDEO_LOG).isSupported())
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
