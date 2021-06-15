package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.qcom.CaptureRequestQcom;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QcomEisOpcodeProcessor extends BaseOpcodeProcessor {

    public QcomEisOpcodeProcessor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
    }

    @Override
    public void applyOpCodeToSession() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestQcom.eis_mode, (byte) 1,false);
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
