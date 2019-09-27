package freed.cam.events;

import freed.cam.apis.basecamera.modules.ModuleHandlerAbstract;

public class CaptureStateChangedEvent {
    public final ModuleHandlerAbstract.CaptureStates captureState;

    public CaptureStateChangedEvent(ModuleHandlerAbstract.CaptureStates captureState)
    {
        this.captureState = captureState;
    }
}
