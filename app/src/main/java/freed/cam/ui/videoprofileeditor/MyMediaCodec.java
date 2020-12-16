package freed.cam.ui.videoprofileeditor;

import android.media.MediaCodecInfo;

import java.util.HashMap;
import java.util.List;

public class MyMediaCodec {

    private String codecName;

    private String types[];
    private HashMap<String,MediaCodecInfo.CodecProfileLevel> profileLevelList;

    public MyMediaCodec(String name) {
        this.codecName = name;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public void setProfileLevelList(HashMap<String,MediaCodecInfo.CodecProfileLevel> profileLevelList) {
        this.profileLevelList = profileLevelList;
    }

    public HashMap<String,MediaCodecInfo.CodecProfileLevel> getProfileLevelList() {
        return profileLevelList;
    }

    public String getCodecName() {
        return codecName;
    }

    public String[] getTypes() {
        return types;
    }
}
