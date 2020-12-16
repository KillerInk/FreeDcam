package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import freed.cam.ui.videoprofileeditor.enums.VideoCodecs;
import freed.cam.ui.videoprofileeditor.modelview.VideoProfileEditorModelView;

public class VideoCodecModel extends ButtonModel {


    private EncoderModel encoderModel;
    private VideoProfileEditorModelView modelView;

    public VideoCodecModel(PopupModel popupModel, EncoderModel encoderModel, VideoProfileEditorModelView modelView) {
        super(popupModel);
        this.encoderModel = encoderModel;
        this.modelView = modelView;
    }

    @Override
    public void onPopupItemClick(String item) {
        super.onPopupItemClick(item);
        setDefaults();
    }

    @Override
    public List<String> getStrings() {
        List<String> strings = new ArrayList<>();
        strings.add(VideoCodecs.H264.toString());
        strings.add(VideoCodecs.HEVC.toString());
        return strings;
    }

    @Override
    public void setTxt(String txt) {
        super.setTxt(txt);
    }

    public void setDefaults()
    {

        setValues();
        encoderModel.setToDefault();
    }

    public void setValues()
    {
        List<String> values = null;
        if (getTxt().equals(VideoCodecs.HEVC.name()))
            values = modelView.getHevcEncoderNames();
        if (getTxt().equals(VideoCodecs.H264.name()))
            values = modelView.getAvcEncoderNames();
        encoderModel.setValues(values);
    }
}
