package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

import freed.cam.ui.videoprofileeditor.enums.AudioCodecs;

public class AudioCodecModel extends ButtonModel {

    public AudioCodecModel(PopupModel popupModel) {
        super(popupModel);
    }

    @Override
    public List<String> getStrings() {
        List<String> strings = new ArrayList<>();
        for (AudioCodecs codecs : AudioCodecs.values())
            strings.add(codecs.name());
        return strings;
    }
}
