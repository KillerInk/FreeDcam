package freed.cam.event.capture;

import freed.cam.event.MyEvent;

public interface CaptureStateChangedEvent extends MyEvent
{
    void onCaptureStateChanged(CaptureStates states);
}
