package freed.cam.ui.videoprofileeditor.enums;

public enum HdrModes {
    off(0),
    hlg(1),
    pq(2);

    HdrModes(int value)
    {
        this.value = value;
    }
    private final int value;
    public int GetInt()
    {
        return value;
    }
}
