package freed.cam.ui.videoprofileeditor.enums;

import Camera2EXT.OpModes;
import camera2_hidden_keys.lg.Opcode;
import camera2_hidden_keys.xiaomi.OpCode;

public enum  OpCodes {

    off(-1),
    disbled(0),
    xiaomi_hdr10(OpCode.SESSION_OPERATION_MODE_HDR10),
    lg_hdr10(Opcode.LG_VIDEO_HDR),
    eis_realtime(OpModes.OP_RealTimeEIS),
    eis_lookahead(OpModes.OP_LookAheadEIS),
    eis_videnahnc60(OpModes.OP_VidHanceEIS60),
    qbc_hdr(OpModes.qbcHDR);



    OpCodes(int value)
    {
        this.value = value;
    }
    private int value;
    public int GetInt()
    {
        return value;
    }
    public static OpCodes get(int val)
    {
        for (OpCodes audio : OpCodes.values())
        {
            if (audio.GetInt() == val)
                return audio;
        }
        return off;
    }
}
