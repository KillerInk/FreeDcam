package freed.cam.events;

import org.greenrobot.eventbus.EventBus;

public class EventBusHelper {

    public static void register(Object ob)
    {
        if (!EventBus.getDefault().isRegistered(ob))
            EventBus.getDefault().register(ob);
    }

    public static void unregister(Object ob)
    {
        if (EventBus.getDefault().isRegistered(ob))
            EventBus.getDefault().unregister(ob);
    }

    public static void post(Object ob)
    {
        EventBus.getDefault().post(ob);
    }
}
