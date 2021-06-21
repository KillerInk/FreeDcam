package freed.cam.event.capture;

import freed.cam.event.BaseEventHandler;

public class CaptureStateChangedEventHandler extends BaseEventHandler<CaptureStateChangedEvent>
{
    public void fireCaptureStateChangedEvent(CaptureStates event)
    {
        for (CaptureStateChangedEvent listner : eventListners)
            if (listner == null)
                eventListners.remove(listner);
            else
                listner.onCaptureStateChanged(event);
    }
}
