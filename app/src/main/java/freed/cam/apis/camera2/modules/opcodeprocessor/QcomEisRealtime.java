package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.devices.pocof2.CaptureRequestDump;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QcomEisRealtime extends BaseOpcodeProcessor {

    public QcomEisRealtime(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
    }

    @Override
    public void applyOpCodeToSession() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestDump.org_quic_camera_eisrealtime_Enabled,(byte)1,false);
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
