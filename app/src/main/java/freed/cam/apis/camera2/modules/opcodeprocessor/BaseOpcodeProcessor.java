package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.hardware.camera2.CameraCaptureSession;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;

@RequiresApi(api = Build.VERSION_CODES.N)
public abstract class BaseOpcodeProcessor implements OpcodeProcessor
{
    protected CaptureSessionHandler captureSessionHandler;
    protected OpCodes active_op;

    public BaseOpcodeProcessor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes)
    {
        this.captureSessionHandler = captureSessionHandler;
        this.active_op = opCodes;
    }

    @Override
    public void createOpCodeSession(CameraCaptureSession.StateCallback recordingSessionCallback) {
        captureSessionHandler.setOPMODE(active_op.GetInt());
        captureSessionHandler.CreateCustomCaptureSession(recordingSessionCallback);
    }

}
