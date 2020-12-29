package freed.cam.ui.videoprofileeditor.modelview;

import android.media.MediaCodecInfo;
import android.os.Build;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import freed.cam.ui.videoprofileeditor.MediaCodecInfoParser;
import freed.cam.ui.videoprofileeditor.MyMediaCodec;

import freed.cam.ui.videoprofileeditor.binding.Converter;
import freed.cam.ui.videoprofileeditor.enums.AudioCodecs;
import freed.cam.ui.videoprofileeditor.enums.VideoCodecs;
import freed.cam.ui.videoprofileeditor.models.AudioCodecModel;
import freed.cam.ui.videoprofileeditor.models.EncoderModel;
import freed.cam.ui.videoprofileeditor.models.OpcodeModel;
import freed.cam.ui.videoprofileeditor.models.PopupModel;
import freed.cam.ui.videoprofileeditor.models.PreviewOpcodeModel;
import freed.cam.ui.videoprofileeditor.models.ProfileLevelModel;
import freed.cam.ui.videoprofileeditor.models.ProfileModel;
import freed.cam.ui.videoprofileeditor.models.RecordModel;
import freed.cam.ui.videoprofileeditor.models.VideoCodecModel;
import freed.settings.SettingKeys;
import freed.settings.SettingsManager;
import freed.utils.VideoMediaProfile;

public class VideoProfileEditorModelView extends ViewModel {

    public final ObservableField<VideoMediaProfile> currentProfile = new ObservableField<>();

    private HashMap<String, VideoMediaProfile> videoMediaProfiles;

    private List<MyMediaCodec> hevcCodecs;
    private List<MyMediaCodec> avccCodecs;
    private List<MyMediaCodec> av1Codecs;

    private ProfileModel profileModel;
    private PopupModel popupModel;
    private RecordModel recordModel;
    private VideoCodecModel videoCodecModel;
    private AudioCodecModel audioCodecModel;
    private EncoderModel encoderModel;
    private ProfileLevelModel profileLevelModel;
    private OpcodeModel opcodeModel;
    private PreviewOpcodeModel preview_opcodeModel;

    public VideoProfileEditorModelView()
    {
        if (!SettingsManager.getInstance().isInit()){
            SettingsManager.getInstance().init();
        }
        SettingsManager.getInstance().getCamApi();
        videoMediaProfiles = SettingsManager.getInstance().getMediaProfiles();
        popupModel = new PopupModel();
        profileModel = new ProfileModel(this,popupModel);
        recordModel = new RecordModel(popupModel);

        audioCodecModel = new AudioCodecModel(popupModel);
        profileLevelModel = new ProfileLevelModel(popupModel,this);
        encoderModel = new EncoderModel(popupModel,profileLevelModel,this);
        videoCodecModel = new VideoCodecModel(popupModel,encoderModel,this);
        opcodeModel = new OpcodeModel(popupModel);
        preview_opcodeModel = new PreviewOpcodeModel(popupModel);

        MediaCodecInfoParser mediaCodecInfoParser = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mediaCodecInfoParser = new MediaCodecInfoParser();
            hevcCodecs = mediaCodecInfoParser.getHevcCodecs();
            avccCodecs = mediaCodecInfoParser.getAvcCodecs();
            av1Codecs = mediaCodecInfoParser.getAv1Codecs();
        }
        if (videoMediaProfiles != null) {
            setProfile(videoMediaProfiles.get(SettingsManager.get(SettingKeys.VideoProfiles).get()));
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

    public PreviewOpcodeModel getPreview_opcodeModel() {
        return preview_opcodeModel;
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
        preview_opcodeModel.setTxt(Converter.convertOpCodecIntToString(null, currentProfile.get().preview_opcode));
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encoderModel.setVisibility(true);
            if (currentProfile.get().encoderName.isEmpty()) {
                encoderModel.setTxt("Default");
                profileLevelModel.setVisibility(false);
            }
            else
                encoderModel.setTxt(currentProfile.get().encoderName);
            encoderModel.setValues();
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
            if (strings != null) return strings;
        }
        else if (videoCodecModel.getTxt().equals(VideoCodecs.HEVC.name()))
        {
            List<String> strings = getStrings(hevcCodecs);
            if (strings != null) return strings;
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

}
