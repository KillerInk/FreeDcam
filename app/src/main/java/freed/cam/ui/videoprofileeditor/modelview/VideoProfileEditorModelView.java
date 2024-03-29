package freed.cam.ui.videoprofileeditor.modelview;

import android.media.MediaCodecInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import freed.cam.ui.videoprofileeditor.MediaCodecInfoParser;
import freed.cam.ui.videoprofileeditor.MyMediaCodec;
import freed.cam.ui.videoprofileeditor.binding.Converter;
import freed.cam.ui.videoprofileeditor.enums.VideoCodecs;
import freed.cam.ui.videoprofileeditor.models.AudioCodecModel;
import freed.cam.ui.videoprofileeditor.models.EncoderModel;
import freed.cam.ui.videoprofileeditor.models.HdrModel;
import freed.cam.ui.videoprofileeditor.models.OpcodeModel;
import freed.cam.ui.videoprofileeditor.models.PopupModel;
import freed.cam.ui.videoprofileeditor.models.ProfileLevelModel;
import freed.cam.ui.videoprofileeditor.models.ProfileModel;
import freed.cam.ui.videoprofileeditor.models.RecordModel;
import freed.cam.ui.videoprofileeditor.models.VideoCodecModel;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.VideoMediaProfile;

@HiltViewModel
public class VideoProfileEditorModelView extends ViewModel {

    public final ObservableField<VideoMediaProfile> currentProfile = new ObservableField<>();

    private HashMap<String, VideoMediaProfile> videoMediaProfiles;

    private List<MyMediaCodec> hevcCodecs;
    private List<MyMediaCodec> avccCodecs;
    private List<MyMediaCodec> av1Codecs;

    private final ProfileModel profileModel;
    private final PopupModel popupModel;
    private final RecordModel recordModel;
    private final VideoCodecModel videoCodecModel;
    private final AudioCodecModel audioCodecModel;
    private final EncoderModel encoderModel;
    private final ProfileLevelModel profileLevelModel;
    private final OpcodeModel opcodeModel;
    //private PreviewOpcodeModel preview_opcodeModel;
    private final HdrModel hdrModes;
    SettingsManager settingsManager;

