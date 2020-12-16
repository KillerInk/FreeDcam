package freed.cam.ui.videoprofileeditor.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.troop.freedcam.BR;

import java.util.ArrayList;
import java.util.List;

import freed.utils.VideoMediaProfile;

public class RecordModel extends ButtonModel {

    public RecordModel(PopupModel popupModel) {
        super(popupModel);
    }

    @Override
    public List<String> getStrings() {
        List<String> strings = new ArrayList<>();
        strings.add(VideoMediaProfile.VideoMode.Normal.toString());
        strings.add(VideoMediaProfile.VideoMode.Timelapse.toString());
        strings.add(VideoMediaProfile.VideoMode.Highspeed.toString());
        return strings;
    }

}
