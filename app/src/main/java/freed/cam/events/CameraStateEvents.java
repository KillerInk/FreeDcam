package freed.cam.events;

public class CameraStateEvents {

    public static class CameraOpenEvent
    {}

    public static class CameraOpenFinishEvent
    {
    }

    public static class CameraCloseEvent
    {}

    public static class PreviewOpenEvent
    {}

    public static class PreviewCloseEvent
    {}

    public static class CameraErrorEvent
    {
        public final String msg;

        public CameraErrorEvent(String msg)
        {
            this.msg = msg;
        }
    }
}
