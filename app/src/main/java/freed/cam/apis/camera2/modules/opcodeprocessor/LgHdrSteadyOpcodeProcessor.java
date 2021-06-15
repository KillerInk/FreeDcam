package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.hardware.camera2.CaptureRequest;
import android.os.Build;

import androidx.annotation.RequiresApi;

import camera2_hidden_keys.lg.CaptureRequestLg;
import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LgHdrSteadyOpcodeProcessor extends BaseOpcodeProcessor {

    public LgHdrSteadyOpcodeProcessor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes) {
        super(captureSessionHandler, opCodes);
    }

    @Override
    public void applyOpCodeToSession() {
        captureSessionHandler.SetPreviewParameter(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE_ON,false);
    }

    @Override
    public void prepareRecording() {
        //captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_EIS_END_STREAM,(byte)CaptureRequestXiaomi.VALUE_VIDEO_RECORD_CONTROL_PREPARE,false);
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_RECORD_MODE,1,false);
    }

    @Override
    public void startRecording() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_RECORDING_START_OF_STREAM,(byte)1,false);
    }

    @Override
    public void stopRecording() {
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_RECORDING_START_OF_STREAM,(byte)0,false);
        captureSessionHandler.SetPreviewParameter(CaptureRequestLg.KEY_EIS_END_STREAM,(byte)1,false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
