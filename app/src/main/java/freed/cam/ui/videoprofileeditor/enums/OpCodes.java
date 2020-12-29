package freed.cam.ui.videoprofileeditor.enums;

import Camera2EXT.OpModes;
import camera2_hidden_keys.lg.Opcode;
import camera2_hidden_keys.xiaomi.OpCode;

public enum  OpCodes {

    off(-1),
    disabled(0),
    eis_realtime(OpModes.OP_RealTimeEIS),
    eis_lookahead(OpModes.OP_LookAheadEIS),
    eis_videnahnc60(OpModes.OP_VidHanceEIS60),
    lg_hdr10(Opcode.LG_VIDEO_HDR),
    lg_preview_hdr10(Opcode.LG_VIDEO_HDR_PREVIEW),
    qbc_hdr(OpModes.qbcHDR),
    xiaomi_hdr10(OpCode.SESSION_OPERATION_MODE_HDR10),
    xiaomi_supereis(OpCode.SESSION_OPERATION_MODE_VIDEO_SUPEREIS),
    xiaomi_supereispro(OpCode.SESSION_OPERATION_MODE_VIDEO_SUPEREISPRO);



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
