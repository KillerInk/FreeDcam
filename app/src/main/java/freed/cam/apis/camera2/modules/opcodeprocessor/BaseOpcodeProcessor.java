package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.hardware.camera2.CameraCaptureSession;
import android.os.Build;

import androidx.annotation.RequiresApi;

import freed.cam.apis.camera2.CaptureSessionHandler;
import freed.cam.ui.videoprofileeditor.enums.OpCodes;
import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.N)
public abstract class BaseOpcodeProcessor implements OpcodeProcessor
{
    private final String TAG = BaseOpcodeProcessor.class.getSimpleName();
    protected CaptureSessionHandler captureSessionHandler;
    protected OpCodes active_op;

    public BaseOpcodeProcessor(CaptureSessionHandler captureSessionHandler, OpCodes opCodes)
    {
        this.captureSessionHandler = captureSessionHandler;
        this.active_op = opCodes;
    }

    @Override
    public void createOpCodeSession(CameraCaptureSession.StateCallback recordingSessionCallback) {
        Log.d(TAG,"createOpcodeSession " + active_op.name() + ":"+ active_op.GetInt());
        captureSessionHandler.CreateCustomCaptureSession(active_op.GetInt(),recordingSessionCallback);
    }

}
