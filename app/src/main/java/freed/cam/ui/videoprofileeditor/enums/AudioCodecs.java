package freed.cam.ui.videoprofileeditor.enums;

public enum AudioCodecs {
    AMR_NB(1),
    AMR_WB(2),
    AAC(3),
    HE_AAC(4),
    AAC_ELD(5),
    VORBIS(6);


    AudioCodecs(int value)
    {
        this.value = value;
    }
    private int value;
    public int GetInt()
    {
        return value;
    }
}
