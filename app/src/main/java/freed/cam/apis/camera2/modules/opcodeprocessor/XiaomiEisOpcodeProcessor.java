package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.xiaomi.CaptureRequestXiaomi;
import freed.FreedApplication;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;

@RequiresApi(api = Build.VERSION_CODES.N)
public class XiaomiEisOpcodeProcessor extends BaseOpcodeProcessor {
    private SettingsManager settingsManager;
    public XiaomiEisOpcodeProcessor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
        settingsManager = FreedApplication.settingsManager();
    }

    @Override
    public void applyOpCodeToSession() {
        if (settingsManager.get(SettingKeys.XIAOMI_PRO_VIDEO_LOG).isSupported())
            captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.PRO_VIDEO_LOG_ENABLED, (byte) 1, false);
        /*captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.AUTOZOOM_SCALE_OFFSET, 0f, false);
        captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.AUTOZOOM_INPREVIEW, 0, false);*/
        captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON,false);
        captureSessionHandler.SetPreviewParameter(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF,false);
    }

    @Override
    public void prepareRecording() {
        if (settingsManager.get(SettingKeys.XIAOMI_VIDEO_RECORD_CONTROL).isSupported())
            captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.VIDEO_RECORD_CONTROL,CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_PREPARE,false);
    }

    @Override
    public void startRecording() {
        //captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.RECORDING_END_STREAM,(byte)0,false);
        if (settingsManager.get(SettingKeys.XIAOMI_VIDEO_RECORD_CONTROL).isSupported())
            captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.VIDEO_RECORD_CONTROL,CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_START,false);
    }


    @Override
    public void stopRecording() {
        //captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.RECORDING_END_STREAM,(byte)1,false);
        if (settingsManager.get(SettingKeys.XIAOMI_VIDEO_RECORD_CONTROL).isSupported())
            captureSessionHandler.SetPreviewParameter(CaptureRequestXiaomi.VIDEO_RECORD_CONTROL, CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_STOP, true);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
