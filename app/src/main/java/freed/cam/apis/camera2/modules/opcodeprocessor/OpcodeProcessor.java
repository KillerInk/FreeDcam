package freed.cam.apis.camera2.modules.opcodeprocessor;

import android.hardware.camera2.CameraCaptureSession;

public interface OpcodeProcessor {
    void createOpCodeSession(CameraCaptureSession.StateCallback recordingSessionCallback);
    void applyOpCodeToSession();
    void prepareRecording();
    void startRecording();
    void stopRecording();
}
