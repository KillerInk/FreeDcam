package freed.cam.event;

import java.util.ArrayList;
import java.util.List;

import freed.utils.Log;

public abstract class BaseEventHandler<E extends MyEvent> implements BaseEventInterface<E>
{
    private final String TAG = BaseEventHandler.class.getSimpleName();
    protected List<E> eventListners;

    public BaseEventHandler()
    {
        eventListners = new ArrayList<>();
    }

    @Override
    public void setEventListner(E listner) {
        Log.d(TAG, "set EventListner : " + listner.getClass().getSimpleName());
        if (!eventListners.contains(listner))
            eventListners.add(listner);
    }

    @Override
    public void removeEventListner(E listner) {
        Log.d(TAG, "remove EventListner : " + listner.getClass().getSimpleName());
        if (eventListners.contains(listner))
            eventListners.remove(listner);
    }
}
