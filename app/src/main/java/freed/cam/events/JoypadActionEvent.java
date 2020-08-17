package freed.cam.events;

public class JoypadActionEvent {

    public static class Move
    {
        public final int x;
        public final int y;
        public Move(int x, int y)
        {
            this.x = x;
            this.y = y;
        }
    }

    public static class Touch
    {
        public final boolean up;

        public Touch(boolean up)
        {
            this.up = up;
        }
    }

    public static void fireJoypadMove(int x, int y)
    {
        EventBusHelper.post(new Move(x, y));
    }

    public static void fireJoypadTouch(boolean up)
    {
        EventBusHelper.post(new Touch(up));
    }

}
