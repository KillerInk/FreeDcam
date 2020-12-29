package freed.cam.ui.videoprofileeditor.enums;

public enum VideoCodecs {
    H263(1),
    H264(2),
    MPEG_4_SP(3),
    VP8(4),
    HEVC(5);

    VideoCodecs(int value)
    {
        this.value = value;
    }
    private int value;
    public int GetInt()
    {
        return value;
    }
}