    @Inject
    public VideoProfileEditorModelView(SettingsManager settingsManager)
    {
        this.settingsManager = settingsManager;
        if (!settingsManager.isInit()){
            settingsManager.init();
        }
        settingsManager.getCamApi();
        try {
            videoMediaProfiles = settingsManager.getMediaProfiles();
        }
        catch (NullPointerException e)
        {
            videoMediaProfiles = new HashMap<>();
            VideoMediaProfile m = new VideoMediaProfile(320000,2,3,48000,0,2,6,20000000,2,30,1920,1080,0,"1080p", VideoMediaProfile.VideoMode.Normal,true,-1,-1,"Default",0,-1);
            currentProfile.set(m);
            videoMediaProfiles.put("1080p",m);
        }
        popupModel = new PopupModel();
        profileModel = new ProfileModel(this,popupModel);
        recordModel = new RecordModel(popupModel);

        audioCodecModel = new AudioCodecModel(popupModel);
        profileLevelModel = new ProfileLevelModel(popupModel,this);
        encoderModel = new EncoderModel(popupModel,profileLevelModel,this);
        videoCodecModel = new VideoCodecModel(popupModel,encoderModel,this);
        opcodeModel = new OpcodeModel(popupModel);
        //preview_opcodeModel = new PreviewOpcodeModel(popupModel);
        hdrModes = new HdrModel(popupModel);

        MediaCodecInfoParser mediaCodecInfoParser = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mediaCodecInfoParser = new MediaCodecInfoParser();
            hevcCodecs = mediaCodecInfoParser.getHevcCodecs();
            avccCodecs = mediaCodecInfoParser.getAvcCodecs();
            av1Codecs = mediaCodecInfoParser.getAv1Codecs();
        }
        if (videoMediaProfiles != null
                && videoMediaProfiles.size() > 0
                && settingsManager.get(SettingKeys.VIDEO_PROFILES).get() != null
                && videoMediaProfiles.get(settingsManager.get(SettingKeys.VIDEO_PROFILES).get())!= null) {
            setProfile(videoMediaProfiles.get(settingsManager.get(SettingKeys.VIDEO_PROFILES).get()));
        }
    }

    public PopupModel getPopupModel() {
        return popupModel;
    }

    public ProfileModel getProfileModel() {
        return profileModel;
    }

    public RecordModel getRecordModel() {
        return recordModel;
    }

    public VideoCodecModel getVideoCodecModel() {
        return videoCodecModel;
    }

    public AudioCodecModel getAudioCodecModel() {
        return audioCodecModel;
    }

    public EncoderModel getEncoderModel() {
        return encoderModel;
    }

    public ProfileLevelModel getProfileLevelModel() {
        return profileLevelModel;
    }

    public OpcodeModel getOpcodeModel() {
        return opcodeModel;
    }

    //public PreviewOpcodeModel getPreview_opcodeModel() {
    //    return preview_opcodeModel;
    //}

    public HdrModel getHdrModes() {
        return hdrModes;
    }

    public void setProfile(VideoMediaProfile currentProfile) {
        this.currentProfile.set(currentProfile.clone());
        updateModels();
    }

    public void setProfile(String currentProfile) {
        this.currentProfile.set(videoMediaProfiles.get(currentProfile).clone());
        updateModels();
    }

    private void updateModels() {
        profileModel.setTxt(currentProfile.get().ProfileName);
        recordModel.setTxt(currentProfile.get().Mode.toString());
        videoCodecModel.setTxt(Converter.convertVideoCodecIntToString(null, currentProfile.get().videoCodec));
        videoCodecModel.setValues();
        audioCodecModel.setTxt(Converter.convertAudioCodecIntToString(null, currentProfile.get().audioCodec));
        opcodeModel.setTxt(Converter.convertOpCodecIntToString(null, currentProfile.get().opcode));
        //preview_opcodeModel.setTxt(Converter.convertOpCodecIntToString(null, currentProfile.get().preview_opcode));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            encoderModel.setVisibility(true);
            if (currentProfile.get().encoderName.isEmpty()) {
                encoderModel.setTxt("Default");
                profileLevelModel.setVisibility(false);
            }
            else
                encoderModel.setTxt(currentProfile.get().encoderName);
            encoderModel.setValues();
            if (settingsManager.get(SettingKeys.QCOM_VIDEO_HDR10).isSupported()) {
                hdrModes.setTxt(Converter.convertHdrModecIntToString(null, currentProfile.get().videoHdr));
                hdrModes.setVisibility(true);
            }
            if (currentProfile.get().level == -1 && currentProfile.get().profile == -1)
                profileLevelModel.setDefault();
            else {
                String t = null;
                t = MediaCodecInfoParser.getProfileString(translateVideoCodecString(), currentProfile.get().profile) + " " + MediaCodecInfoParser.getLevelString(translateVideoCodecString(), currentProfile.get().level);
                profileLevelModel.setTxt(t);
            }
        }
        else {
            profileLevelModel.setVisibility(false);
            encoderModel.setVisibility(false);
            hdrModes.setVisibility(false);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private String translateVideoCodecString()
    {
        if (videoCodecModel.getTxt().equals(VideoCodecs.HEVC.name()))
            return MediaCodecInfoParser.hevc;
        if (videoCodecModel.getTxt().equals(VideoCodecs.H264.name()))
            return MediaCodecInfoParser.avc;
        return "";
    }

    public VideoMediaProfile getProfile() {
        return currentProfile.get();
    }

    public HashMap<String, VideoMediaProfile> getVideoMediaProfiles() {
        return videoMediaProfiles;
    }

    public List<String> getHevcEncoderNames()
    {
        List<String> s = new ArrayList<>();
        s.add("Default");
        for (MyMediaCodec m : hevcCodecs)
            s.add(m.getCodecName());
        return s;
    }

    public List<String> getAvcEncoderNames()
    {
        List<String> s = new ArrayList<>();
        s.add("Default");
        for (MyMediaCodec m : avccCodecs)
            s.add(m.getCodecName());
        return s;
    }

    public List<String> getProfileLevels(String encoder)
    {
        if (videoCodecModel.getTxt().equals(VideoCodecs.H264.name()))
        {
            List<String> strings = getStrings(avccCodecs);
            return strings;
        }
        else if (videoCodecModel.getTxt().equals(VideoCodecs.HEVC.name()))
        {
            List<String> strings = getStrings(hevcCodecs);
            return strings;
        }
        return null;
    }

    private List<String> getStrings(List<MyMediaCodec> mediaCodecs) {
        for (MyMediaCodec m : mediaCodecs)
        {
            if (m.getCodecName().equals(encoderModel.getTxt()))
            {
                List<String> strings = new ArrayList<>();
                for (String s : m.getProfileLevelList().keySet())
                    strings.add(s);
                return strings;
            }
        }
        return null;
    }

    public MediaCodecInfo.CodecProfileLevel getCodecProfileLevel(String profilelvl)
    {
        if (videoCodecModel.getTxt().equals(VideoCodecs.H264.name()) && !encoderModel.getTxt().equals("Default"))
        {
            for (MyMediaCodec myMediaCodec : avccCodecs)
            {
                if (myMediaCodec.getCodecName().equals(encoderModel.getTxt()))
                {
                    return myMediaCodec.getProfileLevelList().get(profilelvl);
                }
            }
        }
        else if (videoCodecModel.getTxt().equals(VideoCodecs.HEVC.name()) && !encoderModel.getTxt().equals("Default"))
        {
            for (MyMediaCodec myMediaCodec : hevcCodecs)
            {
                if (myMediaCodec.getCodecName().equals(encoderModel.getTxt()))
                {
                    return myMediaCodec.getProfileLevelList().get(profilelvl);
                }
            }
        }
        return null;
    }

    public void removeMediaProfile(String name)
    {
        videoMediaProfiles.remove(getProfile().ProfileName);
        settingsManager.saveMediaProfiles(videoMediaProfiles);
        currentProfile.set(videoMediaProfiles.entrySet().iterator().next().getValue());
        settingsManager.get(SettingKeys.VIDEO_PROFILES).set(currentProfile.get().ProfileName);
        settingsManager.save();
        updateModels();
    }

    public void addMediaProfile(VideoMediaProfile profile)
    {
        videoMediaProfiles.put(profile.ProfileName,profile);
        settingsManager.saveMediaProfiles(videoMediaProfiles);
        settingsManager.get(SettingKeys.VIDEO_PROFILES).set(currentProfile.get().ProfileName);
        settingsManager.save();
        updateModels();
    }

}
