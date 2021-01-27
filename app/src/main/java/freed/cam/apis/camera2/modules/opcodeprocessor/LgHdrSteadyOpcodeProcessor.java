package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.lg.CaptureRequestLg;
import camera2_hidden_keys.xiaomi.CaptureRequestXiaomi;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LgHdrSteadyOpcodeProcessor extends BaseOpcodeProcessor {

    public LgHdrSteadyOpcodeProcessor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
    }

    @Override
    public void applyOpCodeToSession() {

    }

    @Override
    public void prepareRecording() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_EIS_END_STREAM,(byte)CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_PREPARE,false);
    }

    @Override
    public void startRecording() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_EIS_END_STREAM,(byte)CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_START,false);
    }

    @Override
    public void stopRecording() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_EIS_END_STREAM,(byte)CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_STOP,false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
