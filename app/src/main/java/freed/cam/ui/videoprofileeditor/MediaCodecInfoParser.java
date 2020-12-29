package freed.cam.ui.videoprofileeditor;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freed.utils.Log;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MediaCodecInfoParser
{
    private final String TAG = MediaCodecInfoParser.class.getSimpleName();
    public final static String hevc = "video/hevc";
    public final static String avc = "video/avc";
    public final static String av1 = "video/av1";

    public List<MyMediaCodec> getHevcCodecs()
    {
        return getCodecs(hevc);
    }

    public List<MyMediaCodec> getAvcCodecs()
    {
        return getCodecs(avc);
    }

    public List<MyMediaCodec> getAv1Codecs()
    {
        return getCodecs(av1);
    }

    private List<MyMediaCodec> getCodecs(String type)
    {
        List<MyMediaCodec> list =new ArrayList<>();
        int count  = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++)
        {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);

            if(info.isEncoder()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Log.d(TAG, "MediaCodecInfo Name:" + info.getName() + " hwaccel:" + info.isHardwareAccelerated());
                }
                else
                {
                    Log.d(TAG, "MediaCodecInfo Name:" + info.getName());
                }
                String types[] = info.getSupportedTypes();
                if (containType(types,type))
                {
                    MyMediaCodec codec = new MyMediaCodec(info.getName());
                    list.add(codec);
                    codec.setTypes(types);
                    MediaCodecInfo.CodecCapabilities codecCapabilities = info.getCapabilitiesForType(type);
                    HashMap<String,MediaCodecInfo.CodecProfileLevel> codecProfileLevels = new HashMap<>();
                    codec.setProfileLevelList(codecProfileLevels);
                    for (int t = 0; t < codecCapabilities.profileLevels.length; t++) {
                        MediaCodecInfo.CodecProfileLevel lvl = codecCapabilities.profileLevels[t];
                        String prof = getProfileString(type, lvl.profile);
                        String lvle = getLevelString(type,lvl.level);
                        codecProfileLevels.put(prof + " " + lvle, lvl);
                        Log.d(TAG, "Type: " + type + " profile: " + prof + " lvl: " + lvle);
                    }
                }
            }
        }
        return list;
    }

    public static String getProfileString(String type, int profile)
    {
        if (type.equals(hevc))
            return getHevcProfileString(profile);
        else if (type.equals(avc))
            return getAvcProfileString(profile);
        else if (type.equals(av1))
            return getAv1ProfileString(profile);
        return "";
    }

    public static String getLevelString(String type, int profile)
    {
        if (type.equals(hevc))
            return getHevcLevelString(profile);
        else if (type.equals(avc))
            return getAvcLevelString(profile);
        else if (type.equals(av1))
            return getAv1LevelString(profile);
        return "";
    }

    private boolean containType(String[] types, String type)
    {
        for (String t : types)
            if (t.equals(type))
                return true;
        return false;
    }

    public void logMediaCodecInfos()
    {
        int count  = MediaCodecList.getCodecCount();
        for (int i = 0; i < count; i++)
        {
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);

            if(info.isEncoder()) {
                Log.d(TAG, "MediaCodecInfo Name:" + info.getName() + " hwaccel:" /*+ info.isHardwareAccelerated()*/);
                String types[] = info.getSupportedTypes();
                for (String s : types) {
                    MediaCodecInfo.CodecCapabilities codecCapabilities = info.getCapabilitiesForType(s);
                    for (int t = 0; t < codecCapabilities.profileLevels.length; t++) {
                        MediaCodecInfo.CodecProfileLevel lvl = codecCapabilities.profileLevels[t];
                        if (s.equals(hevc))
                            Log.d(TAG, "Type: " + s + " profile: " + getHevcProfileString(lvl.profile) + " lvl: " + getHevcLevelString(lvl.level));
                        if (s.equals(avc))
                            Log.d(TAG, "Type: " + s + " profile: " + getAvcProfileString(lvl.profile) + " lvl: " + getAvcLevelString(lvl.level));
                    }
                }
            }
        }
    }

    private static String getHevcProfileString(int profile) {
        switch (profile) {
            case MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10:
                return "HEVCProfileMain10HDR10";
            case MediaCodecInfo.CodecProfileLevel.HEVCProfileMain:
                return "HEVCProfileMain";
            case MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10:
                return "HEVCProfileMain10";
            case MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10Plus:
                return "HEVCProfileMain10HDR10Plus";
            case MediaCodecInfo.CodecProfileLevel.HEVCProfileMainStill:
                return "HEVCProfileMainStill";
            default:
                return String.valueOf(profile);
        }
    }

    private static String getHevcLevelString(int profile)
    {
        switch (profile)
        {
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel1:
                return "HEVCMainTierLevel1";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel1:
                return "HEVCHighTierLevel1";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel2:
                return "HEVCMainTierLevel2";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel2:
                return "HEVCHighTierLevel2";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel21:
                return "HEVCMainTierLevel21";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel21:
                return "HEVCHighTierLevel21";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel3:
                return "HEVCMainTierLevel3";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel3:
                return "HEVCHighTierLevel3";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel31:
                return "HEVCMainTierLevel31";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel31:
                return "HEVCHighTierLevel31";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel4:
                return "HEVCMainTierLevel4";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel4:
                return "HEVCHighTierLevel4";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel41:
                return "HEVCMainTierLevel41";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel41:
                return "HEVCHighTierLevel41";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel5:
                return "HEVCMainTierLevel5";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel5:
                return "HEVCHighTierLevel5";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel51:
                return "HEVCMainTierLevel51";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel51:
                return "HEVCHighTierLevel51";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel52:
                return "HEVCMainTierLevel52";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel52:
                return "HEVCHighTierLevel52";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel6:
                return "HEVCMainTierLevel6";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel6:
                return "HEVCHighTierLevel6";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel61:
                return "HEVCMainTierLevel61";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel61:
                return "HEVCHighTierLevel61";
            case MediaCodecInfo.CodecProfileLevel.HEVCMainTierLevel62:
                return "HEVCMainTierLevel62";
            case MediaCodecInfo.CodecProfileLevel.HEVCHighTierLevel62:
            default:
                return String.valueOf(profile);
        }
    }

    private static String getAvcLevelString(int profile)
    {
        switch (profile)
        {
            case MediaCodecInfo.CodecProfileLevel.AVCLevel5:
                return "AVCLevel5";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel1:
                return "AVCLevel1";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel1b:
                return "AVCLevel1b";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel11:
                return "AVCLevel11";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel12:
                return "AVCLevel12";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel13:
                return "AVCLevel13";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel2:
                return "AVCLevel2";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel21:
                return "AVCLevel21";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel22:
                return "AVCLevel22";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel3:
                return "AVCLevel3";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel31:
                return "AVCLevel31";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel32:
                return "AVCLevel32";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel4:
                return "AVCLevel4";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel41:
                return "AVCLevel41";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel42:
                return "AVCLevel42";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel51:
                return "AVCLevel51";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel52:
                return "AVCLevel52";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel6:
                return "AVCLevel6";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel61:
                return "AVCLevel61";
            case MediaCodecInfo.CodecProfileLevel.AVCLevel62:
                return "AVCLevel62";
            default:
                return String.valueOf(profile);
        }
    }

    public static String getAvcProfileString(int profile) {
        switch (profile) {
            /**
             * AVC Baseline profile.
             * See definition in
             * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
             * Annex A.
             */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline:
                return "AVCProfileBaseline";
                /**
                 * AVC Main profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileMain:
                return "AVCProfileMain";
                /**
                 * AVC Extended profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileExtended:
                return "AVCProfileExtended";
                /**
                 * AVC High profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileHigh:
                return "AVCProfileHigh";
                /**
                 * AVC High 10 profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileHigh10:
                return "AVCProfileHigh10";
                /**
                 * AVC High 4:2:2 profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileHigh422:
                return "AVCProfileHigh422";
                /**
                 * AVC High 4:4:4 profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileHigh444:
                return "AVCProfileHigh444";
                /**
                 * AVC Constrained Baseline profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileConstrainedBaseline:
                return "AVCProfileConstrainedBaseline";
                /**
                 * AVC Constrained High profile.
                 * See definition in
                 * <a href="https://www.itu.int/rec/T-REC-H.264-201704-I">H.264 recommendation</a>,
                 * Annex A.
                 */
            case MediaCodecInfo.CodecProfileLevel.AVCProfileConstrainedHigh:
                return "AVCProfileConstrainedHigh";
            default:
                return String.valueOf(profile);
        }
    }



    private static String getAv1ProfileString(int profile)
    {
        switch (profile)
        {
            // Profiles and levels for AV1 Codec, corresponding to the definitions in
            // "AV1 Bitstream & Decoding Process Specification", Annex A
            // found at https://aomedia.org/av1-bitstream-and-decoding-process-specification/

            /**
             * AV1 Main profile 4:2:0 8-bit
             *
             * See definition in
             * <a href="https://aomedia.org/av1-bitstream-and-decoding-process-specification/">AV1 Specification</a>
             * Annex A.
             */
            case MediaCodecInfo.CodecProfileLevel.AV1ProfileMain8:
                return "AV1ProfileMain8";
            /**
             * AV1 Main profile 4:2:0 10-bit
             *
             * See definition in
             * <a href="https://aomedia.org/av1-bitstream-and-decoding-process-specification/">AV1 Specification</a>
             * Annex A.
             */
            case MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10:
                return "AV1ProfileMain10";

            /** AV1 Main profile 4:2:0 10-bit with HDR10. */
            case MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10HDR10:
                return "AV1ProfileMain10HDR10";
            /** AV1 Main profile 4:2:0 10-bit with HDR10Plus. */
            case MediaCodecInfo.CodecProfileLevel.AV1ProfileMain10HDR10Plus:
                return "AV1ProfileMain10HDR10Plus";
            default:
                return String.valueOf(profile);
        }
    }

    private static String getAv1LevelString(int profile)
    {
        switch (profile)
        {
            case MediaCodecInfo.CodecProfileLevel.AV1Level2:
                return "AV1Level2";
            case MediaCodecInfo.CodecProfileLevel.AV1Level21:
                return "AV1Level21";
            case MediaCodecInfo.CodecProfileLevel.AV1Level22:
                return "AV1Level22";
            case MediaCodecInfo.CodecProfileLevel.AV1Level23:
                return "AV1Level23";
            case MediaCodecInfo.CodecProfileLevel.AV1Level3:
                return "AV1Level3";
            case MediaCodecInfo.CodecProfileLevel.AV1Level31:
                return "AV1Level31";
            case MediaCodecInfo.CodecProfileLevel.AV1Level32:
                return "AV1Level32";
            case MediaCodecInfo.CodecProfileLevel.AV1Level33:
                return "AV1Level33";
            case MediaCodecInfo.CodecProfileLevel.AV1Level4:
                return "AV1Level4";
            case MediaCodecInfo.CodecProfileLevel.AV1Level41:
                return "AV1Level41";
            case MediaCodecInfo.CodecProfileLevel.AV1Level42:
                return "AV1Level42";
            case MediaCodecInfo.CodecProfileLevel.AV1Level43:
                return "AV1Level43";
            case MediaCodecInfo.CodecProfileLevel.AV1Level5:
                return "AV1Level5";
            case MediaCodecInfo.CodecProfileLevel.AV1Level51:
                return "AV1Level51";
            case MediaCodecInfo.CodecProfileLevel.AV1Level52:
                return "AV1Level52";
            case MediaCodecInfo.CodecProfileLevel.AV1Level53:
                return "AV1Level53";
            case MediaCodecInfo.CodecProfileLevel.AV1Level6:
                return "AV1Level6";
            case MediaCodecInfo.CodecProfileLevel.AV1Level61:
                return "AV1Level61";
            case MediaCodecInfo.CodecProfileLevel.AV1Level62:
                return "AV1Level62";
            case MediaCodecInfo.CodecProfileLevel.AV1Level63:
                return "AV1Level63";
            case MediaCodecInfo.CodecProfileLevel.AV1Level7:
                return "AV1Level7";
            case MediaCodecInfo.CodecProfileLevel.AV1Level71:
                return "AV1Level71";
            case MediaCodecInfo.CodecProfileLevel.AV1Level72:
                return "AV1Level72";
            case MediaCodecInfo.CodecProfileLevel.AV1Level73:
                return "AV1Level73";
            default:
                return String.valueOf(profile);
        }
    }
}
