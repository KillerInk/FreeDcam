package freed.cam.events;

public class UserMessageEvent {
    public final boolean asToast;
    public final  String msg;

    public UserMessageEvent(String msg, boolean asToast)
    {
        this.asToast = asToast;
        this.msg = msg;
    }
}
